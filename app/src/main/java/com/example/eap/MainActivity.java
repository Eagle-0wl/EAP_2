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
import android.text.TextUtils;
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
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pd = null;
    private Object data = null;

    private ImageView splashImageView;
    boolean splashLoading = false;

    private static final String url = "jdbc:mysql://192.168.0.178:3306/EAP_DB";
    private static final String user = "EAP";
    private static final String pass = "!v+(Vq)Da#P5Aec";

    TextView evView;
    TextView traditionalView;
    TextView editTextElectricityPrice;
    TextView editTextFuelPrice;
    TextView editTextDistance;
    Button btnCalculate;
    Dialog dialogEv;
    Dialog dialogTraditional;

    String diesel="diesel";
    boolean isDiesel=false;
    ArrayList<String> arrayListEv;
    ArrayList<String> arrayListTraditional;
    ArrayList<String> arrayListFuelType;
    ArrayList<Float> arrayListEfficiencyTraditional;
    ArrayList<Integer> arrayListEfficiencyEv;
    ArrayList<Integer> arrayListC02;
    ArrayList<Integer> arrayListEvPrice;
    ArrayList<Integer> arrayListTraditionalPrice;
    float prices[] = new float[3];
    float efficiencyTraditional;
    float efficiencyEv;
    int co2;
    int evPrice;
    int tradicionalPrice;
    int subsidy[] = new int[2];

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        splashImageView = new ImageView(this);
        splashImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        splashImageView.setImageResource(R.drawable.loadscreenad);
        setContentView(splashImageView);
        splashLoading = true;
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                splashLoading = false;
                setContentView(R.layout.activity_main);

                // assign variable
                evView = findViewById(R.id.textViewEv);
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

                                if (position>=1){
                                    efficiencyEv = arrayListEfficiencyEv.get(position - 1);
                                    evPrice = arrayListEvPrice.get(position - 1);
                                }

                                // Dismiss dialogEv
                                dialogEv.dismiss();
                            }
                        });

                    }
                });

                traditionalView = findViewById(R.id.textViewTraditional);
                editTextFuelPrice = findViewById(R.id.editTextGasPrice);
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

                                if (position>=1){
                                    efficiencyTraditional = arrayListEfficiencyTraditional.get(position - 1);
                                    co2 = arrayListC02.get(position - 1);
                                    tradicionalPrice = arrayListTraditionalPrice.get(position - 1);
                                    if (arrayListFuelType.get(position-1).equals(diesel)){
                                        editTextFuelPrice.setText(String.valueOf(prices[2]));
                                        isDiesel=true;
                                    }
                                    else{
                                        editTextFuelPrice.setText(String.valueOf(prices[1]));
                                        isDiesel=false;
                                    }
                                }

                                // Dismiss dialog
                                dialogTraditional.dismiss();
                            }
                        });

                    }
                });

                editTextElectricityPrice = findViewById(R.id.editTextElectricityPrice);
                editTextElectricityPrice.setText(String.valueOf(prices[0]));
                editTextFuelPrice.setText(String.valueOf(prices[1]));

                editTextDistance = findViewById(R.id.editTextDistancePerMonth);

                btnCalculate = findViewById(R.id.buttonCalculate);
                btnCalculate.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(evView.getText())) {
                            evView.setError("Input electricity price");
                            return;
                        }
                        else evView.setError(null);

                        if(TextUtils.isEmpty(traditionalView.getText())) {
                            traditionalView.setError("Input electricity price");
                            return;
                        }
                        else traditionalView.setError(null);

                        if(TextUtils.isEmpty(editTextElectricityPrice.getText())) {
                            editTextElectricityPrice.setError("Input electricity price");
                            return;
                        }
                        else editTextElectricityPrice.setError(null);

                        if(TextUtils.isEmpty(editTextFuelPrice.getText())) {
                            editTextFuelPrice.setError("Input fuel price");
                            return;
                        }
                        else editTextFuelPrice.setError(null);

                        if(TextUtils.isEmpty(editTextDistance.getText())) {
                            editTextDistance.setError("Input distance");
                            return;
                        }
                        else editTextDistance.setError(null);
//                    if (editTextDistance.getText()=="\n"){
                        editTextElectricityPrice.getText();
                        //float electricityPrice = Float.valueOf(editTextElectricityPrice.getText().toString());
                        //Toast.makeText(getApplicationContext(),String.valueOf(electricityPrice),Toast.LENGTH_SHORT).show();
//                    }

                        float electricityPrice = Float.valueOf(editTextElectricityPrice.getText().toString());
                        float distance = Integer.valueOf(editTextDistance.getText().toString());

                        float fuelPrice = Float.valueOf(editTextFuelPrice.getText().toString());



                        float resultEv =  electricityPrice * efficiencyEv/1000 * distance;
                        float resultTraditional = fuelPrice * efficiencyTraditional/100 * distance;

                        int i = 0;
                        while(resultEv * i + evPrice >= resultTraditional * i  +tradicionalPrice)
                        {
                            i++;
                        }

                        Toast.makeText(getApplicationContext(),String.valueOf(i/12) + "years and " +String.valueOf(i%12) + "months",Toast.LENGTH_SHORT).show();

                    }
                }

                );
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
            arrayListFuelType =  new ArrayList<String>();
            arrayListEfficiencyTraditional =  new ArrayList<Float>();
            arrayListEfficiencyEv =  new ArrayList<Integer>();
            arrayListC02 =  new ArrayList<Integer>();
            arrayListEvPrice =  new ArrayList<Integer>();
            arrayListTraditionalPrice =  new ArrayList<Integer>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Database connection success");
                Statement st = con.createStatement();


                ResultSet rs = st.executeQuery("select Name from ev");              //get EVs from db
                while (rs.next()) {
                    arrayListEv.add(rs.getString(1).toString());
                }
                rs = st.executeQuery("select Name from fuel_cars");                 //get fuel_cars from db
                while (rs.next()) {
                    arrayListTraditional.add(rs.getString(1).toString());
                }

                rs = st.executeQuery("select Efficiency from fuel_cars");           //get efficiency of fuel_cars from db
                while (rs.next()) {
                    arrayListEfficiencyTraditional.add(rs.getFloat(1));
                }

                rs = st.executeQuery("select Fuel_type from fuel_cars");           //get Fuel_type of fuel_cars from db
                while (rs.next()) {
                    arrayListFuelType.add(rs.getString(1).toString());
                }

                rs = st.executeQuery("select Efficiency from ev");                  //get EVs efficiency from db
                while (rs.next()) {
                    arrayListEfficiencyEv.add(rs.getInt(1));
                }

                rs = st.executeQuery("select Pollution from fuel_cars");                  //get EVs efficiency from db
                while (rs.next()) {
                    arrayListC02.add(rs.getInt(1));
                }

                rs = st.executeQuery("select Price from ev");                  //get EVs price from db
                while (rs.next()) {
                    arrayListEvPrice.add(rs.getInt(1));
                }

                rs = st.executeQuery("select Price from fuel_cars");                  //get tradicional cars price from db
                while (rs.next()) {
                    arrayListTraditionalPrice.add(rs.getInt(1));
                }

                rs = st.executeQuery("select electricity from prices");
                rs.next();
                prices[0]=rs.getFloat(1);

                rs = st.executeQuery("select gasoline from prices");
                rs.next();
                prices[1]=rs.getFloat(1);

                rs = st.executeQuery("select diesel from prices");
                rs.next();
                prices[2]=rs.getFloat(1);

                rs = st.executeQuery("select Physical from subsidy");
                rs.next();
                subsidy[0]=rs.getInt(1);

                rs = st.executeQuery("select Legal from subsidy");
                rs.next();
                subsidy[1]=rs.getInt(1);

            } catch (Exception e) {
                e.printStackTrace();
                arrayListEv.add(e.toString());
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
