package com.adlx.xposedradarbeep;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findField;

public class Module implements IXposedHookLoadPackage {

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals("com.radarbeep"))
            return;

        XposedBridge.log("*** ALEX *** Loaded app: " + lpparam.packageName);

        // i()
        //findAndHookMethod("com.radarbeep.z", lpparam.classLoader, "i", myHook);
        //XposedBridge.log("*** ALEX *** Hooked to com.radarbeep.z.i(i)");

        // .method public final a(Ljava/lang/String;Lcom/radarbeep/aa;)V
        //Class aa = XposedHelpers.findClass("com.radarbeep.aa", lpparam.classLoader);
        //XposedBridge.log("*** ALEX *** Found class com.radarbeep.aa");

        //findAndHookMethod("com.radarbeep.z", lpparam.classLoader, "a", String.class, aa, myHook );
        //XposedBridge.log("*** ALEX *** Hooked to com.radarbeep.z.a(String,aa)");

        findAndHookMethod("android.media.MediaPlayer", lpparam.classLoader, "setAudioStreamType", int.class, setAudioStreamHook);

    }

    XC_MethodHook setAudioStreamHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            XposedBridge.log("Forcing AudioManager.STREAM_RING " + param.args[0]);
            param.args[0]=AudioManager.STREAM_RING;
            //param.thisObject.equals()
        }
    };

    XC_MethodHook myHook = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {

            // .field private k:Landroid/media/MediaPlayer;
            Field fieldPlayer = findField(param.thisObject.getClass(), "k");
            // Object mPlayer = fieldPlayer.get(param.thisObject);
            MediaPlayer mPlayer = (MediaPlayer) fieldPlayer.get(param.thisObject);
            if (mPlayer == null) {
                XposedBridge.log("mPlayer is null");
                return;
            }
            XposedBridge.log("YOYOYOYO Found mPlayer is not null!");
            // mPlayer.reset();
            mPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        }
    };

}