package com.zmsoft.utils;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ex.ProjectRootManagerEx;
import com.intellij.util.containers.hash.HashMap;
import com.intellij.util.ui.UIUtil;
import com.zmsoft.TDFConstants;
import com.zmsoft.handler.GradleTaskHandler;
import com.zmsoft.task.GradleTask;
import io.netty.util.internal.StringUtil;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaProject;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * auth aboom
 * date 2018/5/3
 */
public class PlatformUtils {

    /**
     * 判断平台是否是AndroidStudio
     */
    public static boolean isAndroidStudio() {
//        return "AndroidStudio".equals(getPlatformPrefix());
        return true;
    }

    /**
     * 涉及到Project修改的操作。在主线程执行某个耗时任务，需要调用invokeAndWaitIfNeeded
     * 注意，该方法执行的任务会阻塞主线程！
     */
    public static void executeProjectChanges(@NotNull Project project, @NotNull Runnable changes) {
        if (ApplicationManager.getApplication().isWriteAccessAllowed()) {
            if (!project.isDisposed()) {
                changes.run();
            }
            return;
        }
        UIUtil.invokeAndWaitIfNeeded((Runnable) () -> {
            //在UI主线程执行任务。执行耗时的任务
            //官方文档：http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/general_threading_rules.html
            //总结：文件修改，涉及project module的修改，等操作都需要用该API来run.
            ApplicationManager.getApplication().runWriteAction(() -> {
                if (!project.isDisposed()) {
                    ProjectRootManagerEx.getInstanceEx(project).mergeRootsChangesDuring(changes);
                }
            });
        });
    }

    /**
     * 在异步线程池中执行任务，真正的异步任务。不会阻塞主线程
     */
    public static void executeBackgroundTask(@NotNull Runnable runnable) {
        //如果是UI线程。则必须加入队列才能执行
        if (SwingUtilities.isEventDispatchThread()) {
            ApplicationManager.getApplication().executeOnPooledThread(runnable);
        } else {
            runnable.run();
        }
    }

    /**
     * 显示该project 的各个task 以及子module的各个task
     * 该代码为Gradle Tooling API的sample代码，可以用来获得一个project中的所有module.getModules()方法。
     */
    public static List<String> getProjectModules(Project project) {
        List<String> moduleNames = new ArrayList<>();
        String basePath = project.getBasePath();
        if (StringUtil.isNullOrEmpty(basePath)) {
            NotificationUtils.info("basePath 获取失败", project);
            return moduleNames;
        }
        GradleConnector connector = GradleConnector.newConnector().forProjectDirectory(new File(basePath));
        ProjectConnection connection = connector.connect();
        IdeaProject projectIdea = connection.getModel(IdeaProject.class);
        try {
            if (projectIdea != null) {
                for (IdeaModule module : projectIdea.getModules()) {
//                    String path = module.getGradleProject().getProjectDirectory().getAbsolutePath();
//                    NotificationUtils.info(path + "",project);
                    moduleNames.add(module.getName());
                }
            } else {
                NotificationUtils.info("connection.getModel(IdeaProject.class)出错，没有找到相应的IdeaProject", project);
            }
        } finally {
            connection.close();
        }
        return moduleNames;
    }

    /**
     * 判断project中是否包含某个task
     */
    public static boolean hasTaskInProject(ProjectConnection connection, String taskName) {
        if (connection == null || taskName == null) {
            return false;
        }
        GradleProject projectIdea = connection.getModel(GradleProject.class);
        if (projectIdea != null) {
            for (org.gradle.tooling.model.GradleTask task : projectIdea.getTasks()) {
                if (task.getName().equals(taskName)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 获取到所有模块后
     */
    public static void toProjectModulesGet(AnActionEvent anActionEvent, List<String> allModules) {
        Project project = anActionEvent.getProject();
        //去创建dialog 这里是所有勾选的module
        List<String> selectModules = UIUtils.selectModuleDialog(project, "选择加载的模块\r\n这里勾选的模块将以源码方式引入", allModules, false);
        if (selectModules.isEmpty()) {
            NotificationUtils.info("没有选择，任务取消", project);
            return;
        }
        List<String> inputModules = PlatformUtils.getInputModules().stream().filter(s -> !s.startsWith(TDFConstants.SYMBOL_1)).collect(Collectors.toList());
        if (selectModules.size() != inputModules.size()) {
            List<String> unModules = UIUtils.selectModuleDialog(project, "选择卸载的模块\r\n这里展示的是上一个弹框所没有勾选的模块，那么这里，不勾选，则卸载模块，勾选则以maven形式进行依赖", selectModules, true);
            selectModules.addAll(unModules);
        }
        String s1 = selectModules.stream()
                .reduce((s, s2) -> s + "-" + s2)
                .map(s -> "-P" + TDFConstants.REQUIRED_MODULES + "=" + s).get();
        List<String> arguments = Stream.of(s1)
                .collect(Collectors.toList());
        String taskName = TDFConstants.SELECT_MODULE_TASK;
        PlatformUtils.executeBackgroundTask(() -> new GradleTask(project, taskName, arguments, new GradleTaskHandler(taskName, anActionEvent)).queue());
    }


    /**
     * 根据路径，执行某个命令。既：File-->Sync...
     */
    public static void preformActionPath(AnActionEvent anActionEvent, String menuName, String... args) {
        if (StringUtil.isNullOrEmpty(menuName)) {
            return;
        }
        if (CollectionUtil.isEmpty(args)) {
            NotificationUtils.error("在id为" + menuName + "的菜单栏,paths为空。");
            return;
        }

        ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup actionGroup = (DefaultActionGroup) actionManager.getAction(menuName);
        if (Objects.isNull(actionGroup)) {
            NotificationUtils.error(menuName + "的菜单栏, 没有该窗口。");
            return;
        }
        AnAction[] anActions = actionGroup.getChildActionsOrStubs();

        if (CollectionUtil.isEmpty(anActions)) {
            NotificationUtils.error(menuName + "的菜单栏下没有任何窗口。");
            return;
        }

        Map<String, AnAction> anActionMap = new HashMap<>();
        for (AnAction anAction : anActions) {
            NotificationUtils.info(anAction.getTemplatePresentation().getText(), anActionEvent.getProject());
            anActionMap.put(anAction.getTemplatePresentation().getText(), anAction);
        }

        if (anActionMap.containsKey(args[0])) {
            AnAction anAction = anActionMap.get(args[0]);
            action(anAction, 1, args, anActionEvent);
        } else {
            throw new NullPointerException("没有找到为" + menuName + "的菜单栏");
        }
    }


    private static void action(AnAction anAction, int i, String[] args, AnActionEvent event) {
        AnAction[] anActions = actionGroup(anAction);
        if (CollectionUtil.isEmpty(anActions)) {
            anAction.actionPerformed(event);
            NotificationUtils.info("调用了 " + anAction.getTemplatePresentation().getText(), event.getProject());
            return;
        }

        NotificationUtils.info("-----------------------------", event.getProject());

        if (i < args.length) {
            for (AnAction action : anActions) {
                NotificationUtils.info(action.getTemplatePresentation().getText(), event.getProject());
                if (args[i].equals(action.getTemplatePresentation().getText())) {
                    action(action, i++, args, event);
                }
            }
        } else {
            NotificationUtils.error("在" + anAction.getTemplatePresentation().getText() + "的菜单栏下没有找到");
        }
    }


    private static AnAction[] actionGroup(AnAction anAction) {
        if (anAction instanceof DefaultActionGroup && anAction.getTemplatePresentation().isEnabledAndVisible()) {
            return ((DefaultActionGroup) anAction).getChildActionsOrStubs();
        }
        return null;
    }


    /**
     * 获取本地存储的key value ，string类型
     */
    public static String getData(String key) {
        return PropertiesComponent.getInstance().getValue(key, "");
    }

    /**
     * 存储key value数据，string类型
     */
    public static void setData(String key, String setValue) {
        PropertiesComponent.getInstance().setValue(key, setValue);
    }


    public static List<String> getInputModules() {
        List<String> list = new ArrayList<>();
        String inputModules = getData(TDFConstants.INPUT_MODULE);
        if (!StringUtil.isNullOrEmpty(inputModules)) {
            String[] split = inputModules.split("\n");
            list = Arrays.stream(split).map(String::trim).collect(Collectors.toList());
        }
        return list;
    }

}
