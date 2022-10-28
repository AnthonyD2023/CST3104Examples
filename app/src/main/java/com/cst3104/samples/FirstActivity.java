package com.cst3104.samples;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FirstActivity extends AppCompatActivity {

    AsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startbtn = findViewById( R.id.startId);

        Button cancelbtn = findViewById( R.id.cancel);

        startbtn.setOnClickListener(view -> task = new MyTask().execute());

        cancelbtn.setOnClickListener(view -> task.cancel(true));
    }


    //Type1     Type2   Type3
    private class MyTask extends AsyncTask< String, Integer, String>
    {
        static private final String TAG = "MyTask";

        TextView tv = findViewById(R.id.textViewId);

        //Type3                Type1
        @Override
        public String doInBackground(String ... args)
        {
            try {

                int i = 0;
                while (i <= 20) {
                    try {
                        Thread.sleep(1000);
                        i++;
                        publishProgress(i);
                    }
                    catch (Exception e) {
                        Log.w(TAG, "Error in doInBackground");
                    }
                }

            }
            catch (Exception e)
            {
                Log.w(TAG, "Error in doInBackground");
            }
            return "Activity Done";
        }

        //Type 2
        public void onProgressUpdate(Integer ... args)
        {

            Log.i(TAG, "onProgressUpdate " + args[0] );
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);

            Log.i(TAG, "onProgressUpdate " + s  );

            tv.setText(s);
        }

        //Type3
        public void onPostExecute(String fromDoInBackground)
        {
            Log.i(TAG, fromDoInBackground);
            tv.setText(fromDoInBackground);
        }
    }
}

