package com.example.iaulostfound;

/** Lost report screen: everything is shared in BaseReportActivity. */
public class ReportLostActivity extends BaseReportActivity {

    @Override protected String getReportType() { return Report.TYPE_LOST; }
    @Override protected String getInitialStatus() { return Report.STATUS_LOST; }
    @Override protected int getScreenTitleRes() { return R.string.title_report_lost; }
    @Override protected int getDateLabelRes() { return R.string.label_date_last_seen; }
}