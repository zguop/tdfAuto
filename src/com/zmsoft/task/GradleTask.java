package com.zmsoft.task;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.zmsoft.utils.NotificationUtils;
import com.zmsoft.utils.PlatformUtils;
import io.netty.util.internal.StringUtil;
import org.gradle.tooling.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * auth aboom
 * date 2018/5/4
 */
public class GradleTask extends Task.Backgroundable {

    private Project project;
    private String taskName;
    private List<String> arguments;
    private ResultHandler resultHandler;

    public GradleTask(@Nullable Project project, @Nls @NotNull String title, List<String> arguments, ResultHandler resultHandler) {
        super(project, title, false);
        this.project = project;
        this.taskName = title;
        this.arguments = arguments;
        this.resultHandler = resultHandler;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        runGradleTask(project, taskName, arguments);
        if (!progressIndicator.isCanceled()) {
            progressIndicator.stop();
        }
    }

    private void runGradleTask(Project project, String taskName, List<String> arguments) {
        //使用安装目录下的gradle运行task。
        String basePath = project.getBasePath();
        if (StringUtil.isNullOrEmpty(basePath)) {
            NotificationUtils.info("basePath 获取失败", project);
            return;
        }
        ProjectConnection connection = GradleConnector.newConnector()
                .forProjectDirectory(new File(basePath))
                .connect();
        try {
            if (!PlatformUtils.hasTaskInProject(connection, taskName)) {
                NotificationUtils.error("project:" + project.getName() + "中没有找到相应的task:" + taskName);
                return;
            }
            //将Gradle嵌入到工程中，并提供API让用户实现gradle的功能，包括执行并监控gradle的构建，查询构建的详细信息等
            BuildLauncher buildLauncher = connection.newBuild();
            buildLauncher.forTasks(taskName);
            List<String> buildArgs = new ArrayList<>();
            buildArgs.add("--parallel");
            buildArgs.add("--max-workers=8");
            buildArgs.add("--configure-on-demand");
            buildArgs.add("--offline");
            if (!arguments.isEmpty()) {
                buildArgs.addAll(arguments);
            }
            NotificationUtils.info("执行语句：" + Arrays.toString(buildArgs.toArray(new String[]{})), project);
            buildLauncher.withArguments(buildArgs.toArray(new String[]{}));
            //设置任务输出流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            buildLauncher.setStandardOutput(outputStream);
            buildLauncher.setStandardError(System.err);
            buildLauncher.addProgressListener((ProgressListener) progressEvent -> {
                String outResult = outputStream.toString();
                if (!StringUtil.isNullOrEmpty(outResult)) {
                    NotificationUtils.info(outResult, project);
                    outputStream.reset();
                }
            });
            buildLauncher.run(resultHandler);
        } catch (Exception e) {
            NotificationUtils.info("错误信息---：" + e.getMessage(), project);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}

