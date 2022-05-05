package com.example.eap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pd = null;
    private ImageView splashImageView;

    private static final String url = "amRiYzpteXNxbDovLzE5Mi4xNjguMC4xNzg6MzMwNi9FQVArQUY4LURC"; //"jdbc:mysql://192.168.0.178:3306/EAP_DB";
    private static final String user = "AAAARQAAAEEAAABQ";
    private static final String pass = "IXYrKFZxKURhI1A1QWVj";

    TextView textViewEvList;
    TextView textViewTraditionalCarList;
    TextView editTextElectricityPrice;
    TextView editTextFuelPrice;
    TextView editTextDistance;
    TextView editTextTraditionalCarPrice;
    TextView editTextEvPrice;
    Button btnCalculate;
    Dialog dialogEv;
    Dialog dialogTraditional;
    ArrayList<String> arrayListEv;
    ArrayList<String> arrayListTraditional;
    ArrayList<String> arrayListFuelType;
    ArrayList<Float> arrayListEfficiencyTraditional;
    ArrayList<Integer> arrayListEfficiencyEv;
    ArrayList<Integer> arrayListC02;
    ArrayList<Integer> arrayListEvPrice;
    ArrayList<Integer> arrayListTraditionalPrice;
    final float[] prices = new float[3];
    final int[] subsidy = new int[2];
    boolean isDiesel=false;
    final String diesel="diesel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        splashImageView = new ImageView(this);
        splashImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        splashImageView.setImageResource(R.drawable.loadscreenad);
        setContentView(splashImageView);

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {

                setContentView(R.layout.activity_main);
                textViewEvList = findViewById(R.id.textViewEvList);
                TextView editTextEvEfficiency = findViewById(R.id.editTextEvEfficiency);
                View textViewEvEfficiency = findViewById(R.id.textViewEvEfficiency);
                View textViewTraditionasEfficiency = findViewById(R.id.textViewTraditionalEfficiency);
                TextView editTextTraditionalCarEfficiency = findViewById(R.id.editTextTraditionalCarEfficiency);
                View textViewPollution = findViewById(R.id.textViewPollution);
                TextView editTextPollution = findViewById(R.id.editTextPollution);
                //hidding elements
                textViewPollution.setVisibility(View.GONE);
                editTextPollution.setVisibility(View.GONE);
                editTextTraditionalCarEfficiency.setVisibility(View.GONE);
                textViewTraditionasEfficiency.setVisibility(View.GONE);
                editTextEvEfficiency.setVisibility(View.GONE);
                textViewEvEfficiency.setVisibility(View.GONE);

                textViewEvList.setOnClickListener(new View.OnClickListener() {
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
                                textViewEvList.setText(adapter.getItem(position));

                                if (position>=1){
                                    editTextEvPrice.setText(String.valueOf(arrayListEvPrice.get(position - 1)));
                                    editTextEvEfficiency.setText(String.valueOf(arrayListEfficiencyEv.get(position - 1)));
                                    //hiding elemenets
                                    editTextEvEfficiency.setVisibility(View.GONE);
                                    textViewEvEfficiency.setVisibility(View.GONE);
                                }
                                else {
                                    //showind elements
                                    editTextEvEfficiency.setVisibility(View.VISIBLE);
                                    textViewEvEfficiency.setVisibility(View.VISIBLE);
                                }

                                // Dismiss dialogEv
                                dialogEv.dismiss();
                            }
                        });

                    }
                });

                textViewTraditionalCarList = findViewById(R.id.textViewTraditionalCarList);
                editTextFuelPrice = findViewById(R.id.editTextGasPrice);

                editTextTraditionalCarPrice = findViewById(R.id.editTextTraditionalCarPrice);
                editTextEvPrice = findViewById(R.id.editTextEvPrice);

                textViewTraditionalCarList.setOnClickListener(new View.OnClickListener() {
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
                                textViewTraditionalCarList.setText(adapter.getItem(position));

                                if (position>=1){
                                    editTextTraditionalCarPrice.setText(String.valueOf(arrayListTraditionalPrice.get(position - 1)));
                                    editTextPollution.setText(String.valueOf(arrayListC02.get(position - 1)));
                                    editTextTraditionalCarEfficiency.setText(String.valueOf(arrayListEfficiencyTraditional.get(position - 1)));
                                    //hiding elements
                                    textViewPollution.setVisibility(View.GONE);
                                    editTextPollution.setVisibility(View.GONE);
                                    editTextTraditionalCarEfficiency.setVisibility(View.GONE);
                                    textViewTraditionasEfficiency.setVisibility(View.GONE);

                                    if (arrayListFuelType.get(position-1).equals(diesel)){
                                        editTextFuelPrice.setText(String.valueOf(prices[2]));
                                        isDiesel=true;
                                    }
                                    else{
                                        editTextFuelPrice.setText(String.valueOf(prices[1]));
                                        isDiesel=false;
                                    }
                                }
                                else{
                                    //showing elements
                                    textViewPollution.setVisibility(View.VISIBLE);
                                    editTextPollution.setVisibility(View.VISIBLE);
                                    editTextTraditionalCarEfficiency.setVisibility(View.VISIBLE);
                                    textViewTraditionasEfficiency.setVisibility(View.VISIBLE);
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

                TextView textViewPaybackTime = findViewById(R.id.textViewPaybackTime);
                TextView textViewCalculationDetails = findViewById(R.id.textViewCalculationDetails);

                btnCalculate = findViewById(R.id.buttonCalculate);
                btnCalculate.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(textViewEvList.getText())) {
                            textViewEvList.setError(getResources().getString(R.string.select_EV));
                            return;
                        }
                        else textViewEvList.setError(null);

                        if(TextUtils.isEmpty(textViewTraditionalCarList.getText())) {
                            textViewTraditionalCarList.setError(getResources().getString(R.string.select_traditional_car));
                            return;
                        }
                        else textViewTraditionalCarList.setError(null);

                        if(TextUtils.isEmpty(editTextEvEfficiency.getText())) {
                            editTextEvEfficiency.setError(getResources().getString(R.string.input_ev_efficiency));
                            return;
                        }
                        else editTextEvEfficiency.setError(null);

                        if(TextUtils.isEmpty(editTextTraditionalCarEfficiency.getText())) {
                            editTextTraditionalCarEfficiency.setError(getResources().getString(R.string.input_traditional_car_efficiency));
                            return;
                        }
                        else editTextTraditionalCarEfficiency.setError(null);

                        if(TextUtils.isEmpty(editTextPollution.getText())) {
                            editTextPollution.setError(getResources().getString(R.string.input_traditional_car_c02));
                            return;
                        }
                        else editTextPollution.setError(null);

                        if(TextUtils.isEmpty(editTextEvPrice.getText())) {
                            editTextEvPrice.setError(getResources().getString(R.string.input_ev_price));
                            return;
                        }
                        else editTextEvPrice.setError(null);

                        if(TextUtils.isEmpty(editTextTraditionalCarPrice.getText())) {
                            editTextTraditionalCarPrice.setError(getResources().getString(R.string.input_traditional_car_price));
                            return;
                        }
                        else editTextTraditionalCarPrice.setError(null);

                        if(TextUtils.isEmpty(editTextElectricityPrice.getText())) {
                            editTextElectricityPrice.setError(getResources().getString(R.string.input_electricity_price));
                            return;
                        }
                        else editTextElectricityPrice.setError(null);

                        if(TextUtils.isEmpty(editTextFuelPrice.getText())) {
                            editTextFuelPrice.setError(getResources().getString(R.string.input_fuel_price));
                            return;
                        }
                        else editTextFuelPrice.setError(null);

                        if(TextUtils.isEmpty(editTextDistance.getText())) {
                            editTextDistance.setError(getResources().getString(R.string.input_distance));
                            return;
                        }
                        else editTextDistance.setError(null);

                        float electricityPrice = Float.valueOf(editTextElectricityPrice.getText().toString());
                        int distance = Integer.valueOf(editTextDistance.getText().toString());
                        int evPrice = Integer.valueOf(editTextEvPrice.getText().toString());
                        int tradicionalPrice = Integer.valueOf(editTextTraditionalCarPrice.getText().toString());
                        int efficiencyEv =  Integer.valueOf(editTextEvEfficiency.getText().toString());
                        int co2 = Integer.valueOf(editTextPollution.getText().toString());
                        float efficiencyTraditional = Float.valueOf(editTextTraditionalCarEfficiency.getText().toString());
                        int i = 0;
                        double tax = 0.0F;
                        int currentCO2 = 130;
                        float fuelPrice = Float.valueOf(editTextFuelPrice.getText().toString());
                        float resultEv =  electricityPrice * efficiencyEv/1000 * distance;
                        float resultTraditional = fuelPrice * efficiencyTraditional/100 * distance;

                        while (currentCO2<co2){
                            currentCO2=currentCO2 + 10;
                            tax = tax + 33.24F;
                        }
                        if (isDiesel==false){
                            tax = tax / 2;
                        }
                        boolean isChecked = ((CheckBox) findViewById(R.id.checkBoxBusiness)).isChecked();

                        if (isChecked==false){
                            while(resultEv * i + evPrice - subsidy[0] >= resultTraditional * i  + tradicionalPrice + tax)
                            {
                                i++;
                            }
                           textViewCalculationDetails.setText(getString(R.string.calculation_detail, subsidy[0],tax,resultTraditional - resultEv));

                        }
                        else{
                            while(resultEv * i + evPrice - subsidy[1] >= resultTraditional * i  + tradicionalPrice + tax)
                            {
                                i++;
                            }
                            textViewCalculationDetails.setText(getString(R.string.calculation_detail, subsidy[1],tax,resultTraditional - resultEv));
                        }
                        textViewPaybackTime.setText(getString(R.string.will_pay_back_in, i/12,i%12));
                    }
                });
            }
        }, 1500);

        // Show the ProgressDialog on this thread

        this.pd = ProgressDialog.show(this, "", getResources().getString(R.string.db_download), true, false);
        this.pd.getWindow().setGravity(Gravity.BOTTOM);
        this.pd.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        this.pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Start a new thread that will download all the data

        new DownloadTask().execute(); //arrayListEv,arrayListTraditional
    }

    private class DownloadTask extends AsyncTask<ArrayList<String>, Void, Object> {
        protected Object doInBackground(ArrayList<String>... args) {
            Log.i("MyApp", "Background thread starting");
            arrayListEv = new ArrayList<>(Arrays.asList(getResources().getString(R.string.other_ev)));
            arrayListTraditional = new ArrayList<>(Arrays.asList(getResources().getString(R.string.other_car)));
            arrayListFuelType =  new ArrayList<String>();
            arrayListEfficiencyTraditional =  new ArrayList<Float>();
            arrayListEfficiencyEv =  new ArrayList<Integer>();
            arrayListC02 =  new ArrayList<Integer>();
            arrayListEvPrice =  new ArrayList<Integer>();
            arrayListTraditionalPrice =  new ArrayList<Integer>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(new String(Base64.decode(url, 0), "UTF-7"), new String(Base64.decode(user, 0), "UTF-32"), new String(Base64.decode(pass, 0), "UTF-8"));
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
            if (MainActivity.this.pd != null) {
                MainActivity.this.pd.dismiss();
            }
        }
    }
}
