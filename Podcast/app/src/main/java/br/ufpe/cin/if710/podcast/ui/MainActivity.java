package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
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
import br.ufpe.cin.if710.podcast.ui.adapter.XmlFeedAdapter;


public class MainActivity extends Activity {


    //ao fazer envio da resolucao, use este link no seu codigo!
    private final String RSS_FEED = "http://leopoldomt.com/if710/fronteirasdaciencia.xml";
    private ContentResolver cr ; //adicionado o content
    //TODO teste com outros links de podcast
    private ListView items;
    private final String ACTION_NOTIFICATION = "br.ufpe.cin.if710.podcast.action.NOTIFICATION";
    //conectando meu broadcastRecieber
    //private PodcastBroadCastReciever receiver;
    BroadcastReceiver receiver =  new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ACTION_NOTIFICATION)) {
                Log.d("Broadcast", "IntentBroadCast");
                Toast.makeText(context,"Download Acabou!", Toast.LENGTH_SHORT).show();
                int position = Integer.parseInt(intent.getStringExtra("position"));
                String state = intent.getStringExtra("state");

                Button button  = items.getChildAt(position-1).findViewById(R.id.item_action);
                button.setText(state);
                // Log.d("Broadcast", "IntentBroadCast");
                //context.getClass().items.

            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cr = getContentResolver(); //instancia para a activity
        items = (ListView) findViewById(R.id.items);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
        XmlFeedAdapter adapter = (XmlFeedAdapter) items.getAdapter();
        adapter.clear();
        unregisterReceiver(receiver);

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
                     // Log.d("Main Activity", "Item inserido");
                        getContentResolver().insert(PodcastProviderContract.EPISODE_LIST_URI,content);
                        //  Log.d("Main Activity", "insert funcinando")
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch(IllegalArgumentException e) {
                //  Log.d("Main Activity", "ERROR");
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
            List<ItemFeed> mArrayList = new ArrayList<ItemFeed>();
            if (c.moveToFirst()){
                do{
                    String title = c.getString(c.getColumnIndex("title"));
                    String pubDate = c.getString(c.getColumnIndex("pubDate"));
                    String description = c.getString(c.getColumnIndex("description"));
                    String link = c.getString(c.getColumnIndex("link"));
                    String downloadLink = c.getString(c.getColumnIndex("downloadLink"));

                    mArrayList.add(new ItemFeed(title,link,pubDate,description,downloadLink));
                    // do what ever you want here
                }while(c.moveToNext());
            }
            c.close();


            //dapter Personalizado
            XmlFeedAdapter adapter = new XmlFeedAdapter(getApplicationContext(), R.layout.itemlista, mArrayList);

            //atualizar o list view
            items.setAdapter(adapter);
            items.setTextFilterEnabled(true);


/*
            //ação quando clicando no listener
            items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   // final TextView title_id = (TextView) view.findViewById(R.id.item_title);
                    XmlFeedAdapter adapter = (XmlFeedAdapter) parent.getAdapter();
                    ItemFeed item = adapter.getItem(position);
                    String msg = item.getTitle() + " " + "Link:"+ item.getDownloadLink();
                   // Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(getApplicationContext(),EpisodeDetailActivity.class);
                    i.putExtra("itemFeed", item);
                    startActivity(i);

                }


            });
*/
        }
    }


    //TODO Opcional - pesquise outros meios de obter arquivos da internet
    public String getRssFeed(String feed) throws IOException {
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
