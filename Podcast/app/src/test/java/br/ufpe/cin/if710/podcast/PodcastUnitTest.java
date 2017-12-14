package br.ufpe.cin.if710.podcast;

import org.junit.Test;

import br.ufpe.cin.if710.podcast.db.Podcast;

/**
 * Created by gabri on 11/12/2017.
 */

public class PodcastUnitTest {
    @Test
    public void testPodcastObject() throws Exception{
        Podcast podcast;
        podcast = new Podcast("Title", "Link", "Date", "Description", "DownloadLInk," ,
             "DownloadUri", "State", "Time");
        assert podcast != null;
    }
    @Test
    public void testAutoIncIdPodcast() throws Exception{
        Podcast podcast = new Podcast("Title", "Link", "Date", "Description", "DownloadLInk," ,
                "DownloadUri", "State", "Time");
        assert podcast.getId() != 0;
    }


}
