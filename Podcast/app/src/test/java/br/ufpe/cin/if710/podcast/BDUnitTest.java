package br.ufpe.cin.if710.podcast;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import br.ufpe.cin.if710.podcast.db.AppDatabase;
import br.ufpe.cin.if710.podcast.db.Podcast;

/**
 * Created by gabri on 11/12/2017.
 */

public class BDUnitTest {
    private Context instrumentationCtx;
    private AppDatabase databaseRoom;

    @Before  //necessita de ajustes
    public void iniciaBD(){
        databaseRoom = AppDatabase.getAppDatabase(instrumentationCtx.getApplicationContext());
    }
    @Test
    public void insertPodcast (){

        databaseRoom.podcastDao().insertPodcast(new Podcast("Title", "Link", "Date", "Description", "DownloadLInk," ,
                "DownloadUri", "State", "Time"));
        int quantidade = databaseRoom.podcastDao().selectAll().getCount();
        assert quantidade > 0;
    }

}
