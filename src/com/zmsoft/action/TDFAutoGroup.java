package com.zmsoft.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;

public class TDFAutoGroup extends DefaultActionGroup {

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        if(project == null){
            return;
        }
        e.getPresentation().setEnabled(project.isInitialized());
    }
}
