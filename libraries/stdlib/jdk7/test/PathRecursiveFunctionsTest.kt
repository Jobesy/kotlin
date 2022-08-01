/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.jdk7.test

import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributeView
import kotlin.io.path.*
import kotlin.jdk7.test.PathTreeWalkTest.Companion.createTestFiles
import kotlin.jdk7.test.PathTreeWalkTest.Companion.referenceFilenames
import kotlin.jdk7.test.PathTreeWalkTest.Companion.referenceFilesOnly
import kotlin.jdk7.test.PathTreeWalkTest.Companion.testVisitedFiles
import kotlin.test.*

class PathRecursiveFunctionsTest : AbstractPathTest() {
    @Test
    fun deleteFile() {
        val file = createTempFile()

        assertTrue(file.exists())
        file.deleteRecursively()
        assertFalse(file.exists())

        file.createFile().writeText("non-empty file")

        assertTrue(file.exists())
        file.deleteRecursively()
        assertFalse(file.exists())
        file.deleteRecursively() // successfully deletes recursively a non-existent file
    }

    @Test
    fun deleteDirectory() {
        val dir = createTestFiles()

        assertTrue(dir.exists())
        dir.deleteRecursively()
        assertFalse(dir.exists())
        dir.deleteRecursively() // successfully deletes recursively a non-existent directory
    }

    @Test
    fun deleteNotExistingParent() {
        val basedir = createTempDirectory().cleanupRecursively()
        basedir.resolve("a/b").deleteRecursively()
        basedir.resolve("a/b/c").deleteRecursively()
    }

    private fun Path.walkIncludeDirectories(): Sequence<Path> =
        this.walk(PathWalkOption.INCLUDE_DIRECTORIES)

    @Test
    fun deleteRestrictedRead() {
        val basedir = createTestFiles().cleanupRecursively()
        val restrictedEmptyDir = basedir.resolve("6")
        val restrictedDir = basedir.resolve("1")
        val restrictedFile = basedir.resolve("7.txt")

        withRestrictedRead(restrictedEmptyDir, restrictedDir, restrictedFile) {
            val error = assertFailsWith<java.nio.file.FileSystemException>("Expected incomplete recursive deletion") {
                basedir.deleteRecursively()
            }

            // AccessDeniedException when opening restrictedEmptyDir and restrictedDir
            // DirectoryNotEmptyException is not thrown from parent directory
            assertEquals(2, error.suppressedExceptions.size)
            assertIs<java.nio.file.AccessDeniedException>(error.suppressedExceptions[0])
            assertIs<java.nio.file.AccessDeniedException>(error.suppressedExceptions[1])

            // Couldn't read directory entries.
            // No attempt to delete even when empty directories can be removed without write permission
            assertTrue(restrictedEmptyDir.exists())
            assertTrue(restrictedDir.exists()) // couldn't read directory entries
            assertFalse(restrictedFile.exists()) // restricted read allows removal of file

            restrictedEmptyDir.toFile().setReadable(true)
            restrictedDir.toFile().setReadable(true)
            testVisitedFiles(listOf("", "1", "1/2", "1/3", "1/3/4.txt", "1/3/5.txt", "6"), basedir.walkIncludeDirectories(), basedir)
            basedir.deleteRecursively()
        }
    }

    @Test
    fun deleteRestrictedWrite() {
        val basedir = createTestFiles().cleanupRecursively()
        val restrictedEmptyDir = basedir.resolve("6")
        val restrictedDir = basedir.resolve("8")
        val restrictedFile = basedir.resolve("1/3/5.txt")

        withRestrictedWrite(restrictedEmptyDir, restrictedDir, restrictedFile) {
            val error = assertFailsWith<FileSystemException>("Expected incomplete recursive deletion") {
                basedir.deleteRecursively()
            }

            // AccessDeniedException when deleting "8/9.txt"
            // DirectoryNotEmptyException is not thrown from parent directories
            val accessDenied = assertIs<java.nio.file.AccessDeniedException>(error.suppressedExceptions.single())
            assertTrue(accessDenied.file.endsWith("9.txt"))

            assertFalse(restrictedEmptyDir.exists()) // empty directories can be removed without write permission
            assertTrue(restrictedDir.exists())
            assertTrue(restrictedDir.resolve("9.txt").exists())
            assertFalse(restrictedFile.exists()) // plain files can be removed without write permission
        }
    }

    @Test
    fun deleteBaseSymlinkToFile() {
        val file = createTempFile().cleanup()
        val link = createTempDirectory().cleanupRecursively().resolve("link").tryCreateSymbolicLinkTo(file) ?: return

        link.deleteRecursively()
        assertFalse(link.exists(LinkOption.NOFOLLOW_LINKS))
        assertTrue(file.exists())
    }

    @Test
    fun deleteBaseSymlinkToDirectory() {
        val dir = createTestFiles().cleanupRecursively()
        val link = createTempDirectory().cleanupRecursively().resolve("link").tryCreateSymbolicLinkTo(dir) ?: return

        link.deleteRecursively()
        assertFalse(link.exists(LinkOption.NOFOLLOW_LINKS))
        testVisitedFiles(listOf("") + referenceFilenames, dir.walkIncludeDirectories(), dir)
    }

    @Test
    fun deleteSymlinkToFile() {
        val file = createTempFile().cleanup()
        val dir = createTestFiles().cleanupRecursively().also { it.resolve("8/link").tryCreateSymbolicLinkTo(file) ?: return }

        dir.deleteRecursively()
        assertFalse(dir.exists())
        assertTrue(file.exists())
    }

    @Test
    fun deleteSymlinkToDirectory() {
        val dir1 = createTestFiles().cleanupRecursively()
        val dir2 = createTestFiles().cleanupRecursively().also { it.resolve("8/link").tryCreateSymbolicLinkTo(dir1) ?: return }

        dir2.deleteRecursively()
        assertFalse(dir2.exists())
        testVisitedFiles(listOf("") + referenceFilenames, dir1.walkIncludeDirectories(), dir1)
    }

    @Test
    fun deleteParentSymlink() {
        val dir1 = createTestFiles().cleanupRecursively()
        val dir2 = createTempDirectory().cleanupRecursively().also { it.resolve("link").tryCreateSymbolicLinkTo(dir1) ?: return }

        dir2.resolve("link/8").deleteRecursively()
        assertFalse(dir1.resolve("8").exists())

        dir2.resolve("link/1/3").deleteRecursively()
        assertFalse(dir1.resolve("1/3").exists())
    }

    @Test
    fun deleteSymlinkToSymlink() {
        val dir = createTestFiles().cleanupRecursively()
        val link = createTempDirectory().cleanupRecursively().resolve("link").tryCreateSymbolicLinkTo(dir) ?: return
        val linkToLink = createTempDirectory().cleanupRecursively().resolve("linkToLink").tryCreateSymbolicLinkTo(link) ?: return

        linkToLink.deleteRecursively()
        assertFalse(linkToLink.exists(LinkOption.NOFOLLOW_LINKS))
        assertTrue(link.exists(LinkOption.NOFOLLOW_LINKS))
        testVisitedFiles(listOf("") + referenceFilenames, dir.walkIncludeDirectories(), dir)
    }

    @Test
    fun deleteSymlinkCyclic() {
        val basedir = createTestFiles().cleanupRecursively()
        val original = basedir.resolve("1")
        original.resolve("2/link").tryCreateSymbolicLinkTo(original) ?: return

        basedir.deleteRecursively()
        assertFalse(basedir.exists())
    }

    @Test
    fun deleteSymlinkCyclicWithTwo() {
        val basedir = createTestFiles().cleanupRecursively()
        val dir8 = basedir.resolve("8")
        val dir2 = basedir.resolve("1/2")
        dir8.resolve("linkTo2").tryCreateSymbolicLinkTo(dir2) ?: return
        dir2.resolve("linkTo8").tryCreateSymbolicLinkTo(dir8) ?: return

        basedir.deleteRecursively()
        assertFalse(basedir.exists())
    }

    @Test
    fun deleteSymlinkPointingToItself() {
        val basedir = createTempDirectory().cleanupRecursively()
        val link = basedir.resolve("link")
        link.tryCreateSymbolicLinkTo(link) ?: return

        basedir.deleteRecursively()
        assertFalse(basedir.exists())
    }

    @Test
    fun deleteSymlinkTwoPointingToEachOther() {
        val basedir = createTempDirectory().cleanupRecursively()
        val link1 = basedir.resolve("link1")
        val link2 = basedir.resolve("link2").tryCreateSymbolicLinkTo(link1) ?: return
        link1.tryCreateSymbolicLinkTo(link2) ?: return

        basedir.deleteRecursively()
        assertFalse(basedir.exists())
    }

    private fun compareFiles(src: Path, dst: Path, message: String? = null) {
        assertTrue(dst.exists())
        assertEquals(src.isRegularFile(), dst.isRegularFile(), message)
        assertEquals(src.isDirectory(), dst.isDirectory(), message)
        if (dst.isRegularFile()) {
            assertTrue(src.readBytes().contentEquals(dst.readBytes()), message)
        }
    }

    private fun compareDirectories(src: Path, dst: Path) {
        for (srcFile in src.walkIncludeDirectories()) {
            val dstFile = dst.resolve(srcFile.relativeTo(src))
            compareFiles(srcFile, dstFile)
        }
    }

    @Test
    fun copyFileToFile() {
        val src = createTempFile().cleanup().also { it.writeText("hello") }
        val dst = createTempDirectory().cleanupRecursively().resolve("dst")

        src.copyToRecursively(dst, followLinks = false)
        compareFiles(src, dst)

        dst.writeText("bye")
        assertFailsWith<java.nio.file.FileAlreadyExistsException> {
            src.copyToRecursively(dst, followLinks = false)
        }
        assertEquals("bye", dst.readText())

        src.copyToRecursively(dst, followLinks = false, overwrite = true)
        compareFiles(src, dst)
    }

    @Test
    fun copyFileToDirectory() {
        val src = createTempFile().cleanup().also { it.writeText("hello") }
        val dst = createTestFiles().cleanupRecursively()

        assertFailsWith<java.nio.file.FileAlreadyExistsException> {
            src.copyToRecursively(dst, followLinks = false)
        }
        assertTrue(dst.isDirectory())

        assertFailsWith<java.nio.file.DirectoryNotEmptyException> {
            src.copyToRecursively(dst, followLinks = false) { source, target ->
                source.copyTo(target, overwrite = true)
                CopyActionResult.CONTINUE
            }
        }
        assertTrue(dst.isDirectory())

        src.copyToRecursively(dst, followLinks = false, overwrite = true)
        compareFiles(src, dst)
    }

    private fun Path.relativePathString(base: Path): String {
        return relativeToOrSelf(base).invariantSeparatorsPathString
    }

    @Test
    fun copyDirectoryToDirectory() {
        val src = createTestFiles().cleanupRecursively()
        val dst = createTempDirectory().cleanupRecursively().resolve("dst")

        src.copyToRecursively(dst, followLinks = false)
        compareDirectories(src, dst)

        src.resolve("1/3/4.txt").writeText("hello")
        dst.resolve("10").createDirectory()

        val conflictingFiles = mutableListOf<String>()
        src.copyToRecursively(dst, followLinks = false, onError = { path, exception ->
            assertIs<java.nio.file.FileAlreadyExistsException>(exception)
            conflictingFiles.add(path.relativePathString(src))
            OnErrorResult.SKIP_SUBTREE
        })
        assertEquals(referenceFilesOnly.sorted(), conflictingFiles.sorted())
        assertTrue(dst.resolve("1/3/4.txt").readText().isEmpty())

        src.copyToRecursively(dst, followLinks = false, overwrite = true)
        compareDirectories(src, dst)
        assertTrue(dst.resolve("10").exists())
    }

    @Test
    fun copyDirectoryToFile() {
        val src = createTestFiles().cleanupRecursively()
        val dst = createTempFile().cleanupRecursively().also { it.writeText("hello") }

        val existsException = assertFailsWith<java.nio.file.FileAlreadyExistsException> {
            src.copyToRecursively(dst, followLinks = false)
        }
        // attempted to copy only the root directory(src)
        assertEquals(dst.toString(), existsException.file)
        assertTrue(dst.isRegularFile())

        src.copyToRecursively(dst, followLinks = false, overwrite = true)
        compareDirectories(src, dst)
    }

    @Test
    fun copyNonExistentSource() {
        val src = createTempDirectory().also { it.deleteExisting() }
        val dst = createTempDirectory()

        assertFailsWith<java.nio.file.NoSuchFileException> {
            src.copyToRecursively(dst, followLinks = false)
        }

        dst.deleteExisting()
        assertFailsWith<java.nio.file.NoSuchFileException> {
            src.copyToRecursively(dst, followLinks = false)
        }
    }

    @Test
    fun copyNonExistentDestinationParent() {
        val src = createTempDirectory().cleanupRecursively()
        val dst = createTempDirectory().cleanupRecursively().resolve("parent/dst")

        assertFalse(dst.parent.exists())

        src.copyToRecursively(dst, followLinks = false, onError = { path, exception ->
            assertIs<java.nio.file.NoSuchFileException>(exception)
            assertEquals(src, path)
            assertEquals(dst.toString(), exception.file)
            OnErrorResult.SKIP_SUBTREE
        })

        src.copyToRecursively(dst.apply { parent?.createDirectories() }, followLinks = false)
    }

    @Test
    fun copyRestrictedReadInSource() {
        val src = createTestFiles().cleanupRecursively()
        val dst = createTempDirectory().cleanupRecursively()

        val restrictedDir = src.resolve("1/3")
        val restrictedFile = src.resolve("7.txt")

        withRestrictedRead(restrictedDir, restrictedFile, alsoReset = listOf(dst.resolve("1/3"), dst.resolve("7.txt"))) {
            val accessDeniedFiles = mutableListOf<String>()
            src.copyToRecursively(dst, followLinks = false, onError = { path, exception ->
                assertIs<java.nio.file.AccessDeniedException>(exception)
                accessDeniedFiles.add(path.relativePathString(src))
                OnErrorResult.SKIP_SUBTREE
            })
            assertEquals(listOf("1/3", "7.txt"), accessDeniedFiles.sorted())

            assertFalse(dst.resolve("1/3").exists()) // restricted directory is not copied
            assertFalse(dst.resolve("7.txt").exists()) // restricted file is not copied
        }
    }

    @Test
    fun copyRestrictedWriteInSource() {
        val src = createTestFiles().cleanupRecursively()
        val dst = createTempDirectory().cleanupRecursively()

        val restrictedDir = src.resolve("1/3")
        val restrictedFile = src.resolve("7.txt")

        withRestrictedWrite(restrictedDir, restrictedFile, alsoReset = listOf(dst.resolve("1/3"), dst.resolve("7.txt"))) {
            val accessDeniedFiles = mutableListOf<String>()
            src.copyToRecursively(dst, followLinks = false, onError = { _, exception ->
                assertIs<java.nio.file.AccessDeniedException>(exception)
                accessDeniedFiles.add(exception.file)
                OnErrorResult.SKIP_SUBTREE
            })
            assertEquals(listOf("1/3/4.txt", "1/3/5.txt").map { dst.resolve(it).toString() }, accessDeniedFiles.sorted())

            assertTrue(dst.resolve("1/3").exists()) // restricted directory is copied
            assertFalse(dst.resolve("1/3").isWritable()) // access permissions are copied
            assertTrue(dst.resolve("7.txt").exists()) // restricted file is copied
            assertFalse(dst.resolve("7.txt").isWritable()) // access permissions are copied
        }
    }

    @Test
    fun copyRestrictedWriteInDestination() {
        val src = createTestFiles().cleanupRecursively()
        val dst = createTestFiles().cleanupRecursively()

        src.resolve("1/3/4.txt").writeText("hello")
        src.resolve("7.txt").writeText("hi")

        val restrictedDir = dst.resolve("1/3")
        val restrictedFile = dst.resolve("7.txt")

        withRestrictedWrite(restrictedDir, restrictedFile) {
            val accessDeniedFiles = mutableListOf<String>()
            src.copyToRecursively(dst, followLinks = false, overwrite = true, onError = { _, exception ->
                assertIs<java.nio.file.AccessDeniedException>(exception)
                accessDeniedFiles.add(exception.file)
                OnErrorResult.SKIP_SUBTREE
            })
            assertEquals(listOf("1/3/4.txt", "1/3/5.txt").map { dst.resolve(it).toString() }, accessDeniedFiles.sorted())

            assertNotEquals(src.resolve("1/3/4.txt").readText(), dst.resolve("1/3/4.txt").readText())
            assertEquals(src.resolve("7.txt").readText(), dst.resolve("7.txt").readText())
        }
    }

    @Test
    fun copyBrokenBaseSymlink() {
        val basedir = createTempDirectory().cleanupRecursively()
        val target = basedir.resolve("target")
        val link = basedir.resolve("link").tryCreateSymbolicLinkTo(target) ?: return
        val dst = basedir.resolve("dst")

        // the same behavior as link.copyTo(dst, LinkOption.NOFOLLOW_LINKS)
        link.copyToRecursively(dst, followLinks = false)
        assertTrue(dst.isSymbolicLink())
        assertTrue(dst.exists(LinkOption.NOFOLLOW_LINKS))
        assertFalse(dst.exists())

        // the same behavior as link.copyTo(dst)
        dst.deleteExisting()
        assertFailsWith<java.nio.file.NoSuchFileException> {
            link.copyToRecursively(dst, followLinks = true)
        }
        assertFalse(dst.exists(LinkOption.NOFOLLOW_LINKS))
    }

    @Test
    fun copyBrokenSymlink() {
        val src = createTestFiles().cleanupRecursively()
        val dst = createTempDirectory().cleanupRecursively().resolve("dst")
        val target = createTempDirectory().cleanupRecursively().resolve("target")
        src.resolve("8/link").tryCreateSymbolicLinkTo(target) ?: return
        val dstLink = dst.resolve("8/link")

        // the same behavior as link.copyTo(dst, LinkOption.NOFOLLOW_LINKS)
        src.copyToRecursively(dst, followLinks = false)
        assertTrue(dstLink.isSymbolicLink())
        assertTrue(dstLink.exists(LinkOption.NOFOLLOW_LINKS))
        assertFalse(dstLink.exists())

        // the same behavior as link.copyTo(dst)
        dst.deleteRecursively()
        assertFailsWith<java.nio.file.NoSuchFileException> {
            src.copyToRecursively(dst, followLinks = true)
        }
        assertFalse(dstLink.exists(LinkOption.NOFOLLOW_LINKS))
    }

    @Test
    fun copyBaseSymlinkPointingToFile() {
        val src = createTempFile().cleanup().also { it.writeText("hello") }
        val link = createTempDirectory().cleanupRecursively().resolve("link").tryCreateSymbolicLinkTo(src) ?: return
        val dst = createTempDirectory().cleanupRecursively().resolve("dst")

        link.copyToRecursively(dst, followLinks = false)
        compareFiles(link, dst)

        dst.deleteExisting()

        link.copyToRecursively(dst, followLinks = true)
        compareFiles(src, dst)
    }

    @Test
    fun copyBaseSymlinkPointingToDirectory() {
        val src = createTestFiles().cleanupRecursively()
        val link = createTempDirectory().cleanupRecursively().resolve("link").tryCreateSymbolicLinkTo(src) ?: return
        val dst = createTempDirectory().cleanupRecursively().resolve("dst")

        link.copyToRecursively(dst, followLinks = false)
        compareFiles(link, dst)

        dst.deleteExisting()

        link.copyToRecursively(dst, followLinks = true)
        compareDirectories(src, dst)
    }

    @Test
    fun copySymlinkPointingToDirectory() {
        val symlinkTarget = createTestFiles().cleanupRecursively()
        val src = createTestFiles().cleanupRecursively().also { it.resolve("8/link").tryCreateSymbolicLinkTo(symlinkTarget) ?: return }
        val dst = createTempDirectory().cleanupRecursively().resolve("dst")

        src.copyToRecursively(dst, followLinks = false)
        val srcContent = listOf("", "8/link") + referenceFilenames
        testVisitedFiles(srcContent, dst.walkIncludeDirectories(), dst)

        dst.deleteRecursively()

        src.copyToRecursively(dst, followLinks = true)
        val expectedDstContent = srcContent + referenceFilenames.map { "8/link/$it" }
        testVisitedFiles(expectedDstContent, dst.walkIncludeDirectories(), dst)
    }

    @Test
    fun copyIgnoreExistingDirectoriesFollowLinks() {
        val src = createTestFiles().cleanupRecursively()
        val symlinkTarget = createTempDirectory().cleanupRecursively()
        val dst = createTempDirectory().cleanupRecursively().also {
            it.resolve("1").createDirectory()
            it.resolve("1/3").tryCreateSymbolicLinkTo(symlinkTarget) ?: return
        }

        src.copyToRecursively(dst, followLinks = true, onError = { path, exception ->
            assertIs<java.nio.file.FileAlreadyExistsException>(exception)
            assertEquals(src.resolve("1/3"), path)
            assertEquals(dst.resolve("1/3").toString(), exception.file)
            OnErrorResult.SKIP_SUBTREE
        })
        assertTrue(dst.resolve("1/3").isSymbolicLink())
        assertTrue(symlinkTarget.listDirectoryEntries().isEmpty())

        src.copyToRecursively(dst, followLinks = true, overwrite = true)
        assertFalse(dst.resolve("1/3").isSymbolicLink())
        assertTrue(symlinkTarget.listDirectoryEntries().isEmpty())
    }

    @Test
    fun copyIgnoreExistingDirectoriesNoFollowLinks() {
        val src = createTestFiles().cleanupRecursively()
        val symlinkTarget = createTempDirectory().cleanupRecursively()
        val dst = createTempDirectory().cleanupRecursively().also {
            it.resolve("1").createDirectory()
            it.resolve("1/3").tryCreateSymbolicLinkTo(symlinkTarget) ?: return
        }

        src.copyToRecursively(dst, followLinks = false, onError = { path, exception ->
            assertIs<java.nio.file.FileAlreadyExistsException>(exception)
            assertEquals(src.resolve("1/3"), path)
            assertEquals(dst.resolve("1/3").toString(), exception.file)
            OnErrorResult.SKIP_SUBTREE
        })
        assertTrue(dst.resolve("1/3").isSymbolicLink())
        assertTrue(symlinkTarget.listDirectoryEntries().isEmpty())

        src.copyToRecursively(dst, followLinks = false, overwrite = true)
        assertFalse(dst.resolve("1/3").isSymbolicLink())
        assertTrue(symlinkTarget.listDirectoryEntries().isEmpty())
    }

    @Test
    fun copyParentSymlink() {
        val source = createTestFiles().cleanupRecursively()
        val linkToSource = createTempDirectory().cleanupRecursively().resolve("link").tryCreateSymbolicLinkTo(source) ?: return
        val sources = listOf(
            source to referenceFilenames,
            linkToSource.resolve("8") to listOf("9.txt"),
            linkToSource.resolve("1/3") to listOf("4.txt", "5.txt")
        )

        for ((src, srcContent) in sources) {
            for (followLinks in listOf(false, true)) {
                val target = createTempDirectory().cleanupRecursively().also { it.resolve("a/b").createDirectories() }
                val linkToTarget = createTempDirectory().cleanupRecursively().resolve("link").tryCreateSymbolicLinkTo(target) ?: return
                val targets = listOf(
                    target to listOf("a", "a/b"),
                    linkToTarget.resolve("a") to listOf("b"),
                    linkToTarget.resolve("a/b") to listOf()
                )

                for ((dst, dstContent) in targets) {
                    src.copyToRecursively(dst, followLinks = followLinks)
                    val expectedDstContent = listOf("") + dstContent + srcContent
                    testVisitedFiles(expectedDstContent, dst.walkIncludeDirectories(), dst)
                }
            }
        }
    }

    @Test
    fun copySymlinkToSymlink() {
        val src = createTestFiles().cleanupRecursively()
        val link = createTempDirectory().cleanupRecursively().resolve("link").tryCreateSymbolicLinkTo(src) ?: return
        val linkToLink = createTempDirectory().cleanupRecursively().resolve("linkToLink").tryCreateSymbolicLinkTo(link) ?: return
        val dst = createTempDirectory().cleanupRecursively().resolve("dst")

        linkToLink.copyToRecursively(dst, followLinks = true)
        testVisitedFiles(listOf("") + referenceFilenames, dst.walkIncludeDirectories(), dst)
    }

    @Test
    fun copySymlinkCyclic() {
        val src = createTestFiles().cleanupRecursively()
        val original = src.resolve("1")
        original.resolve("2/link").tryCreateSymbolicLinkTo(original) ?: return
        val dst = createTempDirectory().cleanupRecursively().resolve("dst")

        src.copyToRecursively(dst, followLinks = true, onError = { _, exception ->
            assertIs<java.nio.file.FileSystemLoopException>(exception)
            assertEquals(src.resolve("1/2/link").toString(), exception.file)
            OnErrorResult.SKIP_SUBTREE
        })

        // partial copy, only "1/2/link" is not copied
        testVisitedFiles(listOf("") + referenceFilenames, dst.walkIncludeDirectories(), dst)
    }

    @Test
    fun copySymlinkCyclicWithTwo() {
        val src = createTestFiles().cleanupRecursively()
        val dir8 = src.resolve("8")
        val dir2 = src.resolve("1/2")
        dir8.resolve("linkTo2").tryCreateSymbolicLinkTo(dir2) ?: return
        dir2.resolve("linkTo8").tryCreateSymbolicLinkTo(dir8) ?: return
        val dst = createTempDirectory().cleanupRecursively().resolve("dst")

        val loops = mutableListOf<String>()
        src.copyToRecursively(dst, followLinks = true, onError = { _, exception ->
            assertIs<java.nio.file.FileSystemLoopException>(exception)
            loops.add(exception.file)
            OnErrorResult.SKIP_SUBTREE
        })
        assertEquals(listOf("1/2/linkTo8/linkTo2", "8/linkTo2/linkTo8").map { src.resolve(it).toString() }, loops.sorted())

        // partial copy, only "1/2/linkTo8/linkTo2" and "8/linkTo2/linkTo8" are not copied
        val expected = listOf("", "1/2/linkTo8", "1/2/linkTo8/9.txt", "8/linkTo2") + referenceFilenames
        testVisitedFiles(expected, dst.walkIncludeDirectories(), dst)
    }

    @Test
    fun copySymlinkPointingToItself() {
        val src = createTempDirectory().cleanupRecursively()
        val link = src.resolve("link")
        link.tryCreateSymbolicLinkTo(link) ?: return
        val dst = createTempDirectory().cleanupRecursively().resolve("dst")

        assertFailsWith<java.nio.file.FileSystemException> {
            // throws with message "Too many levels of symbolic links"
            src.copyToRecursively(dst, followLinks = true)
        }
    }

    @Test
    fun copySymlinkTwoPointingToEachOther() {
        val src = createTempDirectory().cleanupRecursively()
        val link1 = src.resolve("link1")
        val link2 = src.resolve("link2").tryCreateSymbolicLinkTo(link1) ?: return
        link1.tryCreateSymbolicLinkTo(link2) ?: return
        val dst = createTempDirectory().cleanupRecursively().resolve("dst")

        assertFailsWith<java.nio.file.FileSystemException> {
            // throws with message "Too many levels of symbolic links"
            src.copyToRecursively(dst, followLinks = true)
        }
    }

    @Test
    fun copyWithNestedCopyToRecursively() {
        val src = createTestFiles().cleanupRecursively()
        val dst = createTempDirectory().cleanupRecursively().resolve("dst")
        val nested = createTestFiles().cleanupRecursively()

        src.copyToRecursively(dst, followLinks = false) { source, target ->
            if (source.name == "2") {
                nested.copyToRecursively(target, followLinks = false)
            } else {
                source.copyTo(target, followLinks = false, ignoreExistingDirectory = true)
            }
            CopyActionResult.CONTINUE
        }

        val expected = listOf("") + referenceFilenames + referenceFilenames.map { "1/2/$it" }
        testVisitedFiles(expected, dst.walkIncludeDirectories(), dst)
    }

    @Test
    fun copyWithSkipSubtree() {
        val src = createTestFiles().cleanupRecursively()
        val dst = createTempDirectory().cleanupRecursively().resolve("dst")

        src.copyToRecursively(dst, followLinks = false) { source, target ->
            source.copyTo(target, followLinks = false, ignoreExistingDirectory = true)
            if (source.name == "3" || source.name == "9.txt") {
                CopyActionResult.SKIP_SUBTREE
            } else {
                CopyActionResult.CONTINUE
            }
        }

        // both "3" and "9.txt" are copied
        val copied3 = dst.resolve("1/3").exists()
        val copied9 = dst.resolve("8/9.txt").exists()
        assertTrue(copied3 && copied9)

        // content of "3" is not copied
        assertTrue(dst.resolve("1/3").listDirectoryEntries().isEmpty())
    }

    @Test
    fun copyWithTerminate() {
        val src = createTestFiles().cleanupRecursively()
        val dst = createTempDirectory().cleanupRecursively().resolve("dst")

        src.copyToRecursively(dst, followLinks = false) { source, target ->
            source.copyTo(target, followLinks = false, ignoreExistingDirectory = true)
            if (source.name == "3" || source.name == "9.txt") {
                CopyActionResult.TERMINATE
            } else {
                CopyActionResult.CONTINUE
            }
        }

        // either "3" or "9.txt" is not copied
        val copied3 = dst.resolve("1/3").exists()
        val copied9 = dst.resolve("8/9.txt").exists()
        assertTrue(copied3 || copied9)
        assertFalse(copied3 && copied9)
    }

    @Test
    fun copyFailureWithTerminate() {
        val src = createTestFiles().cleanupRecursively()
        val dst = createTempDirectory().cleanupRecursively().resolve("dst")

        src.copyToRecursively(dst, followLinks = false, onError = { path, exception ->
            assertIs<IllegalArgumentException>(exception)
            assertTrue(path.name == "3" || path.name == "9.txt")
            OnErrorResult.TERMINATE
        }) { source, target ->
            source.copyTo(target, followLinks = false, ignoreExistingDirectory = true)
            if (source.name == "3" || source.name == "9.txt") throw IllegalArgumentException()
            CopyActionResult.CONTINUE
        }

        // either "3" or "9.txt" is not copied
        val copied3 = dst.resolve("1/3").exists()
        val copied9 = dst.resolve("8/9.txt").exists()
        assertTrue(copied3 || copied9)
        assertFalse(copied3 && copied9)
    }

    @Test
    fun copyIntoSourceDirectory() {
        val source = createTestFiles().cleanupRecursively()
        val linkToSource = createTempDirectory().cleanupRecursively().resolve("link").tryCreateSymbolicLinkTo(source) ?: return
        val sources = listOf(
            source to source,
            linkToSource.resolve("8") to source.resolve("8"),
            linkToSource.resolve("1/3") to source.resolve("1/3")
        )

        for ((src, resolvedSrc) in sources) {
            val linkToSrc = createTempDirectory().cleanupRecursively().resolve("linkToSrc").tryCreateSymbolicLinkTo(resolvedSrc) ?: return
            val targets = listOf(
                linkToSrc,
                linkToSrc.resolve("a").createDirectory(),
                linkToSrc.resolve("a/b").createDirectories()
            )

            for (followLinks in listOf(false, true)) {
                for (dst in targets) {
                    val error = assertFailsWith<java.nio.file.FileSystemException> {
                        src.copyToRecursively(dst, followLinks = followLinks)
                    }
                    assertEquals("Recursively copying a directory into its subdirectory is prohibited.", error.reason)
                }
            }
        }
    }

    @Test
    fun canDeleteCurrentlyOpenDirectory() {
        val basedir = createTempDirectory().cleanupRecursively()
        val relativePath = basedir.relativeTo(basedir)
        Files.newDirectoryStream(basedir).use { directoryStream ->
            if (directoryStream is SecureDirectoryStream) {
                println("Secure, relativePath: $relativePath")
                directoryStream.deleteDirectory(basedir)
            } else {
                println("Insecure, relativePath: $relativePath")
                basedir.deleteIfExists()
            }
        }
        println("Was deleted: ${basedir.notExists()}")
    }

    @Test
    fun canSecureDirectoryStreamDeleteFileOutsideOfDirectory() {
        val basedir = createTempDirectory().cleanupRecursively()
        val fileToDelete = createTempFile().cleanup()
        val dirToDelete = createTempDirectory().cleanupRecursively()

        val directoryStream = Files.newDirectoryStream(basedir)
        if (directoryStream is SecureDirectoryStream) {
            println("Secure")
            directoryStream.deleteFile(fileToDelete)
            println("Could delete file: " + fileToDelete.notExists())
            directoryStream.deleteDirectory(dirToDelete)
            println("Could delete directory: " + dirToDelete.notExists())
        } else {
            println("Insecure")
        }
    }

    @Test
    fun canSecureDirectoryStreamDeleteFileDeepInsideDirectory() {
        val basedir = createTestFiles().cleanupRecursively()
        val fileToDelete = basedir.resolve("1/3/5.txt")
        val dirToDelete = basedir.resolve("1/2")

        val directoryStream = Files.newDirectoryStream(basedir)
        if (directoryStream is SecureDirectoryStream) {
            println("Secure")
            directoryStream.deleteFile(fileToDelete)
            println("Could delete file: " + fileToDelete.notExists())
            directoryStream.deleteDirectory(dirToDelete)
            println("Could delete directory: " + dirToDelete.notExists())
        } else {
            println("Insecure")
        }
    }

    @Test
    fun isDirectoryEntryInRelativePath() {
        val basedir = createTestFiles().cleanupRecursively()
        Files.newDirectoryStream(basedir).use { directoryStream ->
            if (directoryStream is SecureDirectoryStream) {
                println("Secure")
            } else {
                println("Not secure")
            }
            if (directoryStream is SecureDirectoryStream) {
                for (path in directoryStream) {
                    println(path)
                    val name = path.fileName
                    val attributes = directoryStream.getFileAttributeView(name, BasicFileAttributeView::class.java).readAttributes()
                    if (attributes.isDirectory) {
                        directoryStream.newDirectoryStream(name).forEach {
                            println("    $it")
                        }
                    }
                }
            } else {
                directoryStream.forEach {
                    println(it)
                }
            }
        }
    }
}
