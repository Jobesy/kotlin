/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.standalone.fir.test.cases.generated.cases.components.callResolver;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.analysis.api.standalone.fir.test.AnalysisApiFirStandaloneModeTestConfiguratorFactory;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiTestConfiguratorFactoryData;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiTestConfigurator;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.TestModuleKind;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.FrontendKind;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisSessionMode;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiMode;
import org.jetbrains.kotlin.analysis.api.impl.base.test.cases.components.callResolver.AbstractResolveCandidatesTest;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link GenerateNewCompilerTests.kt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("analysis/analysis-api/testData/components/callResolver/resolveCandidates")
@TestDataPath("$PROJECT_ROOT")
public class FirStandaloneNormalAnalysisSourceModuleResolveCandidatesTestGenerated extends AbstractResolveCandidatesTest {
    @NotNull
    @Override
    public AnalysisApiTestConfigurator getConfigurator() {
        return AnalysisApiFirStandaloneModeTestConfiguratorFactory.INSTANCE.createConfigurator(
            new AnalysisApiTestConfiguratorFactoryData(
                FrontendKind.Fir,
                TestModuleKind.Source,
                AnalysisSessionMode.Normal,
                AnalysisApiMode.Standalone
            )
        );
    }

    @Test
    public void testAllFilesPresentInResolveCandidates() throws Exception {
        KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("analysis/analysis-api/testData/components/callResolver/resolveCandidates"), Pattern.compile("^(.+)\\.kt$"), null, true);
    }

    @Nested
    @TestMetadata("analysis/analysis-api/testData/components/callResolver/resolveCandidates/multipleCandidates")
    @TestDataPath("$PROJECT_ROOT")
    public class MultipleCandidates {
        @Test
        public void testAllFilesPresentInMultipleCandidates() throws Exception {
            KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("analysis/analysis-api/testData/components/callResolver/resolveCandidates/multipleCandidates"), Pattern.compile("^(.+)\\.kt$"), null, true);
        }

        @Test
        @TestMetadata("ambiguous.kt")
        public void testAmbiguous() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/multipleCandidates/ambiguous.kt");
        }

        @Test
        @TestMetadata("ambiguousImplicitInvoke.kt")
        public void testAmbiguousImplicitInvoke() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/multipleCandidates/ambiguousImplicitInvoke.kt");
        }

        @Test
        @TestMetadata("ambiguousWithExplicitTypeParameters.kt")
        public void testAmbiguousWithExplicitTypeParameters() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/multipleCandidates/ambiguousWithExplicitTypeParameters.kt");
        }

        @Test
        @TestMetadata("ambiguousWithInferredTypeParameters.kt")
        public void testAmbiguousWithInferredTypeParameters() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/multipleCandidates/ambiguousWithInferredTypeParameters.kt");
        }

        @Test
        @TestMetadata("delegatedConstructor.kt")
        public void testDelegatedConstructor() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/multipleCandidates/delegatedConstructor.kt");
        }

        @Test
        @TestMetadata("implicitInvoke.kt")
        public void testImplicitInvoke() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/multipleCandidates/implicitInvoke.kt");
        }

        @Test
        @TestMetadata("implicitInvokeWithReceiver.kt")
        public void testImplicitInvokeWithReceiver() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/multipleCandidates/implicitInvokeWithReceiver.kt");
        }
    }

    @Nested
    @TestMetadata("analysis/analysis-api/testData/components/callResolver/resolveCandidates/noCandidates")
    @TestDataPath("$PROJECT_ROOT")
    public class NoCandidates {
        @Test
        public void testAllFilesPresentInNoCandidates() throws Exception {
            KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("analysis/analysis-api/testData/components/callResolver/resolveCandidates/noCandidates"), Pattern.compile("^(.+)\\.kt$"), null, true);
        }

        @Test
        @TestMetadata("hiddenDeprecated.kt")
        public void testHiddenDeprecated() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/noCandidates/hiddenDeprecated.kt");
        }

        @Test
        @TestMetadata("unresolvableOperator_elvis_1.kt")
        public void testUnresolvableOperator_elvis_1() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/noCandidates/unresolvableOperator_elvis_1.kt");
        }

        @Test
        @TestMetadata("unresolvableOperator_elvis_2.kt")
        public void testUnresolvableOperator_elvis_2() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/noCandidates/unresolvableOperator_elvis_2.kt");
        }

        @Test
        @TestMetadata("unresolvableOperator_eqeqeq_1.kt")
        public void testUnresolvableOperator_eqeqeq_1() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/noCandidates/unresolvableOperator_eqeqeq_1.kt");
        }

        @Test
        @TestMetadata("unresolvableOperator_eqeqeq_2.kt")
        public void testUnresolvableOperator_eqeqeq_2() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/noCandidates/unresolvableOperator_eqeqeq_2.kt");
        }

        @Test
        @TestMetadata("unresolvableOperator_excleqeq_1.kt")
        public void testUnresolvableOperator_excleqeq_1() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/noCandidates/unresolvableOperator_excleqeq_1.kt");
        }

        @Test
        @TestMetadata("unresolvableOperator_excleqeq_2.kt")
        public void testUnresolvableOperator_excleqeq_2() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/noCandidates/unresolvableOperator_excleqeq_2.kt");
        }

        @Test
        @TestMetadata("unresolvedReference.kt")
        public void testUnresolvedReference() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/noCandidates/unresolvedReference.kt");
        }
    }

    @Nested
    @TestMetadata("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate")
    @TestDataPath("$PROJECT_ROOT")
    public class SingleCandidate {
        @Test
        public void testAllFilesPresentInSingleCandidate() throws Exception {
            KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate"), Pattern.compile("^(.+)\\.kt$"), null, true);
        }

        @Test
        @TestMetadata("arrayOfInAnnotation.kt")
        public void testArrayOfInAnnotation() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/arrayOfInAnnotation.kt");
        }

        @Test
        @TestMetadata("builderInference.kt")
        public void testBuilderInference() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/builderInference.kt");
        }

        @Test
        @TestMetadata("checkNotNullCall.kt")
        public void testCheckNotNullCall() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/checkNotNullCall.kt");
        }

        @Test
        @TestMetadata("checkNotNullCallAsCallee.kt")
        public void testCheckNotNullCallAsCallee() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/checkNotNullCallAsCallee.kt");
        }

        @Test
        @TestMetadata("comparisonCall.kt")
        public void testComparisonCall() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/comparisonCall.kt");
        }

        @Test
        @TestMetadata("consecutiveImplicitInvoke1.kt")
        public void testConsecutiveImplicitInvoke1() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/consecutiveImplicitInvoke1.kt");
        }

        @Test
        @TestMetadata("consecutiveImplicitInvoke2.kt")
        public void testConsecutiveImplicitInvoke2() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/consecutiveImplicitInvoke2.kt");
        }

        @Test
        @TestMetadata("consecutiveImplicitInvoke3.kt")
        public void testConsecutiveImplicitInvoke3() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/consecutiveImplicitInvoke3.kt");
        }

        @Test
        @TestMetadata("delegatedConstructorApplicable.kt")
        public void testDelegatedConstructorApplicable() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/delegatedConstructorApplicable.kt");
        }

        @Test
        @TestMetadata("delegatedConstructorInapplicable.kt")
        public void testDelegatedConstructorInapplicable() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/delegatedConstructorInapplicable.kt");
        }

        @Test
        @TestMetadata("delegatedConstructorInapplicableDifferentParametersCount.kt")
        public void testDelegatedConstructorInapplicableDifferentParametersCount() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/delegatedConstructorInapplicableDifferentParametersCount.kt");
        }

        @Test
        @TestMetadata("eqEqCall_fromAny.kt")
        public void testEqEqCall_fromAny() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/eqEqCall_fromAny.kt");
        }

        @Test
        @TestMetadata("eqEqCall_fromSuperType.kt")
        public void testEqEqCall_fromSuperType() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/eqEqCall_fromSuperType.kt");
        }

        @Test
        @TestMetadata("eqEqCall_overridden.kt")
        public void testEqEqCall_overridden() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/eqEqCall_overridden.kt");
        }

        @Test
        @TestMetadata("functionCallInTheSameFile.kt")
        public void testFunctionCallInTheSameFile() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/functionCallInTheSameFile.kt");
        }

        @Test
        @TestMetadata("functionCallWithExtensionReceiverAndTypeArgument.kt")
        public void testFunctionCallWithExtensionReceiverAndTypeArgument() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/functionCallWithExtensionReceiverAndTypeArgument.kt");
        }

        @Test
        @TestMetadata("functionCallWithLambdaArgument.kt")
        public void testFunctionCallWithLambdaArgument() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/functionCallWithLambdaArgument.kt");
        }

        @Test
        @TestMetadata("functionCallWithNamedArgument.kt")
        public void testFunctionCallWithNamedArgument() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/functionCallWithNamedArgument.kt");
        }

        @Test
        @TestMetadata("functionCallWithNonTrailingLambdaArgument.kt")
        public void testFunctionCallWithNonTrailingLambdaArgument() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/functionCallWithNonTrailingLambdaArgument.kt");
        }

        @Test
        @TestMetadata("functionCallWithSpreadArgument.kt")
        public void testFunctionCallWithSpreadArgument() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/functionCallWithSpreadArgument.kt");
        }

        @Test
        @TestMetadata("functionCallWithTypeArgument.kt")
        public void testFunctionCallWithTypeArgument() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/functionCallWithTypeArgument.kt");
        }

        @Test
        @TestMetadata("functionCallWithVarargArgument.kt")
        public void testFunctionCallWithVarargArgument() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/functionCallWithVarargArgument.kt");
        }

        @Test
        @TestMetadata("functionTypeVariableCall_dispatchReceiver.kt")
        public void testFunctionTypeVariableCall_dispatchReceiver() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/functionTypeVariableCall_dispatchReceiver.kt");
        }

        @Test
        @TestMetadata("functionTypeVariableCall_extensionReceiver.kt")
        public void testFunctionTypeVariableCall_extensionReceiver() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/functionTypeVariableCall_extensionReceiver.kt");
        }

        @Test
        @TestMetadata("functionWithReceiverCall.kt")
        public void testFunctionWithReceiverCall() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/functionWithReceiverCall.kt");
        }

        @Test
        @TestMetadata("functionWithReceiverSafeCall.kt")
        public void testFunctionWithReceiverSafeCall() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/functionWithReceiverSafeCall.kt");
        }

        @Test
        @TestMetadata("hiddenConstructor.kt")
        public void testHiddenConstructor() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/hiddenConstructor.kt");
        }

        @Test
        @TestMetadata("implicitConstructorDelegationCall.kt")
        public void testImplicitConstructorDelegationCall() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/implicitConstructorDelegationCall.kt");
        }

        @Test
        @TestMetadata("implicitConstuctorCall.kt")
        public void testImplicitConstuctorCall() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/implicitConstuctorCall.kt");
        }

        @Test
        @TestMetadata("implicitJavaConstuctorCall.kt")
        public void testImplicitJavaConstuctorCall() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/implicitJavaConstuctorCall.kt");
        }

        @Test
        @TestMetadata("indexedGet.kt")
        public void testIndexedGet() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/indexedGet.kt");
        }

        @Test
        @TestMetadata("indexedGetWithNotEnoughArgs.kt")
        public void testIndexedGetWithNotEnoughArgs() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/indexedGetWithNotEnoughArgs.kt");
        }

        @Test
        @TestMetadata("indexedGetWithTooManyArgs.kt")
        public void testIndexedGetWithTooManyArgs() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/indexedGetWithTooManyArgs.kt");
        }

        @Test
        @TestMetadata("indexedSet.kt")
        public void testIndexedSet() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/indexedSet.kt");
        }

        @Test
        @TestMetadata("indexedSetWithNotEnoughArgs.kt")
        public void testIndexedSetWithNotEnoughArgs() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/indexedSetWithNotEnoughArgs.kt");
        }

        @Test
        @TestMetadata("indexedSetWithTooManyArgs.kt")
        public void testIndexedSetWithTooManyArgs() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/indexedSetWithTooManyArgs.kt");
        }

        @Test
        @TestMetadata("intArrayOfInAnnotation.kt")
        public void testIntArrayOfInAnnotation() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/intArrayOfInAnnotation.kt");
        }

        @Test
        @TestMetadata("javaFunctionCall.kt")
        public void testJavaFunctionCall() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/javaFunctionCall.kt");
        }

        @Test
        @TestMetadata("memberFunctionCallWithTypeArgument.kt")
        public void testMemberFunctionCallWithTypeArgument() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/memberFunctionCallWithTypeArgument.kt");
        }

        @Test
        @TestMetadata("privateMember.kt")
        public void testPrivateMember() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/privateMember.kt");
        }

        @Test
        @TestMetadata("resolveCallInSuperConstructorParam.kt")
        public void testResolveCallInSuperConstructorParam() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/resolveCallInSuperConstructorParam.kt");
        }

        @Test
        @TestMetadata("samConstructorCall.kt")
        public void testSamConstructorCall() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/samConstructorCall.kt");
        }

        @Test
        @TestMetadata("simpleCallWithNonMatchingArgs.kt")
        public void testSimpleCallWithNonMatchingArgs() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/simpleCallWithNonMatchingArgs.kt");
        }

        @Test
        @TestMetadata("smartCastExplicitExtensionReceiver.kt")
        public void testSmartCastExplicitExtensionReceiver() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/smartCastExplicitExtensionReceiver.kt");
        }

        @Test
        @TestMetadata("variableAsFunction.kt")
        public void testVariableAsFunction() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/variableAsFunction.kt");
        }

        @Test
        @TestMetadata("variableAsFunctionLikeCall.kt")
        public void testVariableAsFunctionLikeCall() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/variableAsFunctionLikeCall.kt");
        }

        @Test
        @TestMetadata("variableAsFunctionWithParameterName.kt")
        public void testVariableAsFunctionWithParameterName() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/variableAsFunctionWithParameterName.kt");
        }

        @Test
        @TestMetadata("variableAsFunctionWithParameterNameAnnotation.kt")
        public void testVariableAsFunctionWithParameterNameAnnotation() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/variableAsFunctionWithParameterNameAnnotation.kt");
        }

        @Test
        @TestMetadata("variableAsFunctionWithParameterNameAnnotationConflict.kt")
        public void testVariableAsFunctionWithParameterNameAnnotationConflict() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/variableAsFunctionWithParameterNameAnnotationConflict.kt");
        }

        @Test
        @TestMetadata("variableAsFunctionWithParameterNameGeneric.kt")
        public void testVariableAsFunctionWithParameterNameGeneric() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/variableAsFunctionWithParameterNameGeneric.kt");
        }

        @Test
        @TestMetadata("variableAsFunctionWithParameterNameInNonFunctionType.kt")
        public void testVariableAsFunctionWithParameterNameInNonFunctionType() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/variableAsFunctionWithParameterNameInNonFunctionType.kt");
        }

        @Test
        @TestMetadata("variableAsFunctionWithParameterNameMixed.kt")
        public void testVariableAsFunctionWithParameterNameMixed() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/variableAsFunctionWithParameterNameMixed.kt");
        }

        @Test
        @TestMetadata("variableWithExtensionInvoke.kt")
        public void testVariableWithExtensionInvoke() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/variableWithExtensionInvoke.kt");
        }

        @Test
        @TestMetadata("variableWithInvokeFunctionCall_dispatchReceiver.kt")
        public void testVariableWithInvokeFunctionCall_dispatchReceiver() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/variableWithInvokeFunctionCall_dispatchReceiver.kt");
        }

        @Test
        @TestMetadata("variableWithInvokeFunctionCall_extensionReceiver.kt")
        public void testVariableWithInvokeFunctionCall_extensionReceiver() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/variableWithInvokeFunctionCall_extensionReceiver.kt");
        }

        @Test
        @TestMetadata("variableWithMemberInvoke.kt")
        public void testVariableWithMemberInvoke() throws Exception {
            runTest("analysis/analysis-api/testData/components/callResolver/resolveCandidates/singleCandidate/variableWithMemberInvoke.kt");
        }
    }
}
