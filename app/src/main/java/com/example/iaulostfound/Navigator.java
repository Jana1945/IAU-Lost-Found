package com.example.iaulostfound;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import com.google.android.material.snackbar.Snackbar;

public final class Navigator {

    private Navigator() {}

    public static final String BROWSE = "com.example.iaulostfound.BrowseActivity";
    public static final String ITEM_DETAILS = "com.example.iaulostfound.ItemDetailsActivity";
    public static final String MY_REPORTS = "com.example.iaulostfound.MyReportsActivity";
    public static final String COLLECTION_POINTS = "com.example.iaulostfound.CollectionPointsActivity";
    public static final String SETTINGS = "com.example.iaulostfound.SettingsActivity";

    public static void open(Activity from, String className) {
        openWithReportId(from, className, -1);
    }

    public static void openWithReportId(Activity from, String className, long reportId) {
        Intent intent = new Intent();
        intent.setClassName(from, className);
        if (reportId >= 0) {
            intent.putExtra(Report.EXTRA_REPORT_ID, reportId);
        }
        try {
            from.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Snackbar.make(from.findViewById(android.R.id.content),
                    R.string.screen_not_ready, Snackbar.LENGTH_SHORT).show();
        }
    }
}