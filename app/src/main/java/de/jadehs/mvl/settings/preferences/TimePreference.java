package de.jadehs.mvl.settings.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;

import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

import de.jadehs.mvl.R;

public class TimePreference extends DialogPreference {

    private final PeriodFormatter parser;


    private Period period;

    public TimePreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        parser = ISOPeriodFormat.standard();
        setDialogLayoutResource(R.layout.preference_time);
    }

    @Nullable
    @Override
    protected Object onGetDefaultValue(@NonNull TypedArray a, int index) {
        return a.getString(index);
    }

    public Period getPeriod() {
        return period;
    }

    public void setTime(Period period) {
        String timeString = parser.print(period);
        if (callChangeListener(timeString)) {
            this.period = period;

            if (isPersistent())
                persistString(timeString);
        }


    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        super.onSetInitialValue(defaultValue);
        if (defaultValue == null) {
            defaultValue = "PT5H";
        }
        String value = getPersistedString((String) defaultValue);

        this.setTime(parser.parsePeriod(value));
    }
}
