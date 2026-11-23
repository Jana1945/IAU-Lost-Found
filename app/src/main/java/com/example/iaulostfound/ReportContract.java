package com.example.iaulostfound;

import android.net.Uri;

public final class ReportContract {

    private ReportContract() {}

    public static final String TABLE = "reports";

    public static final String COL_ID = "_id";
    public static final String COL_TYPE = "type";
    public static final String COL_TITLE = "title";
    public static final String COL_CATEGORY = "category";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_DATE = "date";
    public static final String COL_BUILDING = "building";
    public static final String COL_SPECIFIC_LOCATION = "specificLocation";
    public static final String COL_STATUS = "status";
    public static final String COL_CREATED_AT = "createdAt";
    public static final String COL_UPDATED_AT = "updatedAt";
    public static final String COL_COLLECTION_POINT = "collectionPoint";

    public static final String AUTHORITY = "com.example.iaulostfound.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/reports");

    /** ONLY these non-sensitive columns are exposed through the provider. */
    public static final String[] PUBLIC_COLUMNS = {
            COL_ID, COL_TYPE, COL_TITLE, COL_CATEGORY,
            COL_BUILDING, COL_DATE, COL_STATUS, COL_CREATED_AT
    };
}