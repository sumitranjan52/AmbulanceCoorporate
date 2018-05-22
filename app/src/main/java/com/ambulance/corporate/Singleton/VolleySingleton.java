package com.ambulance.corporate.Singleton;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.ambulance.corporate.CustomClasses.CustomHurlStack;
import com.ambulance.corporate.CustomClasses.OkHttpHurlStack;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;

/**
 * Created by sumit on 22-Feb-18.
 */

public class VolleySingleton {

    private static VolleySingleton mInstance;
    private Context context;
    private RequestQueue requestQueue;

    private VolleySingleton(Context context) {
        this.context = context;
        this.requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            HttpStack httpStack = new CustomHurlStack();
            if (Build.VERSION.SDK_INT > 19) {
                httpStack = new CustomHurlStack();
            } else if (Build.VERSION.SDK_INT >= 15 && Build.VERSION.SDK_INT <= 19) {
                httpStack = new OkHttpHurlStack();
            } else {
                httpStack = new HttpClientStack(AndroidHttpClient.newInstance("Android"));
            }
            requestQueue = Volley.newRequestQueue(context, httpStack);
        }
        return requestQueue;
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        requestQueue.add(request);
    }
}
