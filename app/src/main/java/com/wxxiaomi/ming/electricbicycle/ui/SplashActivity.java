package com.wxxiaomi.ming.electricbicycle.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.TextView;

import com.hyphenate.easeui.controller.EaseUI;
import com.wxxiaomi.ming.electricbicycle.ConstantValue;
import com.wxxiaomi.ming.electricbicycle.EBApplication;
import com.wxxiaomi.ming.electricbicycle.api.HttpMethods;
import com.wxxiaomi.ming.electricbicycle.common.GlobalManager;
import com.wxxiaomi.ming.electricbicycle.common.util.AppManager;
import com.wxxiaomi.ming.electricbicycle.R;
import com.wxxiaomi.ming.electricbicycle.common.util.SharedPreferencesUtils;
import com.wxxiaomi.ming.electricbicycle.ui.activity.HomeActivity;
import com.wxxiaomi.ming.electricbicycle.ui.activity.RegisterActivity;
import com.wxxiaomi.ming.electricbicycle.dao.bean.User;
import com.wxxiaomi.ming.electricbicycle.dao.db.AppDao;
import com.wxxiaomi.ming.electricbicycle.dao.db.UserService;
import com.wxxiaomi.ming.electricbicycle.dao.db.impl.AppDaoImpl;
import com.wxxiaomi.ming.electricbicycle.support.aliyun.OssEngine;
import com.wxxiaomi.ming.electricbicycle.support.cache.DiskCache;
import com.wxxiaomi.ming.electricbicycle.support.easemob.EmEngine;
import com.wxxiaomi.ming.electricbicycle.support.easemob.ui.MyUserProvider;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * 入口activity
 *
 * @author Mr.W
 */
public class SplashActivity extends Activity {

    private TextView tv_tv;
    final CountDownLatch order = new CountDownLatch(3);
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.i("wang", "asd");
                    tv_tv.setText(tv_tv.getText() + "\n-初始化百度地图完成\n-");
                    Log.i("wang", "初始化百度地图完成");
                    break;
                case 2:
                    tv_tv.setText(tv_tv.getText() + "初始化缓存系统完成\n-");
                    Log.i("wang", "初始化缓存系统完成");
                    break;
                case 3:
                    tv_tv.setText(tv_tv.getText() + "初始化Oss完成\n-");
                    Log.i("wang", "初始化Oss完成");
                    break;
                case 4:
                    tv_tv.setText(tv_tv.getText() + "初始化Ease完成\n");
                    Log.i("wang", "初始化Ease完成");
                    break;
                case 5:
//					Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
//					startActivity(intent);
//					finish();
//                    autoLogin();
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppManager.getAppManager().addActivity(this);
        tv_tv = (TextView) findViewById(R.id.tv_tv);
        init();

    }

    /**
     * Log.i("wang","result:"+s);
     * //此时验证通过，进入主界面
     * //在此之前必须先取出currentuser
     * AppDao appDao = new AppDaoImpl(EBApplication.applicationContext);
     * User user = appDao.getLocalUser(Integer.valueOf(s));
     * GlobalManager.getInstance().savaUser(user);
     */
    private void autoLogin() {
        String ltoken = (String) SharedPreferencesUtils.getParam(EBApplication.applicationContext, ConstantValue.LONGTOKEN, "");
        if (!"".equals(ltoken)) {
            HttpMethods.getInstance().Token_Long2Short()
                    .map(new Func1<String, User>() {
                        @Override
                        public User call(String s) {
                            Log.i("wang", "根据长token获取的uid：" + s);
                            AppDao appDao = new AppDaoImpl(EBApplication.applicationContext);
                            User user = appDao.getLocalUser(Integer.valueOf(s));
                            GlobalManager.getInstance().savaUser(user);
                            return user;
                        }
                    })
                    .flatMap(new Func1<User, Observable<Boolean>>() {
                        @Override
                        public Observable<Boolean> call(User user) {
                            Log.i("wang", user.toString());
                            GlobalManager.getInstance().savaUser(user);
                            return EmEngine.getInstance().LoginFromEm(user.username, user.password);
                        }
                    })
                    //从服务器获取好友列表
                    .flatMap(new Func1<Boolean, Observable<List<String>>>() {
                        @Override
                        public Observable<List<String>> call(Boolean aBoolean) {
                            return EmEngine.getInstance().getContactFromEm();
                        }
                    })
                    .flatMap(new Func1<List<String>, Observable<Integer>>() {
                        @Override
                        public Observable<Integer> call(List<String> strings) {
                            if (strings.size() == 0) {
                                return Observable.just(0);
                            }
                            return UserService.getInstance().UpdateFriendList(strings);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            order.countDown();
                        }
                    })
            ;

        } else {
            Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }

    }


    /**
     * 初始化各类参数 决定程序往哪里走
     */
    private void init() {


        handler.sendEmptyMessage(1);
        new Thread() {
            @Override
            public void run() {
                MultiDex.install(getApplicationContext());
                DiskCache.getInstance().open(getApplicationContext());
                handler.sendEmptyMessage(2);
                OssEngine.getInstance().initOssEngine(getApplicationContext());
                handler.sendEmptyMessage(3);
                EaseUI.getInstance().setUserProfileProvider(new MyUserProvider());
                handler.sendEmptyMessage(4);
                order.countDown();
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    order.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                   autoLogin();

            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                try {
                    order.await();
                    handler.sendEmptyMessage(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        AppManager.getAppManager().finishActivity(this);
        super.onDestroy();

    }

}