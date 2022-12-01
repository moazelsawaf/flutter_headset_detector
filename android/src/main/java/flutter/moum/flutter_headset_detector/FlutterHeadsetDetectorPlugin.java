package flutter.moum.flutter_headset_detector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import androidx.annotation.NonNull;

import android.util.ArraySet;
import android.util.Log;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * FlutterHeadsetDetectorPlugin
 */
public class FlutterHeadsetDetectorPlugin implements FlutterPlugin, MethodCallHandler {
    private static Context context;

    /// The MethodChannel that will the communication between Flutter and native
    /// Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine
    /// and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;

    private final HeadsetEventListener headsetEventListener = new HeadsetEventListener() {
        @Override
        public void onWiredHeadsetConnect(String name,int micState) {
            final HashMap args = new HashMap<>();
            args.put("state", "connected");
            args.put("name",name);
            args.put("micState",micState==1 ? true : false);
            if ((boolean) args.get("micState")){
                return;
            }
            channel.invokeMethod("wired_connected", args);
        }

        @Override
        public void onWiredHeadsetDisconnect(String name,int micState) {
            final HashMap args = new HashMap<>();
            args.put("state", "disconnected");
            args.put("name",name);
            args.put("micState",micState==1 ? true : false);
            channel.invokeMethod("wired_disconnected", args);
        }

        @Override
        public void onWirelessHeadsetConnect() {
            channel.invokeMethod("wireless_connected", "true");
        }

        @Override
        public void onWirelessHeadsetDisconnect() {
            channel.invokeMethod("wireless_disconnected", "true");
        }

    };

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter.moum/flutter_headset_detector");
        channel.setMethodCallHandler(this);

        context = flutterPluginBinding.getApplicationContext();

        final HeadsetBroadcastReceiver hReceiver = new HeadsetBroadcastReceiver(headsetEventListener);
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        flutterPluginBinding.getApplicationContext().registerReceiver(hReceiver, filter);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getCurrentState")) {
            HashMap<Integer, Boolean> state = new HashMap<>();
            state.put(0, wiredHeadphonesConnectionState() == 1);
            state.put(1, bluetoothHeadphonesConnectionState() == 1);
            result.success(state);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    private int wiredHeadphonesConnectionState() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        AudioDeviceInfo[] audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);

        for (AudioDeviceInfo deviceInfo : audioDevices) {
            int deviceType = deviceInfo.getType();
            System.out.println("[JAVA: device type] ");
            System.out.println(deviceType);
            if (deviceType == AudioDeviceInfo.TYPE_WIRED_HEADPHONES /* || deviceType == AudioDeviceInfo.TYPE_WIRED_HEADSET
                    || deviceType == AudioDeviceInfo.TYPE_USB_HEADSET
                    || deviceType == AudioDeviceInfo.TYPE_USB_DEVICE */) {
                return 1;
            }
        }
        return 0;

    }

    private int bluetoothHeadphonesConnectionState() {
      
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        AudioDeviceInfo[] audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);

        for (AudioDeviceInfo deviceInfo : audioDevices) {
            int deviceType = deviceInfo.getType();
            System.out.println("[JAVA: bluetooth type] ");
            System.out.println(deviceType);
            if (deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_SCO ) {
                return 1;
            }
        }
        return 0;
    }
}
