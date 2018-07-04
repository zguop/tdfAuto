package com.zmsoft.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBList;
import com.intellij.util.containers.BidirectionalMap;
import com.zmsoft.utils.NotificationUtils;
import org.jdesktop.swingx.JXRadioGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * auth aboom
 * date 2018/5/24
 */
public class RadioList<T> extends JBList<JXRadioGroup> {
    private static final String[] strings = {"源码", "maven", "卸载"};
    private final RadioList<T>.CellRenderer myCellRenderer;
    private int index;
    private final BidirectionalMap<T, JXRadioGroup> myItemMap;
    private final Project project;

    public RadioList(Project project) {
        this.project = project;
        this.myItemMap = new BidirectionalMap<>();
        this.setModel(new DefaultListModel<>());
        this.myCellRenderer = new CellRenderer();
        this.setCellRenderer(myCellRenderer);
    }

    public void addItem(T item) {
        JXRadioGroup<String> jxRadioGroup = JXRadioGroup.create(strings);
        myItemMap.put(item, jxRadioGroup);
        ((DefaultListModel<JXRadioGroup>) this.getModel()).addElement(jxRadioGroup);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char keyChar = e.getKeyChar();
                NotificationUtils.info("a---------" + keyChar ,project);
            }
        });
    }


    private class CellRenderer implements ListCellRenderer<JXRadioGroup> {

        @Override
        public Component getListCellRendererComponent(JList<? extends JXRadioGroup> list, JXRadioGroup value, int index, boolean isSelected, boolean cellHasFocus) {
            NotificationUtils.info("--------------" + index + " isSelected = " + isSelected + "cellHasFocus " + cellHasFocus,project) ;
            Font font = RadioList.this.getFont();
            value.setEnabled(RadioList.this.isEnabled());
            value.setOpaque(true);
            value.setFocusable(true);
            AbstractButton selectedButton = value.getSelectedButton();
            return value;
        }
    }

}
