/*
 * Copyright (C) 2023 The LibreMobileOS Foundation
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

package com.android.settings.libremobileos.buttons;

import android.content.Context;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

public class ButtonPreferenceController extends BasePreferenceController {

    public static final String KEY = "button_settings";

    private Context mContext;

    public ButtonPreferenceController(Context context, String key) {
        super(context, key);
        mContext = context;
    }

    public ButtonPreferenceController(Context context) {
        this(context, KEY);
        mContext = context;
    }

    @Override
    public int getAvailabilityStatus() {
        boolean exists = mContext.getResources()
                .getBoolean(R.bool.config_show_lmo_features_settings);
        return (exists ? AVAILABLE : UNSUPPORTED_ON_DEVICE);
    }

}
