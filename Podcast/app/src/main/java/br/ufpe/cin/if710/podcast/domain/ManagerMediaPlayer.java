package br.ufpe.cin.if710.podcast.domain;

import android.media.MediaPlayer;
import android.provider.MediaStore;

/**
 * Created by Matheus on 15/10/2017.
 */
//CLASSE PARA SUPORTAR O GERENCIAMENTO
public class ManagerMediaPlayer {
    private int id;
    private MediaPlayer mPlayer;
    private int time; //tempo em que o video foi pausado
    public ManagerMediaPlayer(int id, MediaPlayer mPlayer, int time) {
        this.id = id;
        this.mPlayer = mPlayer;
        this.time = time;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MediaPlayer getmPlayer() {
        return mPlayer;
    }

    public void setmPlayer(MediaPlayer mPlayer) {
        this.mPlayer = mPlayer;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

}
