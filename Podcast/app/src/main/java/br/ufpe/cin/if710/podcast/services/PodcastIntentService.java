package br.ufpe.cin.if710.podcast.services;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    //database
    private ContentResolver cr;

    //arquivo mediaplayer , mas vou criar uma nova classe para o service
    public PodcastIntentService() {
        super(CLASSNAME);
        setIntentRedelivery(false);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //recuperando command do sistema
        String action = intent.getAction();
        //recuperando o contexto do banco de dados
        cr = getContentResolver();
        Log.e("PodcastIntentService", "Entrou");
        String id = intent.getStringExtra("position");
        byte[] fileBytes = null;

        if (action.equals(ACTION_DOWNLOAD)) {
            ItemFeed item = (ItemFeed) intent.getSerializableExtra("itemFeed");
            Log.d("PodcastIntentService", "BAIXANDO");
            try {
                fileBytes = downloadFileBytes(item.getDownloadLink());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri download = Uri.parse(item.getDownloadLink());
            //DIRETORIO BASE PARA RECUPERAR OS EPISODIOS
            File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            root.mkdirs();

            File output = new File(root, download.getLastPathSegment());
            if (output.exists()) {
                output.delete();
            }
            try {
                FileOutputStream fos = new FileOutputStream(output.getPath());
                BufferedOutputStream out = new BufferedOutputStream(fos);
                out.write(fileBytes);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("DOWNLOAD",download.getLastPathSegment());

            ContentValues content = new ContentValues();

            //modificar o objeto
            content.put(PodcastProviderContract.TITLE, item.getTitle());
            content.put(PodcastProviderContract.DATE, item.getPubDate());
            content.put(PodcastProviderContract.LINK, item.getLink()); //revisar o parser
            content.put(PodcastProviderContract.DESCRIPTION, item.getDescription());
            content.put(PodcastProviderContract.DOWNLOAD_LINK, item.getDownloadLink());
            content.put(PodcastProviderContract.URI,output.getAbsolutePath());
            content.put(PodcastProviderContract.STATE, "PLAY"); //STATUS ATUALIZADOS PARA PLAY

            Log.d("PodcastIntentService", "FINISH DOWNLOAD");

            Log.d("PodcastIntentService", "Update");
            int idN = Integer.parseInt(id) + 1;
            cr.update(PodcastProviderContract.EPISODE_LIST_URI,
                    content,
                    idN + "",
                    null
            );

            Log.d("PodcastIntentService", "FinishUpdate");
            //iniciando uma intent para o broadcast
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("br.ufpe.cin.if710.podcast.action.NOTIFICATION");
            broadcastIntent.putExtra("position", id);
            broadcastIntent.putExtra("state", "DOWNLOAD");
            sendBroadcast(broadcastIntent);


        } else {

            Log.d("IntentSerivceDownload", "URI DE DOWNLOAD NÃO INDETIFICADO");
        }
    }


       //METODO PARA REALIZAR O DOWNLOAD DE UM ARQUIVO
    private byte[] downloadFileBytes(String urlStr) throws IOException {
        InputStream in = null;
        byte[] response = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            response = out.toByteArray();
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return response;
    }


}