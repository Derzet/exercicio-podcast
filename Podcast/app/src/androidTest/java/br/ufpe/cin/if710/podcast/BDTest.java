package br.ufpe.cin.if710.podcast;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import br.ufpe.cin.if710.podcast.db.AppDatabase;
import br.ufpe.cin.if710.podcast.db.Podcast;

import static org.junit.Assert.assertEquals;

/**
 * Created by gabri on 13/12/2017.
 */

public class BDTest {
    private AppDatabase databaseRoom;
    private Context context = InstrumentationRegistry.getContext();
    @Before  //necessita de ajustes
    public void iniciaBD() {
        databaseRoom = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
    }

    @Test
    public void bdLimpo(){
        int quantidade = databaseRoom.podcastDao().selectAll().getCount();
        assertEquals(0, quantidade);
    }
    @Test
    public void insertPodcast (){
        databaseRoom.podcastDao().insertPodcast(new Podcast("Title", "Link", "Date", "Description", "DownloadLInk," ,
                "DownloadUri", "State", "Time"));
        int quantidade = databaseRoom.podcastDao().selectAll().getCount();
        assertEquals(1, quantidade);
    }
    @Test
    public void update (){}

    @Test
    public void delete (){}

}
