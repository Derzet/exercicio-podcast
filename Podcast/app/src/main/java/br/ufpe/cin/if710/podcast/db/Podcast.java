package br.ufpe.cin.if710.podcast.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created by Matheus on 09/12/2017.
 */
@Entity(tableName = "podcast")
public class Podcast {


    @PrimaryKey(autoGenerate = true)
        private int id;

        private String title;
        private String link;
        private  String pubDate;
        private String description;
        private String downloadLink;
        private String downloadUri;
        private String state;
        private String time;


        public Podcast(String title, String link, String pubDate, String description, String downloadLink) {
            this.title = title;
            this.link = link;
            this.pubDate = pubDate;
            this.description = description;
            this.downloadLink = downloadLink;
        }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }




}
