package com.wuxianlin.hookcoloros.hooks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.wuxianlin.hookcoloros.ColorOSUtils;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Settings {
    public static void hookDsu(final XC_LoadPackage.LoadPackageParam lpparam,
                               int colorOsVersion, XSharedPreferences prefs){
        if (colorOsVersion < ColorOSUtils.OS_13_0)
            return;
        if (!prefs.getBoolean("hook_dsu", true))
            return;
        XposedHelpers.findAndHookMethod("com.android.settings.development.DevelopmentSettingsDashboardFragment", lpparam.classLoader,
                "buildPreferenceControllers",
                Context.class, Activity.class,
                "com.android.settingslib.core.lifecycle.Lifecycle",
                "com.android.settings.development.DevelopmentSettingsDashboardFragment",
                "com.android.settings.development.BluetoothA2dpConfigStore", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                XposedBridge.log("buildPreferenceControllers");
                Context context = (Context) param.args[0];
                List<Object> controllers = (List) param.getResult();
                Object controller = XposedHelpers.newInstance(XposedHelpers.findClass("com.android.settings.development.SelectDSUPreferenceController", lpparam.classLoader), context);
                controllers.add(controller);
                param.setResult(controllers);
            }
        });
        /*XposedHelpers.findAndHookMethod("com.android.settings.development.DevelopmentSettingsDashboardFragment", lpparam.classLoader,
                "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                Object preferenceScreen = XposedHelpers.callMethod(param.thisObject, "getPreferenceScreen");
                if (preferenceScreen == null) {
                    XposedBridge.log("preferenceScreen null");
                    return;
                }
                Object preferenceCategory = XposedHelpers.callMethod(preferenceScreen, "findPreference", "dev_top");
                if (preferenceCategory == null) {
                    XposedBridge.log("preferenceCategory null");
                    return;
                }
                Object preference = XposedHelpers.findConstructorExact(XposedHelpers.findClass("com.coui.appcompat.preference.COUIPreference", lpparam.classLoader), Context.class).newInstance(XposedHelpers.callMethod(param.thisObject, "getActivity"));
                XposedHelpers.callMethod(preference, "setKey", "dsu_loader");
                XposedHelpers.callMethod(preference, "setOrder", 45);
                XposedHelpers.callMethod(preference, "setTitle", "DSU Loader");
                XposedHelpers.callMethod(preferenceCategory, "addPreference", preference);
            }
        });*/
        XposedHelpers.findAndHookMethod("com.oplus.settings.feature.othersettings.development.OplusDevelopmentSettingsDashboardFragment", lpparam.classLoader,
                "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                Object preferenceCategory = XposedHelpers.getObjectField(param.thisObject, "mTopCategory");
                Object mActivity = XposedHelpers.getObjectField(param.thisObject, "mActivity");
                if (preferenceCategory == null) {
                    XposedBridge.log("preferenceCategory null");
                    return;
                }
                Object preference = XposedHelpers.findConstructorExact(XposedHelpers.findClass("com.coui.appcompat.preference.COUIPreference", lpparam.classLoader), Context.class).newInstance(mActivity);
                XposedHelpers.callMethod(preference, "setKey", "dsu_loader");
                XposedHelpers.callMethod(preference, "setOrder", 45);
                XposedHelpers.callMethod(preference, "setTitle", "DSU Loader");
                XposedHelpers.callMethod(preferenceCategory, "addPreference", preference);
            }
        });
    }

    public static void hookGms(final XC_LoadPackage.LoadPackageParam lpparam,
                               int colorOsVersion, XSharedPreferences prefs){
        if (prefs.getBoolean("hook_remove_cn_gms",true) && prefs.getBoolean("hook_keep_gms_switch",true)) {
            XposedHelpers.findAndHookMethod("com.oplus.settings.utils.SettingsUtils", lpparam.classLoader,
                    "isGoogleAccountSupport", XC_MethodReplacement.returnConstant(true));
        }
    }
}
