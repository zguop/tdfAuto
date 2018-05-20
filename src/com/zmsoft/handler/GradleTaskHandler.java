package com.zmsoft.handler;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.zmsoft.TDFConstants;
import com.zmsoft.utils.NotificationUtils;
import com.zmsoft.utils.PlatformUtils;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.ResultHandler;

/**
 * auth aboom
 * date 2018/5/7
 */
public class GradleTaskHandler implements ResultHandler {

    private String taskName;
    private AnActionEvent event;

    public GradleTaskHandler(String taskName, AnActionEvent event) {
        this.taskName = taskName;
        this.event = event;
    }

    @Override
    public void onComplete(Object o) {
        NotificationUtils.info("Gradle任务\"" + taskName + "\"执行完毕，开始sync整工程", AnAction.getEventProject(event));
        PlatformUtils.preformActionPath(event, TDFConstants.ANDROID_MAIN_TOOLBAR_GRADLEGROUP, TDFConstants.ACTION_ANDROID_SYNC);
    }

    @Override
    public void onFailure(GradleConnectionException e) {
        NotificationUtils.error("执行Gradle任务\"" + taskName + "\"时：e:" + e.getMessage());
    }
}
