package de.jadehs.mvl.settings.preferences;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDialogFragmentCompat;

import org.joda.time.Period;

import de.jadehs.mvl.R;

public class TimePreferenceDialog extends PreferenceDialogFragmentCompat {


    @NonNull
    public static TimePreferenceDialog newInstance(String key) {
        final TimePreferenceDialog
                fragment = new TimePreferenceDialog();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    private TimePicker picker;

    @Nullable
    @Override
    protected View onCreateDialogView(@NonNull Context context) {
        return super.onCreateDialogView(context);
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);

        picker = view.findViewById(R.id.preference_time_picker);
        TimePreference preference = getTimePreference();
        picker.setIs24HourView(true);

        int hour = preference.getPeriod().getHours();
        int minute = preference.getPeriod().getMinutes();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            picker.setHour(hour);
            picker.setMinute(minute);
        } else {
            picker.setCurrentHour(hour);
            picker.setCurrentMinute(minute);
        }

    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int hour = picker.getCurrentHour();
            int minute = picker.getCurrentMinute();
            getTimePreference().setTime(new Period(hour, minute, 0, 0));
        }
    }


    private TimePreference getTimePreference() {
        return (TimePreference) getPreference();
    }
}
