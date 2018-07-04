package com.zmsoft.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.zmsoft.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class Tan extends AnAction {

    private int limitSec = 10;
    private Timer timer;


    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
//        NotificationUtils.error("干嘛点我，十秒内将关闭你电脑！！！");
//        PlatformUtils.executeBackgroundTask(() -> {
//            try {
//
//                if (timer == null) {
//                    timer = new Timer();
//                }
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        if (limitSec <= 0) {
//                            timer.cancel();
//                            timer = null;
//                            limitSec = 10;
//                            NotificationUtils.error("骗你的！");
//                            return;
//                        }
//                        NotificationUtils.error("剩余" + (--limitSec));
//                    }
//                }, 1000, 1000);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });


        Project project = anActionEvent.getProject();

//
        List<String> allModules = new ArrayList<>();
        allModules.add("111");
        allModules.add("222");
        allModules.add("333");
//
//        new ModuleSelectDialog(project).showAndGet();


        UIUtils.selectModuleDialogtest(project,"选择模块",allModules);



    }


}
