package br.ufpe.cin.if710.podcast.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class PodcastProvider extends ContentProvider {
    private SQLiteDatabase database; // instancia do db


    public PodcastProvider() {
    }

    @Override
    public boolean onCreate() {
        PodcastDBHelper db = PodcastDBHelper.getInstance(getContext());
        database = db.getWritableDatabase();
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
            long rowId = database.insertOrThrow(PodcastProviderContract.EPISODE_TABLE, null, values);
            return Uri.withAppendedPath(PodcastProviderContract.EPISODE_LIST_URI, Long.toString(rowId));
        }else{
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Usando SQLiteQueryBuilder para criar uma seleção
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(PodcastProviderContract.EPISODE_TABLE);
        Cursor cursor;
        Log.d("PROVIDER",uri.getLastPathSegment());
        if (uri.getLastPathSegment().equals(PodcastProviderContract.EPISODE_TABLE)){
             cursor = queryBuilder.query(
                    database,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

        }else{
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
          int count = 0;

        if (uri.getLastPathSegment().equals(PodcastProviderContract.EPISODE_TABLE)){
            count = database.update(
                    "episodes",
                    values,
                      PodcastProviderContract._ID
                              +" = "
                              + selection,
                    selectionArgs

            );

        }else{
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }


        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
