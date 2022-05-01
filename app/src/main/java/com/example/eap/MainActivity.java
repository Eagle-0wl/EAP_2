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
import android.widget.Button;
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

    private static final String url = "jdbc:mysql://192.168.0.178:3306/Cars";
    private static final String user = "EAP";
    private static final String pass = "!v+(Vq)Da#P5Aec";

    TextView textview;
    ArrayList<String> arrayList;
    Dialog dialog;

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
                textview = findViewById(R.id.testView);

                textview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Initialize dialog
                        dialog = new Dialog(MainActivity.this);

                        // set custom dialog
                        dialog.setContentView(R.layout.dialog_searchable_spinner);

                        // set custom height and width
                        dialog.getWindow().setLayout(650, 800);

                        // set transparent background
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        // show dialog
                        dialog.show();

                        // Initialize and assign variable
                        EditText editText = dialog.findViewById(R.id.edit_text);
                        ListView listView = dialog.findViewById(R.id.list_view);

                        // Initialize array adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);

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
                                // set selected item on textView
                                textview.setText(adapter.getItem(position));

                                // Dismiss dialog
                                dialog.dismiss();
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

        new DownloadTask().execute(arrayList);
    }

    private class DownloadTask extends AsyncTask<ArrayList<String>, Void, Object> {
        protected Object doInBackground(ArrayList<String>... args) {
            Log.i("MyApp", "Background thread starting");
            arrayList = new ArrayList<>(Arrays.asList("Other2"));

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Databaseection success");
                arrayList.add("Databaseection success");
                //String result = "Database Connection Successful\n";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select Name from ev");
                ResultSetMetaData rsmd = rs.getMetaData();

                while (rs.next()) {
                    arrayList.add(rs.getString(1).toString() + "\n");
                }
                //res = result;
            } catch (Exception e) {
                e.printStackTrace();
                arrayList.add( e.toString());
            }
            return arrayList;



            //             This is where you would do all the work of downloading your data
//            arrayList = new ArrayList<>(Arrays.asList("Other"));
//            arrayList.add("Tesla Model S Plaid");
//            arrayList.add("Tesla Model X");
//            arrayList.add("Porsche Taycan Turbo S");
//            arrayList.add("Tesla Model S Long Range");
//            arrayList.add("Kia EV6 GT");
//            arrayList.add("BMW i4 M50");
//            arrayList.add("Audi e-tron S");
//            arrayList.add("Hyundai IONIQ 5 Long Range AWD");
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            //return arrayList;
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
