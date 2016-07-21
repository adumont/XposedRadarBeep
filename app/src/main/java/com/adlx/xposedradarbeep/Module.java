package com.adlx.xposedradarbeep;

import android.media.AudioAttributes;
import android.media.AudioManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import android.media.SoundPool;
import android.os.Build;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;

public class Module implements IXposedHookLoadPackage {

    XC_MethodHook setAudioStreamHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            XposedBridge.log("RadarBeep - setAudioStreamHook - Forcing STREAM_RING");
            param.args[0] = AudioManager.STREAM_RING;
        }
    };
    XC_MethodHook spbHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            XposedBridge.log("RadarBeep - spbHook: we're in SoundPool.Builder.build() - BEFORE HOOK");

            if (Build.VERSION.SDK_INT >= 21) {
                SoundPool.Builder spb = (SoundPool.Builder) param.thisObject;

                if (spb == null) {
                    XposedBridge.log("RadarBeep - spbHook: spb es NULL damn it - static!!");
                } else {
                    XposedBridge.log("RadarBeep - spbHook: we're in SoundPool.Builder.build() - IN the IF...");
                    spb.setAudioAttributes(new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_RING).build());
                }
            }
        }
    };
    XC_MethodHook spHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            XposedBridge.log("RadarBeep - spHook: we're in SoundPool constructor() - BEFORE HOOK");
            param.args[1] = AudioManager.STREAM_RING;
        }
    };

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals("com.radarbeep"))
            return;

        XposedBridge.log("RadarBeep - HERE WE GO");

        findAndHookMethod("android.media.MediaPlayer", lpparam.classLoader, "setAudioStreamType", int.class, setAudioStreamHook);

        if (Build.VERSION.SDK_INT >= 21) {
            findAndHookMethod("android.media.SoundPool.Builder", lpparam.classLoader, "build", spbHook);
        } else {
            findAndHookConstructor("android.media.SoundPool", lpparam.classLoader, int.class, int.class, int.class, spHook);
        }

    }

}