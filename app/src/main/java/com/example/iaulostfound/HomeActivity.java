package com.example.iaulostfound;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final String UNIVERSITY_URL = "https://www.iau.edu.sa";

    private LinearLayout recentContainer;
    private TextView emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_home);

        recentContainer = findViewById(R.id.recentContainer);
        emptyState = findViewById(R.id.emptyState);

        // Explicit Intents to the two screens of this package
        findViewById(R.id.tileReportLost).setOnClickListener(v ->
                startActivity(new Intent(this, ReportLostActivity.class)));
        findViewById(R.id.tileReportFound).setOnClickListener(v ->
                startActivity(new Intent(this, ReportFoundActivity.class)));

        // Screens owned by Packages B / C
        findViewById(R.id.tileBrowse).setOnClickListener(v -> Navigator.open(this, Navigator.BROWSE));
        findViewById(R.id.btnSeeAll).setOnClickListener(v -> Navigator.open(this, Navigator.BROWSE));
        findViewById(R.id.tileMyReports).setOnClickListener(v -> Navigator.open(this, Navigator.MY_REPORTS));
        findViewById(R.id.btnCollectionPoints).setOnClickListener(v -> Navigator.open(this, Navigator.COLLECTION_POINTS));
        findViewById(R.id.btnSettings).setOnClickListener(v -> Navigator.open(this, Navigator.SETTINGS));

        // Optional implicit Intent: official university web page
        findViewById(R.id.btnUniversitySite).setOnClickListener(v -> openUniversityWebsite());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        loadRecentItems(); // refresh after saving a report
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    /** Content Provider demo: latest 3 reports through ContentResolver + content URI. */
    private void loadRecentItems() {
        recentContainer.removeAllViews();

        Uri uri = ReportContract.CONTENT_URI.buildUpon()
                .appendQueryParameter("limit", "3")
                .build();
        String[] projection = {
                ReportContract.COL_ID, ReportContract.COL_TITLE, ReportContract.COL_STATUS,
                ReportContract.COL_BUILDING, ReportContract.COL_DATE
        };

        try (Cursor c = getContentResolver().query(
                uri, projection, null, null, ReportContract.COL_CREATED_AT + " DESC")) {

            if (c == null || c.getCount() == 0) {
                emptyState.setVisibility(View.VISIBLE);
                return;
            }
            emptyState.setVisibility(View.GONE);

            LayoutInflater inflater = getLayoutInflater();
            while (c.moveToNext()) {
                final long id = c.getLong(c.getColumnIndexOrThrow(ReportContract.COL_ID));
                View card = inflater.inflate(R.layout.item_recent_report, recentContainer, false);

                ((TextView) card.findViewById(R.id.itemTitle))
                        .setText(c.getString(c.getColumnIndexOrThrow(ReportContract.COL_TITLE)));
                ((TextView) card.findViewById(R.id.itemLocation))
                        .setText(c.getString(c.getColumnIndexOrThrow(ReportContract.COL_BUILDING)));
                ((TextView) card.findViewById(R.id.itemDate))
                        .setText(c.getString(c.getColumnIndexOrThrow(ReportContract.COL_DATE)));
                StatusChip.apply((TextView) card.findViewById(R.id.itemStatus),
                        c.getString(c.getColumnIndexOrThrow(ReportContract.COL_STATUS)));

                card.setOnClickListener(v ->
                        Navigator.openWithReportId(this, Navigator.ITEM_DETAILS, id));
                recentContainer.addView(card);
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "Could not load recent items", e);
            emptyState.setVisibility(View.VISIBLE);
        }
    }

    private void openUniversityWebsite() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(UNIVERSITY_URL));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Snackbar.make(findViewById(android.R.id.content),
                    R.string.no_app_for_link, Snackbar.LENGTH_SHORT).show();
        }
    }
}
