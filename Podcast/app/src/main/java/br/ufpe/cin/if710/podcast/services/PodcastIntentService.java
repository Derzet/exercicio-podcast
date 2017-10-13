package br.ufpe.cin.if710.podcast.services;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;


/**
 * Created by Matheus on 08/10/2017.
 */

public class PodcastIntentService extends IntentService {
    //constants
    private static final String CLASSNAME = "PodcastIntentService";
    //ações para o intent
    public static final String ACTION_DOWNLOAD = "DOWNLOAD";
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_PAUSE = "PAUSE";
    //database
    private ContentResolver cr ;
    //arquivo mediaplayer , mas vou criar uma nova classe para o service
    private MediaPlayer mPlayer;
    private String teste="NAO É A MESMA";
    public PodcastIntentService() {
        super(CLASSNAME);
        setIntentRedelivery(false);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //recuperando command do usuário
        String action = intent.getAction();
        //recuperando o contexto do banco de dados
        cr = getContentResolver();
        Log.e("PodcastIntentService","Entrou");
        String id =  intent.getStringExtra("position");

        if(action.equals(ACTION_DOWNLOAD)){
           ItemFeed item = (ItemFeed)intent.getSerializableExtra("itemFeed");

            Log.d("PodcastIntentService","BAIXANDO");
            // String baixado = this.getRssFeed(item.getDownloadLink());
            ContentValues content = new ContentValues();

            //modificar o objeto
            content.put(PodcastProviderContract.TITLE, item.getTitle());
            content.put(PodcastProviderContract.DATE, item.getPubDate());
            content.put(PodcastProviderContract.LINK, item.getLink()); //revisar o parser
            content.put(PodcastProviderContract.DESCRIPTION, item.getDescription());
            content.put(PodcastProviderContract.DOWNLOAD_LINK, item.getDownloadLink());
            content.put(PodcastProviderContract.URI,item.getDownloadLink()); //simulando o Download

            Log.d("PodcastIntentService","ACABOU");
            Log.d("PodcastIntentService","Update");
            teste = "ESTA MERDA É A MESMA THEARD!";
            cr.update(PodcastProviderContract.EPISODE_LIST_URI,
                      content,
                      id,
                      null
            );
            Log.d("PodcastIntentService","FinishUpdate");
            //iniciando uma intent para o broadcast

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("br.ufpe.cin.if710.podcast.action.NOTIFICATION");
            broadcastIntent.putExtra("position",id);
            broadcastIntent.putExtra("state","PLAY");
            sendBroadcast(broadcastIntent);


        }else if(action.equals(ACTION_PLAY)){
            Log.d("PodcastIntentService","Start");
            Log.d("ESPECIAL",teste);
            //iniciando uma intent para o broadcast
            Cursor c = cr.query(PodcastProviderContract.EPISODE_LIST_URI,
                    PodcastProviderContract.ALL_COLUMNS,
                    "_id = "+ id ,
                    null,
                    null);
            c.moveToFirst();
            String downloadURI = c.getString(c.getColumnIndex("downloadUri"));
            c.close();

            mPlayer = new MediaPlayer();       //26
            try {
                mPlayer.setDataSource(downloadURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.start();


            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("br.ufpe.cin.if710.podcast.action.NOTIFICATION");
            broadcastIntent.putExtra("position",id);
            broadcastIntent.putExtra("state","PAUSE");
            sendBroadcast(broadcastIntent);



        }else if(action.equals(ACTION_PAUSE)){
            Log.d("IntentService","PAUSE");
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            }

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("br.ufpe.cin.if710.podcast.action.NOTIFICATION");
            broadcastIntent.putExtra("position",id);
            broadcastIntent.putExtra("state","PLAY");
            sendBroadcast(broadcastIntent);

        }else{
            Log.d("IntentSerivce","NoMatchEntreAsPalavras");
        }
    }


    //metodo para baixar extraido,inutilizado
    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }

}