package com.zmsoft.utils;

import com.intellij.openapi.externalSystem.util.ExternalSystemUiUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.CheckBoxList;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * auth aboom
 * date 2018/5/4
 */
public class UIUtils {

    public static List<String> selectModuleDialog(Project project, String title, List<String> allsModules) {
        List<String> selectModules = new ArrayList<>();
        final JPanel content = new JPanel(new GridBagLayout());
        content.add(new JLabel(title), ExternalSystemUiUtil.getFillLineConstraints(0));
        final CheckBoxList<String> orphanModulesList = new CheckBoxList<>();
        orphanModulesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        //获取到项目配置的模块
        List<String> inputModules = PlatformUtils.getInputModules();
        //组装map，默认勾选扫到的项目模块
        Map<String, Boolean> map = new LinkedHashMap<>();
        for (String inputModule : inputModules) {
            map.put(inputModule, allsModules.contains(inputModule));
        }
        orphanModulesList.setStringItems(map);
        content.add(orphanModulesList, ExternalSystemUiUtil.getFillLineConstraints(0));
        DialogWrapper dialogWrapper = new DialogWrapper(project) {
            {
                init();
            }

            @Override
            protected JComponent createCenterPanel() {
                return new JBScrollPane(content);
            }

            /**
             * 只留下ok的按钮
             */
            @NotNull
            @Override
            protected Action[] createActions() {
                return new Action[]{getOKAction()};
            }

        };
        boolean b = dialogWrapper.showAndGet();

        if (!b) {
            return selectModules;
        }

        for (String module : inputModules) {
            if (orphanModulesList.isItemSelected(module)) {
                selectModules.add(module);
            }
        }
        return selectModules;
    }

}
