package com.example.eap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pd = null;
    private Object data = null;

    private ImageView splashImageView;
    boolean splashloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        splashImageView = new ImageView(this);
        splashImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        splashImageView.setImageResource(R.drawable.loadscreenad);
        setContentView(splashImageView);
        splashloading = true;
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                splashloading = false;
                setContentView(R.layout.activity_main);
            }
        }, 1000);

        // Show the ProgressDialog on this thread

        this.pd = ProgressDialog.show(this, "", "Downloading DataBase...", true, false);
        this.pd.getWindow().setGravity(Gravity.BOTTOM);
        this.pd.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        this.pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Start a new thread that will download all the data
        new DownloadTask().execute("Any parameters my download task needs here");
    }

    private class DownloadTask extends AsyncTask<String, Void, Object> {
        protected Object doInBackground(String... args) {
            Log.i("MyApp", "Background thread starting");

            // This is where you would do all the work of downloading your data


            return "replace this with your data object";
        }

        protected void onPostExecute(Object result) {
            // Pass the result data back to the main activity
            MainActivity.this.data = result;

            if (MainActivity.this.pd != null) {
                MainActivity.this.pd.dismiss();
            }
        }
    }

}