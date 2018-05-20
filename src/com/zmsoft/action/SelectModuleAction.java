package com.zmsoft.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.zmsoft.TDFConstants;
import com.zmsoft.handler.SelectModuleHandler;
import com.zmsoft.task.GetProjectModuleTask;
import com.zmsoft.ui.ModuleInputDialog;
import com.zmsoft.utils.NotificationUtils;
import com.zmsoft.utils.PlatformUtils;
import io.netty.util.internal.StringUtil;

/**
 * auth aboom
 * date 2018/5/3
 */
public class SelectModuleAction extends BaseAction {

    @Override
    void onActionPerform(AnActionEvent event) {
        //展示所有模块提供选择
        Project project = event.getProject();
        String inputModule = PlatformUtils.getData(TDFConstants.INPUT_MODULE, null);
        if (StringUtil.isNullOrEmpty(inputModule)) {
            NotificationUtils.error("未配置项目模块");
            new ModuleInputDialog(project).setVisible(true);
            return;
        }
        new GetProjectModuleTask(project,
                "正在获取模块",
                false,
                new SelectModuleHandler(event))
                .queue();
    }
}
