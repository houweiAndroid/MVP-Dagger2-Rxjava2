package com.zenglb.framework.jsbridge;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.webkit.WebView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Native 回调JS 注册的callback
 *
 */
public class Callback {
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    //格式化一下
    private static final String CALLBACK_JS_FORMAT = "javascript:JSBridge.onFinish('%s', %s);";
    private String mPort;   //回调JS的那个服务，这个是标示
    private WeakReference<WebView> mWebViewRef;

    public Callback(WebView view, String port) {
        mWebViewRef = new WeakReference<>(view);
        mPort = port;
    }


    /**
     * 最终返回给JS
     *
     */
    public void applyDataToJS(JSBridgeResult jsBridgeResult) {
        String jsBridgeResultStr=new Gson().toJson(jsBridgeResult);
        final String execJs = String.format(CALLBACK_JS_FORMAT, mPort, jsBridgeResultStr);  //String 出来

        if (mWebViewRef != null && mWebViewRef.get() != null) {
            mHandler.post(() -> {
                //Java要调用js的方法，是非常容易做到的，使用WebView.loadUrl(“JavaScript:function()”)即可
                mWebViewRef.get().loadUrl(execJs);
            });
        }
    }




}
