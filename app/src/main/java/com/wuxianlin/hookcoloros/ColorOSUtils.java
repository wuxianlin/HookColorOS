package com.wuxianlin.hookcoloros;

import de.robv.android.xposed.XposedHelpers;

public class ColorOSUtils {

    public static final int OplusOS_1_0 = 1;
    public static final int OplusOS_1_2 = 2;
    public static final int OplusOS_1_4 = 3;
    public static final int OplusOS_2_0 = 4;
    public static final int OplusOS_2_1 = 5;
    public static final int OplusOS_3_0 = 6;
    public static final int OplusOS_3_1 = 7;
    public static final int OplusOS_3_2 = 8;
    public static final int OplusOS_5_0 = 9;
    public static final int OplusOS_5_1 = 10;
    public static final int OplusOS_5_2 = 11;
    public static final int OplusOS_6_0 = 12;
    public static final int OplusOS_6_1 = 13;
    public static final int OplusOS_6_2 = 14;
    public static final int OplusOS_6_7 = 15;
    public static final int OplusOS_7_0 = 16;
    public static final int OplusOS_7_1 = 17;
    public static final int OplusOS_7_2 = 18;
    public static final int OplusOS_11_0 = 19;
    public static final int OplusOS_11_1 = 20;
    public static final int OplusOS_11_2 = 21;
    public static final int OplusOS_11_3 = 22;
    public static final int OplusOS_12_0 = 23;
    public static final int OplusOS_12_1 = 24;
    public static final int OplusOS_12_2 = 25;
    public static final int OplusOS_13_0 = 26;
    public static final int OplusOS_13_1 = 27;
    public static final int OS_13_0 = 26;
    public static final int OS_13_1 = 27;
    public static final int OS_13_1_1 = 28;
    public static final int OS_13_2 = 29;
    public static final int OS_14_0 = 30;
    public static final int OS_14_0_1 = 31;
    public static final int OS_14_0_2 = 32;
    public static final int OS_14_1_0 = 33;
    public static final int OS_15_0_0 = 34;
    public static final int OS_15_0_1 = 35;
    public static final int OS_15_0_2 = 36;
    public static final int OS_16_0 = 37;

    public static int getColorOSVersion() {
        try {
            return XposedHelpers.getStaticIntField(
                    XposedHelpers.findClass("com.oplus.os.OplusBuild$VERSION", null), "SDK_VERSION");
        } catch (Throwable ignored1) {
            try {
                return (int) XposedHelpers.callStaticMethod(
                        XposedHelpers.findClass("com.oplus.os.OplusBuild", null),
                        "getOplusOSVERSION", 0);
            } catch (Throwable ignored2) {
                try {
                    return (int) XposedHelpers.callStaticMethod(
                            XposedHelpers.findClass("com.color.os.ColorBuild", null),
                            "getColorOSVERSION", 0);
                } catch (Throwable ignored3) {
                    return -1;
                }
            }
        }
    }

}
