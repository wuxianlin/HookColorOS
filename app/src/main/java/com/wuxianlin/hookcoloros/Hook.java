package com.wuxianlin.hookcoloros;

import android.os.Build;

import com.wuxianlin.hookcoloros.hooks.*;

import java.io.File;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by wuxianlin on 2016/3/27.
 */
public class Hook implements IXposedHookZygoteInit, IXposedHookInitPackageResources,
        IXposedHookLoadPackage {
    int colorOsVersion = ColorOSUtils.getColorOSVersion();

    public static XSharedPreferences prefs;
    public static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    public static String MODULE_PATH = null;
    private static File prefsFileProt = new File("/data/user_de/" +
            HookUtils.getMyUserId() +
            "/" + PACKAGE_NAME + "/shared_prefs/" + PACKAGE_NAME + "_preferences.xml");

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && prefsFileProt.canRead()) {
            prefs = new XSharedPreferences(prefsFileProt);
        } else {
            prefs = new XSharedPreferences(PACKAGE_NAME);
        }
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        prefs.reload();
        if ("android".equals(lpparam.packageName)) {
            Pms.hookAndroid(lpparam, colorOsVersion, prefs);
            Feature.hookOplusFeature(lpparam, colorOsVersion, prefs);
            MultiApp.hookAndroid(lpparam, colorOsVersion, prefs);
            Feature.hookPms(lpparam, colorOsVersion, prefs);
        } else if ("com.android.launcher".equals(lpparam.packageName) ||
                "com.oppo.launcher".equals(lpparam.packageName)) {
            Launcher.hookLauncher(lpparam, colorOsVersion, prefs);
        } else if("com.android.packageinstaller".equals(lpparam.packageName)){
            PackageInstaller.hookPackageInstaller(lpparam, colorOsVersion, prefs);
        } else if("com.android.systemui".equals(lpparam.packageName)){
            SystemUI.hookVolume(lpparam, colorOsVersion, prefs);
        } else if("com.android.settings".equals(lpparam.packageName)) {
            Settings.hookDsu(lpparam, colorOsVersion, prefs);
        } else if("com.oplus.customize.coreapp".equals(lpparam.packageName)){
            if(colorOsVersion<ColorOSUtils.OplusOS_13_0)
                Feature.hookAppFeature(lpparam, colorOsVersion, prefs);
        } else if("com.oplus.appplatform".equals(lpparam.packageName)){
            if(colorOsVersion>=ColorOSUtils.OplusOS_13_0)
                Feature.hookAppFeature(lpparam, colorOsVersion, prefs);
        } else if("com.heytap.appplatform".equals(lpparam.packageName)){
            Feature.hookAppFeature(lpparam, colorOsVersion, prefs);
        } else if("com.oplus.notificationmanager".equals(lpparam.packageName)){
            NotificationManager.hookVolume(lpparam, colorOsVersion, prefs);
        }
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        prefs.reload();
        if ("com.oplus.safecenter.config_plugin".equals(resparam.packageName)) {
            resparam.res.setReplacement("com.oplus.safecenter.config_plugin", "integer",
                    "auto_start_max_allow_count", 1000);
        }
    }

}
