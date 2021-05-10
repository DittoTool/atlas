package com.taobao.android.builder.tasks.appbundles;

import com.android.build.gradle.api.BaseVariantOutput;
import com.android.build.gradle.internal.api.VariantContext;
import com.android.build.gradle.internal.tasks.AndroidBuilderTask;
import com.android.build.gradle.internal.tasks.factory.TaskFactoryImpl;
import com.android.build.gradle.tasks.FeaturePackageApplication;
import com.android.build.gradle.tasks.factory.FeatureAndroidJavaCompile;
import com.taobao.android.builder.AtlasBuildContext;
import com.taobao.android.builder.tasks.manager.FeatureBaseTaskAction;
import com.taobao.android.builder.tasks.manager.MtlBaseTaskAction;
import org.dom4j.DocumentException;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * @ClassName FeaturesParallelTask
 * @Description TODO
 * @Author zhayu.ll
 * @Date 2019-08-21 09:53
 * @Version 1.0
 */
public class FeaturesParallelTask extends AndroidBuilderTask {

    private ProcessType processType;

    private VariantContext variantContext;

    private BaseVariantOutput variantOutput;

    @TaskAction
    public void taskCreation() {

        switch (processType) {

            case MERGE_MANIFEST:
                AtlasBuildContext.androidDependencyTrees.get(variantName).getAwbBundles().stream().forEach(awbBundle -> {
                    if (awbBundle.dynamicFeature) {
                        TaskProvider<ProcessFeatureManifestTask> provider = new TaskFactoryImpl(getProject().getTasks()).register(new ProcessFeatureManifestTask.CreationAction(awbBundle, variantContext, variantOutput));
                        try {
                            provider.get().doFullTaskAction();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;

            case BUNDLE_RES:
                AtlasBuildContext.androidDependencyTrees.get(variantName).getAwbBundles().stream().forEach(awbBundle -> {
                    if (awbBundle.dynamicFeature) {
                        TaskProvider<BundleFeatureResourceTask> provider = new TaskFactoryImpl(getProject().getTasks()).register(new BundleFeatureResourceTask.CreationAction(awbBundle, variantContext, variantOutput));
                        provider.get().taskAction();
                    }
                });

                break;


            case MERGE_RESOURCE:

                AtlasBuildContext.androidDependencyTrees.get(variantName).getAwbBundles().stream().forEach(awbBundle -> {
                    if (awbBundle.dynamicFeature) {
                        TaskProvider<MergeFeatureResource> provider = new TaskFactoryImpl(getProject().getTasks()).register(new MergeFeatureResource.CreationAction(awbBundle, variantContext, variantOutput));
                        try {
                            provider.get().doFullTaskAction();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JAXBException e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;


            case MERGE_ASSETS:

                AtlasBuildContext.androidDependencyTrees.get(variantName).getAwbBundles().stream().forEach(awbBundle -> {
                    if (awbBundle.dynamicFeature) {
                        TaskProvider<MergeFeatureAssets> provider = new TaskFactoryImpl(getProject().getTasks()).register(new MergeFeatureAssets.CreationAction(awbBundle, variantContext, variantOutput));
                        try {
                            provider.get().doFullTaskAction();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


                break;


            case PROCESS_RESOURCE:

                AtlasBuildContext.androidDependencyTrees.get(variantName).getAwbBundles().stream().forEach(awbBundle -> {
                    if (awbBundle.dynamicFeature) {
                        TaskProvider<ProcessFeatureResource> provider = new TaskFactoryImpl(getProject().getTasks()).register(new ProcessFeatureResource.CreationAction(awbBundle, variantContext, variantOutput));
                            provider.get().doFullTaskAction();

                    }
                });

                break;



            case JAVAC:

                AtlasBuildContext.androidDependencyTrees.get(variantName).getAwbBundles().stream().forEach(awbBundle -> {
                    if (awbBundle.dynamicFeature) {
                        TaskProvider<FeatureAndroidJavaCompile> provider = new TaskFactoryImpl(getProject().getTasks()).register(new FeatureAndroidJavaCompile.CreationAction(awbBundle, variantContext, variantOutput));
                        provider.get().doFullTaskAction();

                    }
                });

                break;


            case PRE_BUNDLE:
                AtlasBuildContext.androidDependencyTrees.get(variantName).getAwbBundles().stream().forEach(awbBundle -> {
                    if (awbBundle.dynamicFeature) {
                        TaskProvider<PerModuleBundlesTask> provider = new TaskFactoryImpl(getProject().getTasks()).register(new PerModuleBundlesTask.CreationAction(awbBundle, variantContext, variantOutput));
                        provider.get().zip();

                    }
                });
                break;


            case COLLECT_DEP:
                AtlasBuildContext.androidDependencyTrees.get(variantName).getAwbBundles().stream().forEach(awbBundle -> {
                    if (awbBundle.dynamicFeature) {
                        TaskProvider<PreFeatureDepsTask> provider = new TaskFactoryImpl(getProject().getTasks()).register(new PreFeatureDepsTask.CreationAction(awbBundle, variantContext, variantOutput));
                        provider.get().writeFile();

                    }
                });
                break;


            case FEATURE_APPLICATION:

                AtlasBuildContext.androidDependencyTrees.get(variantName).getAwbBundles().stream().forEach(awbBundle -> {
                    if (awbBundle.dynamicFeature) {
                        TaskProvider<FeaturePackageApplication> provider = new TaskFactoryImpl(getProject().getTasks()).register(new FeaturePackageApplication.StandardCreationAction(awbBundle, variantContext, variantOutput));
                        provider.get().doFullTaskAction();

                    }
                });
                break;
        }


    }


    public static class CreationManifestsAction extends FeaturesBaseAction {


        public CreationManifestsAction(VariantContext variantContext, BaseVariantOutput baseVariantOutput) {

            super(variantContext, baseVariantOutput);
        }

        @Override
        public void configure(FeaturesParallelTask task) {
            super.configure(task);
            task.processType = ProcessType.MERGE_MANIFEST;

        }

        @NotNull
        @Override
        public String getName() {
            return scope.getTaskName("processFeatures", "Manifests");
        }


    }


    public static class CreationAssetsAction extends FeaturesBaseAction {


        public CreationAssetsAction(VariantContext variantContext, BaseVariantOutput baseVariantOutput) {

            super(variantContext, baseVariantOutput);
        }

        @Override
        public void configure(FeaturesParallelTask task) {
            super.configure(task);
            task.processType = ProcessType.MERGE_ASSETS;
            task.variantContext = variantContext;
            task.variantOutput = baseVariantOutput;

        }

        @NotNull
        @Override
        public String getName() {
            return scope.getTaskName("processFeatures", "Assets");
        }


    }


    public static abstract class FeaturesBaseAction extends MtlBaseTaskAction<FeaturesParallelTask> {

        public FeaturesBaseAction(VariantContext variantContext, BaseVariantOutput baseVariantOutput) {
            super(variantContext, baseVariantOutput);
        }

        @Override
        public void configure(FeaturesParallelTask task) {
            super.configure(task);
            task.variantContext = variantContext;
            task.variantOutput = baseVariantOutput;
        }

        @NotNull
        @Override
        public Class<FeaturesParallelTask> getType() {
            return FeaturesParallelTask.class;

        }


    }

    public static class MergeResourceAction extends FeaturesBaseAction {


        public MergeResourceAction(VariantContext variantContext, BaseVariantOutput baseVariantOutput) {

            super(variantContext, baseVariantOutput);
        }

        @Override
        public void configure(FeaturesParallelTask task) {
            super.configure(task);
            task.processType = ProcessType.MERGE_RESOURCE;

        }


        @NotNull
        @Override
        public String getName() {
            return scope.getTaskName("mergeFeatures", "Resource");
        }


    }


    public static class CreationBundleResourceAction extends FeaturesBaseAction {


        public CreationBundleResourceAction(VariantContext variantContext, BaseVariantOutput baseVariantOutput) {

            super(variantContext, baseVariantOutput);
        }

        @Override
        public void configure(FeaturesParallelTask task) {
            super.configure(task);
            task.processType = ProcessType.BUNDLE_RES;


        }

        @NotNull
        @Override
        public String getName() {
            return scope.getTaskName("processBundle", "resources");
        }



    }


    public static class CreationProcessResourceAction extends FeaturesBaseAction {

        public CreationProcessResourceAction(VariantContext variantContext, BaseVariantOutput baseVariantOutput) {
            super(variantContext, baseVariantOutput);
        }

        @Override
        public void configure(FeaturesParallelTask task) {
            super.configure(task);
            task.processType = ProcessType.PROCESS_RESOURCE;
        }

        @NotNull
        @Override
        public String getName() {
            return scope.getTaskName("processFeatures", "resources");
        }

    }


    public static class CreationFeatureCompileAction extends FeaturesBaseAction {

        public CreationFeatureCompileAction(VariantContext variantContext, BaseVariantOutput baseVariantOutput) {
            super(variantContext, baseVariantOutput);
        }

        @Override
        public void configure(FeaturesParallelTask task) {
            super.configure(task);
            task.processType = ProcessType.JAVAC;
        }

        @NotNull
        @Override
        public String getName() {
            return scope.getTaskName("compileFeature", "javac");
        }

    }


    public static class CreationPreBundleAction extends FeaturesBaseAction {

        public CreationPreBundleAction(VariantContext variantContext, BaseVariantOutput baseVariantOutput) {
            super(variantContext, baseVariantOutput);
        }

        @Override
        public void configure(FeaturesParallelTask task) {
            super.configure(task);
            task.processType = ProcessType.PRE_BUNDLE;
        }

        @NotNull
        @Override
        public String getName() {
            return scope.getTaskName("PreBundles", "");
        }

    }

    public static class CreationBundleDepsAction extends FeaturesBaseAction {

        public CreationBundleDepsAction(VariantContext variantContext, BaseVariantOutput baseVariantOutput) {
            super(variantContext, baseVariantOutput);
        }

        @Override
        public void configure(FeaturesParallelTask task) {
            super.configure(task);
            task.processType = ProcessType.COLLECT_DEP;
        }

        @NotNull
        @Override
        public String getName() {
            return scope.getTaskName("bundleDeps", "collect");
        }

    }

    public static class CreationFeatureApplicationAction extends FeaturesBaseAction {

        public CreationFeatureApplicationAction(VariantContext variantContext, BaseVariantOutput baseVariantOutput) {
            super(variantContext, baseVariantOutput);
        }

        @Override
        public void configure(FeaturesParallelTask task) {
            super.configure(task);
            task.processType = ProcessType.FEATURE_APPLICATION;
        }

        @NotNull
        @Override
        public String getName() {
            return scope.getTaskName("featuresApplication");
        }

    }


    enum ProcessType {

        COLLECT_DEP,
        MERGE_MANIFEST,
        PROCESS_RESOURCE,
        MERGE_ASSETS,
        JAVAC,
        DEX,
        MERGE_JAVA_RES,
        MEGGE_LIBS,
        BUNDLE_RES,
        MERGE_RESOURCE,
        PRE_BUNDLE,
        FEATURE_APPLICATION


        }


}
