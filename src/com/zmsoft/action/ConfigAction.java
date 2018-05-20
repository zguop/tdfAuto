package com.zmsoft.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.zmsoft.ui.ModuleInputDialog;

/**
 * auth aboom
 * date 2018/5/16
 */
public class ConfigAction extends BaseAction {

    @Override
    void onActionPerform(AnActionEvent event) {
        Project project = event.getProject();
        new ModuleInputDialog(project).setVisible(true);
    }
}
