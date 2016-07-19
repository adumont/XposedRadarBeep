package com.adlx.xposedradarbeep;

import android.media.AudioManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Module implements IXposedHookLoadPackage {

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals("com.radarbeep"))
            return;

        findAndHookMethod("android.media.MediaPlayer", lpparam.classLoader, "setAudioStreamType", int.class, setAudioStreamHook);

    }

    XC_MethodHook setAudioStreamHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            //XposedBridge.log("Forcing AudioManager.STREAM_RING");
            param.args[0]=AudioManager.STREAM_RING;
            }
    };

}