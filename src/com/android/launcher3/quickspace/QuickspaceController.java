/*
 * Copyright (C) 2018 CypherOS
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
package com.android.launcher3.quickspace;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.provider.Settings;

import com.android.launcher3.quickspace.OmniJawsClient;
import com.android.launcher3.quickspace.OmniJawsClient.WeatherInfo;

import java.util.ArrayList;

public class QuickspaceController implements OmniJawsClient.OmniJawsObserver {

    public final ArrayList<OnDataListener> mListeners = new ArrayList();

    private Context mContext;
    private final Handler mHandler;
    private QuickEventsController mEventsController;
    private OmniJawsClient mWeatherClient;
    private OmniJawsClient.WeatherInfo mWeatherInfo;

    public interface OnDataListener {
        void onDataUpdated();
    }

    public QuickspaceController(Context context) {
        mContext = context;
        mEventsController = new QuickEventsController(context);
        mHandler = new Handler();
        mWeatherClient = new OmniJawsClient(context);
    }

    public void addListener(OnDataListener listener) {
        mListeners.add(listener);
        listener.onDataUpdated();
    }

    public void removeListener(OnDataListener listener) {
        mListeners.remove(listener);
    }

    public boolean isQuickEvent() {
        return mEventsController.isQuickEvent();
    }

    public QuickEventsController getEventController() {
        return mEventsController;
    }

    public boolean isWeatherAvailable() {
        return mWeatherClient.isOmniJawsServiceInstalled();
    }

    @Override
    public void weatherError(int errorReason) {
    }

    @Override
    public void weatherUpdated() {
        queryAndUpdateWeather();
    }

    private void queryAndUpdateWeather() {
        if (mWeatherClient != null) {
            mWeatherClient.queryWeather();
        }
    }

    public void onWeatherUpdated(WeatherInfo weatherInfo) {
        mWeatherInfo = weatherInfo;
        notifyListeners();
    }

    public void notifyListeners() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (OnDataListener list : mListeners) {
                    list.onDataUpdated();
                }
            }
        });
    }
}
