package com.example.iaulostfound;

/** Found report screen: same form + optional collection point / handover note. */
public class ReportFoundActivity extends BaseReportActivity {

    @Override protected String getReportType() { return Report.TYPE_FOUND; }
    @Override protected String getInitialStatus() { return Report.STATUS_FOUND; }
    @Override protected int getScreenTitleRes() { return R.string.title_report_found; }
    @Override protected int getDateLabelRes() { return R.string.label_date_found; }
    @Override protected boolean hasCollectionPoint() { return true; }
}