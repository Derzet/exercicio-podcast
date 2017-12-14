package br.ufpe.cin.if710.podcast.ui;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import br.ufpe.cin.if710.podcast.BuildConfig;
import br.ufpe.cin.if710.podcast.domain.LiveDataTeste;

// com.frogermcs.androiddevmetrics.AndroidDevMetrics;

/**
 * Created by gabri on 13/12/2017.
 */
//Classe usada para utilizar o LeakCanary.
public class MyApplication extends Application {
    static LiveDataTeste mModel;
    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);

        if (BuildConfig.DEBUG) {
 //           AndroidDevMetrics.initWith(this);
        }
    }
}
