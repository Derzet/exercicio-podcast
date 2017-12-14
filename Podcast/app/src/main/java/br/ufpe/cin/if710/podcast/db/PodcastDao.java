package br.ufpe.cin.if710.podcast.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

/**
 * Created by Matheus on 09/12/2017.
 */

@Dao
public interface PodcastDao {

    @Insert(onConflict = OnConflictStrategy.ABORT) //DEFAULT
    public long insertPodcast(Podcast podcast);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public void insertPodcasts(Podcast ... podcasts);

    @Update
    public int updatePodcast(Podcast podcasts);

    @Delete
    public void deletePodcasts(Podcast... podcasts);


    @Query("SELECT * FROM podcast")
    public Cursor selectAll();

    @Query("SELECT * FROM " + "podcast" + " WHERE " + "id" + " = :id")
    public Cursor selectById (long id);

    @Query("UPDATE podcast SET " + "time" + "= :time" + " WHERE " + "id" + "=:id ")
    public int UpdateTime(long id, long time);

    @Query("UPDATE podcast SET " + "state" + "= :state" + " WHERE " + "id" + "=:id ")
    public int UpdateState(long id, String state);

}
