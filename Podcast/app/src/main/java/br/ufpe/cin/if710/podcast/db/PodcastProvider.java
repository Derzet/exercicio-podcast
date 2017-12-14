package br.ufpe.cin.if710.podcast.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import static java.lang.String.valueOf;

public class PodcastProvider extends ContentProvider {
    private SQLiteDatabase database; // instancia do db
    private AppDatabase databaseRoom;

    public PodcastProvider() {
    }

    @Override
    public boolean onCreate() {
        PodcastDBHelper db = PodcastDBHelper.getInstance(getContext());
        database = db.getWritableDatabase();
        databaseRoom = AppDatabase.getAppDatabase(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uri.getLastPathSegment().equals(PodcastProviderContract.EPISODE_TABLE)) {
            // long rowId = database.insertOrThrow(PodcastProviderContract.EPISODE_TABLE, null, values);
            //return Uri.withAppendedPath(PodcastProviderContract.EPISODE_LIST_URI, Long.toString(rowId));
            String title = values.getAsString(PodcastProviderContract.TITLE);
            String date = values.getAsString(PodcastProviderContract.DATE);
            String link = values.getAsString(PodcastProviderContract.LINK);
            String description = values.getAsString(PodcastProviderContract.DESCRIPTION);
            String downloadLink = values.getAsString(PodcastProviderContract.DOWNLOAD_LINK);
            String uriValue = values.getAsString(PodcastProviderContract.URI);
            String state = values.getAsString(PodcastProviderContract.STATE);
            String time = values.getAsString(PodcastProviderContract.TIME);

            Podcast podcast = new Podcast(title, link, date, description, downloadLink, uriValue, state, time);
            // Log.d("Tag", podcast.getDownloadLink());
            final long id = databaseRoom.podcastDao().insertPodcast(podcast);
            return ContentUris.withAppendedId(uri, id);
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(PodcastProviderContract.EPISODE_TABLE);
        Cursor cursor;

        if (selection != null) {
            cursor = databaseRoom.podcastDao().selectById(Long.parseLong(selection));
            return cursor;
        } else {
            // Usando SQLiteQueryBuilder para criar uma seleção
            Log.d("PROVIDER", uri.getLastPathSegment());
            if (uri.getLastPathSegment().equals(PodcastProviderContract.EPISODE_TABLE)) {
                cursor = databaseRoom.podcastDao().selectAll();
                return cursor;
            } else {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }

        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        if (uri.getLastPathSegment().equals(PodcastProviderContract.EPISODE_TABLE)) {
           if (selectionArgs==null){
                // long id = values.getAsLong(PodcastProviderContract._ID);
                String title = values.getAsString(PodcastProviderContract.TITLE);
                String date = values.getAsString(PodcastProviderContract.DATE);
                String link = values.getAsString(PodcastProviderContract.LINK);
                String description = values.getAsString(PodcastProviderContract.DESCRIPTION);
                String downloadLink = values.getAsString(PodcastProviderContract.DOWNLOAD_LINK);
                String uriValue = values.getAsString(PodcastProviderContract.URI);
                String state = values.getAsString(PodcastProviderContract.STATE);
                String time = values.getAsString(PodcastProviderContract.TIME);

                Podcast podcast = new Podcast(title, link, date, description, downloadLink, uriValue, state, time);
                Log.d("States", podcast.getState() + " ");
                podcast.setId(Long.parseLong(selection));
                Log.d("ID", valueOf(podcast.getId()));

                int count = databaseRoom.podcastDao().updatePodcast(podcast);
                //         Log.d("Count", valueOf(count));
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }

            if (selectionArgs[0].equals("Status")) {

                Log.d("Status", "Entrou");
                String status = values.getAsString(PodcastProviderContract.STATE);
                return databaseRoom.podcastDao().UpdateState(Long.parseLong(selection), status);
            } else if (selectionArgs[0].equals("Time")) {

                Log.d("Time", "Entrou");
                long time = values.getAsLong(PodcastProviderContract.TIME);
                return databaseRoom.podcastDao().UpdateTime(Long.parseLong(selection), time);
            }
        }
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
  /*  public int updateStatus(Uri uri, ContentValues values, String selection,
                            String[] selectionArgs) {
        String status = values.getAsString(PodcastProviderContract.STATE);
        return databaseRoom.podcastDao().UpdateState(Long.parseLong(selection), status);

    }
    public int updateTime(Uri uri, ContentValues values, String selection,
                            String[] selectionArgs) {
        long time = values.getAsLong(PodcastProviderContract.TIME);
        return databaseRoom.podcastDao().UpdateTime(Long.parseLong(selection), time);

    }
    */
}