package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.Button;

import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.domain.XmlFeedParser;
import br.ufpe.cin.if710.podcast.services.PodcastService;
import br.ufpe.cin.if710.podcast.ui.adapter.XmlFeedAdapter;


public class MainActivity extends Activity {


    //ao fazer envio da resolucao, use este link no seu codigo!
    private final String RSS_FEED = "http://leopoldomt.com/if710/fronteirasdaciencia.xml";
    private ContentResolver cr ; //adicionado o content
    //TODO teste com outros links de podcast
    private ListView items;
    private final String ACTION_NOTIFICATION = "br.ufpe.cin.if710.podcast.action.NOTIFICATION";
    private Context mContext;
    //conectando meu broadcastRecieber
    //private PodcastBroadCastReciever receiver;
    BroadcastReceiver receiver =  new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ACTION_NOTIFICATION)) {
                Log.d("Broadcast", "IntentBroadCast");
                    int position = Integer.parseInt(intent.getStringExtra("position"));
                String state = intent.getStringExtra("state");
                Button button  = items.getChildAt(position).findViewById(R.id.item_action);
                //verificar que é um download
                if(state.equals("DOWNLOAD")) {
                    Intent intentZ = new Intent(context, MainActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intentZ, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder b = new NotificationCompat.Builder(context);
                    //.setSmallIcon(R.drawable.ic_launcher)
                    b.setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setWhen(System.currentTimeMillis())
                            .setTicker("PodCast100")
                            .setContentTitle("Podcast notification")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentText("Seu download foi concluído!")
                            .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                            .setContentIntent(contentIntent)
                            .setContentInfo("Info");

                      Toast.makeText(context,"Lista Atualizada,Novo episódio!",Toast.LENGTH_LONG).show();

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, b.build());

                    button.setText("PLAY");
                }else{
                    button.setText(state);
                }
            }
        }
    };

    private MainActivity getMainActivity(){
        return this;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cr = getContentResolver(); //instancia para a activity para comunicar com o provide
        items = (ListView) findViewById(R.id.items);
       // final Intent podcastServiceIntent = new Intent(this, PodcastService.class);
       // startService(podcastServiceIntent);
        mContext = this.getApplication();
        if(!isBound) {
            Intent intent = new Intent(this, PodcastService.class);
            startService(intent);
            bindService(intent, sConn, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ACTION_NOTIFICATION);
        //filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver,filter );
        new DownloadXmlTask().execute(RSS_FEED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //limpando adapter
        XmlFeedAdapter adapter = (XmlFeedAdapter) items.getAdapter();
        adapter.clear();
        unregisterReceiver(receiver);

        showNotification();


    }

    private void showNotification(){
        //NOTIFICAÇÃO QUANDO ESTÁ EM BACKGROUND
        Context ctx = getApplicationContext();
        Intent intent = new Intent(ctx, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx);
        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("PodCast100")
                .setContentTitle("Podcast notification")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("aproveite sua música!")
                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("Info");
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());
    }


    private class DownloadXmlTask extends AsyncTask<String, Void, List<ItemFeed>> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "iniciando...", Toast.LENGTH_SHORT).show();
        }
        @Override
        protected List<ItemFeed> doInBackground(String... params) {
            List<ItemFeed> itemList = new ArrayList<>();
            try {
                itemList = XmlFeedParser.parse(getRssFeed(params[0]));
                for (ItemFeed item : itemList) {
                    ContentValues content = new ContentValues();
                    content.put(PodcastProviderContract.TITLE, item.getTitle());
                    content.put(PodcastProviderContract.DATE, item.getPubDate());
                    content.put(PodcastProviderContract.LINK, item.getLink()); //revisar o parser
                    content.put(PodcastProviderContract.DESCRIPTION, item.getDescription());
                    content.put(PodcastProviderContract.DOWNLOAD_LINK, item.getDownloadLink());
                    content.put(PodcastProviderContract.URI, "");
                    content.put(PodcastProviderContract.STATE,"BAIXAR");
                          //INSERINDO O ITEM COM STATUS BAIXAR
                        getContentResolver().insert(PodcastProviderContract.EPISODE_LIST_URI,content);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch(IllegalArgumentException e) {
                //  CASO A URI SEJA INVÁLIDA
                Toast.makeText(getApplicationContext(), "URI Inválida", Toast.LENGTH_SHORT).show();
            }
            return itemList;
        }

        @Override
        protected void onPostExecute(List<ItemFeed> feed) {
            Toast.makeText(getApplicationContext(), "terminando...", Toast.LENGTH_SHORT).show();
            //Cursor
            Cursor c = getContentResolver().query(PodcastProviderContract.EPISODE_LIST_URI,
                    PodcastProviderContract.ALL_COLUMNS,
                    null,
                    null,
                    null);
            //ADICIONANDO INFORMAÇÃO DO BANCO DE DADOS NO ARRAYLIST QUE SERÁ ENVIADO PARA O ADAPTER,ATUALIZAÇÃO CONSTANTE
            // TODA VEZ QUE ENTRA NA TELA EXERCICIO 11
            List<ItemFeed> mArrayList = new ArrayList<ItemFeed>();
            if (c.moveToFirst()){
                do{
                    String title = c.getString(c.getColumnIndex("title"));
                    String pubDate = c.getString(c.getColumnIndex("pubDate"));
                    String description = c.getString(c.getColumnIndex("description"));
                    String link = c.getString(c.getColumnIndex("link"));
                    String downloadLink = c.getString(c.getColumnIndex("downloadLink"));
                    mArrayList.add(new ItemFeed(title,link,pubDate,description,downloadLink));
                }while(c.moveToNext());
            }
            c.close();

            //adapter Personalizado
            XmlFeedAdapter adapter = new XmlFeedAdapter(getMainActivity().getApplicationContext(),R.layout.itemlista, mArrayList);
            adapter.setMainActivity(getMainActivity());
            //atualizar o list view
            items.setAdapter(adapter);
            items.setTextFilterEnabled(true);

        }
    }

    //TODO Opcional - pesquise outros meios de obter arquivos da internet
    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
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

    //adicionando um singleton para o adapter acessar o bind
    public PodcastService getPodcastService(){
        return this.podcastService;
    }
    private PodcastService podcastService;

    private boolean isBound = false;

    //instanciando o bind
    private ServiceConnection sConn = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("BIDING", "Binding com service");
            podcastService = ((PodcastService.LocalBinder) service).getService();
            isBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("UNBIDING", "UNBinding com service");
            podcastService = null;
            isBound = false;
        }
    };

    @Override
    protected void onDestroy() {

        super.onDestroy();
        unbindService(sConn);
        isBound = false;
    }

}
