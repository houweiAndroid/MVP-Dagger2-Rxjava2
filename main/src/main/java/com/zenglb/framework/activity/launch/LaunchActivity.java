package com.zenglb.framework.activity.launch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.zenglb.baselib.base.BaseActivity;
import com.zenglb.baselib.sharedpreferences.SharedPreferencesDao;
import com.zenglb.framework.R;
import com.zenglb.framework.activity.access.LoginActivity;
import com.zenglb.framework.navigation.MainActivityBottomNavi;
import com.zenglb.framework.config.SPKey;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 启动页面的背景图放在不同的目录还会导致内存的占用大小不一样啊
 */
public class LaunchActivity extends BaseActivity {

   private String TAG="Rxjava2";

    private Handler UiHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String accessToken = SharedPreferencesDao.getInstance().getData(SPKey.KEY_ACCESS_TOKEN, "", String.class);

                    if (TextUtils.isEmpty(accessToken)) {
                        Intent i1 = new Intent();
                        i1.setClass(LaunchActivity.this, LoginActivity.class);
                        startActivity(i1);
                        LaunchActivity.this.finish();
                    } else {
//                          goWebView("https://www.baidu.com");
                        Intent i1 = new Intent();
                        i1.setClass(LaunchActivity.this, MainActivityBottomNavi.class);
                        startActivity(i1);
                        LaunchActivity.this.finish();
                    }

                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected int setLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void initViews() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UiHandler.sendEmptyMessageDelayed(0, 2000); //
        testRxjava();
    }


//    Disposable subscribe() {}
//    Disposable subscribe(Consumer<? super T> onNext) {}
//    Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError) {}
//    Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete) {}
//    Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete, Consumer<? super Disposable> onSubscribe) {}

    /**
     * 测试Rxjava2 的基本使用
     */
    private void testRxjava(){


        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "This is result " + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });



//        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                Log.d(TAG, "Observable thread is : " + Thread.currentThread().getName());
//                Log.d(TAG, "emit 1");
//                emitter.onNext(1);
//            }
//        });
//
//        Consumer<Integer> consumer = new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                Log.d(TAG, "Observer thread is :" + Thread.currentThread().getName());
//                Log.d(TAG, "onNext: " + integer);
//            }
//        };
//
//        observable.subscribeOn(Schedulers.newThread())     //订阅新的线程中的事件源
//                  .observeOn(AndroidSchedulers.mainThread()) //在主线程中观察，切换到主线程处理问题
//                  .subscribe(consumer);

    }


    /**
     * 获取手机号码，一般获取不到
     *
     * 用到的权限：
     * name="android.permission.READ_PHONE_STATE"
     *
     * 要想获取更多电话、数据、移动网络相关信息请查阅TelephonyManager资料
     */
    public String getLineNum(Context ctx) {
        String strResult = "";
        TelephonyManager telephonyManager = (TelephonyManager) ctx
                      .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            strResult = telephonyManager.getLine1Number();
        }
        return strResult;
    }

    /**
     *
     *
     */
    private void getSomeInfo(){
        int cid=-1,lac=-1;
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try {
            GsmCellLocation location = (GsmCellLocation) mTelephonyManager.getCellLocation();
            if(null!=location){
                lac= location.getLac();
                cid = location.getCid();
            }
        }catch (Exception e){
        }

        try {
            CdmaCellLocation location2 = (CdmaCellLocation) mTelephonyManager.getCellLocation();
            if(null!=location2){
                cid = location2.getBaseStationId();
                lac = location2.getNetworkId();
            }
        }catch (Exception e){
        }
        Toast.makeText(this,"cid:+"+cid+"  lac:"+lac,Toast.LENGTH_LONG).show();
    }

}
