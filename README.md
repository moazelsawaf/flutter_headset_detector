# this pacakge is modified to be used in my work it's not good for public use as it now only detects headphones only (AUXs or micless hedsets)


# Flutter Headset Detector Plugin

A Flutter plugin to get a headset event.

*This is a clone of [headset_connection_event](https://github.com/themobilecoder/headset_connection_event), but seperated wired and wireless event.*


## Current Status

| Platform    | Physical Headset | Bluetooth |
| ----------- | ---------------- | --------- |
| iOS         | ✅               | ✅        |
| Android     | ✅               | ✅        |


## Usage
To use this plugin, add `flutter_headset_detector` as a [dependency in your pubspec.yaml file](https://flutter.io/platform-plugins/).

### Example

``` dart
// Import package
import 'package:flutter_headset_detector/flutter_headset_detector.dart';

// Instantiate it
final headsetDetector = HeadsetDetector();
Map<HeadsetType, HeadsetState> headsetState = {
  HeadsetType.WIRED: HeadsetState.DISCONNECTED,
  HeadsetType.WIRELESS: HeadsetState.DISCONNECTED,
};

// if headset is plugged
headsetDetector.getCurrentState.then((_val){
  headsetState = _val;
  setState(() {
  });
});

// Detect the moment headset is plugged or unplugged with a Listener
headsetDetector.setListener((_val) {
  switch (_val) {
    case HeadsetChangedEvent.WIRED_CONNECTED:
      headsetState[HeadsetType.WIRED] = HeadsetState.CONNECTED;
      break;
    case HeadsetChangedEvent.WIRED_DISCONNECTED:
      headsetState[HeadsetType.WIRED] = HeadsetState.DISCONNECTED;
      break;
    case HeadsetChangedEvent.WIRELESS_CONNECTED:
      headsetState[HeadsetType.WIRELESS] = HeadsetState.CONNECTED;
      break;
    case HeadsetChangedEvent.WIRELESS_DISCONNECTED:
      headsetState[HeadsetType.WIRELESS] = HeadsetState.DISCONNECTED;
      break;
  }
  setState(() {
  });
});

// Remove the Listener
headsetDetector.removeListener();
```


## Screenshot
* There are no connected headphones:
<img src = "screenshot/both_not_connected.jpg" width="30%">

* There are wired connected headphones:
<img src = "screenshot/wired_connected.jpg" width="30%">

* There are Bluetooth connected headphones:
<img src = "screenshot/wireless_connected.jpg" width="30%">

* All types are connected:
<img src = "screenshot/all_connected.jpg" width="30%">
