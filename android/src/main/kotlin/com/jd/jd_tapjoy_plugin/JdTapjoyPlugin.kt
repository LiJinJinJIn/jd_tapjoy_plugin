package com.jd.jd_tapjoy_plugin

import android.app.Activity
import com.tapjoy.*
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.util.*

/** JdTapjoyPlugin */
class JdTapjoyPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var channel: MethodChannel
    private lateinit var activity: Activity
    private lateinit var placement: TJPlacement

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "jd_tapjoy_plugin")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        try {
            when (call.method) {

                "setDebugEnabled" -> {
                    val isDebug: Boolean = if (call.argument<Any?>("isDebug") != null) {
                        call.argument<Boolean>("isDebug")!!
                    } else {
                        return
                    }
                    Tapjoy.setDebugEnabled(isDebug)
                }

                "setUserId" -> {
                    val userId = if (call.argument<Any?>("userId") != null) {
                        call.argument<String>("userId")
                    } else {
                        return
                    }
                    Tapjoy.setUserID(userId)
                }

                "connect" -> {
                    val apiToken = if (call.argument<Any?>("api_token") != null) {
                        call.argument<String>("api_token")
                    } else {
                        return
                    }
                    val connectFlags = Hashtable<String, Any>()
                    Tapjoy.connect(activity.applicationContext, apiToken, connectFlags, object : TJConnectListener {
                        override fun onConnectSuccess() {
                            try {
                                activity.runOnUiThread(Runnable { channel.invokeMethod("onConnectSuccess", null) })
                            } catch (e: Exception) {

                            }
                        }

                        override fun onConnectFailure() {
                            try {
                                activity.runOnUiThread(Runnable { channel.invokeMethod("onConnectFailure", null) })
                            } catch (e: Exception) {

                            }
                        }
                    })
                }

                "getPlacement" -> {
                    val placementName: String? = if (call.argument<Any?>("placementName") != null) {
                        call.argument<String>("placementName")
                    } else {
                        result.error("no_placement_name", "a null placement name was provided", null)
                        return
                    }
                    Tapjoy.setActivity(activity)
                    placement = Tapjoy.getPlacement(placementName, object : TJPlacementListener {
                        override fun onRequestSuccess(tjPlacement: TJPlacement) {
                            activity.runOnUiThread { channel.invokeMethod("onRequestSuccess", null) }
                        }

                        override fun onRequestFailure(tjPlacement: TJPlacement, tjError: TJError) {
                            val error = tjError.message
                            activity.runOnUiThread { channel.invokeMethod("onRequestFailure", error) }
                        }

                        override fun onContentReady(tjPlacement: TJPlacement) {
                            placement = tjPlacement
                            activity.runOnUiThread { channel.invokeMethod("onContentReady", null) }
                        }

                        override fun onContentShow(tjPlacement: TJPlacement) {
                            activity.runOnUiThread { channel.invokeMethod("onContentShow", null) }
                        }

                        override fun onContentDismiss(tjPlacement: TJPlacement) {
                            activity.runOnUiThread { channel.invokeMethod("onContentDismiss", null) }
                        }

                        override fun onRewardRequest(tjPlacement: TJPlacement, tjActionRequest: TJActionRequest, s: String, rew: Int) {
                            activity.runOnUiThread { channel.invokeMethod("onReward", rew) }
                        }

                        override fun onPurchaseRequest(tjPlacement: TJPlacement, tjActionRequest: TJActionRequest, s: String) {}

                        override fun onClick(tjPlacement: TJPlacement) {
                            activity.runOnUiThread { channel.invokeMethod("onClick", null) }
                        }
                    })
                    placement.videoListener = object : TJPlacementVideoListener {
                        override fun onVideoStart(placement: TJPlacement) {
                            activity.runOnUiThread { channel.invokeMethod("onVideoStart", null) }
                        }

                        override fun onVideoError(placement: TJPlacement, message: String) {
                            activity.runOnUiThread { channel.invokeMethod("onVideoError", null) }
                        }

                        override fun onVideoComplete(placement: TJPlacement) {
                            activity.runOnUiThread { channel.invokeMethod("onVideoComplete", null) }
                        }
                    }
                }

                "requestContent" -> if (Tapjoy.isConnected()) {
                    placement.requestContent()
                }

                "showContent" -> if (placement.isContentReady) {
                    placement.showContent()
                }

            }
            result.success(java.lang.Boolean.TRUE)
        } catch (e: Exception) {

        }
    }

    override fun onDetachedFromEngine( binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onDetachedFromActivity() {
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }
}
