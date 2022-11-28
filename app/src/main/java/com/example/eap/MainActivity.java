package com.example.eap;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private ImageView splashImageView;

    private static final String url = "jdbc:mysql://192.168.0.178:3306/EAP_DB";
    private static final String user = "EAP";
    private static final String pass = "-fb1(2zrtSP7/78hP]_-/x@gRP@hvP@u";

    TextView textViewEvList;
    TextView textViewTraditionalCarList;
    TextView editTextElectricityPrice;
    TextView editTextFuelPrice;
    TextView editTextDistance;
    TextView editTextTraditionalCarPrice;
    TextView editTextEvPrice;
    Spinner spinnerSubsidy;
    Spinner spinnerSolarPanels;
    Button btnCalculate;
    Dialog dialogEv;
    Dialog dialogTraditional;

    ArrayList<String> arrayListEv;
    ArrayList<String> arrayListTraditional;
    ArrayList<String> arrayListFuelType;
    ArrayList<Float> arrayListEfficiencyTraditional;
    ArrayList<Integer> arrayListEfficiencyEv;
    ArrayList<Integer> arrayListC02;
    ArrayList<Float> arrayListEvPrice;
    ArrayList<Float> arrayListTraditionalPrice;
    ArrayList<String> subsidyList;
    ArrayList<Integer> subsidySize;
    ArrayList<String> solarPanelList;
    ArrayList<Integer> solarPanleSize;
    ArrayList<Integer> solarPanlePrice;
    float[] prices = new float[3];
    boolean isDiesel=false;
    final String diesel="diesel";
    String dbUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        splashImageView = new ImageView(this);
        splashImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
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

                SharedPreferences sharedPref = getSharedPreferences("EAP", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                Gson gson = new Gson();
                dbUpdate = gson.fromJson(sharedPref.getString("dbUpdate", null), String.class);

                //If there is no data close the app
                if (dbUpdate == null ){
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.error),Toast.LENGTH_LONG).show();
                    finishAffinity();
                    System.exit(0);
                }

                arrayListEv = gson.fromJson(sharedPref.getString("arrayListEv", null), new TypeToken<ArrayList<String>>() {}.getType());
                arrayListTraditional = gson.fromJson(sharedPref.getString("arrayListTraditional", null),new TypeToken<ArrayList<String>>() {}.getType());
                arrayListEfficiencyTraditional = gson.fromJson(sharedPref.getString("arrayListEfficiencyTraditional", null), new TypeToken<ArrayList<Float>>() {}.getType());
                arrayListFuelType = gson.fromJson(sharedPref.getString("arrayListFuelType", null), new TypeToken<ArrayList<String>>() {}.getType());
                arrayListEfficiencyEv = gson.fromJson(sharedPref.getString("arrayListEfficiencyEv", null), new TypeToken<ArrayList<Integer>>() {}.getType());
                arrayListC02 = gson.fromJson(sharedPref.getString("arrayListC02", null),  new TypeToken<ArrayList<Integer>>() {}.getType());
                arrayListEvPrice = gson.fromJson(sharedPref.getString("arrayListEvPrice", null), new TypeToken<ArrayList<Float>>() {}.getType());
                arrayListTraditionalPrice = gson.fromJson(sharedPref.getString("arrayListTraditionalPrice", null),  new TypeToken<ArrayList<Float>>() {}.getType());
                prices = gson.fromJson(sharedPref.getString("prices", null), float[].class);
                subsidyList = gson.fromJson(sharedPref.getString("subsidyList", null),  new TypeToken<ArrayList<String>>() {}.getType());
                subsidySize = gson.fromJson(sharedPref.getString("subsidySize", null),  new TypeToken<ArrayList<Integer>>() {}.getType());
                solarPanelList = gson.fromJson(sharedPref.getString("solarPanelList", null),  new TypeToken<ArrayList<String>>() {}.getType());
                solarPanleSize = gson.fromJson(sharedPref.getString("solarPanleSize", null),  new TypeToken<ArrayList<Integer>>() {}.getType());
                solarPanlePrice = gson.fromJson(sharedPref.getString("solarPanlePrice", null),  new TypeToken<ArrayList<Integer>>() {}.getType());


                editTextElectricityPrice = findViewById(R.id.editTextElectricityPrice);
                editTextElectricityPrice.setText(String.valueOf(prices[0]));
                editTextFuelPrice.setText(String.valueOf(prices[1]));
                editTextDistance = findViewById(R.id.editTextDistancePerMonth);

                TextView textViewPaybackTime = findViewById(R.id.textViewPaybackTime);
                TextView textViewCalculationDetails = findViewById(R.id.textViewCalculationDetails);

                Spinner spinnerSubsidy = (Spinner) findViewById(R.id.spinnerSubsidy);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, subsidyList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSubsidy.setAdapter(dataAdapter);

                Spinner spinnerSolarPanels = (Spinner) findViewById(R.id.spinnerSolarPanels);
                dataAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, solarPanelList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSolarPanels.setAdapter(dataAdapter);

                btnCalculate = findViewById(R.id.buttonCalculate);
                btnCalculate.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        try {
                            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        } catch (Exception e) {}
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

                        int i = 0;
                        double tax = 0.0F;
                        int currentCO2 = 130;
                        float electricityPrice = Float.valueOf(editTextElectricityPrice.getText().toString());
                        int distance = Integer.valueOf(editTextDistance.getText().toString());
                        float evPrice = Float.valueOf(editTextEvPrice.getText().toString());
                        float tradicionalPrice = Float.valueOf(editTextTraditionalCarPrice.getText().toString());
                        int efficiencyEv =  Integer.valueOf(editTextEvEfficiency.getText().toString());
                        int co2 = Integer.valueOf(editTextPollution.getText().toString());
                        float efficiencyTraditional = Float.valueOf(editTextTraditionalCarEfficiency.getText().toString());
                        float fuelPrice = Float.valueOf(editTextFuelPrice.getText().toString());
                        float resultEv = 0;
                        float resultTraditional = fuelPrice * efficiencyTraditional/100 * distance;

                        float electricityNeeded;
                        float extraElectricity = 0;
                        //kiek papildomai elektros reikia pirkti iš elektros tinklų
                        electricityNeeded = 100.0F - (85.5F * solarPanleSize.get(spinnerSolarPanels.getSelectedItemPosition()) * 100 ) / ( distance * efficiencyEv /1000) * 0.67F;
                        if (electricityPrice * efficiencyEv/1000 >= fuelPrice * efficiencyTraditional/100 && electricityNeeded >0 ){
                            textViewPaybackTime.setText(getString(R.string.will_not_pay_back));
                            textViewCalculationDetails.setText(null);
                            return;
                        }
                        //jeigu papildomai elektros pirkti nereikia tuomet paskaičiuojamas papildomai pagaminamos elektros kiekis.
                        if (electricityNeeded < 0.0F){
                            extraElectricity = (85.5F * solarPanleSize.get(spinnerSolarPanels.getSelectedItemPosition()) - distance * efficiencyEv /1000) * 0.67F;
                            electricityNeeded = 0;
                        }
                        //
                        resultEv =  electricityPrice * efficiencyEv/1000 * distance * electricityNeeded / 100;

                        while (currentCO2<co2){
                            co2 = co2 - 10;
                            tax = tax + 33.24F;
                        }
                        if (isDiesel==false){
                            tax = tax / 2;
                        }

                        while(evPrice +  solarPanlePrice.get(spinnerSolarPanels.getSelectedItemPosition()) - tradicionalPrice - subsidySize.get(spinnerSubsidy.getSelectedItemPosition()) - tax - (resultTraditional - resultEv )* i > 0)
                            {
                                i++;
                            }
                            textViewCalculationDetails.setText(getString(R.string.calculation_detail, subsidySize.get(spinnerSubsidy.getSelectedItemPosition()),tax,resultTraditional - resultEv, extraElectricity));
                        textViewPaybackTime.setText(getString(R.string.will_pay_back_in,i/12,i%12));
                    }
                });
            }
        }, 1000);
        // Start a new thread that will download all the data
        new DownloadTask().execute();
    }
    private class DownloadTask extends AsyncTask<ArrayList<String>, Void, Object> {
        protected Object doInBackground(ArrayList<String>... args) {
            Log.i("MyApp", "Background thread starting");

            arrayListFuelType =  new ArrayList<String>();
            arrayListEfficiencyTraditional =  new ArrayList<Float>();
            arrayListEfficiencyEv =  new ArrayList<Integer>();
            arrayListC02 =  new ArrayList<Integer>();
            arrayListEvPrice =  new ArrayList<Float>();
            arrayListTraditionalPrice =  new ArrayList<Float>();
            subsidyList = new ArrayList<String>();
            subsidySize = new ArrayList<Integer>();
            solarPanelList =  new ArrayList<String>();
            solarPanleSize = new ArrayList<Integer>();
            solarPanlePrice= new ArrayList<Integer>();

            SharedPreferences sharedPref = getSharedPreferences("EAP", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            Gson gson = new Gson();

            try {
                //Connect to db
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);

                System.out.println("Database connection success");
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT UNIX_TIMESTAMP(MAX(UPDATE_TIME)) as last_update FROM information_schema.tables;");
                rs.next();

                //Check if db was updated
                dbUpdate = gson.fromJson(sharedPref.getString("dbUpdate", null), String.class);
                if (dbUpdate != null && dbUpdate.equals(rs.getString(1).toString())) {
                    return null;
                }

                //Get data from database
                arrayListEv = new ArrayList<>(Arrays.asList(getResources().getString(R.string.other_ev)));
                arrayListTraditional = new ArrayList<>(Arrays.asList(getResources().getString(R.string.other_car)));

                dbUpdate = rs.getString(1).toString();

                rs = st.executeQuery("select Name from Electric_cars");              //get EVs from db
                while (rs.next()) {
                    arrayListEv.add(rs.getString(1).toString());
                }
                rs = st.executeQuery("select Name from Traditional_cars");                 //get fuel_cars from db
                while (rs.next()) {
                    arrayListTraditional.add(rs.getString(1).toString());
                }

                rs = st.executeQuery("select Fuel_consumption from Traditional_cars");           //get efficiency of fuel_cars from db
                while (rs.next()) {
                    arrayListEfficiencyTraditional.add(rs.getFloat(1));
                }

                rs = st.executeQuery("select Fuel_type from Traditional_cars");           //get Fuel_type of fuel_cars from db
                while (rs.next()) {
                    arrayListFuelType.add(rs.getString(1).toString());
                }

                rs = st.executeQuery("select Energy_consumption from Electric_cars");                  //get EVs efficiency from db
                while (rs.next()) {
                    arrayListEfficiencyEv.add(rs.getInt(1));
                }

                rs = st.executeQuery("select Pollution from Traditional_cars");                  //get EVs efficiency from db
                while (rs.next()) {
                    arrayListC02.add(rs.getInt(1));
                }

                rs = st.executeQuery("select Price from Electric_cars");                  //get EVs price from db
                while (rs.next()) {
                    arrayListEvPrice.add(rs.getFloat(1));
                }

                rs = st.executeQuery("select Price from Traditional_cars");                  //get tradicional cars price from db
                while (rs.next()) {
                    arrayListTraditionalPrice.add(rs.getFloat(1));
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

                subsidyList.clear();
                subsidySize.clear();
                solarPanelList.clear();
                solarPanleSize.clear();
                solarPanlePrice.clear();

                rs = st.executeQuery("select NAme from Subsidy");           //get Fuel_type of fuel_cars from db
                while (rs.next()) {
                    subsidyList.add(rs.getString(1).toString());
                }

                rs = st.executeQuery("select Size from Subsidy");                  //get EVs efficiency from db
                while (rs.next()) {
                    subsidySize.add(rs.getInt(1));
                }

                rs = st.executeQuery("select Name from solar_panels order by Power");           //get Fuel_type of fuel_cars from db
                while (rs.next()) {
                    solarPanelList.add(rs.getString(1).toString());
                }

                rs = st.executeQuery("select Power from solar_panels order by Power");                  //get EVs efficiency from db
                while (rs.next()) {
                    solarPanleSize.add(rs.getInt(1));
                }
                rs = st.executeQuery("select Price from solar_panels order by Power");                  //get EVs efficiency from db
                while (rs.next()) {
                    solarPanlePrice.add(rs.getInt(1));
                }
                //clears old data
                editor.clear();
                editor.apply();
                //Caching data (so no internet access is needed after first use of application)
                editor.putString("dbUpdate", gson.toJson(dbUpdate));
                editor.putString("arrayListEv", gson.toJson(arrayListEv));
                editor.putString("arrayListTraditional", gson.toJson(arrayListTraditional));
                editor.putString("arrayListEfficiencyTraditional", gson.toJson(arrayListEfficiencyTraditional));
                editor.putString("arrayListFuelType", gson.toJson(arrayListFuelType));
                editor.putString("arrayListEfficiencyEv", gson.toJson(arrayListEfficiencyEv));
                editor.putString("arrayListC02", gson.toJson(arrayListC02));
                editor.putString("arrayListEvPrice", gson.toJson(arrayListEvPrice));
                editor.putString("arrayListTraditionalPrice", gson.toJson(arrayListTraditionalPrice));
                editor.putString("prices", gson.toJson(prices));
                editor.putString("subsidyList", gson.toJson(subsidyList));
                editor.putString("subsidySize", gson.toJson(subsidySize));
                editor.putString("solarPanelList", gson.toJson(solarPanelList));
                editor.putString("solarPanleSize", gson.toJson(solarPanleSize));
                editor.putString("solarPanlePrice", gson.toJson(solarPanlePrice));
                editor.apply();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
