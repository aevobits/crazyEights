package com.aevobits.games.crazyeights;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by vito on 28/11/16.
 */

public class ActionResolverAndroid implements ActionResolver {
    Handler handler;
    Context context;

    public ActionResolverAndroid(Context context) {
        handler = new Handler();
        this.context = context;
    }

    public void showToast(final CharSequence text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

}