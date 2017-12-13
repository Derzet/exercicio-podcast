package br.ufpe.cin.if710.podcast.ui;

import android.app.Application;

// com.frogermcs.androiddevmetrics.AndroidDevMetrics;
import com.squareup.leakcanary.LeakCanary;

import br.ufpe.cin.if710.podcast.BuildConfig;

/**
 * Created by gabri on 13/12/2017.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);

        if (BuildConfig.DEBUG) {
 //           AndroidDevMetrics.initWith(this);
        }
    }

}
