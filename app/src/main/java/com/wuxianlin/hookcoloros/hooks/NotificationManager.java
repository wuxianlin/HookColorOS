package com.wuxianlin.hookcoloros.hooks;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class NotificationManager {
    public static void hookVolume(final XC_LoadPackage.LoadPackageParam lpparam,
                                  int colorOsVersion, XSharedPreferences prefs) {
        if (!prefs.getBoolean("hook_systemui_volume", true))
            return;
        try {
            XposedHelpers.findAndHookMethod("com.oplus.notificationmanager.config.BaseFeatureOption",
                    lpparam.classLoader, "isSupportVolumeSeekBar", XC_MethodReplacement.returnConstant(false));
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }
}
