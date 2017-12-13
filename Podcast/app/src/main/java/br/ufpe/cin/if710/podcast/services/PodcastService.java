package br.ufpe.cin.if710.podcast.services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;

import static java.lang.String.valueOf;


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

    //database
    private ContentResolver cr ;
    private MediaPlayer mPlayer;
    private int idAtual; //detectar o podcast atual

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */

    @Override
    public void onCreate() {
        super.onCreate();
        cr = getContentResolver();
        idAtual = -1; //id indicando que não foi inicializado
        mPlayer = new MediaPlayer();
        mPlayer.setLooping(false); //INDICANDO QUE NÃO HAVERÁ LOOP NESTE CASO
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer player) {
                if(player.isPlaying()) {
                    removeExternalStorage(); //REMOVENDO QUANDO COMPLETADO O PODCAST
                }
               // Log.e("CHAMANDO","ERRADO");
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        if(mPlayer!=null) {
            mPlayer.pause();
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
        }
    }


    //referencia da pasta url
    private String referencia(int id){
        Cursor c = cr.query(PodcastProviderContract.EPISODE_LIST_URI,
                PodcastProviderContract.ALL_COLUMNS,
                valueOf(id+1),
                null,
                null);
        c.moveToFirst();
        String downloadURI = c.getString(c.getColumnIndex("downloadUri"));
        Log.d("UriDown", downloadURI + " ");
        c.close();
        return downloadURI;

    }
    //recuperando tempo
    private int geTime(int id){
        Cursor c = cr.query(PodcastProviderContract.EPISODE_LIST_URI,
                PodcastProviderContract.ALL_COLUMNS,
                valueOf(id+1),
                null,
                null);
        c.moveToFirst();
        int time = Integer.parseInt(c.getString(c.getColumnIndex("time")));
        c.close();
        return time;

    }
    //editando tempo
    private void setTime(int id,int time){
        ContentValues content = new ContentValues();
        content.put(PodcastProviderContract.TIME,time+"" ); //STATUS ATUALIZADOS
        String[] array = new String[1];
        array[0] = "Time";
       int idN = id;
        cr.update(PodcastProviderContract.EPISODE_LIST_URI,
                content,
                valueOf(idN+1),
                array
        );
    }
     //atualizando estado no banco e mainActivity
    private void updateState(int id,String state){
        ContentValues content = new ContentValues();
        content.put(PodcastProviderContract.STATE, state); //STATUS ATUALIZADOS
        String[] array = new String[1];
        array[0] = "Status";
        int idN = id;
        cr.update(PodcastProviderContract.EPISODE_LIST_URI,
                content,
                valueOf(idN+1),
                array
        );

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("br.ufpe.cin.if710.podcast.action.NOTIFICATION");
        broadcastIntent.putExtra("position", id+"");
        broadcastIntent.putExtra("state", state);
        sendBroadcast(broadcastIntent);

    }
    //tocando
    public void playPodcast(int id) {
        Log.i("Service", "PLAY");
        Log.d("IDPlay", valueOf(id));
        Log.d("Ref", referencia(id));
        //iniciando uma intent para o broadcast
            //observando que o elemento com id 0 não existe , precisamos tratar com uma som
            //inicializando o mediaplayer
           if((idAtual!= -1) && (idAtual != id) ){ //se musica nova
              pausePodcast(idAtual);
               idAtual = id; //GERENCIA PELO ID ATUAL
               try {
                   //SETANDO NOVA MUSICA;
                   mPlayer.stop();
                  // mPlayer.release();
                   mPlayer.reset();
                   mPlayer.setDataSource(referencia(id));
                   mPlayer.start();

               } catch (IOException e) {
                   e.printStackTrace();
               }

           }else {
               try {
                   mPlayer.setDataSource(referencia(id)); //recuperar a referencia do download
                   mPlayer.prepare();
                   mPlayer.start();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        idAtual = id; //GERENCIA PELO ID ATUAL

        updateState(id, "PAUSE");


    }

    //pausando
    public void pausePodcast(int id){
        Log.i("Service","PAUSE");
            if (mPlayer.isPlaying()) {
                int time = mPlayer.getCurrentPosition();
                this.setTime(id,time); //ATUALIZANDO TIME
                mPlayer.pause();
            }
        updateState(id,"RESUME");
    }

    public void resumePodcast(int id){
        Log.i("Service","RESUME");
        if((idAtual!= -1) && (idAtual != id) ){ //se musica nova , da tocada atual
            pausePodcast(idAtual); //PAUSO A MUSICA QUE ESTA TOCANDO
            idAtual = id; //GERENCIA PELO ID ATUAL
        }

        mPlayer.stop();

        try {
         //   mPlayer = new MediaPlayer();
            mPlayer.reset();
            mPlayer.setDataSource(referencia(id));
            mPlayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
         mPlayer.seekTo(this.geTime(id));
         mPlayer.start();
           // updateState(id, "PAUSE");
            idAtual = id; //GERENCIA PELO ID ATUAL
      updateState(id,"PAUSE");

    }

    //metodo de remoção da memória externa
    private void removeExternalStorage(){
        Cursor c = cr.query(PodcastProviderContract.EPISODE_LIST_URI,
                PodcastProviderContract.ALL_COLUMNS,
                valueOf(idAtual+1),
                null,
                null);
        c.moveToFirst();
        String downloadLink = c.getString(c.getColumnIndex("downloadLink"));
        c.close();
        Uri download = Uri.parse(downloadLink);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                download.getLastPathSegment()); //pegando diretorio

        if(file.exists()){ //VERIFICANDO EXISTENCIA DO ARQUIVO
           file.delete();
        }
        setTime(idAtual,0);
        updateState(idAtual,"BAIXAR");
    }




}
