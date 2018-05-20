package com.zmsoft.task;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.zmsoft.utils.NotificationUtils;
import com.zmsoft.utils.PlatformUtils;
import org.gradle.tooling.ResultHandler;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * auth aboom
 * date 2018/5/4
 */
public class GetProjectModuleTask extends Task.Backgroundable {

    private Project project;
    private ResultHandler<List<String>> resultHandler;

    public GetProjectModuleTask(@Nullable Project project, @Nls @NotNull String title, boolean canBeCancelled, ResultHandler<List<String>> resultHandler) {
        super(project, title, canBeCancelled);
        this.project = project;
        this.resultHandler = resultHandler;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        List<String> allModules = PlatformUtils.getProjectModules(project);
        for (String allModule : allModules) {
            NotificationUtils.info(allModule, project);
        }
        if (!progressIndicator.isCanceled()) {
            progressIndicator.stop();
        }
        if (resultHandler != null) {
            if (!allModules.isEmpty()) {
                resultHandler.onComplete(allModules);
            } else {
                resultHandler.onFailure(null);
            }
        }
    }
}