package br.ufpe.cin.if710.podcast;

import br.ufpe.cin.if710.podcast.db.PodcastProvider;

/**
 * Created by gabri on 13/12/2017.
 */

public class ProviderTest {
    PodcastProvider p;
/*
    @Before
    public void init(){
        p = new PodcastProvider();
    }

    @Test
    public void queryInitProvider(){
        Cursor c = p.query(PodcastProviderContract.EPISODE_LIST_URI,
                PodcastProviderContract.ALL_COLUMNS,
                null,
                null,
                null);
        assertEquals(0, c.getCount());
    }
    @Test
    public void insertProvider(){
        ContentValues content = new ContentValues();
        content.put(PodcastProviderContract.TITLE, "t");
        content.put(PodcastProviderContract.DATE, "d");
        content.put(PodcastProviderContract.LINK, "a"); //revisar o parser
        content.put(PodcastProviderContract.DESCRIPTION, "desc");
        content.put(PodcastProviderContract.DOWNLOAD_LINK, "f");
        content.put(PodcastProviderContract.URI, " ");
        content.put(PodcastProviderContract.STATE,"BAIXAR");
        content.put(PodcastProviderContract.TIME,"0");
        //INSERINDO O ITEM COM STATUS BAIXAR
        p.insert(PodcastProviderContract.EPISODE_LIST_URI,content);

        Cursor c = p.query(PodcastProviderContract.EPISODE_LIST_URI,
                PodcastProviderContract.ALL_COLUMNS,
                null,
                null,
                null);
        assertEquals(1, c.getCount());
    }*/
}
