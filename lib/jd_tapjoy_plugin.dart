import 'dart:async';

import 'package:flutter/services.dart';

enum TapjoyPlacementListener {
  onRequestSuccess,
  onRequestFailure,
  onContentReady,
  onContentShow,
  onContentDismiss,
  onClick,
  onReward,
  onEarned,
  onConnectSuccess,
  onContentFailure,
  onVideoStarted,
  onVideoError,
  onVideoComplete,
}

typedef TapjoyListener(TapjoyPlacementListener listener, int result, String error);

class JdTapjoyPlugin {
  static const MethodChannel _channel = const MethodChannel('jd_tapjoy_plugin');

  static final Map<String, TapjoyPlacementListener> tapjoyPlacementListener = {
    'onRequestSuccess': TapjoyPlacementListener.onRequestSuccess,
    'onRequestFailure': TapjoyPlacementListener.onRequestFailure,
    'onContentReady': TapjoyPlacementListener.onContentReady,
    'onContentShow': TapjoyPlacementListener.onContentShow,
    'onContentDismiss': TapjoyPlacementListener.onContentDismiss,
    'onClick': TapjoyPlacementListener.onClick,
    'onReward': TapjoyPlacementListener.onReward,
    'onConnectSuccess': TapjoyPlacementListener.onConnectSuccess,
    'onContentFailure': TapjoyPlacementListener.onContentFailure,
    'onEarnedCurrency': TapjoyPlacementListener.onEarned,
    'onVideoStart': TapjoyPlacementListener.onVideoStarted,
    'onVideoError': TapjoyPlacementListener.onVideoError,
    'onVideoComplete': TapjoyPlacementListener.onVideoComplete,
  };

  static Future<void> setDebugEnabled({bool isDebug}) async {
    assert(isDebug != null);
    return _channel.invokeMethod("setDebugEnabled", <String, dynamic>{
      'isDebug': isDebug,
    });
  }

  static Future<void> setUserId({String userId}) async {
    assert(userId != null && userId.isNotEmpty);
    return _channel.invokeMethod("setUserId", <String, dynamic>{
      'userId': userId,
    });
  }

  static Future<void> connect(TapjoyListener tapjoyListener, {String apiToken}) async {
    _channel.setMethodCallHandler((MethodCall call) => _handleMethod(call, tapjoyListener));
    assert(apiToken != null && apiToken.isNotEmpty);
    return _channel.invokeMethod("connect", <String, dynamic>{
      'api_token': apiToken,
    });
  }

  static Future<void> getPlacement({String placementName}) async {
    assert(placementName != null && placementName.isNotEmpty);
    return _channel.invokeMethod("getPlacement", <String, dynamic>{
      'placementName': placementName,
    });
  }

  static Future<void> requestContent() async {
    try {
      await _channel.invokeMethod("requestContent");
    } catch (e) {
      print(e.toString());
    }
  }

  static Future<void> showContent() async {
    try {
      await _channel.invokeMethod("showContent");
    } catch (e) {
      print(e.toString());
    }
  }

  static Future<void> _handleMethod(MethodCall call, TapjoyListener listener) async {
    listener(tapjoyPlacementListener[call.method], call.arguments, call.arguments);
  }
}
