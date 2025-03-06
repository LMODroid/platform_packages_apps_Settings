/*
 * Copyright (C) 2017 The Android Open Source Project
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
 * limitations under the License
 */
package com.android.settings.system;

import android.content.Context;
import android.content.Intent;
import android.os.UserManager;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.Settings;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.fuelgauge.BatteryBroadcastReceiver;
import com.android.settings.fuelgauge.BatteryInfo;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class FactoryResetPreferenceController extends BasePreferenceController implements
        LifecycleObserver, OnStart, OnStop {

    private final UserManager mUm;

    private final BatteryBroadcastReceiver mBatteryBroadcastReceiver;
    private boolean mIsBatteryPresent = true;
    private BatteryInfo mBatteryInfo;

    public FactoryResetPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
        mUm = (UserManager) context.getSystemService(Context.USER_SERVICE);
        mBatteryBroadcastReceiver = new BatteryBroadcastReceiver(mContext);
        mBatteryBroadcastReceiver.setBatteryChangedListener(type -> {
            if (type == BatteryBroadcastReceiver.BatteryUpdateType.BATTERY_NOT_PRESENT) {
                mIsBatteryPresent = false;
            }
            BatteryInfo.getBatteryInfo(mContext, info -> {
                mBatteryInfo = info;
            }, true /* shortString */);
        });
    }

    /** Hide "Factory reset" settings for secondary users. */
    @Override
    public int getAvailabilityStatus() {
        return mUm.isAdminUser() ? AVAILABLE : DISABLED_FOR_USER;
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (mPreferenceKey.equals(preference.getKey())) {
            // If battery level is less than 15% and not charger plugged in.
            // then don't proceed to factory reset.
            if (mIsBatteryPresent && mBatteryInfo != null && mBatteryInfo.batteryLevel < 15
                    && mBatteryInfo.pluggedStatus == 0) {
                showBatteryLowDialog();
                return true;
            }
            final Intent intent = new Intent(mContext, Settings.FactoryResetActivity.class);
            mContext.startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        mBatteryBroadcastReceiver.register();
    }

    @Override
    public void onStop() {
        mBatteryBroadcastReceiver.unRegister();
    }

    private void showBatteryLowDialog() {
        new AlertDialog.Builder(mContext)
            .setTitle(R.string.factory_reset_battery_low_dialog_title)
            .setMessage(R.string.factory_reset_battery_low_dialog_message)
            .setPositiveButton(R.string.factory_reset_battery_low_dialog_button_text,
                (dialog, which) -> {
                    dialog.dismiss();
                }
            )
            .show();
    }
}
