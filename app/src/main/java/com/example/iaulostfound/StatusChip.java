package com.example.iaulostfound;

import android.widget.TextView;

import androidx.core.content.ContextCompat;

public final class StatusChip {

    private StatusChip() {}

    public static void apply(TextView chip, String status) {
        int label;
        int background;
        int textColor;

        if (Report.STATUS_FOUND.equals(status)) {
            label = R.string.status_found;
            background = R.drawable.bg_chip_found;
            textColor = R.color.colorFound;
        } else if (Report.STATUS_RETURNED.equals(status)) {
            label = R.string.status_returned;
            background = R.drawable.bg_chip_returned;
            textColor = R.color.colorReturned;
        } else if (Report.STATUS_PENDING.equals(status)) {
            label = R.string.status_pending;
            background = R.drawable.bg_chip_pending;
            textColor = R.color.colorTextSecondary;
        } else { // LOST (default)
            label = R.string.status_lost;
            background = R.drawable.bg_chip_lost;
            textColor = R.color.colorLost;
        }

        chip.setText(label);
        chip.setBackgroundResource(background);
        chip.setTextColor(ContextCompat.getColor(chip.getContext(), textColor));
    }
}