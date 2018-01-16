package com.zenglb.framework.di;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.zenglb.framework.http.ApiService;
import com.zenglb.framework.persistence.SPDao;
import com.zenglb.framework.persistence.dbmaster.DaoMaster;
import com.zenglb.framework.persistence.dbmaster.DaoSession;
import com.zenglb.framework.persistence.dbupdate.MySQLiteOpenHelper;
import com.zenglb.framework.http.HttpRetrofit;
import com.zlb.httplib.core.SPKey;
import org.greenrobot.greendao.database.Database;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

/**
 * 在这里提供全局的并且是唯一的东西，SharedPrefence,DB,HTTP,etc
 * <p>
 * <p>
 * Created by zlb on 2018/1/11.
 */

// TODO: 2018/1/12 怎样的去保证这里的东西是全局@Singleton ？替换了以后又怎么能动态修改呢？比如DaoSession

@Module
public class AppModule {

    public static final boolean ENCRYPTED = false;
    Application mContext;

    /**
     * Module  带有构造方法并且参数被使用的情况下所产生的Component 是没有Create方法的
     *
     * @param mContext
     */
    public AppModule(Application mContext) {
        this.mContext = mContext;
    }


    /***
     * @return
     */
    @Provides
    @Singleton
    Context providemContext() {
        return mContext;
    }


    /***
     * @return
     */
    @Provides
    @Singleton
    RefWatcher provideRefWatcher(Application mContext) {
        return LeakCanary.install(mContext);  //只管主进程的,其他的进程自保吧
    }

    /**
     * SharedPreferences 保存KEY VALUE 配置信息
     *
     * @return
     */
    @Provides
    @Singleton
    SPDao provideSPDao() {
        return new SPDao(mContext);
    }


    /**
     * 网络访问
     *
     * @return
     */
    @Provides
    @Singleton
    ApiService provideApiService(SPDao spDao,Context mContext){
        return HttpRetrofit.getRetrofit(spDao,mContext).create(ApiService.class);
    }


    /**
     * 数据库访问的DaoSession,登录的时候切换账号后怎么更换呢？
     * <p>
     * 这个Daosession 和SP 还不一样
     *
     * @param spDao return
     */
    @Provides
//    @Singleton //这个怎么能够动态的替换呢DB 链接的a
    DaoSession provideDaoSession(SPDao spDao) {
        String account = spDao.getData(SPKey.KEY_LAST_ACCOUNT, "default_error_db", String.class);
        String DBName = ENCRYPTED ? account + "encrypted" : account;
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(mContext, DBName, null);
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        return new DaoMaster(db).newSession();
    }

}