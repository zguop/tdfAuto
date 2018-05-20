package com.zmsoft.handler;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.ui.UIUtil;
import com.zmsoft.utils.PlatformUtils;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.ResultHandler;

import java.util.List;

/**
 * auth aboom
 * date 2018/5/7
 */
public class SelectModuleHandler implements ResultHandler<List<String>> {

    private AnActionEvent anActionEvent;

    public SelectModuleHandler(AnActionEvent anActionEvent) {
        this.anActionEvent = anActionEvent;
    }

    @Override
    public void onComplete(List<String> strings) {
        strings.remove(0);
        UIUtil.invokeAndWaitIfNeeded((Runnable) () -> ApplicationManager.getApplication().invokeLater(() -> PlatformUtils.toProjectModulesGet(anActionEvent, strings)));
    }

    @Override
    public void onFailure(GradleConnectionException e) {
        throw new NullPointerException("无法找到工程中的module");
    }
}
