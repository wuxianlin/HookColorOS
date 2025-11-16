package com.wuxianlin.hookcoloros;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * https://github.com/LSPosed/LSPosed/wiki/New-XSharedPreferences
 */
public class LSPosedUtils {
    private static boolean checkResult = false;
    private static boolean checked = false;

    @SuppressLint({"WorldReadableFiles", "NewApi"})
    public static boolean checkLSPosed(Context context) {
        if (checked)
            return checkResult;
        /*try {
            Class<?> clazz = Class.forName("android.app.ContextImpl");
            if(clazz!=null){
                Method method = clazz.getMethod("getSharedPreferencesPath", String.class);//getPreferencesDir
                if(method !=null) {
                    method.setAccessible(true);
                    File mPreferencesDir = (File)method.invoke(context, context.getPackageName() + "_preferences");
                    if (mPreferencesDir != null) {
                        String preferencesDir = mPreferencesDir.getAbsolutePath();
                        if (!new File(context.getDataDir(), "shared_prefs").getAbsolutePath().equals(preferencesDir)) {
                            checked = true;
                            checkResult = true;
                        } else {
                            checked = true;
                            checkResult = false;
                            Toast.makeText(context, "LuckyHooker Settings may not work", Toast.LENGTH_LONG).show();
                        }
                        return checkResult;
                    }
                }
            }
        } catch (SecurityException
                 | ClassNotFoundException | NoSuchMethodException
                 | InvocationTargetException | IllegalAccessException e) {
        }*/
        try {
            context.getSharedPreferences(context.getPackageName() + "_preferences",
                    Context.MODE_WORLD_READABLE);
            checkResult = true;
            checked = true;
        } catch (SecurityException exception) {
            Toast.makeText(context, "LuckyHooker Settings may not work", Toast.LENGTH_LONG).show();
            checkResult = false;
            checked = true;
        }
        return checkResult;
    }

}
