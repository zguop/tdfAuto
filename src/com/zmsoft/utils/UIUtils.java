package com.zmsoft.utils;

import com.intellij.openapi.externalSystem.util.ExternalSystemUiUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.ui.CheckBoxList;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.zmsoft.TDFConstants;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * auth aboom
 * date 2018/5/4
 */
public class UIUtils {

    /**
     * 选择模块弹框
     *
     * @param modules 勾选的模块
     */
    public static List<String> selectModuleDialog(Project project, String title, List<String> modules, boolean isUnModule) {
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
            //如果是提示性文字，那么修改过提示描述
            if (inputModule.startsWith(TDFConstants.SYMBOL_1)) {
                inputModule = TDFConstants.SYMBOL_2 + inputModule.replace(TDFConstants.SYMBOL_1, "") + TDFConstants.SYMBOL_2;
            }
            //如果是卸载模块
            if (isUnModule) {
                if (!modules.contains(inputModule)) {
                    map.put(inputModule, !inputModule.startsWith(TDFConstants.SYMBOL_2));
                }
            } else {
                map.put(inputModule, modules.contains(inputModule));
            }
        }
        orphanModulesList.setStringItems(map);
        content.add(orphanModulesList, ExternalSystemUiUtil.getFillLineConstraints(0));
        content.setBorder(IdeBorderFactory.createEmptyBorder(JBUI.insets(5, 5, 5, 5)));
        DialogWrapper dialogWrapper = createDialogWrapper(project, content);

        orphanModulesList.setCheckBoxListListener((i, b) -> {
            if (b) {
                String itemAt = orphanModulesList.getItemAt(i);
                if (itemAt != null && itemAt.startsWith(TDFConstants.SYMBOL_2)) {
                    orphanModulesList.setItemSelected(itemAt, false);
                    NotificationUtils.toast(content, MessageType.ERROR, "提示性信息，不可以勾选");
                }
            }
        });

        boolean b = dialogWrapper.showAndGet();

        if (!b) {
            return selectModules;
        }

        if (isUnModule) {
            map.forEach((s, aBoolean) -> {
                if (!s.startsWith(TDFConstants.SYMBOL_2) && !orphanModulesList.isItemSelected(s)) {
                    selectModules.add(TDFConstants.UN_MODULE + s);
                }
            });
        } else {
            for (String module : inputModules) {
                if (orphanModulesList.isItemSelected(module)) {
                    selectModules.add(module);
                }
            }
        }
        return selectModules;
    }

//    /**
//     * 选择卸载的模块弹窗
//     */
//    public static List<String> uninstallModule(Project project, String title, List<String> selectModules) {
//        List<String> unModules = new ArrayList<>();
//        final JPanel content = new JPanel(new GridBagLayout());
//        content.add(new JLabel(title), ExternalSystemUiUtil.getFillLineConstraints(0));
//        List<String> inputModules = PlatformUtils.getInputModules();
//
//        Map<String, Boolean> map = new LinkedHashMap<>();
//        for (String inputModule : inputModules) {
//            if (inputModule.startsWith(TDFConstants.SYMBOL_1)) {
//                inputModule = TDFConstants.SYMBOL_2 + inputModule.replace(TDFConstants.SYMBOL_1, "") + TDFConstants.SYMBOL_2;
//            }
//            if (!selectModules.contains(inputModule)) {
//                map.put(inputModule, !inputModule.startsWith(TDFConstants.SYMBOL_2));
//            }
//        }
//        final CheckBoxList<String> orphanModulesList = new CheckBoxList<>();
//        orphanModulesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//        orphanModulesList.setStringItems(map);
//        content.add(orphanModulesList, ExternalSystemUiUtil.getFillLineConstraints(0));
//        content.setBorder(IdeBorderFactory.createEmptyBorder(JBUI.insets(5, 5, 5, 5)));
//        DialogWrapper dialogWrapper = createDialogWrapper(project, content);
//
//        boolean b = dialogWrapper.showAndGet();
//        //默认是所有仓库都加载，
//        if (!b) {
//            return unModules;
//        }
//        for (String noSelectModule : noSelectModules) {
//            if (!orphanModulesList.isItemSelected(noSelectModule)) {
//                unModules.add(noSelectModule + TDFConstants.UN_MODULE);
//            }
//        }
//        return unModules;
//    }

    private static DialogWrapper createDialogWrapper(Project project, Component content) {
        return new DialogWrapper(project) {
            {
                setTitle(TDFConstants.PLUGIN_NAME);
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
    }


    public static List<String> selectModuleDialogtest(Project project, String title, List<String> allsModules) {
        List<String> selectModules = new ArrayList<>();
        final JPanel content = new JPanel(new GridBagLayout());

//        final String[] strings = {"源码", "maven", "卸载"};
//
//        for (String s : allsModules) {
//            JPanel jPanel = new JPanel();
//            JLabel jLabel = new JLabel(s);
//            jLabel.setmar
//            JXRadioGroup<String> stringJXRadioGroup = JXRadioGroup.create(strings);
//            jPanel.add(stringJXRadioGroup);
//            jPanel.add(jLabel);
//            content.add(jPanel);
//        }


        DialogWrapper dialogWrapper = new DialogWrapper(project) {
            {
                setTitle(title);
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


        return selectModules;
    }
}
