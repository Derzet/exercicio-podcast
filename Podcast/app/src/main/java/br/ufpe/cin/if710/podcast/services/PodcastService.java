package br.ufpe.cin.if710.podcast.services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ManagerMediaPlayer;

/**
 * Created by Matheus on 14/10/2017.
 */

public class PodcastService extends Service {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public PodcastService getService() {
            return PodcastService.this;
        }
    }
    private String id="1";
    //database
    private ContentResolver cr ;
    private List<ManagerMediaPlayer> managerMediaPlayers;


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */

    @Override
    public void onCreate() {
        super.onCreate();
        //criar mediaplayer
        cr = getContentResolver();
        managerMediaPlayers = new ArrayList<ManagerMediaPlayer>();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("PodcastIntentService","Entrou");
       // String id =  intent.getStringExtra("position");

        return mBinder;
    }

    @Override
    public void onDestroy() {
        for (ManagerMediaPlayer item : managerMediaPlayers) {
            if (item.getmPlayer()!=null) {
                item.getmPlayer().stop();
                item.getmPlayer().release();
            }
        }
    }

    public void playPodcast(int id) throws IOException {
        Log.e("PLAY", "INTENT SERVICE PLAY");
        Log.d("PodcastIntentService", "Start");
        Log.e("ID",id+"");
        //iniciando uma intent para o broadcast
        Boolean existe = false;

        for (ManagerMediaPlayer item : managerMediaPlayers) {
            if (id==item.getId()) {
                existe = true;
                int time = item.getTime();
                item.getmPlayer().seekTo(time);
                item.getmPlayer().start();
                Log.e("PODCASTSERVICE","MUSICA JA EXISTIA");
                break;
            }
        }

        if (!existe) {
            //observando que o elemento com id 0 não existe , precisamos tratrar
            Cursor c = cr.query(PodcastProviderContract.EPISODE_LIST_URI,
                    PodcastProviderContract.ALL_COLUMNS,
                    "_id = " + (id+1),
                    null,
                    null);
            c.moveToFirst();
            String downloadURI = c.getString(c.getColumnIndex("downloadUri"));
            c.close();

            //inicializando o mediaplayer
            MediaPlayer mPlayer;
            mPlayer = new MediaPlayer();       //26
            mPlayer.reset();
            mPlayer.setDataSource(downloadURI);
            mPlayer.prepare();
            ManagerMediaPlayer  mMP = new ManagerMediaPlayer(id,mPlayer,mPlayer.getCurrentPosition());
            managerMediaPlayers.add(mMP);

            for (ManagerMediaPlayer item : managerMediaPlayers) {
                if (id==item.getId()) {
                    item.getmPlayer().start();
                    break;
                }
            }



        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("br.ufpe.cin.if710.podcast.action.NOTIFICATION");
        broadcastIntent.putExtra("position", id+"");
        broadcastIntent.putExtra("state", "PAUSE");
        sendBroadcast(broadcastIntent);

    }
    public void pausePodcast(int id){
        Log.d("IntentService","PAUSE");
        for (ManagerMediaPlayer item : managerMediaPlayers) {
            if (id==item.getId() && item.getmPlayer().isPlaying()) {
                int time = item.getmPlayer().getCurrentPosition();
                 item.setTime(time);
                 item.getmPlayer().pause();
                break;
            }
        }


        //mando alterar
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("br.ufpe.cin.if710.podcast.action.NOTIFICATION");
        broadcastIntent.putExtra("position",id+"");
        broadcastIntent.putExtra("state","PLAY");
        sendBroadcast(broadcastIntent);
    }

}
