package com.wuxianlin.hookcoloros.hooks;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.ArrayMap;

import com.wuxianlin.hookcoloros.ColorOSUtils;
import com.wuxianlin.hookcoloros.HookUtils;

import java.io.File;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Feature {

    public static void hookPms(final XC_LoadPackage.LoadPackageParam lpparam,
                               int colorOsVersion, XSharedPreferences prefs){
        if (colorOsVersion >= ColorOSUtils.OplusOS_11_0)
            return;
        XposedHelpers.findAndHookMethod("com.android.server.pm.PackageManagerService",
                lpparam.classLoader, "hasSystemFeature",
                String.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String name=(String)param.args[0];
                if(name.equals("oppo.systemui.highlight.nodeveloper")
                        ||name.equals("oppo.settings.verification.dialog.disallow")
                        ||name.equals("oppo.settings.account.dialog.disallow")
                        /*||name.equals("oppo.systemui.notdisadblenotification.dm")*/)
                    param.setResult(true);
                else if("oppo.common_center.lock.simcard".equals(name) && (Build.MODEL.endsWith("t")||Build.MODEL.endsWith("T00")||Build.MODEL.endsWith("T10")||Build.MODEL.endsWith("T20")))
                    param.setResult(false);
            }
        });
    }

    public static void hookSystemConfig(final XC_LoadPackage.LoadPackageParam lpparam,
                                        int colorOsVersion, XSharedPreferences prefs) {
        XposedHelpers.findAndHookMethod("com.android.server.SystemConfig",
                lpparam.classLoader, "getAvailableFeatures", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ArrayMap<String, Object> res = (ArrayMap<String, Object>)param.getResult();
                if (prefs.getBoolean("hook_remove_cn_gms", true)) {
                    res.remove("cn.google.services");
                    res.remove("com.google.android.feature.services_updater");
                }
            }
        });
    }

    public static void hookOplusFeature(final XC_LoadPackage.LoadPackageParam lpparam,
                                    int colorOsVersion, XSharedPreferences prefs) {
        if(Build.VERSION.SDK_INT < 30)
            return;
        XposedHelpers.findAndHookMethod("com.android.server.content.OplusFeatureConfigManagerService",
                lpparam.classLoader, "hasFeatureMap", String.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if("oplus.software.startup_strategy_restrict".equals(param.args[0])) {
                            param.setResult(false);
                        }
                        if("oplus.software.radio.networkless_support".equals(param.args[0])){
                            param.setResult(true);
                        }
                        //XposedBridge.log("hasFeatureMap:"+param.args[0]);
                    }
                });
        /*XposedHelpers.findAndHookMethod("com.android.server.content.OplusFeatureConfigManagerService", lpparam.classLoader, "systemReady", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.callMethod(param.thisObject,"availableFeature","oplus.software.radio.networkless_support", 9);
                XposedHelpers.callMethod(param.thisObject,"unavailableFeature","oplus.software.startup_strategy_restrict", 9);
                XposedHelpers.callMethod(param.thisObject,"filterAvailableFeatures");
                XposedHelpers.callMethod(param.thisObject,"updateFeaturesMapForStatic");
                XposedHelpers.callMethod(param.thisObject,"updateTotalFeaturesMap");
            }
        });*/
    }

    private static void insertFeature(SQLiteDatabase sqLiteDatabase){
        for(String feature:new String[]{"com.android.systemui.highlight_nodeveloper",
                "com.android.settings.account_dialog.disable",
                "com.android.settings.verification_dialog.disable",
                "com.android.systemui.otg_auto_close_alarm_disable",
                "com.android.settings.need_show_2g3g"}) {
            ContentValues values = new ContentValues();
            values.put("featurename", feature);
            //values.put("parameters", "boolean=true");
            long num = sqLiteDatabase.insert("app_feature",null, values);
            //XposedBridge.log(feature+","+num);
        }
    }

    public static void hookAppFeature(final XC_LoadPackage.LoadPackageParam lpparam,
                                          int colorOsVersion, XSharedPreferences prefs){
        XposedHelpers.findAndHookMethod("android.database.sqlite.SQLiteDatabase",
                lpparam.classLoader, "delete", String.class, String.class, String[].class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        //XposedBridge.log("delete");
                        //XposedBridge.log(param.args[0].toString());
                        if(!"app_feature".equals(param.args[0]))
                            return;
                        SQLiteDatabase sqLiteDatabase = (SQLiteDatabase)param.thisObject;
                        //XposedBridge.log(sqLiteDatabase.getPath());
                        if(!sqLiteDatabase.getPath().contains("config_feature.db"))
                            return;
                        insertFeature(sqLiteDatabase);
                    }
                });
        XposedHelpers.findAndHookMethod("com.oplus.customize.appfeature.configprovider.AppFeatureProvider",
                lpparam.classLoader, "onCreate", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        //XposedBridge.log("onCreate");
                        SQLiteOpenHelper mConfigDbOpenHelper = (SQLiteOpenHelper)XposedHelpers
                                .findFirstFieldByExactType(param.thisObject.getClass(),
                                        XposedHelpers.findClass(
                                                "com.oplus.customize.appfeature.configprovider.ConfigDbOpenHelper",
                                                lpparam.classLoader)).get(param.thisObject);
                        insertFeature(mConfigDbOpenHelper.getWritableDatabase());
                    }
                });
    }

}
