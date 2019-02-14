package org.gamega;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;

import java.util.ArrayList;
import java.util.Map;

public class RNAsyncStorageModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public RNAsyncStorageModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNAsyncStorage";
    }

    /**
     * Write string value by key
     *
     * @param key
     * @param value
     */
    @ReactMethod
    public void setItem(String key, String value, final Callback callback) {
        try {
            SharedPreferences sharedPref = getCurrentActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(key, value);
            editor.commit();
            callback.invoke();
        } catch (Exception e) {
            callback.invoke(RNAsyncStorageErrorUtil.getError(null, e.getMessage()), null);
            e.printStackTrace();
        }
    }

    @ReactMethod
    public void multiSet(ReadableArray keyValueArray, final Callback callback) {
        if (keyValueArray == null) {
            callback.invoke(RNAsyncStorageErrorUtil.getInvalidKeyError(null));
        }

        try {
            SharedPreferences sharedPref = getCurrentActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            for (int i = 0; i < keyValueArray.size(); i++) {
                if (keyValueArray.getArray(i).size() != 2) {
                    callback.invoke(RNAsyncStorageErrorUtil.getInvalidValueError(null));
                    return;
                }
                if (keyValueArray.getArray(i).getString(0) == null) {
                    callback.invoke(RNAsyncStorageErrorUtil.getInvalidKeyError(null));
                    return;
                }
                if (keyValueArray.getArray(i).getString(1) == null) {
                    callback.invoke(RNAsyncStorageErrorUtil.getInvalidValueError(null));
                    return;
                }

                editor.putString(keyValueArray.getArray(i).getString(0), keyValueArray.getArray(i).getString(1));

            }
            editor.commit();
            callback.invoke();
        } catch (Exception e) {
            callback.invoke(RNAsyncStorageErrorUtil.getError(null, e.getMessage()), null);
            e.printStackTrace();
        }
    }

    /**
     * Remove value by key
     *
     * @param key
     */
    @ReactMethod
    public void removeItem(String key, final Callback callback) {

        try {
            SharedPreferences sharedPref = getCurrentActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(key);
            editor.commit();
            callback.invoke();
        } catch (Exception e) {
            callback.invoke(RNAsyncStorageErrorUtil.getError(null, e.getMessage()), null);
            e.printStackTrace();
        }
    }

    /**
     * Get string value by key
     *
     * @param key
     * @return string value or null
     */
    @ReactMethod
    public void getItem(String key, final Callback callback) {
        try {
            SharedPreferences sharedPref = getCurrentActivity().getPreferences(Context.MODE_PRIVATE);
            String value = sharedPref.getString(key, null);
            callback.invoke(null, value);
        } catch (Exception e) {
            callback.invoke(RNAsyncStorageErrorUtil.getError(null, e.getMessage()), null);
            e.printStackTrace();
        }
    }

    @ReactMethod
    public void multiGet(final ReadableArray keys, final Callback callback) {
        if (keys == null) {
            callback.invoke(RNAsyncStorageErrorUtil.getInvalidKeyError(null), null);
        }
        try {
            SharedPreferences sharedPref = getCurrentActivity().getPreferences(Context.MODE_PRIVATE);
            WritableArray data = Arguments.createArray();
            for (int i = 0; i < keys.size(); i++) {
                WritableArray row = Arguments.createArray();
                row.pushString(keys.getString(i));
                row.pushString(sharedPref.getString(keys.getString(i), null));
                data.pushArray(row);
            }
            callback.invoke(null, data);
        } catch (Exception e) {
            callback.invoke(RNAsyncStorageErrorUtil.getError(null, e.getMessage()), null);
            e.printStackTrace();
        }
    }

    /**
     * Clear storage
     */
    @ReactMethod
    public void clear(final Callback callback) {
        try {
            SharedPreferences sharedPref = getCurrentActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.commit();
            callback.invoke();
        } catch (Exception e) {
            callback.invoke(RNAsyncStorageErrorUtil.getError(null, e.getMessage()), null);
            e.printStackTrace();
        }
    }


    @ReactMethod
    public void getAllKeys(final Callback callback) {
        try {
            SharedPreferences sharedPref = getCurrentActivity().getPreferences(Context.MODE_PRIVATE);
            Map<String, ?> map = sharedPref.getAll();
            ArrayList<String> arrayList = new ArrayList(map.size());
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                arrayList.add(entry.getKey());
            }

            String[] returnArray = new String[arrayList.size()];
            returnArray = arrayList.toArray(returnArray);
            WritableArray promiseArray = Arguments.createArray();
            for (int i = 0; i < returnArray.length; i++) {
                promiseArray.pushString(returnArray[i]);
            }
            callback.invoke(null, promiseArray);


        } catch (Exception e) {
            callback.invoke(RNAsyncStorageErrorUtil.getError(null, e.getMessage()), null);
            e.printStackTrace();

        }

    }
}