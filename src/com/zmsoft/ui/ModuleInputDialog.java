package com.zmsoft.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.zmsoft.TDFConstants;
import com.zmsoft.utils.NotificationUtils;
import com.zmsoft.utils.PlatformUtils;
import io.netty.util.internal.StringUtil;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ModuleInputDialog extends JFrame {

    private JPanel contentPane;
    private JButton okButton;
    private JTextPane editTP;
    private JButton cancelButton;
    private Project project;

    public ModuleInputDialog(Project project) throws HeadlessException {
        this.project = project;
        setContentPane(contentPane);
        setTitle("配置项目模块");
        getRootPane().setDefaultButton(okButton);
        setAlwaysOnTop(true);
        init();
        initListener();
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    private void init() {
        String info = PlatformUtils.getData(TDFConstants.INPUT_MODULE, null);
        if (StringUtil.isNullOrEmpty(info)) {
            return;
        }
        editTP.setText(info);
    }

    private void initListener() {
        cancelButton.addActionListener(e -> dispose());
        okButton.addActionListener(e -> submit());
        contentPane.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void submit() {
        this.setAlwaysOnTop(false);
        String moduleInput = editTP.getText().trim();
        if (TextUtils.isEmpty(moduleInput)) {
            NotificationUtils.toast(contentPane, MessageType.ERROR, "你什么都没输入，就不要点ok啊");
            return;
        }
        PlatformUtils.setData(TDFConstants.INPUT_MODULE, moduleInput);
        NotificationUtils.info("配置完成", project);
        dispose();
    }
}
