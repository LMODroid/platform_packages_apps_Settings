/*
 * Copyright (C) 2019 The Android Open Source Project
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

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for changing vibrate for calls options.
 */
public class VibrateForCallsPreferenceFragment extends DashboardFragment
        implements VibrateForCallsRadioButtonPreferenceController.OnChangeListener {

    private static final String TAG = "VibrateForCallsPreferenceFragment";

    @VisibleForTesting
    static final String KEY_NEVER_VIBRATE = "never_vibrate";
    @VisibleForTesting
    static final String KEY_ALWAYS_VIBRATE = "always_vibrate";
    @VisibleForTesting
    static final String KEY_RAMPING_RINGER = "ramping_ringer";

    private static final int ON = 1;
    private static final int OFF = 0;

    private static final List<AbstractPreferenceController> sControllers = new ArrayList<>();

    private static List<AbstractPreferenceController> buildPreferenceControllers(Context context,
            Lifecycle lifecycle) {
        if (sControllers.size() == 0) {
            final String[] vibratorKeys = {KEY_NEVER_VIBRATE, KEY_ALWAYS_VIBRATE, KEY_RAMPING_RINGER};

            for (int i = 0; i < vibratorKeys.length; i++) {
                sControllers.add(new VibrateForCallsRadioButtonPreferenceController(
                        context, lifecycle, vibratorKeys[i]));
            }
        }
        return sControllers;
    }

    @Override
    public void onCheckedChanged(Preference preference) {
        for (AbstractPreferenceController controller : sControllers) {
            controller.updateState(preference);
        }
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.vibrate_for_calls_settings;
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.VIBRATE_FOR_CALLS;
    }

    @Override
    public void onResume() {
        super.onResume();

        for (AbstractPreferenceController controller :
                buildPreferenceControllers(getPrefContext(), getSettingsLifecycle())) {
            ((VibrateForCallsRadioButtonPreferenceController) controller).setOnChangeListener(this);
            ((VibrateForCallsRadioButtonPreferenceController) controller).displayPreference(
                    getPreferenceScreen());
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        for (AbstractPreferenceController controller :
                buildPreferenceControllers(getPrefContext(), getSettingsLifecycle())) {
            ((VibrateForCallsRadioButtonPreferenceController) controller).setOnChangeListener(null);
        }
    }
}
