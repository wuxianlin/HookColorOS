package com.wuxianlin.hookcoloros.hooks;

import android.content.Context;
import android.view.View;

import com.wuxianlin.hookcoloros.ColorOSUtils;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Launcher {

    public static void hookLauncher(final XC_LoadPackage.LoadPackageParam lpparam,
                                    int colorOsVersion, XSharedPreferences prefs) {
        if (prefs.getBoolean("hook_launcher_app_limit", true)) {
            try{
                hookLauncherAppLimit(lpparam, colorOsVersion);
            } catch (Throwable t){
                XposedBridge.log(t);
            }
        }
        if (colorOsVersion < ColorOSUtils.OS_15_0_0){
            return;
        }
        if (prefs.getBoolean("hook_memory_info", true)){
            try{
                hookMemoryInfo(lpparam);
            } catch (Throwable t){
                XposedBridge.log(t);
            }
        }
        if (prefs.getBoolean("hook_recents_auto_focus_to_next_page", true)){
            try{
                hookRecentsAutoFocusToNextPage(lpparam);
            } catch (Throwable t){
                XposedBridge.log(t);
            }
        }
        if (prefs.getBoolean("hook_home_auto_close_folder", true)){
            try{
                hookHomeAutoCloseFolder(lpparam);
            } catch (Throwable t){
                XposedBridge.log(t);
            }
        }
    }

    private static void hookLauncherAppLimit(final XC_LoadPackage.LoadPackageParam lpparam, int colorOsVersion) {
        XposedHelpers.findAndHookMethod("android.app.SharedPreferencesImpl",
                lpparam.classLoader, "getInt", String.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        File mFile = (File) XposedHelpers.getObjectField(param.thisObject, "mFile");
                        if (!mFile.getName().equals("Configuration.xml")) return;
                        String key = (String) param.args[0];
                        if (key.equals("lock_app_limit"))
                            param.setResult(1000);
                    }
                });
        if (colorOsVersion < ColorOSUtils.OS_15_0_0){
            return;
        }
        /*XposedHelpers.findAndHookMethod("com.oplus.quickstep.applock.OplusLockManager$Companion",
                lpparam.classLoader, "getLockAppLimitForAppFeature",
                Context.class, String.class, int.class, XC_MethodReplacement.returnConstant(1000));*/
    }

    private static void hookMemoryInfo(final XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("com.oplus.quickstep.memory.MemoryInfoManager",
                lpparam.classLoader, "judgeWhetherAllowMemoDisplay", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedHelpers.setBooleanField(param.thisObject, "mAllowMemoryInfoDisplay", true);
                        XposedHelpers.callMethod(param.thisObject, "saveAllowMemoryInfoDisplay", true);
                        param.setResult(null);
                    }
                });
    }

    private static void hookRecentsAutoFocusToNextPage(final XC_LoadPackage.LoadPackageParam lpparam) {
        XposedBridge.hookAllMethods(
                XposedHelpers.findClass("com.android.common.util.AppFeatureUtils", lpparam.classLoader),
                "isSupportAutoFocusToNextPageInOverviewState", XC_MethodReplacement.returnConstant(false));
    }

    private static void hookHomeAutoCloseFolder(final XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> AbstractFloatingView = XposedHelpers.findClass("com.android.launcher3.AbstractFloatingView", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(AbstractFloatingView, "closeOpenViews",
                "com.android.launcher3.views.ActivityContext", boolean.class, int.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Object activity = param.args[0];
                boolean animate = (boolean) param.args[1];
                int type = (int) param.args[2];
                if (activity == null) {
                    return;
                }
                Object dragLayer = XposedHelpers.callMethod(activity, "getDragLayer");
                if (dragLayer == null) {
                    return;
                }
                int TYPE_FOLDER = XposedHelpers.getStaticIntField(AbstractFloatingView, "TYPE_FOLDER");
                if ((type & TYPE_FOLDER) == 0) {
                    return;
                }
                int childCount = (int) XposedHelpers.callMethod(dragLayer, "getChildCount");
                for (int i = childCount - 1; i >= 0; i--) {
                    View child = (View) XposedHelpers.callMethod(dragLayer, "getChildAt", i);
                    if (AbstractFloatingView.isInstance(child)) {
                        if ((boolean) XposedHelpers.findMethodExact(AbstractFloatingView, "isOfType", int.class).invoke(child, TYPE_FOLDER)) {
                        //if ((boolean) XposedHelpers.findMethodExact(AbstractFloatingView, "isOfType", int.class).invoke(child, type)) {
                            XposedHelpers.findMethodExact(AbstractFloatingView, "close", boolean.class).invoke(child, animate);
                        }
                    }
                }
            }
        });
    }
}
