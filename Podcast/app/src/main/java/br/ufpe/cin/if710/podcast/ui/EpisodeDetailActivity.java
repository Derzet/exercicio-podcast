package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;

public class EpisodeDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_detail);
        Intent i = getIntent();
        ItemFeed item = (ItemFeed)i.getSerializableExtra("itemFeed");
        //MAPEANDO OS ATRIBUTOS PARA SEUS  RESPECTIVOS TEXTVIEW
         ((TextView)findViewById(R.id.title)).setText(item.getTitle());
         ((TextView)findViewById(R.id.link)).setText(item.getLink());
         ((TextView) findViewById(R.id.pubDate)).setText(item.getPubDate());
         ((TextView) findViewById(R.id.description)).setText(item.getDescription());
         ((TextView) findViewById(R.id.downloadLink)).setText(item.getDownloadLink());

    }
}
