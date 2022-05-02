package com.example.eap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pd = null;
    private Object data = null;

    private ImageView splashImageView;
    boolean splashloading = false;

    private static final String url = "jdbc:mysql://192.168.0.178:3306/EAP_DB";
    private static final String user = "EAP";
    private static final String pass = "!v+(Vq)Da#P5Aec";

    TextView evView;
    TextView traditionalView;
    Dialog dialogEv;
    Dialog dialogTraditional;
    ArrayList<String> arrayListEv;
    ArrayList<String> arrayListTraditional;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                // assign variable
                evView = findViewById(R.id.evView);

                evView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Initialize dialog
                        dialogEv = new Dialog(MainActivity.this);

                        // set custom dialog
                        dialogEv.setContentView(R.layout.searchable_spinner_ev);

                        // set custom height and width
                        dialogEv.getWindow().setLayout(650, 800);

                        // set transparent background
                        dialogEv.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        // show dialog
                        dialogEv.show();

                        // Initialize and assign variable
                        EditText editText = dialogEv.findViewById(R.id.edit_text_ev);
                        ListView listView = dialogEv.findViewById(R.id.list_view_ev);

                        // Initialize array adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayListEv);

                        // set adapter
                        listView.setAdapter(adapter);
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                adapter.getFilter().filter(s);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // when item selected from list
                                // set selected item on evView
                                evView.setText(adapter.getItem(position));

                                // Dismiss dialogEv
                                dialogEv.dismiss();
                            }
                        });

                    }
                });

                traditionalView = findViewById(R.id.traditionalView);
                traditionalView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Initialize dialogTraditional
                        dialogTraditional = new Dialog(MainActivity.this);

                        // set custom dialogTraditional
                        dialogTraditional.setContentView(R.layout.searchable_spinner_traditional);

                        // set custom height and width
                        dialogTraditional.getWindow().setLayout(650, 800);

                        // set transparent background
                        dialogTraditional.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        // show dialog
                        dialogTraditional.show();

                        // Initialize and assign variable
                        EditText editText = dialogTraditional.findViewById(R.id.edit_text_traditional);
                        ListView listView = dialogTraditional.findViewById(R.id.list_view_traditional);

                        // Initialize array adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayListTraditional);

                        // set adapter
                        listView.setAdapter(adapter);
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                adapter.getFilter().filter(s);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // when item selected from list
                                // set selected item on traditionalView
                                traditionalView.setText(adapter.getItem(position));

                                // Dismiss dialog
                                dialogTraditional.dismiss();
                            }
                        });

                    }
                });

            }
        }, 1500);

        // Show the ProgressDialog on this thread

        this.pd = ProgressDialog.show(this, "", "Downloading DataBase...", true, false);
        this.pd.getWindow().setGravity(Gravity.BOTTOM);
        this.pd.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        this.pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Start a new thread that will download all the data

        new DownloadTask().execute(); //arrayListEv,arrayListTraditional
    }

    private class DownloadTask extends AsyncTask<ArrayList<String>, Void, Object> {
        protected Object doInBackground(ArrayList<String>... args) {
            Log.i("MyApp", "Background thread starting");
            arrayListEv = new ArrayList<>(Arrays.asList("Other"));
            arrayListTraditional = new ArrayList<>(Arrays.asList("Other"));
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Database connection success");
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select Name from ev");
                ResultSetMetaData rsmd = rs.getMetaData();
                while (rs.next()) {
                    arrayListEv.add(rs.getString(1).toString() + "\n");
                }
                rs = st.executeQuery("select Name from fuel_cars");
                rsmd = rs.getMetaData();
                while (rs.next()) {
                    arrayListTraditional.add(rs.getString(1).toString() + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
                arrayListEv.add( e.toString());
            }

            return arrayListEv;

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
