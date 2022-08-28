/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JvmMultifileClass
@file:JvmName("PathsKt")

package kotlin.io.path

import java.io.IOException
import java.nio.file.*
import java.nio.file.FileSystemException
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.BasicFileAttributes

/**
 * Copies this file with all its children to the specified destination [target] path.
 * Note that if this function throws, partial copying may have taken place.
 *
 * Unlike `File.copyRecursively`, if some directories on the way to the [target] are missing, then they won't be created automatically.
 * You can use the following approach to ensure that required intermediate directories are created:
 * ```
 * sourcePath.copyToRecursively(destinationPath.apply { parent?.createDirectories() }, followLinks = false)
 * ```
 *
 * If the file located by this path is a directory, this function recursively copies the directory itself and its content.
 * Otherwise, this function copies only the file.
 *
 * If an exception occurs attempting to read, open or copy any file under the given file tree,
 * further actions will depend on the result of the [onError] invoked with
 * the source and destination files, that caused the error, and the exception itself as arguments.
 * By default [onError] rethrows the exception, leading to immediate termination of the recursive copy function.
 * See [OnErrorResult] for available options.
 *
 * This function performs "directory merge" operation. If a file in the source subtree is a directory
 * and the corresponding file in the target subtree already exists and is also a directory, it does nothing.
 * Otherwise, [overwrite] determines whether to overwrite existing destination files and directories.
 * Attributes of a source file, such as creation/modification date, are not preserved.
 * [followLinks] determines whether copy a symbolic link itself or its target.
 * Symbolic links in the target subtree are not followed.
 *
 * To provide a custom logic for copying use the overload that takes a `copyAction` lambda.
 *
 * @param target the destination path to copy recursively this file to.
 * @param onError the function that decides further actions if an error occurs. By default, rethrows the exception.
 * @param followLinks `false` to copy a symbolic link itself, not its target.
 *   `true` to copy the target of a symbolic link and to recursively copy the content of the directory a symbolic link points to.
 * @param overwrite `false` to throw if the destination file already exists.
 *   `true` to overwrite existing destination files and directories.
 * @throws NoSuchFileException if the file located by this path does not exist.
 * @throws Exception if [onError] rethrows.
 */
@ExperimentalPathApi
@SinceKotlin("1.8")
public fun Path.copyToRecursively(
    target: Path,
    onError: (source: Path, target: Path, exception: Exception) -> OnErrorResult = { _, _, exception -> throw exception },
    followLinks: Boolean,
    overwrite: Boolean
): Unit {
    if (overwrite) {
        copyToRecursively(target, onError, followLinks) { src, dst ->
            val options = LinkFollowing.toLinkOptions(followLinks)
            val dstIsDirectory = dst.isDirectory(LinkOption.NOFOLLOW_LINKS)
            val srcIsDirectory = src.isDirectory(*options)
            if ((srcIsDirectory && dstIsDirectory).not()) {
                if (dstIsDirectory)
                    dst.deleteRecursively()

                src.copyTo(dst, *options, StandardCopyOption.REPLACE_EXISTING)
            }

            // else: do nothing, the destination directory already exists
            CopyActionResult.CONTINUE
        }
    } else {
        copyToRecursively(target, onError, followLinks)
    }
}

/**
 * Copies this file with all its children to the specified destination [target] path.
 * Note that if this function throws, partial copying may have taken place.
 *
 * Unlike `File.copyRecursively`, if some directories on the way to the [target] are missing, then they won't be created automatically.
 * You can use the following approach to ensure that required intermediate directories are created:
 * ```
 * sourcePath.copyToRecursively(destinationPath.apply { parent?.createDirectories() }, followLinks = false)
 * ```
 *
 * If the file located by this path is a directory, this function recursively copies the directory itself and its content.
 * Otherwise, this function copies only the file.
 *
 * If an exception occurs attempting to read, open or copy any file under the given file tree,
 * further actions will depend on the result of the [onError] invoked with
 * the source and destination files, that caused the error, and the exception itself as arguments.
 * By default [onError] rethrows the exception, leading to immediate termination of the recursive copy function.
 * See [OnErrorResult] for available options.
 *
 * Copy operation is performed using [copyAction].
 * By default [copyAction] performs "directory merge" operation. If a file in the source subtree is a directory
 * and the corresponding file in the target subtree already exists and is also a directory, it does nothing.
 * Otherwise, the file is copied using `sourcePath.copyTo(destinationPath, *followLinksOption)`,
 * which doesn't preserve attributes of the source file and throws if the destination file already exists.
 * [followLinks] determines whether copy a symbolic link itself or its target.
 * Symbolic links in the target subtree are not followed by default.
 *
 * If a custom implementation of [copyAction] is provided, consider making it consistent with [followLinks] value.
 * See [CopyActionResult] for available options.
 *
 * @param target the destination path to copy recursively this file to.
 * @param onError the function that decides further actions if an error occurs. By default, rethrows the exception.
 * @param followLinks `false` to copy a symbolic link itself, not its target.
 *   `true` to copy the target of a symbolic link and to recursively copy the content of the directory a symbolic link points to.
 * @param copyAction the function to call for copying source files/directories to their destination path rooted in [target].
 *   By default, performs "directory merge" operation.
 * @throws NoSuchFileException if the file located by this path does not exist.
 * @throws Exception if [onError] rethrows.
 */
@ExperimentalPathApi
@SinceKotlin("1.8")
public fun Path.copyToRecursively(
    target: Path,
    onError: (source: Path, target: Path, exception: Exception) -> OnErrorResult = { _, _, exception -> throw exception },
    followLinks: Boolean,
    copyAction: CopyActionContext.(source: Path, target: Path) -> CopyActionResult = { src, dst ->
        src.copyTo(dst, followLinks, ignoreExistingDirectory = true)
    }
): Unit {
    if (!this.exists(*LinkFollowing.toLinkOptions(followLinks)))
        throw NoSuchFileException(this.toString(), target.toString(), "The source file doesn't exist.")

    if (target.exists()) {
        if (!target.isSymbolicLink() && this.isSameFileAs(target))
            return
        if (target.toRealPath().startsWith(this.toRealPath()))
            throw FileSystemException(this.toString(), target.toString(), "Recursively copying a directory into its subdirectory is prohibited.")
    }

    fun destination(source: Path): Path {
        val relativePath = source.relativeTo(this@copyToRecursively)
        return target.resolve(relativePath)
    }

    fun error(source: Path, exception: Exception): FileVisitResult {
        return onError(source, destination(source), exception).toFileVisitResult()
    }

    @Suppress("UNUSED_PARAMETER")
    fun copy(source: Path, attributes: BasicFileAttributes): FileVisitResult {
        return try {
            DefaultCopyActionContext.copyAction(source, destination(source)).toFileVisitResult()
        } catch (exception: Exception) {
            error(source, exception)
        }
    }

    visitFileTree(followLinks = followLinks) {
        onPreVisitDirectory(::copy)
        onVisitFile(::copy)
        onVisitFileFailed(::error)
        onPostVisitDirectory { directory, exception ->
            if (exception == null) {
                FileVisitResult.CONTINUE
            } else {
                error(directory, exception)
            }
        }
    }
}


/**
 * Enum that specifies further actions when copying a file in the [Path.copyToRecursively] function.
 */
@ExperimentalPathApi
@SinceKotlin("1.8")
public enum class CopyActionResult {
    /**
     * Continue with the next file in the traversal order.
     */
    CONTINUE,

    /**
     * Skip the directory content, continue with the next file outside the directory in the traversal order.
     * For regular files this option is equivalent to [CONTINUE].
     */
    SKIP_SUBTREE,

    /**
     * Stop the recursive copy function. The function will return without throwing exception.
     */
    TERMINATE
}

/**
 * Enum that specifies further actions when an exception occurs in the [Path.copyToRecursively] function.
 */
@ExperimentalPathApi
@SinceKotlin("1.8")
public enum class OnErrorResult {
    /**
     * If the file that caused the error if a directory, skip the directory and its content, and
     * continue with the next file outside this directory in the traversal order.
     * Otherwise, skip this file and continue with the next file in the traversal order.
     */
    SKIP_SUBTREE,

    /**
     * Stop the recursive copy function. The function will return without throwing exception.
     * To terminate the function with an exception rethrow instead.
     */
    TERMINATE
}

@ExperimentalPathApi
@SinceKotlin("1.8")
public interface CopyActionContext {
    // TODO: "ignoreExistingDirectory" -> "skipExistingDirectory" ?
    //       "copyTo" -> "copyToOrIgnoreExistingDirectory" ?
    public fun Path.copyTo(target: Path, followLinks: Boolean, ignoreExistingDirectory: Boolean): CopyActionResult
}

@ExperimentalPathApi
private object DefaultCopyActionContext : CopyActionContext {
    override fun Path.copyTo(target: Path, followLinks: Boolean, ignoreExistingDirectory: Boolean): CopyActionResult {
        val options = LinkFollowing.toLinkOptions(followLinks)
        if (ignoreExistingDirectory && this.isDirectory(*options) && target.isDirectory(LinkOption.NOFOLLOW_LINKS)) {
            // do nothing, the destination directory already exists
        } else {
            this.copyTo(target, *options)
        }

        return CopyActionResult.CONTINUE
    }
}

@ExperimentalPathApi
private fun CopyActionResult.toFileVisitResult() = when (this) {
    CopyActionResult.CONTINUE -> FileVisitResult.CONTINUE
    CopyActionResult.TERMINATE -> FileVisitResult.TERMINATE
    CopyActionResult.SKIP_SUBTREE -> FileVisitResult.SKIP_SUBTREE
}

@ExperimentalPathApi
private fun OnErrorResult.toFileVisitResult() = when (this) {
    OnErrorResult.TERMINATE -> FileVisitResult.TERMINATE
    OnErrorResult.SKIP_SUBTREE -> FileVisitResult.SKIP_SUBTREE
}

/**
 * Delete this file with all its children.
 * Note that if this function throws, partial deletion may have taken place.
 *
 * If the file located by this path is a directory, this function recursively deletes its content and the directory itself.
 * Otherwise, this function deletes only the file.
 * Symbolic links are not followed to their targets.
 * This function does nothing if the file located by this path does not exist.
 *
 * If the underlying platform supports [SecureDirectoryStream],
 * traversal of the file tree and removal of entries are performed using it.
 * Otherwise, directories in the file tree are opened with the less secure [Files.newDirectoryStream].
 * Note that on a platform that supports symbolic links and does not support [SecureDirectoryStream],
 * it is possible for a recursive delete to delete files and directories that are outside the directory being deleted.
 * This can happen if, after checking that a file is a directory (and not a symbolic link), that directory is replaced
 * by a symbolic link to an outside directory before the call that opens the directory to read its entries.
 *
 * If an exception occurs attempting to read, open or delete any file under the given file tree,
 * this method skips that file and continues. Such exceptions are collected and, after attempting to delete all files,
 * an [IOException] is thrown containing those exceptions as suppressed exceptions.
 * Maximum of `64` exceptions are collected. After reaching that amount, thrown exceptions are ignored and not collected.
 *
 * @throws IOException if any file in the tree can't be deleted for any reason.
 */
@ExperimentalPathApi
@SinceKotlin("1.8")
public fun Path.deleteRecursively(): Unit {
    val suppressedExceptions = this.deleteRecursivelyImpl()

    if (suppressedExceptions.isNotEmpty()) {
        throw FileSystemException("Failed to delete one or more files. See suppressed exceptions for details.").apply {
            suppressedExceptions.forEach { addSuppressed(it) }
        }
    }
}

private class ExceptionsCollector(private val limit: Int = 64) {
    var totalExceptions: Int = 0
        private set

    val collectedExceptions = mutableListOf<Exception>()

    fun collect(exception: Exception) {
        totalExceptions += 1
        val shouldCollect = collectedExceptions.size < limit
        if (shouldCollect) {
            collectedExceptions.add(exception)
        }
    }
}

private fun Path.deleteRecursivelyImpl(): List<Exception> {
    // TODO: https://github.com/google/guava/blob/78d67c94dbe95b918c839b3a0b50d44508c2838b/guava/src/com/google/common/io/MoreFiles.java#L722
    //       Does it make sense in our case?

    val collector = ExceptionsCollector()
    var useInsecure = true

    this.parent?.let { parent ->
        val directoryStream = try { Files.newDirectoryStream(parent) } catch (exception: Throwable) { null }
        directoryStream?.use { stream ->
            if (stream is SecureDirectoryStream<Path>) {
                useInsecure = false
                stream.handleEntry(this.fileName, collector)
            }
        }
    }

    if (useInsecure) {
        insecureHandleEntry(this, collector)
    }

    return collector.collectedExceptions
}

private inline fun collectIfThrows(collector: ExceptionsCollector, function: () -> Unit) {
    try {
        function()
    } catch (exception: Exception) {
        collector.collect(exception)
    }
}

private inline fun <R> tryIgnoreNoSuchFileException(function: () -> R): R? {
    return try { function() } catch(_: NoSuchFileException) { null }
}

// secure walk

private fun SecureDirectoryStream<Path>.handleEntry(name: Path, collector: ExceptionsCollector) {
    collectIfThrows(collector) {
        if (this.isDirectory(name, LinkOption.NOFOLLOW_LINKS)) {
            val preEnterTotalExceptions = collector.totalExceptions

            this.enterDirectory(name, collector)

            // If something went wrong trying to delete the contents of the
            // directory, don't try to delete the directory as it will probably fail.
            if (preEnterTotalExceptions == collector.totalExceptions) {
                tryIgnoreNoSuchFileException { this.deleteDirectory(name) }
            }
        } else {
            tryIgnoreNoSuchFileException { this.deleteFile(name) } // deletes symlink itself, not its target
        }
    }
}

private fun SecureDirectoryStream<Path>.enterDirectory(name: Path, collector: ExceptionsCollector) {
    collectIfThrows(collector) {
        tryIgnoreNoSuchFileException {
            this.newDirectoryStream(name, LinkOption.NOFOLLOW_LINKS)
        }?.use { directoryStream ->
            for (entry in directoryStream) {
                directoryStream.handleEntry(entry.fileName, collector)
            }
        }
    }
}

private fun SecureDirectoryStream<Path>.isDirectory(entryName: Path, vararg options: LinkOption): Boolean {
    return tryIgnoreNoSuchFileException {
        this.getFileAttributeView(entryName, BasicFileAttributeView::class.java, *options).readAttributes().isDirectory
    } ?: false
}

// insecure walk

private fun insecureHandleEntry(entry: Path, collector: ExceptionsCollector) {
    collectIfThrows(collector) {
        if (entry.isDirectory(LinkOption.NOFOLLOW_LINKS)) {
            val preEnterTotalExceptions = collector.totalExceptions

            insecureEnterDirectory(entry, collector)

            // If something went wrong trying to delete the contents of the
            // directory, don't try to delete the directory as it will probably fail.
            if (preEnterTotalExceptions == collector.totalExceptions) {
                entry.deleteIfExists()
            }
        } else {
            entry.deleteIfExists() // deletes symlink itself, not its target
        }
    }
}

private fun insecureEnterDirectory(path: Path, collector: ExceptionsCollector) {
    collectIfThrows(collector) {
        tryIgnoreNoSuchFileException {
            Files.newDirectoryStream(path)
        }?.use { directoryStream ->
            for (entry in directoryStream) {
                insecureHandleEntry(entry, collector)
            }
        }
    }
}
