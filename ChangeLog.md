## 1.7.0

### Analysis API. FIR

- [`KT-50864`](https://youtrack.jetbrains.com/issue/KT-50864) Analysis API: ISE: "KtCallElement should always resolve to a KtCallInfo" is thrown on call resolution inside plusAssign target
- [`KT-50252`](https://youtrack.jetbrains.com/issue/KT-50252) Analysis API: Implement FirModuleResolveStates for libraries
- [`KT-50862`](https://youtrack.jetbrains.com/issue/KT-50862) Analsysis API: do not create use site subsitution override symbols

### Analysis API. FIR Low Level API

- [`KT-50729`](https://youtrack.jetbrains.com/issue/KT-50729) Type bound is not fully resolved
- [`KT-50728`](https://youtrack.jetbrains.com/issue/KT-50728) Lazy resolve of extension function from 'kotlin' package breaks over unresolved type
- [`KT-50271`](https://youtrack.jetbrains.com/issue/KT-50271) Analysis API: get rid of using FirRefWithValidityCheck

### Backend. Native. Debug

- [`KT-50558`](https://youtrack.jetbrains.com/issue/KT-50558) K/N Debugger. Error is not displayed in variables view for catch block

### Compiler

#### New Features

- [`KT-26245`](https://youtrack.jetbrains.com/issue/KT-26245) Add ability to specify generic type parameters as not-null
- [`KT-45165`](https://youtrack.jetbrains.com/issue/KT-45165) Remove JVM target version 1.6
- [`KT-27435`](https://youtrack.jetbrains.com/issue/KT-27435) Allow implementation by delegation to inlined value of inline class
- [`KT-47939`](https://youtrack.jetbrains.com/issue/KT-47939) Support method references to functional interface constructors
- [`KT-50775`](https://youtrack.jetbrains.com/issue/KT-50775) Support IR partial linkage in Kotlin/Native (disabled by default)
- [`KT-51737`](https://youtrack.jetbrains.com/issue/KT-51737) Kotlin/Native: Remove unnecessary safepoints on watchosArm32 and iosArm32 targets
- [`KT-44249`](https://youtrack.jetbrains.com/issue/KT-44249) NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER with type usage in higher order function

#### Performance Improvements

- [`KT-48233`](https://youtrack.jetbrains.com/issue/KT-48233) Switching to JVM IR backend increases compilation time by more than 15%
- [`KT-51699`](https://youtrack.jetbrains.com/issue/KT-51699) Kotlin/Native: runtime has no LTO in debug binaries
- [`KT-34466`](https://youtrack.jetbrains.com/issue/KT-34466) Use optimized switch over enum only when all entries are constant enum entry expressions
- [`KT-50861`](https://youtrack.jetbrains.com/issue/KT-50861) FIR: Combination of array set convention and plusAssign works exponentially
- [`KT-47171`](https://youtrack.jetbrains.com/issue/KT-47171) For loop doesn't avoid boxing with value class iterators (JVM)
- [`KT-29199`](https://youtrack.jetbrains.com/issue/KT-29199) 'next' calls for iterators of merged primitive progressive values are not specialized
- [`KT-50585`](https://youtrack.jetbrains.com/issue/KT-50585) JVM IR: Array constructor loop should use IINC
- [`KT-22429`](https://youtrack.jetbrains.com/issue/KT-22429) Optimize 'for' loop code generation for reversed arrays
- [`KT-50074`](https://youtrack.jetbrains.com/issue/KT-50074) Performance regression in String-based 'when' with single equality clause
- [`KT-22334`](https://youtrack.jetbrains.com/issue/KT-22334) Compiler backend could generate smaller code for loops using range such as integer..array.size -1
- [`KT-35272`](https://youtrack.jetbrains.com/issue/KT-35272) Unnecessary null check on unsafe cast after not-null assertion operator
- [`KT-27427`](https://youtrack.jetbrains.com/issue/KT-27427) Optimize nullable check introduced with 'as' cast

#### Fixes

- [`KT-46762`](https://youtrack.jetbrains.com/issue/KT-46762) Finalize support for jspecify
- [`KT-51499`](https://youtrack.jetbrains.com/issue/KT-51499) @file:OptIn doesn't cover override methods
- [`KT-52037`](https://youtrack.jetbrains.com/issue/KT-52037) FIR: add error in 1.7.0 branch if run with non-compatible plugins
- [`KT-46756`](https://youtrack.jetbrains.com/issue/KT-46756) Release the K2/JVM compiler in Alpha
- [`KT-49715`](https://youtrack.jetbrains.com/issue/KT-49715) IR: "IllegalStateException: Function has no body: FUN name:toString" during IR lowering with shadowed extension inside interface
- [`KT-45508`](https://youtrack.jetbrains.com/issue/KT-45508) False negative ABSTRACT_CLASS_MEMBER_NOT_IMPLEMENTED on a fake override with an abstract super class member
- [`KT-28078`](https://youtrack.jetbrains.com/issue/KT-28078) Report error "Public property exposes its private type" for primary constructor properties instead of warning
- [`KT-49017`](https://youtrack.jetbrains.com/issue/KT-49017) Forbid usages of super or super<Some> if in fact it accesses an abstract member
- [`KT-38078`](https://youtrack.jetbrains.com/issue/KT-38078) Prohibit calling methods from Any with "super" qualifier once they are overridden as abstract in superclass
- [`KT-52363`](https://youtrack.jetbrains.com/issue/KT-52363) Evaluate impact of qualified `this` behavior change warnings
- [`KT-52561`](https://youtrack.jetbrains.com/issue/KT-52561) JVM: Coroutine state machine loses value after a check-induced smart cast
- [`KT-52311`](https://youtrack.jetbrains.com/issue/KT-52311) java.lang.VerifyError: Bad type on operand stack
- [`KT-41124`](https://youtrack.jetbrains.com/issue/KT-41124) Inconsistency of exceptions at init block for an enum entry with and without a qualifier name
- [`KT-46860`](https://youtrack.jetbrains.com/issue/KT-46860) Make safe calls always nullable
- [`KT-52503`](https://youtrack.jetbrains.com/issue/KT-52503) New green code appeared at the callable reference resolution
- [`KT-51925`](https://youtrack.jetbrains.com/issue/KT-51925) Native: "IllegalStateException: Symbol for kotlinx.cinterop/CStructVar|null[0] is unbound" caused by inline function
- [`KT-49317`](https://youtrack.jetbrains.com/issue/KT-49317) "IllegalStateException: Parent of this declaration is not a class: FUN LOCAL_FUNCTION_FOR_LAMBDA" with parameter of suspend type with the default parameter
- [`KT-51844`](https://youtrack.jetbrains.com/issue/KT-51844) New errors in overload resolution involving vararg extension methods
- [`KT-52006`](https://youtrack.jetbrains.com/issue/KT-52006) "java.lang.Throwable: Unbalanced tree Exception" on indexing kotlin project
- [`KT-51223`](https://youtrack.jetbrains.com/issue/KT-51223) Report warning about conflicting inherited members from deserialized dependencies
- [`KT-51439`](https://youtrack.jetbrains.com/issue/KT-51439) FE 1.0: implement type variance conflict deprecation on qualifier type arguments
- [`KT-51433`](https://youtrack.jetbrains.com/issue/KT-51433) FE 1.0: implement warnings about label resolve changes
- [`KT-51317`](https://youtrack.jetbrains.com/issue/KT-51317) Regression in resolution of lambdas where expected type has an extension receiver parameter
- [`KT-45935`](https://youtrack.jetbrains.com/issue/KT-45935) JVM IR: Add not-null assertion for explicit definitely not-null parameters
- [`KT-51818`](https://youtrack.jetbrains.com/issue/KT-51818) "ClassCastException: class CoroutineSingletons cannot be cast to class" with suspendCoroutineUninterceptedOrReturn and coroutines
- [`KT-51718`](https://youtrack.jetbrains.com/issue/KT-51718) JVM / IR: "VerifyError: Bad type on operand stack" caused by nullable variable inside suspend function
- [`KT-51927`](https://youtrack.jetbrains.com/issue/KT-51927) Native: `The symbol of unexpected type encountered during IR deserialization` error when multiple libraries have non-conflicting declarations with the same name
- [`KT-52394`](https://youtrack.jetbrains.com/issue/KT-52394) JVM: Missing annotation on method with value class return type when a subclass is present in the same file in Kotlin 1.7.0-Beta
- [`KT-51640`](https://youtrack.jetbrains.com/issue/KT-51640) FIR: remove warning about "far from being production ready"
- [`KT-45553`](https://youtrack.jetbrains.com/issue/KT-45553) FIR: support hiding declaration from star import by as import
- [`KT-52404`](https://youtrack.jetbrains.com/issue/KT-52404) Prolong deprecation cycle for errors at contravariant usages of star projected argument from Java
- [`KT-50734`](https://youtrack.jetbrains.com/issue/KT-50734) TYPE_MISMATCH: NonNull parameter with a type of Nullable type argument causes compiler warning
- [`KT-51235`](https://youtrack.jetbrains.com/issue/KT-51235) JVM / IR: "AbstractMethodError: Receiver class does not define or inherit an implementation of the resolved method" when property with inline class type is overridden to return Nothing?
- [`KT-48935`](https://youtrack.jetbrains.com/issue/KT-48935) NI: Multiple generic parameter type constraints are not applied as expected when the parameter is of function type
- [`KT-49661`](https://youtrack.jetbrains.com/issue/KT-49661) NI: No TYPE_INFERENCE_UPPER_BOUND_VIOLATED when argument is inferred by return type
- [`KT-50877`](https://youtrack.jetbrains.com/issue/KT-50877) Inconsistent flexible type
- [`KT-51988`](https://youtrack.jetbrains.com/issue/KT-51988) "NPE: getContainingDeclaration…lDeclarationType.REGULAR) must not be null" when using @BuilderInference with multiple type arguments
- [`KT-48890`](https://youtrack.jetbrains.com/issue/KT-48890) Revert Opt-In restriction "Overriding methods can only have opt-in annotations that are present on their basic declarations."
- [`KT-52035`](https://youtrack.jetbrains.com/issue/KT-52035) FIR: add error in 1.7.0 branch if run on JS / Native configuration
- [`KT-45461`](https://youtrack.jetbrains.com/issue/KT-45461) NI: False negative TYPE_INFERENCE_UPPER_BOUND_VIOLATED when passing an argument to a function with generic constraints
- [`KT-52146`](https://youtrack.jetbrains.com/issue/KT-52146) JVM IR: "AssertionError: Primitive array expected" on vararg of SAM types with self-type and star projection
- [`KT-50730`](https://youtrack.jetbrains.com/issue/KT-50730) Implement error for a super class constructor call on a function interface in supertypes list
- [`KT-52040`](https://youtrack.jetbrains.com/issue/KT-52040) JVM: ClassFormatError Illegal method name "expectFailure$<init>__proxy-0"
- [`KT-50845`](https://youtrack.jetbrains.com/issue/KT-50845) Postpone rxjava errors reporting in the strict mode till 1.8 due to found broken cases
- [`KT-51979`](https://youtrack.jetbrains.com/issue/KT-51979) "AssertionError: No modifier list, but modifier has been found by the analyzer" exception on incorrect Java interface override
- [`KT-51759`](https://youtrack.jetbrains.com/issue/KT-51759) FIR DFA: false positive "Variable must be initialized"
- [`KT-50378`](https://youtrack.jetbrains.com/issue/KT-50378) Unresolved reference for method in Jsoup library in a kts script file
- [`KT-34919`](https://youtrack.jetbrains.com/issue/KT-34919) "Visibility is unknown yet" when named parameter in a function type used in a typealias implemented by an abstract class
- [`KT-51893`](https://youtrack.jetbrains.com/issue/KT-51893) Duplicated [OVERRIDE_DEPRECATION] on overridden properties
- [`KT-41034`](https://youtrack.jetbrains.com/issue/KT-41034) K2: Change evaluation semantics for combination of safe calls and convention operators
- [`KT-51843`](https://youtrack.jetbrains.com/issue/KT-51843) Functional interface constructor references are incorrectly allowed in 1.6.20 without any compiler flags
- [`KT-51914`](https://youtrack.jetbrains.com/issue/KT-51914) False positive RETURN_TYPE_MISMATCH in intellij ultimate
- [`KT-51711`](https://youtrack.jetbrains.com/issue/KT-51711) Compiler warning is displayed in case there is 'if' else branch used with elvis
- [`KT-33517`](https://youtrack.jetbrains.com/issue/KT-33517) Kotlin ScriptEngine does not respect async code when using bindings
- [`KT-51850`](https://youtrack.jetbrains.com/issue/KT-51850) FIR cannot resolve ambiguity with different SinceKotlin/DeprecatedSinceKotlin
- [`KT-44705`](https://youtrack.jetbrains.com/issue/KT-44705) Deprecate using non-exhaustive if's and when's in rhs of elvis
- [`KT-44510`](https://youtrack.jetbrains.com/issue/KT-44510) FIR DFA: smartcast after elvis with escaping lambda
- [`KT-44879`](https://youtrack.jetbrains.com/issue/KT-44879) FIR DFA: Track `inc` and `dec` operator calls in preliminary loop visitor
- [`KT-51758`](https://youtrack.jetbrains.com/issue/KT-51758) FIR: explicit API mode errors should not be reported for effectively internal / private entities
- [`KT-51203`](https://youtrack.jetbrains.com/issue/KT-51203) FIR: Inconsistent RETURN_TYPE_MISMATCH and TYPE_MISMATCH reporting on functions and properties
- [`KT-51624`](https://youtrack.jetbrains.com/issue/KT-51624) FIR: false-positive INAPPLICABLE_LATEINIT_MODIFIER for lateinit properties with unresolved types
- [`KT-51204`](https://youtrack.jetbrains.com/issue/KT-51204) FIR IC: Incremental compilation fails on nested crossinline
- [`KT-51798`](https://youtrack.jetbrains.com/issue/KT-51798) Fix ISE from IR backend when data class inherits equals/hashCode/toString with incompatible signature
- [`KT-46187`](https://youtrack.jetbrains.com/issue/KT-46187) FIR: OVERLOAD_RESOLUTION_AMBIGUITY on SAM-converted callable reference to List::plus
- [`KT-51761`](https://youtrack.jetbrains.com/issue/KT-51761) Incorrect NONE_APPLICABLE in expect class
- [`KT-51756`](https://youtrack.jetbrains.com/issue/KT-51756) FIR: false positive NO_VALUE_FOR_PARAMETER in expect class delegated constructor call
- [`KT-49778`](https://youtrack.jetbrains.com/issue/KT-49778) Support cast to DefinitelyNotNull type in Native
- [`KT-51441`](https://youtrack.jetbrains.com/issue/KT-51441) -Xpartial-linkage option specified in Gradle build script is not passed to Native linker
- [`KT-34515`](https://youtrack.jetbrains.com/issue/KT-34515) NI: "AssertionError: Base expression was not processed: POSTFIX_EXPRESSION" with double not-null assertion to brackets
- [`KT-48546`](https://youtrack.jetbrains.com/issue/KT-48546) PSI2IR: "org.jetbrains.kotlin.psi2ir.generators.ErrorExpressionException: null: KtCallExpression" with recursive property access in lazy block
- [`KT-28109`](https://youtrack.jetbrains.com/issue/KT-28109) "AssertionError: No setter call" for incrementing parenthesized result of indexed access convention operator
- [`KT-46136`](https://youtrack.jetbrains.com/issue/KT-46136) Unsubstituted return type inferred for a function returning anonymous object upcast to supertype
- [`KT-51364`](https://youtrack.jetbrains.com/issue/KT-51364) FIR: ambiguity due to String constructors clash
- [`KT-51621`](https://youtrack.jetbrains.com/issue/KT-51621) FIR: visible VS invisible qualifier conflict
- [`KT-50468`](https://youtrack.jetbrains.com/issue/KT-50468) FIR compilers fails with CCE when meets top-level destruction
- [`KT-51557`](https://youtrack.jetbrains.com/issue/KT-51557) Inline stack frame is not shown for default inline lambda
- [`KT-51358`](https://youtrack.jetbrains.com/issue/KT-51358) OptIn: show default warning/error message in case of empty message argument
- [`KT-44152`](https://youtrack.jetbrains.com/issue/KT-44152) FIR2IR fails on declarations from java stdlib if java classes are loaded from PSI instead of binaries
- [`KT-50949`](https://youtrack.jetbrains.com/issue/KT-50949) PSI2IR: NSEE from `ArgumentsGenerationUtilsKt.createFunctionForSuspendConversion` with providing lambda as argument with suspend type
- [`KT-39256`](https://youtrack.jetbrains.com/issue/KT-39256) ArrayStoreException with list of anonymous objects with inferred types created in reified extension function
- [`KT-39883`](https://youtrack.jetbrains.com/issue/KT-39883) Deprecate computing constant values of complex boolean expressions in when condition branches and conditions of loops
- [`KT-36952`](https://youtrack.jetbrains.com/issue/KT-36952) Exception during codegen: cannot pop operand off an empty stack (reference equality, implicit boxing, type check)
- [`KT-51233`](https://youtrack.jetbrains.com/issue/KT-51233) AssertionError in JavaLikeCounterLoopBuilder with Compose
- [`KT-51254`](https://youtrack.jetbrains.com/issue/KT-51254) Verify Error on passing null to type parameter extending inline class
- [`KT-50996`](https://youtrack.jetbrains.com/issue/KT-50996) [FIR] Support Int -> Long conversion for property initializers
- [`KT-51000`](https://youtrack.jetbrains.com/issue/KT-51000) [FIR] Support Int -> Long? conversion
- [`KT-51003`](https://youtrack.jetbrains.com/issue/KT-51003) [FIR] Consider Int -> Long conversion if expected type is type variable
- [`KT-51018`](https://youtrack.jetbrains.com/issue/KT-51018) [FIR] Wrong type inference if one of constraints is integer literal
- [`KT-51446`](https://youtrack.jetbrains.com/issue/KT-51446) Metadata serialization crashes with IOOBE when deserializing underlying inline class value with type table enabled
- [`KT-50973`](https://youtrack.jetbrains.com/issue/KT-50973) Redundant line number mapping for finally block with JVM IR
- [`KT-51272`](https://youtrack.jetbrains.com/issue/KT-51272) Incompatible types: KClass<Any> and callable reference Collection::class
- [`KT-51274`](https://youtrack.jetbrains.com/issue/KT-51274) "Expected some types" exception on when branch for when expression of erroneous type
- [`KT-51229`](https://youtrack.jetbrains.com/issue/KT-51229) FIR: private constructor of internal data class treated as internal and not private
- [`KT-50750`](https://youtrack.jetbrains.com/issue/KT-50750) [FIR] Report UNSUPPORTED on array literals not from annotation classes
- [`KT-51200`](https://youtrack.jetbrains.com/issue/KT-51200) False EXPOSED_PARAMETER_TYPE for internal type parameter of internal type
- [`KT-49804`](https://youtrack.jetbrains.com/issue/KT-49804) False positive of UPPER_BOUND_VIOLATED and RETURN_TYPE_MISMATCH
- [`KT-51121`](https://youtrack.jetbrains.com/issue/KT-51121) Inconsistent SAM behavior in multiple cases causing AbstractMethodError (Kotlin 1.6.10)
- [`KT-50136`](https://youtrack.jetbrains.com/issue/KT-50136) FIR: syntax error on (T & Any)
- [`KT-49465`](https://youtrack.jetbrains.com/issue/KT-49465) FIR2IR: support definitely not-null types
- [`KT-51357`](https://youtrack.jetbrains.com/issue/KT-51357) FIR: error in inference while using integer literal in expected Long position
- [`KT-49925`](https://youtrack.jetbrains.com/issue/KT-49925) [FIR] Incorrect builder inference (different cases)
- [`KT-50542`](https://youtrack.jetbrains.com/issue/KT-50542) "IllegalStateException: Type parameter descriptor is not initialized: T declared in sort" with definitely non-null type Any & T in generic constraint
- [`KT-51171`](https://youtrack.jetbrains.com/issue/KT-51171) FIR: class `Error` resolution problem
- [`KT-51156`](https://youtrack.jetbrains.com/issue/KT-51156) Multiplatform linkDebugFramework task throws NoSuchElementException when expect class constructors utilize nested enum constant
- [`KT-51017`](https://youtrack.jetbrains.com/issue/KT-51017) [FIR] Ambiguity on callable reference between two functions on generic receiver with different bounds
- [`KT-51007`](https://youtrack.jetbrains.com/issue/KT-51007) [FIR] False positive ILLEGAL_SUSPEND_FUNCTION_CALL if fun interface with suspend function declared in another module
- [`KT-50998`](https://youtrack.jetbrains.com/issue/KT-50998) [FIR] Int.inv() cal does not considered as compile time call
- [`KT-51009`](https://youtrack.jetbrains.com/issue/KT-51009) [FIR] Incorrect inference of lambda in position of return
- [`KT-50997`](https://youtrack.jetbrains.com/issue/KT-50997) [FIR] Incorrect type of typealias for suspend functional type
- [`KT-49714`](https://youtrack.jetbrains.com/issue/KT-49714) Compiler reports "'operator modifier is inapplicable" if expect class with increment operator is provided via type alias
- [`KT-48623`](https://youtrack.jetbrains.com/issue/KT-48623) Type nullability enhancement improvements
- [`KT-44623`](https://youtrack.jetbrains.com/issue/KT-44623) "IllegalStateException: IdSignature is allowed only for PublicApi symbols" when suspending receiver is annotated with something
- [`KT-46000`](https://youtrack.jetbrains.com/issue/KT-46000) JVM / IR: AssertionError on isSubtypeOfClass check in copyValueParametersToStatic with Compose
- [`KT-50211`](https://youtrack.jetbrains.com/issue/KT-50211) Annotation Instantiation with default arguments in Native
- [`KT-49412`](https://youtrack.jetbrains.com/issue/KT-49412) Controversial "type argument is not within its bounds" reported by FIR
- [`KT-48044`](https://youtrack.jetbrains.com/issue/KT-48044) [FIR] Investigate behavior of `UPPER_BOUND_VIOLATED` on complex cases
- [`KT-37975`](https://youtrack.jetbrains.com/issue/KT-37975) Don't show deprecation of enum class itself for its own member
- [`KT-50737`](https://youtrack.jetbrains.com/issue/KT-50737) Inheritance from SuspendFunction leads to compiler crash
- [`KT-50723`](https://youtrack.jetbrains.com/issue/KT-50723) Implement a fix of reporting of uninitialized parameter in default values of parameters
- [`KT-50749`](https://youtrack.jetbrains.com/issue/KT-50749) Implement UNSUPPORTED reporting on array literals inside objects in annotation classes
- [`KT-50753`](https://youtrack.jetbrains.com/issue/KT-50753) Implement reporting errors on cycles in annotation parameter types
- [`KT-50758`](https://youtrack.jetbrains.com/issue/KT-50758) Fix inconsistency of exceptions at init block for an enum entry with and without a qualifier name
- [`KT-50182`](https://youtrack.jetbrains.com/issue/KT-50182) CONST_VAL_NOT_TOP_LEVEL_OR_OBJECT: clarify error message for `const` in object expression
- [`KT-50183`](https://youtrack.jetbrains.com/issue/KT-50183) Fix missing apostrophe escapes in compiler error messages
- [`KT-50788`](https://youtrack.jetbrains.com/issue/KT-50788) FIR: false unsafe call on not-null generic
- [`KT-50785`](https://youtrack.jetbrains.com/issue/KT-50785) FIR: inconsistent smart cast after comparison with true
- [`KT-50858`](https://youtrack.jetbrains.com/issue/KT-50858) [FIR LL] FIR in low level mode creates multiple symbols for same declaration
- [`KT-50822`](https://youtrack.jetbrains.com/issue/KT-50822) Analysis API: make declaration transformers machinery to be a thread safe
- [`KT-50972`](https://youtrack.jetbrains.com/issue/KT-50972) FIR doesn't report VAL_REASSIGNMENT on synthetic properties
- [`KT-50969`](https://youtrack.jetbrains.com/issue/KT-50969) FIR: diamond inheritance with different parameter types depends on a supertype order
- [`KT-50875`](https://youtrack.jetbrains.com/issue/KT-50875) FIR: no smart cast after reassignment with elvis
- [`KT-50835`](https://youtrack.jetbrains.com/issue/KT-50835) Inline functions with suspend lambdas break the tail-call optimization
- [`KT-49485`](https://youtrack.jetbrains.com/issue/KT-49485) JVM / IR: StackOverflowError with long when-expression conditions
- [`KT-35684`](https://youtrack.jetbrains.com/issue/KT-35684) NI: "IllegalStateException: Expected some types" from builder-inference about intersecting empty types on trivial code
- [`KT-50776`](https://youtrack.jetbrains.com/issue/KT-50776) FIR: ambiguity between Sequence.forEach and Iterable.forEach
- [`KT-48908`](https://youtrack.jetbrains.com/issue/KT-48908) Error for annotation on parameter type could have distinct ID and message referring 1.6
- [`KT-48907`](https://youtrack.jetbrains.com/issue/KT-48907) SUPERTYPE_IS_SUSPEND_FUNCTION_TYPE error could have message referring version 1.6
- [`KT-50774`](https://youtrack.jetbrains.com/issue/KT-50774) FIR2IR: NSEE in case of lambda in enum entry constructor call
- [`KT-49016`](https://youtrack.jetbrains.com/issue/KT-49016) Drop QUALIFIED_SUPERTYPE_EXTENDED_BY_OTHER_SUPERTYPE diagnostic
- [`KT-34338`](https://youtrack.jetbrains.com/issue/KT-34338) Parameterless main causes duplicate JVM signature error
- [`KT-50577`](https://youtrack.jetbrains.com/issue/KT-50577) JVM_IR: No NPE when casting uninitialized value of non-null type to non-null type
- [`KT-50476`](https://youtrack.jetbrains.com/issue/KT-50476) JVM_IR: NSME when calling 'super.removeAt(Int)' implemented in Java interface as a default method
- [`KT-50257`](https://youtrack.jetbrains.com/issue/KT-50257) JVM_IR: Incorrect bridge delegate signature for renamed remove(I) causes SOE with Kotlin class inherited from fastutils IntArrayList
- [`KT-50470`](https://youtrack.jetbrains.com/issue/KT-50470) FIR: inapplicable candidate in delegate inference due to nullability
- [`KT-32744`](https://youtrack.jetbrains.com/issue/KT-32744) Inefficient compilation of null-safe call (extra null checks, unreachable code)

### Docs & Examples

- [`KT-52032`](https://youtrack.jetbrains.com/issue/KT-52032) Document performance optimizations of the Kotlin/JVM compiler in 1.7.0
- [`KT-49424`](https://youtrack.jetbrains.com/issue/KT-49424) Update KEEP for OptIn

### IDE

#### Fixes

- [`KTIJ-21735`](https://youtrack.jetbrains.com/issue/KTIJ-21735) Exception when opening a project
- [`KTIJ-17414`](https://youtrack.jetbrains.com/issue/KTIJ-17414) UAST: Synthetic enum methods have null return values
- [`KTIJ-17444`](https://youtrack.jetbrains.com/issue/KTIJ-17444) UAST: Synthetic enum methods are missing nullness annotations
- [`KTIJ-19043`](https://youtrack.jetbrains.com/issue/KTIJ-19043) UElement#comments is empty for a Kotlin property with a getter
- [`KTIJ-10031`](https://youtrack.jetbrains.com/issue/KTIJ-10031) IDE fails to suggest a project declaration import if the name clashes with internal declaration with implicit import from stdlib (ex. @Serializable)
- [`KTIJ-21515`](https://youtrack.jetbrains.com/issue/KTIJ-21515) Load JVM target 1.6 as 1.8 in Maven projects
- [`KTIJ-21151`](https://youtrack.jetbrains.com/issue/KTIJ-21151) Exception about wrong read access from "Java overriding methods searcher" with Kotlin overrides
- [`KTIJ-20736`](https://youtrack.jetbrains.com/issue/KTIJ-20736) NoClassDefFoundError: Could not initialize class org.jetbrains.kotlin.idea.roots.KotlinNonJvmOrderEnumerationHandler. Kotlin plugin 1.7 fails to start
- [`KT-50111`](https://youtrack.jetbrains.com/issue/KT-50111) Resolving into KtUltraLightMethod
- [`KTIJ-21063`](https://youtrack.jetbrains.com/issue/KTIJ-21063) IDE highlighting: False positive error "Context receivers should be enabled explicitly"
- [`KTIJ-20810`](https://youtrack.jetbrains.com/issue/KTIJ-20810) NoClassDefFoundError: org/jetbrains/kotlin/idea/util/SafeAnalyzeKt errors in 1.7.0-master-212 kotlin plugin on project open
- [`KTIJ-19088`](https://youtrack.jetbrains.com/issue/KTIJ-19088) KotlinUFunctionCallExpression.resolve() returns null for calls to @JvmSynthetic functions
- [`KTIJ-17869`](https://youtrack.jetbrains.com/issue/KTIJ-17869) KotlinUFunctionCallExpression.resolve() returns null for instantiations of local classes with default constructors
- [`KTIJ-21061`](https://youtrack.jetbrains.com/issue/KTIJ-21061) UObjectLiteralExpression.getExpressionType() returns the base class type for Kotlin object literals instead of the anonymous class type
- [`KTIJ-20200`](https://youtrack.jetbrains.com/issue/KTIJ-20200) UAST: @Deprecated(level=HIDDEN) constructors are not returning UMethod.isConstructor=true
- [`KTIJ-19624`](https://youtrack.jetbrains.com/issue/KTIJ-19624) NoDescriptorForDeclarationException on iosTest.kt.vm

### IDE. Code Style, Formatting

- [`KTIJ-20554`](https://youtrack.jetbrains.com/issue/KTIJ-20554) Introduce some code style for definitely non-null types

### IDE. Completion

- [`KTIJ-14740`](https://youtrack.jetbrains.com/issue/KTIJ-14740) Multiplatform declaration actualised in an intermediate source set is shown twice in a completion popup called in the source set

### IDE. Debugger

- [`KTIJ-20815`](https://youtrack.jetbrains.com/issue/KTIJ-20815) MPP Debugger: Evaluation of expect function for the project with intermediate source set may fail with java.lang.NoSuchMethodError

### IDE. Decompiler, Indexing, Stubs

- [`KTIJ-21472`](https://youtrack.jetbrains.com/issue/KTIJ-21472) "java.lang.IllegalStateException: Could not read file" exception on indexing invalid class file
- [`KTIJ-20802`](https://youtrack.jetbrains.com/issue/KTIJ-20802) Definitely Not-Null types: "UpToDateStubIndexMismatch: PSI and index do not match" plugin error when trying to use library function with T&Any
- [`KT-51248`](https://youtrack.jetbrains.com/issue/KT-51248) Function and parameter names with special symbols have to backticked

### IDE. FIR

- [`KTIJ-20443`](https://youtrack.jetbrains.com/issue/KTIJ-20443) FIR IDE: Work in Dumb mode
- [`KTIJ-21374`](https://youtrack.jetbrains.com/issue/KTIJ-21374) FIR IDE: Incorrect highlighting for operators
- [`KTIJ-21013`](https://youtrack.jetbrains.com/issue/KTIJ-21013) FIR IDE: Inconsistent smartcasts highlighting
- [`KTIJ-21343`](https://youtrack.jetbrains.com/issue/KTIJ-21343) FIR IDE: Navigation from explicit invoke call does not work
- [`KTIJ-20852`](https://youtrack.jetbrains.com/issue/KTIJ-20852) FIR IDE: Exception when checking `isInheritor` on two classes in different modules
- [`KTIJ-21021`](https://youtrack.jetbrains.com/issue/KTIJ-21021) FIR IDE: Completion of extension function does not work on nullable receiver
- [`KTIJ-20637`](https://youtrack.jetbrains.com/issue/KTIJ-20637) FIR IDE: Strange exception while commenting-uncommenting FirReferenceResolveHelper.kt
- [`KTIJ-20971`](https://youtrack.jetbrains.com/issue/KTIJ-20971) FIR IDE: "Parameter Info" shows parameters of uncallable methods

### IDE. Gradle Integration

- [`KTIJ-21807`](https://youtrack.jetbrains.com/issue/KTIJ-21807) Gradle to IDEA import: language and API version settings are not imported for Native facet
- [`KTIJ-21692`](https://youtrack.jetbrains.com/issue/KTIJ-21692) Kotlin Import Test maintenance: 1.7.0-Beta
- [`KTIJ-20567`](https://youtrack.jetbrains.com/issue/KTIJ-20567) Kotlin/JS: Gradle import into IDEA creates no proper sub-modules, source sets, facets

### IDE. Hints. Inlay

- [`KTIJ-20552`](https://youtrack.jetbrains.com/issue/KTIJ-20552) Support definitely non-null types in inlay hints

### IDE. Inspections and Intentions

#### New Features

- [`KTIJ-18979`](https://youtrack.jetbrains.com/issue/KTIJ-18979) Quickfix for INTEGER_OPERATOR_RESOLVE_WILL_CHANGE to add explicit conversion call
- [`KTIJ-19950`](https://youtrack.jetbrains.com/issue/KTIJ-19950) Provide quickfixes for `INVALID_IF_AS_EXPRESSION_WARNING` and `NO_ELSE_IN_WHEN_WARNING`
- [`KTIJ-19866`](https://youtrack.jetbrains.com/issue/KTIJ-19866) Create quick-fix for effective visibility error on private-in-file interface exposing private class
- [`KTIJ-19939`](https://youtrack.jetbrains.com/issue/KTIJ-19939) Provide quickfix for deprecated confusing expressions in when branches

#### Fixes

- [`KTIJ-20705`](https://youtrack.jetbrains.com/issue/KTIJ-20705) Register quickfix for `NO_CONSTRUCTOR_WARNING` diagnostic
- [`KTIJ-21226`](https://youtrack.jetbrains.com/issue/KTIJ-21226) "Remove else branch" quick fix is not suggested
- [`KTIJ-20981`](https://youtrack.jetbrains.com/issue/KTIJ-20981) Definitely non-null types: quick-fixes suggested incorrectly for LV=1.6 when Xenhance-type-parameter-types-to-def-not-null flag is set
- [`KTIJ-20953`](https://youtrack.jetbrains.com/issue/KTIJ-20953) Add quickfix for OVERRIDE_DEPRECATION warning to 1.7 - 1.9 migration
- [`KTIJ-20734`](https://youtrack.jetbrains.com/issue/KTIJ-20734) Replace with [@JvmInline] value quick fix should be appliable on a whole project
- [`KTIJ-21420`](https://youtrack.jetbrains.com/issue/KTIJ-21420) Add 'else' branch quick fix suggestion is displayed twice in case 'if' isn't completed
- [`KTIJ-21192`](https://youtrack.jetbrains.com/issue/KTIJ-21192) "Make protected" intention is redundant for interface properties
- [`KTIJ-18120`](https://youtrack.jetbrains.com/issue/KTIJ-18120) "Make public" intention does not add explicit "public" modifier when using ExplicitApi Strict mode
- [`KTIJ-20493`](https://youtrack.jetbrains.com/issue/KTIJ-20493) "Create expect" quick fix doesn't warn about platform-specific annotations

### IDE. Misc

- [`KTIJ-21582`](https://youtrack.jetbrains.com/issue/KTIJ-21582) Notification for Kotlin EAP survey

### IDE. Multiplatform

- [`KT-49523`](https://youtrack.jetbrains.com/issue/KT-49523) Improve environment setup experience for KMM projects
- [`KT-50952`](https://youtrack.jetbrains.com/issue/KT-50952) MPP: Commonized cinterops doesn't attach/detach to source set on configuration changes

### IDE. Native

- [`KT-44329`](https://youtrack.jetbrains.com/issue/KT-44329) Improve UX of using Native libraries in Kotlin
- [`KTIJ-21602`](https://youtrack.jetbrains.com/issue/KTIJ-21602) With Native Debugging Support plugin Gradle run configurations can't be executed from IDEA: LLDB_NATVIS_RENDERERS_ENABLED

### IDE. Wizards

- [`KTIJ-20919`](https://youtrack.jetbrains.com/issue/KTIJ-20919) Update ktor-html-builder dependency in kotlin wizards
- [`KTIJ-20962`](https://youtrack.jetbrains.com/issue/KTIJ-20962) Wizard: Invalid Ktor imports

### JavaScript

#### New Features

- [`KT-51735`](https://youtrack.jetbrains.com/issue/KT-51735) KJS / IR: Minimize member names in production mode

#### Performance Improvements

- [`KT-51127`](https://youtrack.jetbrains.com/issue/KT-51127) Kotlin/JS - IR generates plenty of useless `Unit_getInstance()`
- [`KT-50212`](https://youtrack.jetbrains.com/issue/KT-50212) KJS IR: Upcast should be a no-op
- [`KT-16974`](https://youtrack.jetbrains.com/issue/KT-16974) JS: Kotlin.charArrayOf is suboptimal due to Rhino bugs

#### Fixes

- [`KT-44319`](https://youtrack.jetbrains.com/issue/KT-44319) JS IR BE: Add an ability to generate separate JS files for each module
- [`KT-52518`](https://youtrack.jetbrains.com/issue/KT-52518) Kotlin/JS IR: project with 1.6.21 fails to consume library built with 1.7.0-RC: ISE "Unexpected IrType kind: KIND_NOT_SET" at IrDeclarationDeserializer.deserializeIrTypeData()
- [`KT-52010`](https://youtrack.jetbrains.com/issue/KT-52010) K/JS IR: both flows execute when using elvis operator
- [`KT-41096`](https://youtrack.jetbrains.com/issue/KT-41096) KJS IR: @JsExport should use original js name for external declarations
- [`KT-52144`](https://youtrack.jetbrains.com/issue/KT-52144) KJS / IR: Missing property definitions for interfaced defined properties
- [`KT-52252`](https://youtrack.jetbrains.com/issue/KT-52252) KJS / IR: overridden properties are undefined/null
- [`KT-51973`](https://youtrack.jetbrains.com/issue/KT-51973) KJS / IR overridden properties of inherited interface missing
- [`KT-51125`](https://youtrack.jetbrains.com/issue/KT-51125) Provide a way to use `import` keyword in `js` expressions
- [`KT-40888`](https://youtrack.jetbrains.com/issue/KT-40888) KJS / IR: Missing methods are no longer generated (polyfills)
- [`KT-50504`](https://youtrack.jetbrains.com/issue/KT-50504) KJS / IR: Transpiled JS incorrectly uses the unscrambled names of internal fields
- [`KT-51853`](https://youtrack.jetbrains.com/issue/KT-51853) JS compilation fails with "Uninitialized fast cache info" error
- [`KT-51205`](https://youtrack.jetbrains.com/issue/KT-51205) K/JS IR: external class is mapped to any
- [`KT-50806`](https://youtrack.jetbrains.com/issue/KT-50806) Typescript definitions contain invalid nested block comments with generic parent and type argument without `@JsExport`
- [`KT-51841`](https://youtrack.jetbrains.com/issue/KT-51841) KJS / IR: No flat hash for FUN FAKE_OVERRIDE with kotlin.incremental.js.ir=true
- [`KT-51081`](https://youtrack.jetbrains.com/issue/KT-51081) KJS / IR + IC: Passing an inline function with default params as a param to a higher-order function crashes the compiler
- [`KT-51084`](https://youtrack.jetbrains.com/issue/KT-51084) KJS / IR + IC: Cache invalidation doesn't check generic inline functions reified qualifier
- [`KT-51211`](https://youtrack.jetbrains.com/issue/KT-51211) K/JS IR: JsExport:  Can't export nested enum
- [`KT-51438`](https://youtrack.jetbrains.com/issue/KT-51438) KJS / IR: Duplicated import names for the same external names
- [`KT-51238`](https://youtrack.jetbrains.com/issue/KT-51238) Kotlin/JS: IR + IC: build fails after clean on `compileTestDevelopmentExecutableKotlinJs` task: "Failed to create MD5 hash for file '.../build/classes/kotlin/main' as it does not exist"
- [`KT-50674`](https://youtrack.jetbrains.com/issue/KT-50674) KJS / IR: JS code cannot modify local variable
- [`KT-50953`](https://youtrack.jetbrains.com/issue/KT-50953) KJS IR: Incorrect nested commenting in d.ts
- [`KT-15223`](https://youtrack.jetbrains.com/issue/KT-15223) JS: function that overrides external function with `vararg` parameter is translated incorrectly
- [`KT-50657`](https://youtrack.jetbrains.com/issue/KT-50657) KJS / IR 1.6.20-M1-39 - Date in Kotlin JS cannot be created from long.

### Language Design

#### New Features

- [`KT-45618`](https://youtrack.jetbrains.com/issue/KT-45618) Stabilize builder inference
- [`KT-30485`](https://youtrack.jetbrains.com/issue/KT-30485) Underscore operator for type arguments
- [`KT-49006`](https://youtrack.jetbrains.com/issue/KT-49006) Support at least three previous versions of language/API
- [`KT-16768`](https://youtrack.jetbrains.com/issue/KT-16768) Context-sensitive resolution prototype (Resolve unqualified enum constants based on expected type)
- [`KT-14663`](https://youtrack.jetbrains.com/issue/KT-14663) Support having a "public" and a "private" type for the same property
- [`KT-50477`](https://youtrack.jetbrains.com/issue/KT-50477) Functional conversion does not work on suspending functions
- [`KT-32162`](https://youtrack.jetbrains.com/issue/KT-32162) Allow generics for inline classes

#### Fixes

- [`KT-12380`](https://youtrack.jetbrains.com/issue/KT-12380) Support sealed (exhaustive) whens
- [`KT-27750`](https://youtrack.jetbrains.com/issue/KT-27750) Reverse reservation of 'yield' as keyword
- [`KT-22956`](https://youtrack.jetbrains.com/issue/KT-22956) Release OptIn annotations
- [`KT-44866`](https://youtrack.jetbrains.com/issue/KT-44866) Change behavior of private constructors of sealed classes
- [`KT-49110`](https://youtrack.jetbrains.com/issue/KT-49110) Prohibit access to members of companion of enum class from initializers of entries of this enum
- [`KT-29405`](https://youtrack.jetbrains.com/issue/KT-29405) Switch default JVM target version to 1.8

### Libraries

#### New Features

- [`KT-50484`](https://youtrack.jetbrains.com/issue/KT-50484) Extensions for java.util.Optional in stdlib
- [`KT-50146`](https://youtrack.jetbrains.com/issue/KT-50146) Reintroduce min/max(By/With) operations on collections with non-nullable return type
- [`KT-46132`](https://youtrack.jetbrains.com/issue/KT-46132) Specialized default time source with non-allocating time marks
- [`KT-41890`](https://youtrack.jetbrains.com/issue/KT-41890) Support named capture groups in Regex on Native
- [`KT-48179`](https://youtrack.jetbrains.com/issue/KT-48179) Introduce API to retrieve the number of CPUs the runtime has

#### Performance Improvements

- [`KT-42178`](https://youtrack.jetbrains.com/issue/KT-42178) Range and Progression should override last()

#### Fixes

- [`KT-42436`](https://youtrack.jetbrains.com/issue/KT-42436) Support `java.nio.Path` extension in the standard library
- [`KT-51470`](https://youtrack.jetbrains.com/issue/KT-51470) Stabilize experimental API for 1.7
- [`KT-51775`](https://youtrack.jetbrains.com/issue/KT-51775) JS: Support named capture groups in Regex
- [`KT-51776`](https://youtrack.jetbrains.com/issue/KT-51776) Native: Support back references to groups with multi-digit index
- [`KT-51082`](https://youtrack.jetbrains.com/issue/KT-51082) Introduce Enum.declaringJavaClass property
- [`KT-51848`](https://youtrack.jetbrains.com/issue/KT-51848) Promote deepRecursiveFunction to stable API
- [`KT-48924`](https://youtrack.jetbrains.com/issue/KT-48924) KJS: `toString` in base 36 produces different results in JS compare to JVM
- [`KT-50742`](https://youtrack.jetbrains.com/issue/KT-50742) Regular expression is fine on jvm but throws PatternSyntaxException for native macosX64 target
- [`KT-50059`](https://youtrack.jetbrains.com/issue/KT-50059) Stop publishing kotlin-stdlib and kotlin-test artifacts under modular classifier
- [`KT-26678`](https://youtrack.jetbrains.com/issue/KT-26678) Rename buildSequence/buildIterator to sequence/iterator

### Native

- [`KT-49406`](https://youtrack.jetbrains.com/issue/KT-49406) Kotlin/Native: generate standalone executable for androidNative targets by default
- [`KT-48595`](https://youtrack.jetbrains.com/issue/KT-48595) Enable Native embeddable compiler jar in Gradle plugin by default
- [`KT-51377`](https://youtrack.jetbrains.com/issue/KT-51377) Native: synthetic forward declarations are preferred over commonized definitions
- [`KT-49145`](https://youtrack.jetbrains.com/issue/KT-49145) Kotlin/Native static library compilation fails for androidNative*
- [`KT-49496`](https://youtrack.jetbrains.com/issue/KT-49496) Gradle (or the KMM plugin) is caching the Xcode Command Line Tools location
- [`KT-49247`](https://youtrack.jetbrains.com/issue/KT-49247) gradle --offline should translate into airplaneMode for kotin-native compiler

### Native. Build Infrastructure

- [`KT-52259`](https://youtrack.jetbrains.com/issue/KT-52259) kotlin-native releases from GitHub don't contain platform libs

### Native. C and ObjC Import

- [`KT-49455`](https://youtrack.jetbrains.com/issue/KT-49455) Methods from Swift extensions are not resolved in Kotlin shared module
- [`KT-50648`](https://youtrack.jetbrains.com/issue/KT-50648) Incorrect KMM cinterop conversion

### Native. ObjC Export

- [`KT-50982`](https://youtrack.jetbrains.com/issue/KT-50982) RuntimeAssertFailedPanic in iOS when Kotlin framework is initialized before loading
- [`KT-49937`](https://youtrack.jetbrains.com/issue/KT-49937) Kotlin/Native 1.5.31: 'runtime assert: Unexpected selector clash' when 'override fun toString(): String' is used

### Native. Platforms

- [`KT-52232`](https://youtrack.jetbrains.com/issue/KT-52232) Kotlin/Native: simplify toolchain dependency override for MinGW

### Native. Runtime

- [`KT-52365`](https://youtrack.jetbrains.com/issue/KT-52365) Kotlin/Native fails to compile projects for 32-bit targets when new memory manager is enabled

### Native. Runtime. Memory

- [`KT-48537`](https://youtrack.jetbrains.com/issue/KT-48537) Kotlin/Native: improve GC triggers in the new MM.
- [`KT-50713`](https://youtrack.jetbrains.com/issue/KT-50713) Kotlin/Native: Enable Concurrent Sweep GC by default

### Native. Stdlib

- [`KT-50312`](https://youtrack.jetbrains.com/issue/KT-50312) enhancement: kotlin native   -- add  alloc<TVarOf<T>>(T)

### Native. Testing

- [`KT-50316`](https://youtrack.jetbrains.com/issue/KT-50316) Kotlin/Native: Produce a list of available tests alongside the final artifact
- [`KT-50139`](https://youtrack.jetbrains.com/issue/KT-50139) Create tests for Enter/Leave frame optimization

### Reflection

- [`KT-27598`](https://youtrack.jetbrains.com/issue/KT-27598) "KotlinReflectionInternalError" when using `callBy` on constructor that has inline class parameters
- [`KT-31141`](https://youtrack.jetbrains.com/issue/KT-31141) IllegalArgumentException when reflectively accessing nullable property of inline class type

### Tools. CLI

- [`KT-52409`](https://youtrack.jetbrains.com/issue/KT-52409) Report error when use-k2 with Multiplatform
- [`KT-51717`](https://youtrack.jetbrains.com/issue/KT-51717) IllegalArgumentException: Unexpected versionNeededToExtract (0) in 1.6.20-RC2 with useFir enabled
- [`KT-52217`](https://youtrack.jetbrains.com/issue/KT-52217) Rename 'useFir' to 'useK2'
- [`KT-29974`](https://youtrack.jetbrains.com/issue/KT-29974) Add a compiler option '-Xjdk-release' similar to javac's '--release' to control the target JDK version
- [`KT-51673`](https://youtrack.jetbrains.com/issue/KT-51673) Make language version description not in capital letters
- [`KT-48833`](https://youtrack.jetbrains.com/issue/KT-48833) -Xsuppress-version-warnings allows to suppress errors about unsupported versions
- [`KT-51627`](https://youtrack.jetbrains.com/issue/KT-51627) kotlinc fails with `java.lang.RuntimeException` if `/tmp/build.txt` file exists on the disk
- [`KT-51306`](https://youtrack.jetbrains.com/issue/KT-51306) Support reading language settings from an environment variable and overriding the current settings by them
- [`KT-51093`](https://youtrack.jetbrains.com/issue/KT-51093) "-Xopt-in=..." command line argument no longer works

### Tools. Commonizer

- [`KT-43309`](https://youtrack.jetbrains.com/issue/KT-43309) Overwrite return type and parameter types of callable member to succeed commonization
- [`KT-52050`](https://youtrack.jetbrains.com/issue/KT-52050) [Commonizer] 'platform.posix.DIR' not implementing 'CPointed' when commonized for 'nativeMain' on linux or windows hosts
- [`KT-51224`](https://youtrack.jetbrains.com/issue/KT-51224) MPP: For optimistically commonized numbers missed kotlinx.cinterop.UnsafeNumber
- [`KT-51215`](https://youtrack.jetbrains.com/issue/KT-51215) MPP: Update Kdoc description for kotlinx.cinterop.UnsafeNumber
- [`KT-51686`](https://youtrack.jetbrains.com/issue/KT-51686) Cinterop: Overload resolution ambiguity in 1.6.20-RC2
- [`KT-46636`](https://youtrack.jetbrains.com/issue/KT-46636) HMPP: missed classes from `platform.posix.*`
- [`KT-51332`](https://youtrack.jetbrains.com/issue/KT-51332) Optimistic number commonization is disabled by default in KGP with enabled HMPP

### Tools. Compiler Plugins

- [`KT-50992`](https://youtrack.jetbrains.com/issue/KT-50992) jvm-abi-gen breaks inline functions in inline classes with private constructors in Kotlin 1.6.20

### Tools. Daemon

- [`KT-32885`](https://youtrack.jetbrains.com/issue/KT-32885) KT. Kotlin daemon compilation process is broken: java.lang.IllegalStateException Service is dying at entities generation by Kotlin.kts script

### Tools. Gradle

#### New Features

- [`KT-49227`](https://youtrack.jetbrains.com/issue/KT-49227) Support Gradle plugins variants
- [`KT-50869`](https://youtrack.jetbrains.com/issue/KT-50869) Provide API that allow AGP to set up Kotlin compilation
- [`KT-48008`](https://youtrack.jetbrains.com/issue/KT-48008) Consider offering a KotlinBasePlugin
- [`KT-52030`](https://youtrack.jetbrains.com/issue/KT-52030) Provide experimental possibility to view internal information about Kotlin Compiler performance

#### Performance Improvements

- [`KT-52141`](https://youtrack.jetbrains.com/issue/KT-52141) Optimize Java class snapshotting for the `kotlin.incremental.useClasspathSnapshot` feature
- [`KT-51978`](https://youtrack.jetbrains.com/issue/KT-51978) Optimize classpath snapshot cache for the `kotlin.incremental.useClasspathSnapshot` feature
- [`KT-51326`](https://youtrack.jetbrains.com/issue/KT-51326) Kotlin-gradle-plugin performance issue with mass java SourceRoots

#### Fixes

- [`KT-52448`](https://youtrack.jetbrains.com/issue/KT-52448) Compilation tasks are missing input/output/internal annotations on includes/excludes properties
- [`KT-52239`](https://youtrack.jetbrains.com/issue/KT-52239) Type based task configuration-blocks for JVM stopped working in Gradle
- [`KT-52313`](https://youtrack.jetbrains.com/issue/KT-52313) No recompilation in Gradle after adding or removing function parameters, removing functions (and maybe more) in dependent modules
- [`KT-51854`](https://youtrack.jetbrains.com/issue/KT-51854) Add Ktor to gradle performance benchmark
- [`KT-52086`](https://youtrack.jetbrains.com/issue/KT-52086) Rename flag 'use-fir' to 'use-k2'
- [`KT-52509`](https://youtrack.jetbrains.com/issue/KT-52509) Main variant published to Gradle plugin portal uses unshadowed artifact
- [`KT-52392`](https://youtrack.jetbrains.com/issue/KT-52392) Gradle: 1.7.0 does not support custom gradle build configuration on Windows OS
- [`KT-32805`](https://youtrack.jetbrains.com/issue/KT-32805) KotlinCompile inherits properties sourceCompatibility and targetCompatibility which breaks Gradle's incremental compilation
- [`KT-52189`](https://youtrack.jetbrains.com/issue/KT-52189) Provide Gradle Kotlin/DSL friendly deprecated classpath property in KotlinCompiler task
- [`KT-51415`](https://youtrack.jetbrains.com/issue/KT-51415) Confusing build failure reason is displayed in case kapt is used and different JDKs are used for compileKotlin and compileJava tasks
- [`KT-52187`](https://youtrack.jetbrains.com/issue/KT-52187) New IC can not be enabled in an Android project using kapt
- [`KT-51898`](https://youtrack.jetbrains.com/issue/KT-51898) Upgrading Kotlin/Kotlin Gradle plugin to 1.5.3 and above breaks 'com.android.asset-pack' plugin
- [`KT-51913`](https://youtrack.jetbrains.com/issue/KT-51913) Gradle plugin should not add attributes to the legacy configurations
- [`KT-34862`](https://youtrack.jetbrains.com/issue/KT-34862) Restoring from build cache breaks Kotlin incremental compilation
- [`KT-45777`](https://youtrack.jetbrains.com/issue/KT-45777) New IC in Gradle
- [`KT-51360`](https://youtrack.jetbrains.com/issue/KT-51360) Show performance difference in percent between releases
- [`KT-51380`](https://youtrack.jetbrains.com/issue/KT-51380) Add open-source project using Kotlin/JS plugin to build regression benchmarks
- [`KT-51937`](https://youtrack.jetbrains.com/issue/KT-51937) Toolchain usage with configuration cache prevents KotlinCompile task to be UP-TO-DATE
- [`KT-48276`](https://youtrack.jetbrains.com/issue/KT-48276) Remove kotlin2js and kotlin-dce-plugin
- [`KT-52138`](https://youtrack.jetbrains.com/issue/KT-52138) KSP could not access internal methods/properties in Kotlin Gradle Plugin
- [`KT-51342`](https://youtrack.jetbrains.com/issue/KT-51342) Set minimal supported Android Gradle plugin version to 3.6.4
- [`KT-50494`](https://youtrack.jetbrains.com/issue/KT-50494) Remove kotlin.experimental.coroutines Gradle DSL option
- [`KT-49733`](https://youtrack.jetbrains.com/issue/KT-49733) Bump minimal supported Gradle version to 6.7.1
- [`KT-48831`](https://youtrack.jetbrains.com/issue/KT-48831) Remove 'KotlinGradleSubplugin'
- [`KT-47924`](https://youtrack.jetbrains.com/issue/KT-47924) Remove annoying cast in toolchain extension method for Kotlin DSL
- [`KT-46541`](https://youtrack.jetbrains.com/issue/KT-46541) Fail Gradle builds when deprecated kotlinOptions.jdkHome is set
- [`KT-51830`](https://youtrack.jetbrains.com/issue/KT-51830) Gradle: deprecate `kotlin.compiler.execution.strategy` system property
- [`KT-47763`](https://youtrack.jetbrains.com/issue/KT-47763) Gradle DSL: Remove deprecated useExperimentalAnnotation and experimentalAnnotationInUse
- [`KT-51374`](https://youtrack.jetbrains.com/issue/KT-51374) NoSuchFileException in getOrCreateSessionFlagFile()
- [`KT-51837`](https://youtrack.jetbrains.com/issue/KT-51837) kotlin-gradle-plugin:1.6.20 fails xray scan on shadowed Gson 2.8.6.
- [`KT-51454`](https://youtrack.jetbrains.com/issue/KT-51454) KotlinJvmTest is not a cacheable task
- [`KT-45745`](https://youtrack.jetbrains.com/issue/KT-45745) Migrate only Kotlin Gradle Plugin tests to new JUnit5 DSL and run them separately on CI
- [`KT-47318`](https://youtrack.jetbrains.com/issue/KT-47318) Remove deprecated 'kotlinPluginVersion' property in `KotlinBasePluginWrapper'
- [`KT-51378`](https://youtrack.jetbrains.com/issue/KT-51378) Gradle 'buildSrc' compilation fails when newer version of Kotlin plugin is added to the build script classpath
- [`KT-46038`](https://youtrack.jetbrains.com/issue/KT-46038) Gradle: `kotlin_module` files are corrupted in the KotlinCompile output, and gets cached
- [`KT-51064`](https://youtrack.jetbrains.com/issue/KT-51064) Kotlin gradle build hangs on MetricsContainer.flush
- [`KT-48779`](https://youtrack.jetbrains.com/issue/KT-48779) Gradle: Could not connect to kotlin daemon

### Tools. Gradle. Cocoapods

- [`KT-50622`](https://youtrack.jetbrains.com/issue/KT-50622) Cocoapods Plugin: cocoapods-generate does not work correctly with ruby 3.0.0 and higher
- [`KT-51861`](https://youtrack.jetbrains.com/issue/KT-51861) Custom binary name in CocoaPods plugin isn't respected by fatFramework task

### Tools. Gradle. JS

- [`KT-52221`](https://youtrack.jetbrains.com/issue/KT-52221) Kotlin/JS: failed Node tests are not reported in a standard way
- [`KT-51895`](https://youtrack.jetbrains.com/issue/KT-51895) K/JS: Redundant technical messages during JS tests
- [`KT-51414`](https://youtrack.jetbrains.com/issue/KT-51414) Allow set up environment variables for JS tests
- [`KT-51623`](https://youtrack.jetbrains.com/issue/KT-51623) Kotlin/JS: Mocha could not failed when external module not found
- [`KT-51503`](https://youtrack.jetbrains.com/issue/KT-51503) Update NPM dependency versions

### Tools. Gradle. Multiplatform

#### New Features

- [`KT-51386`](https://youtrack.jetbrains.com/issue/KT-51386) [KPM] IdeaKotlinProjectModelBuilder: Implement dependencies

#### Fixes

- [`KT-49524`](https://youtrack.jetbrains.com/issue/KT-49524) Improve DSL for managing Kotlin/Native binary output
- [`KT-51765`](https://youtrack.jetbrains.com/issue/KT-51765) com.android.lint in multiplatform project without android target should not trigger warning
- [`KT-38456`](https://youtrack.jetbrains.com/issue/KT-38456) MPP with Android source set: allTests task does not execute Android unit tests
- [`KT-44227`](https://youtrack.jetbrains.com/issue/KT-44227) Common tests are not launched on local JVM for Android via allTests task in a multiplatform project
- [`KT-51946`](https://youtrack.jetbrains.com/issue/KT-51946) Temporarily mark HMPP tasks as notCompatibleWithConfigurationCache for Gradle 7.4
- [`KT-52140`](https://youtrack.jetbrains.com/issue/KT-52140) Support extensibility Kotlin Artifacts DSL by external gradle plugins
- [`KT-51947`](https://youtrack.jetbrains.com/issue/KT-51947) Mark HMPP tasks as notCompatibleWithConfigurationCache for Gradle 7.4 using Reflection
- [`KT-50925`](https://youtrack.jetbrains.com/issue/KT-50925) Could not resolve all files for configuration ':metadataCompileClasspath'
- [`KT-51262`](https://youtrack.jetbrains.com/issue/KT-51262) [KPM] IDEA import: Move model builder to KGP
- [`KT-51220`](https://youtrack.jetbrains.com/issue/KT-51220) [KPM][Android] Implement generic data storage and import pipeline
- [`KT-48649`](https://youtrack.jetbrains.com/issue/KT-48649) No run task generated for macosArm64 target in Gradle plugin

### Tools. Gradle. Native

- [`KT-47746`](https://youtrack.jetbrains.com/issue/KT-47746) Allow customization of the Kotlin/Native compiler download url
- [`KT-51884`](https://youtrack.jetbrains.com/issue/KT-51884) Gradle Native: "A problem occurred starting process 'command 'xcodebuild''" when building `assembleFooXCFramework` task on Linux

### Tools. Incremental Compile

- [`KT-51546`](https://youtrack.jetbrains.com/issue/KT-51546) FIR incremental compilation fails with assertion "Trying to inline an anonymous object which is not part of the public ABI"
- [`KT-49780`](https://youtrack.jetbrains.com/issue/KT-49780) IncrementalCompilerRunner bug: Outputs are deleted after successful rebuild following fallback from an exception
- [`KT-44741`](https://youtrack.jetbrains.com/issue/KT-44741) Incremental compilation: inspectClassesForKotlinIC doesn't determine changes with imported constant

### Tools. JPS

- [`KTIJ-17280`](https://youtrack.jetbrains.com/issue/KTIJ-17280) JPS: don't use java.io.File.createTempFile as it is not working sometimes
- [`KTIJ-20954`](https://youtrack.jetbrains.com/issue/KTIJ-20954) NPE at at org.jetbrains.kotlin.metadata.jvm.deserialization.JvmProtoBufUtil.readNameResolver on compiling by JPS with LV > 1.7

### Tools. Kapt

- [`KT-49533`](https://youtrack.jetbrains.com/issue/KT-49533) Make kapt work out of the box with latest JDKs
- [`KT-52284`](https://youtrack.jetbrains.com/issue/KT-52284) FIR: add error in 1.7.0 branch if run with Kapt
- [`KT-51463`](https://youtrack.jetbrains.com/issue/KT-51463) KAPT: Incremental compilation not working when rerunning unit tests
- [`KT-51132`](https://youtrack.jetbrains.com/issue/KT-51132) KAPT: Support reporting the number of generated files by each annotation processor
- [`KT-30172`](https://youtrack.jetbrains.com/issue/KT-30172) Kapt: Shutdown kotlinc gracefully in case of error in annotation processor

### Tools. Scripts

- [`KT-49173`](https://youtrack.jetbrains.com/issue/KT-49173) Add support for nullable types in provided properties and other configuration-defined declarations
- [`KT-52294`](https://youtrack.jetbrains.com/issue/KT-52294) [Scripting] Update oudated org.eclipse.aether dependencies to new org.apache.maven.resolver
- [`KT-51213`](https://youtrack.jetbrains.com/issue/KT-51213) Kotlin JSR223 crashes with "ScriptException: ERROR java.lang.NullPointerException:" if bindings contain one or more null values
- [`KT-48812`](https://youtrack.jetbrains.com/issue/KT-48812) Script: "IllegalStateException: unknown classifier kind SCRIPT" when passing a function reference to a Flow
- [`KT-50902`](https://youtrack.jetbrains.com/issue/KT-50902) Scripts loaded from the compilation cache ignore the `loadDependencies` eval configuration property
- [`KT-52186`](https://youtrack.jetbrains.com/issue/KT-52186) Scripts: Backend Internal error: Exception during IR lowering when using symbol from a dependency inside a function
- [`KT-51731`](https://youtrack.jetbrains.com/issue/KT-51731) Script: jsr223 memory leak in spring-boot Fat Jar
- [`KT-49258`](https://youtrack.jetbrains.com/issue/KT-49258) Scripts: method 'void <init>()' not found with multiple evals using kotlin script JSR223
- [`KT-51346`](https://youtrack.jetbrains.com/issue/KT-51346) Scripts: "BackendException: Exception during IR lowering" with variable of imported script inside class



## Recent ChangeLogs:
### [ChangeLog-1.6.X](docs/changelogs/ChangeLog-1.6.X.md)
### [ChangeLog-1.5.X](docs/changelogs/ChangeLog-1.5.X.md)
### [ChangeLog-1.4.X](docs/changelogs/ChangeLog-1.4.X.md)
### [ChangeLog-1.3.X](docs/changelogs/ChangeLog-1.3.X.md)
### [ChangeLog-1.2.X](docs/changelogs/ChangeLog-1.2.X.md)
### [ChangeLog-1.1.X](docs/changelogs/ChangeLog-1.1.X.md)
### [ChangeLog-1.0.X](docs/changelogs/ChangeLog-1.0.X.md)