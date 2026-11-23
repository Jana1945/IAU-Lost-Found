package com.example.iaulostfound;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/** Read-only, not exported, summary columns only. */
public class ReportSummaryProvider extends ContentProvider {

    private static final int REPORTS = 1;
    private static final int REPORT_ID = 2;

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final HashMap<String, String> PROJECTION_MAP = new HashMap<>();

    static {
        MATCHER.addURI(ReportContract.AUTHORITY, "reports", REPORTS);
        MATCHER.addURI(ReportContract.AUTHORITY, "reports/#", REPORT_ID);
        for (String col : ReportContract.PUBLIC_COLUMNS) {
            PROJECTION_MAP.put(col, col);
        }
    }

    private ReportDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = ReportDbHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(ReportContract.TABLE);
        qb.setProjectionMap(PROJECTION_MAP);
        qb.setStrict(true);

        switch (MATCHER.match(uri)) {
            case REPORTS:
                break;
            case REPORT_ID:
                qb.appendWhere(ReportContract.COL_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        String[] columns = (projection == null) ? ReportContract.PUBLIC_COLUMNS : projection;
        String order = TextUtils.isEmpty(sortOrder)
                ? ReportContract.COL_CREATED_AT + " DESC" : sortOrder;

        String limit = uri.getQueryParameter("limit");
        if (limit != null && !limit.matches("\\d{1,3}")) {
            throw new IllegalArgumentException("Invalid limit");
        }

        Cursor cursor = qb.query(dbHelper.getReadableDatabase(), columns, selection,
                selectionArgs, null, null, order, limit);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (MATCHER.match(uri)) {
            case REPORTS:
                return "vnd.android.cursor.dir/vnd." + ReportContract.AUTHORITY + ".report";
            case REPORT_ID:
                return "vnd.android.cursor.item/vnd." + ReportContract.AUTHORITY + ".report";
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Read-only provider");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Read-only provider");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Read-only provider");
    }
}