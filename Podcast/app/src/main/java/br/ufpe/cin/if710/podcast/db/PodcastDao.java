package br.ufpe.cin.if710.podcast.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

/**
 * Created by Matheus on 09/12/2017.
 */

@Dao
public interface PodcastDao {

    @Insert(onConflict = OnConflictStrategy.ABORT) //DEFAULT
    public void insertPodcast(Podcast podcast);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public void insertPodcasts(Podcast ... podcasts);

    @Update
    public void updatePodcasts(Podcast... podcasts);

    @Delete
    public void deletePodcasts(Podcast... podcasts);


    @Query("SELECT * FROM podcast")
    public Podcast[] loadAllPodcast();
}
