/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.sound;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.RadioButtonPreference;

/**
 * Controller for vibrate for calls settings.
 */
public class VibrateForCallsRadioButtonPreferenceController extends BasePreferenceController implements
        LifecycleObserver, RadioButtonPreference.OnClickListener {

    private static final int ON = 1;
    private static final int OFF = 0;

    static final String KEY_NEVER_VIBRATE = "never_vibrate";
    static final String KEY_ALWAYS_VIBRATE = "always_vibrate";
    static final String KEY_RAMPING_RINGER = "ramping_ringer";

    private final ContentResolver mContentResolver;
    private VibrateForCallsRadioButtonPreferenceController.OnChangeListener mOnChangeListener;
    private RadioButtonPreference mPreference;

    public VibrateForCallsRadioButtonPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);

        mContentResolver = context.getContentResolver();
    }

    public VibrateForCallsRadioButtonPreferenceController(Context context, Lifecycle lifecycle,
            String preferenceKey) {
        super(context, preferenceKey);

        mContentResolver = context.getContentResolver();

        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override
    @AvailabilityStatus
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    public void setOnChangeListener(
            VibrateForCallsRadioButtonPreferenceController.OnChangeListener listener) {
        mOnChangeListener = listener;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);

        mPreference = (RadioButtonPreference)
                screen.findPreference(getPreferenceKey());
        mPreference.setOnClickListener(this);
        updateState(mPreference);
    }

    @Override
    public void onRadioButtonClicked(RadioButtonPreference preference) {
        handlePreferenceChange(mPreferenceKey);
        if (mOnChangeListener != null) {
            mOnChangeListener.onCheckedChanged(mPreference);
        }
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);

        // reset RadioButton
        mPreference.setChecked(false);
        updatePreferenceCheckedState(mPreference.getKey());
    }

    /** Listener interface handles checked event. */
    public interface OnChangeListener {
        /** A hook that is called when preference checked. */
        void onCheckedChanged(Preference preference);
    }

    private void updatePreferenceCheckedState(String key) {
        if (TextUtils.equals(key, KEY_ALWAYS_VIBRATE)) {
            mPreference.setChecked(Settings.System.getInt(mContentResolver,
                    Settings.System.VIBRATE_WHEN_RINGING, OFF) == ON);
        } else if (TextUtils.equals(key, KEY_RAMPING_RINGER)) {
            mPreference.setChecked(Settings.Global.getInt(mContentResolver,
                    Settings.Global.APPLY_RAMPING_RINGER, OFF) == ON);
        } else {
            mPreference.setChecked((Settings.Global.getInt(mContentResolver,
                    Settings.Global.APPLY_RAMPING_RINGER, OFF) == OFF) &&
                    (Settings.System.getInt(mContentResolver,
                    Settings.System.VIBRATE_WHEN_RINGING, OFF) == OFF));
        }
    }

    private void handlePreferenceChange(String key) {
        if (TextUtils.equals(key, KEY_ALWAYS_VIBRATE)) {
            Settings.System.putInt(
                    mContentResolver, Settings.System.VIBRATE_WHEN_RINGING, ON);
            Settings.Global.putInt(
                    mContentResolver, Settings.Global.APPLY_RAMPING_RINGER, OFF);
        } else if (TextUtils.equals(key, KEY_RAMPING_RINGER)) {
            Settings.System.putInt(
                    mContentResolver, Settings.System.VIBRATE_WHEN_RINGING, OFF);
            Settings.Global.putInt(
                    mContentResolver, Settings.Global.APPLY_RAMPING_RINGER, ON);
        } else {
            Settings.System.putInt(
                    mContentResolver, Settings.System.VIBRATE_WHEN_RINGING, OFF);
            Settings.Global.putInt(
                    mContentResolver, Settings.Global.APPLY_RAMPING_RINGER, OFF);
        }
    }
}
