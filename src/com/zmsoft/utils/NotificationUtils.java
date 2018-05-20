package com.zmsoft.utils;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import io.netty.util.internal.StringUtil;

import javax.swing.*;

/**
 * auth aboom
 * date 2018/5/3
 */
public class NotificationUtils {

    private static final Logger sPLUGIN_LOGGER = Logger.getInstance(NotificationUtils.class);

    private static final NotificationGroup GROUP_DISPLAY_ID_INFO_BALLOON =
            new NotificationGroup("com.2Dfire.tdf",
                    NotificationDisplayType.BALLOON, true);

    private static final NotificationGroup LOGGING_NOTIFICATION = new NotificationGroup("Gradle sync", NotificationDisplayType.NONE, true);

    /**
     * IntelliJ自带的API输出的info，android studio 会显示在底部statusbar下面。
     */
    public static void infoToStatusBar(String info) {
        sPLUGIN_LOGGER.info(info);
    }

    /**
     * 显示在gradle 的consol中的info
     */
    public static void info(String infoMsg, Project project) {
        if (StringUtil.isNullOrEmpty(infoMsg) || project == null) {
            return;
        }
        LOGGING_NOTIFICATION.createNotification(infoMsg, MessageType.INFO).notify(project);
    }

    public static void toast(JComponent jComponent, MessageType type, String text) {
        if (StringUtil.isNullOrEmpty(text)) {
            return;
        }
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(text, type, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(jComponent), Balloon.Position.above);
    }

    /**
     * 严重的错误，需要弹窗提醒。
     */
    public static void popError(String errorMsg, AnActionEvent event) {
        if (StringUtil.isNullOrEmpty(errorMsg)) {
            return;
        }
        StatusBar statusBar = WindowManager.getInstance()
                .getStatusBar(DataKeys.PROJECT.getData(event.getDataContext()));
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(errorMsg, MessageType.ERROR, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()),
                        Balloon.Position.atRight);
    }

    /**
     * 冒气泡显示错误信息
     */
    public static void error(String errorMsg) {
        if (StringUtil.isNullOrEmpty(errorMsg)) {
            return;
        }
        com.intellij.notification.Notification notificationX = NotificationUtils.GROUP_DISPLAY_ID_INFO_BALLOON.createNotification(errorMsg, NotificationType.ERROR);
        Notifications.Bus.notify(notificationX);
    }

}
