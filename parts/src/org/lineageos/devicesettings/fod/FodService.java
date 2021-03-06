/*
 * Copyright (C) 2019 The LineageOS Project
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

package org.lineageos.devicesettings.fod;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import vendor.xiaomi.hardware.displayfeature.V1_0.IDisplayFeature;

public class FodService extends Service {

  private static final String TAG = "FodService";
  private static final boolean DEBUG = true;
  private Object mLock = new Object();

  private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      final String action = intent.getAction();
      if (Intent.ACTION_SCREEN_ON.equals(action)) {
        setScreenEffect(Constants.SCREEN_ENHANCE, 2);
        setScreenEffect(Constants.SCREEN_EYECARE, 0);
      }
    }
  };

  public void setScreenEffect(int mode, int value) {
    synchronized (this.mLock) {
      try {
        IDisplayFeature mDisplayFeature = IDisplayFeature.getService();
        mDisplayFeature.setFeature(0, mode, value, 255);
        if (DEBUG) {
          Log.d(TAG, "setScreenEffect mode=" + mode + " value=" + value);
        }
      } catch (RemoteException e) {
        // Do nothing
      }
    }
  }

  @Override
  public void onCreate() {
    registerReceiver();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (DEBUG)
      Log.d(TAG, "Starting service");
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    if (DEBUG)
      Log.d(TAG, "Destroying service");
    this.unregisterReceiver(mIntentReceiver);
    super.onDestroy();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  private void registerReceiver() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(Intent.ACTION_SCREEN_ON);
    this.registerReceiver(mIntentReceiver, filter);
  }
}
