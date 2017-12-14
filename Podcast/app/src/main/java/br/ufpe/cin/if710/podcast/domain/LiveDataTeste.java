package br.ufpe.cin.if710.podcast.domain;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by gabri on 12/12/2017.
 */

public class LiveDataTeste extends AndroidViewModel {
    private MutableLiveData<Long> data = new MutableLiveData<>();

    public LiveDataTeste(@NonNull Application application){
        super(application);
        data.setValue(new Date().getTime());
    }

    //sets latest time to LiveDataTeste
   /* public LiveData<Long> getData() {
            if(data == null){
                Log.d("Incializa", "Data");
                data = new MutableLiveData<Long>();
                data.setValue(new Date().getTime());
            }
            return data;
    }*/
    public LiveData<Long> getElapsedTime() {
        if(data == null){
            data = new MutableLiveData<>();
        }
        data.setValue(new Date().getTime());
        return data;
    }
}