package com.example.iaulostfound;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Shared form logic for ReportLostActivity and ReportFoundActivity.
 * Same layout, same field order, same validation.
 */
public abstract class BaseReportActivity extends AppCompatActivity {

    private static final String TAG = "BaseReportActivity";
    private static final String STATE_DATE_MILLIS = "state_date_millis";

    protected abstract String getReportType();
    protected abstract String getInitialStatus();
    @StringRes protected abstract int getScreenTitleRes();
    @StringRes protected abstract int getDateLabelRes();

    /** Only the Found form shows the optional collection point field. */
    protected boolean hasCollectionPoint() { return false; }

    private TextInputLayout tilName, tilCategory, tilDescription, tilDate,
            tilBuilding, tilLocation, tilCollection;
    private TextInputEditText etName, etDescription, etDate, etLocation, etCollection;
    private MaterialAutoCompleteTextView acCategory, acBuilding;

    /** Selected date from the picker (UTC midnight millis). */
    private Long selectedDateMillis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_form);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getScreenTitleRes());
        toolbar.setNavigationOnClickListener(v -> finish());

        bindViews();

        tilDate.setHint(getString(getDateLabelRes()) + " *");
        tilCollection.setVisibility(hasCollectionPoint() ? View.VISIBLE : View.GONE);

        bindDropdown(acCategory, R.array.categories);
        bindDropdown(acBuilding, R.array.buildings);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_DATE_MILLIS)) {
            selectedDateMillis = savedInstanceState.getLong(STATE_DATE_MILLIS);
        }

        clearErrorOnChange(tilName, etName);
        clearErrorOnChange(tilCategory, acCategory);
        clearErrorOnChange(tilDescription, etDescription);
        clearErrorOnChange(tilDate, etDate);
        clearErrorOnChange(tilBuilding, acBuilding);
        clearErrorOnChange(tilLocation, etLocation);

        etDate.setOnClickListener(v -> showDatePicker());
        tilDate.setEndIconOnClickListener(v -> showDatePicker());

        findViewById(R.id.btnSave).setOnClickListener(v -> onSubmit());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedDateMillis != null) {
            outState.putLong(STATE_DATE_MILLIS, selectedDateMillis);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restored text would otherwise filter the dropdown list; re-attach full items.
        bindDropdown(acCategory, R.array.categories);
        bindDropdown(acBuilding, R.array.buildings);
    }

    private void bindViews() {
        tilName = findViewById(R.id.tilName);
        tilCategory = findViewById(R.id.tilCategory);
        tilDescription = findViewById(R.id.tilDescription);
        tilDate = findViewById(R.id.tilDate);
        tilBuilding = findViewById(R.id.tilBuilding);
        tilLocation = findViewById(R.id.tilLocation);
        tilCollection = findViewById(R.id.tilCollection);

        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        etCollection = findViewById(R.id.etCollection);

        acCategory = findViewById(R.id.acCategory);
        acBuilding = findViewById(R.id.acBuilding);
    }

    private void bindDropdown(MaterialAutoCompleteTextView view, @ArrayRes int arrayRes) {
        view.setSimpleItems(arrayRes);
    }

    private void clearErrorOnChange(final TextInputLayout layout, TextView editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) { layout.setError(null); }
        });
    }

    // ------------------------------------------------------------ date picker
    private void showDatePicker() {
        Log.d(TAG, "showDatePicker");
        // Only today or earlier can be selected (no future dates)
        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.before(todayUtcMidnightMillis()))
                .build();

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getDateLabelRes())
                .setCalendarConstraints(constraints);
        if (selectedDateMillis != null) {
            builder.setSelection(selectedDateMillis);
        }

        MaterialDatePicker<Long> picker = builder.build();
        picker.addOnPositiveButtonClickListener(selection -> {
            selectedDateMillis = selection;
            etDate.setText(formatUtc(selection));
        });
        picker.show(getSupportFragmentManager(), "date_picker");
    }

    private static long todayUtcMidnightMillis() {
        Calendar local = Calendar.getInstance();
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.clear();
        utc.set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH));
        return utc.getTimeInMillis();
    }

    private static String formatUtc(long millis) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        return f.format(new Date(millis));
    }

    // ------------------------------------------------------------- validation
    private boolean validate() {
        boolean ok = true;
        // Non-short-circuit &: every field is checked, so all errors show at once.
        ok &= requireText(tilName, etName);
        ok &= requireText(tilCategory, acCategory);
        ok &= requireText(tilDescription, etDescription);
        ok &= validateDate();
        ok &= requireText(tilBuilding, acBuilding);
        ok &= requireText(tilLocation, etLocation);
        return ok;
    }

    private boolean requireText(TextInputLayout layout, TextView field) {
        if (textOf(field).isEmpty()) {
            layout.setError(getString(R.string.error_required));
            return false;
        }
        layout.setError(null);
        return true;
    }

    private boolean validateDate() {
        if (selectedDateMillis == null || textOf(etDate).isEmpty()) {
            tilDate.setError(getString(R.string.error_required));
            return false;
        }
        if (selectedDateMillis > todayUtcMidnightMillis()) {
            tilDate.setError(getString(R.string.error_future_date));
            return false;
        }
        tilDate.setError(null);
        return true;
    }

    private static String textOf(TextView view) {
        return view.getText() == null ? "" : view.getText().toString().trim();
    }

    // ---------------------------------------------------------------- submit
    private void onSubmit() {
        if (!validate()) {
            showMessage(R.string.error_fix_fields);
            return;
        }

        String collection = hasCollectionPoint() ? textOf(etCollection) : "";
        Report report = new Report(
                getReportType(),
                textOf(etName),
                textOf(acCategory),
                textOf(etDescription),
                formatUtc(selectedDateMillis),
                textOf(acBuilding),
                textOf(etLocation),
                getInitialStatus(),
                collection.isEmpty() ? null : collection);

        long id;
        try {
            id = ReportDbHelper.getInstance(this).insertReport(report);
        } catch (SQLiteException e) {
            Log.e(TAG, "Insert failed", e);
            id = -1;
        }

        if (id == -1) {
            // Recoverable error: the form stays untouched so the user does not retype anything.
            Snackbar.make(findViewById(android.R.id.content),
                            R.string.error_save_failed, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_retry, v -> onSubmit())
                    .show();
            return;
        }

        Toast.makeText(this, R.string.msg_report_saved, Toast.LENGTH_SHORT).show();
        finish(); // back to Home, which refreshes in onResume()
    }

    private void showMessage(@StringRes int message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }
}