package com.zmsoft.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.zmsoft.TDFConstants;
import com.zmsoft.utils.NotificationUtils;
import com.zmsoft.utils.PlatformUtils;

/**
 * auth aboom
 * date 2018/5/3
 */
public abstract class BaseAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        if (!PlatformUtils.isAndroidStudio()) {
            NotificationUtils.popError("请在Android Studio 平台下运行该插件(Please use this plugin on Android Studio.)", event);
            return;
        }
        //通过Presentation可以获取Action菜单项的各种属性，如显示的文本、描述、图标（Icon）等
        Presentation presentation = event.getPresentation();
        presentation.setEnabled(false);
        Project project = event.getProject();
        NotificationUtils.info(TDFConstants.PLUGIN_NAME + "开始执行任务", project);
        try {
            onActionPerform(event);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            NotificationUtils.popError("插件运行出错，具体错误信息e:" + throwable.getMessage() + "，请在Android Studio 命令行中执行该任务,查看详细信息", event);
        } finally {
            presentation.setEnabled(true);
        }
    }


    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        Presentation presentation = e.getPresentation();
        //project还没有初始化完毕 设置插件按钮的可见状态
        if (project == null || !project.isInitialized()) {
            presentation.setVisible(false);
        } else {
            presentation.setVisible(true);
        }
    }

    abstract void onActionPerform(AnActionEvent event) throws Throwable;

}
