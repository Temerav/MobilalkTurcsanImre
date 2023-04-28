package com.example.myapplication.util;

import android.os.AsyncTask;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Random;

public class RandomAsyncTask extends AsyncTask<Void, Void, String> {

    private WeakReference<TextView> mtextView;

    public RandomAsyncTask(TextView textView) {


        mtextView = new WeakReference<>(textView);

    }

    @Override
    protected String doInBackground(Void... voids) {


        Random random = new Random();
        int number = random.nextInt(11);
        int ms = number*300;

        try {

            Thread.sleep(ms);

        } catch (InterruptedException ex) {

            ex.printStackTrace();

        }

        return "Bejelentkés vendégként";

    }

    @Override
    protected void onPostExecute(String s){


        super.onPostExecute(s);
        mtextView.get().setText(s);

    }
}
