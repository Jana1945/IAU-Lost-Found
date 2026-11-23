package com.example.iaulostfound;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReportDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "iau_lost_found.db";
    private static final int DB_VERSION = 1;

    private static ReportDbHelper instance;
    private final Context appContext;

    public static synchronized ReportDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ReportDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    private ReportDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.appContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ReportContract.TABLE + " ("
                + ReportContract.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ReportContract.COL_TYPE + " TEXT NOT NULL, "
                + ReportContract.COL_TITLE + " TEXT NOT NULL, "
                + ReportContract.COL_CATEGORY + " TEXT NOT NULL, "
                + ReportContract.COL_DESCRIPTION + " TEXT NOT NULL, "
                + ReportContract.COL_DATE + " TEXT NOT NULL, "
                + ReportContract.COL_BUILDING + " TEXT NOT NULL, "
                + ReportContract.COL_SPECIFIC_LOCATION + " TEXT NOT NULL, "
                + ReportContract.COL_STATUS + " TEXT NOT NULL, "
                + ReportContract.COL_CREATED_AT + " INTEGER NOT NULL, "
                + ReportContract.COL_UPDATED_AT + " INTEGER NOT NULL, "
                + ReportContract.COL_COLLECTION_POINT + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Development only: rebuild.
        db.execSQL("DROP TABLE IF EXISTS " + ReportContract.TABLE);
        onCreate(db);
    }

    /** @return new row id, or -1 if the insert failed. */
    public long insertReport(Report r) {
        long now = System.currentTimeMillis();
        ContentValues v = new ContentValues();
        v.put(ReportContract.COL_TYPE, r.getType());
        v.put(ReportContract.COL_TITLE, r.getTitle());
        v.put(ReportContract.COL_CATEGORY, r.getCategory());
        v.put(ReportContract.COL_DESCRIPTION, r.getDescription());
        v.put(ReportContract.COL_DATE, r.getDate());
        v.put(ReportContract.COL_BUILDING, r.getBuilding());
        v.put(ReportContract.COL_SPECIFIC_LOCATION, r.getSpecificLocation());
        v.put(ReportContract.COL_STATUS, r.getStatus());
        v.put(ReportContract.COL_CREATED_AT, now);
        v.put(ReportContract.COL_UPDATED_AT, now);
        v.put(ReportContract.COL_COLLECTION_POINT, r.getCollectionPoint());

        long id = getWritableDatabase().insert(ReportContract.TABLE, null, v);
        if (id != -1) {
            appContext.getContentResolver().notifyChange(ReportContract.CONTENT_URI, null);
        }
        return id;
    }
}