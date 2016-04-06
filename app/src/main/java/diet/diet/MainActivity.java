        package diet.diet;

        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.net.wifi.WifiInfo;
        import android.net.wifi.WifiManager;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.util.Log;
        import android.view.View;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Spinner;
        import android.widget.TextView;

        import org.ksoap2.*;
        import org.ksoap2.serialization.*;
        import org.ksoap2.transport.*;
        import org.xmlpull.v1.XmlPullParserException;

        import java.io.IOException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.List;
        import java.util.concurrent.ExecutionException;

        import lecho.lib.hellocharts.model.PointValue;

        public class MainActivity extends AppCompatActivity implements View.OnClickListener {

            static Integer spinnerPosition = 0;
            static Boolean trigger = false;

            CommStrings CS;
            Weight weightClass;
            WeightData WD;
            Weight WT;

            private static final Integer TIMEOUT = 50000;

            private static final String NAMESPACE = "http://tempuri.org/";
//            private static final String URL = "http://50.179.137.211:48484";
            private static String URL = "http://rtrdiet.ddns.net:48484";

            // Food Table
            private static final String METHOD_GET_FOOD_LIST = "GetFoodList";
            final static String SOAP_ACTION_GET_FOOD_LIST = "http://tempuri.org/IDietService/GetFoodList";

            // Add Daily Meal
            private static final String METHOD_ADD_DAILY_FOOD_ITEM = "AddDailyFoodItem";
            final static String SOAP_ACTION_ADD_DAILY_FOOD_ITEM ="http://tempuri.org/IDietService/AddDailyFoodItem";

            // Get Daily total calories
            private static final String METHOD_GET_DAILY_TOTAL = "GetDailyTotal";
            final static String SOAP_ACTION_GET_DAILY_TOTAL ="http://tempuri.org/IDietService/GetDailyTotal";

            // Weight Table
            private static final String METHOD_GET_WEIGHT = "GetWeight";
            final static String SOAP_ACTION_GET_WEIGHT = "http://tempuri.org/IDietService/GetWeight";

            // Daily Table
            private static final String METHOD_GET_MEALS = "GetMeals";
            final static String SOAP_ACTION_GET_MEALS = "http://tempuri.org/IDietService/GetMeals";

            Boolean initialized = false;
            Integer tracker = 0;

            List<String> foodList = new ArrayList<>();  // for Spinner

            ArrayAdapter<String> foodListAdapter;

            String foodName;
            double mealQuantity;
            String dailyTotalCalories;

            Button btn_Add;
            Button btn_NewMeal;
            Button btn_TodaysMeals;
            Button btn_Details;
            Button btn_Weight;

            TextView tv_Calories;
            TextView tv_CaloriesLabel;
            TextView tv_DailyTotalCalories;
            Spinner spnr_FoodList;
            EditText et_Quantity;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                weightClass = new Weight();

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

                btn_Add = (Button) findViewById(R.id.btn_Add);
                btn_Add.setOnClickListener(this);

                btn_NewMeal = (Button) findViewById(R.id.btn_NewMeal);
                btn_NewMeal.setOnClickListener(this);

                btn_TodaysMeals = (Button) findViewById(R.id.btn_TodaysMeals);
                btn_TodaysMeals.setOnClickListener(this);

                btn_Details = (Button) findViewById(R.id.btn_Details);
                btn_Details.setOnClickListener(this);

                btn_Weight = (Button) findViewById(R.id.btn_Weight);
                btn_Weight.setOnClickListener(this);

                spnr_FoodList = (Spinner) findViewById(R.id.spnr_FoodList);

                tv_Calories = (TextView) findViewById(R.id.et_Calories);
                tv_CaloriesLabel = (TextView) findViewById(R.id.tv_CaloriesLabel);
                tv_DailyTotalCalories = (TextView) findViewById(R.id.tv_DailyTotalCalories);
                et_Quantity = (EditText) findViewById(R.id.et_Quantity);

                tv_CaloriesLabel.setMaxLines(1);
                tv_CaloriesLabel.setMaxWidth(300);
                tv_CaloriesLabel.setHeight(100);
                tv_CaloriesLabel.setTextSize(18);

                tv_Calories.setHeight(100);
                tv_Calories.setTextSize(18);

                tv_DailyTotalCalories.setHeight(120);
                tv_DailyTotalCalories.setTextSize(22);
                spnr_FoodList.setMinimumHeight(60);

                spnr_FoodList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // your code here
                        FoodItem currentItem;
                        Integer index;
                        String food;

                        if (!spinnerPosition.equals(0) && trigger)
                        {
                            spnr_FoodList.setSelection(spinnerPosition);
                            spinnerPosition = -1;
                            trigger = false;
                        }
                        else
                        {
                            trigger = true;
                        }

                        index = spnr_FoodList.getSelectedItemPosition();
                        food = spnr_FoodList.getSelectedItem().toString();

                        if(!index.equals(0)) {
                            et_Quantity.setText("1");
                            et_Quantity.setSelection(et_Quantity.getText().length());
                            currentItem = GetFoodItem(food);
                            tv_Calories.setText(currentItem.calories.toString());
                            foodName = currentItem.food1;
                            UpdateCalories(currentItem);
                        }
                        else
                        {
                            tv_Calories.setText("");
                            et_Quantity.setText("");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });

                et_Quantity.addTextChangedListener(new TextWatcher() {

                    public void afterTextChanged(Editable s) {
//                        String strQty;
//                        String selItm;
                        Double totCals;

                        FoodItem selectedItem;

//                        strQty = et_Quantity.getText().toString();
//                        selItm = spnr_FoodList.getSelectedItem().toString();

                        if (!et_Quantity.getText().toString().matches("")) {
                            selectedItem = GetFoodItem(spnr_FoodList.getSelectedItem().toString());
                            totCals = UpdateCalories(selectedItem);
                            tv_Calories.setText(String.format("%5.1f", totCals));
                        }
                    }

                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                    }
                });

                setSupportActionBar(toolbar);

//                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//                fab.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
//                    }
//                });

                WifiManager wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                Log.i("CYBERON", "SSID: " + wifiInfo.getSSID());
                if (wifiInfo.getSSID().contains("Cyberon"))
                {
                    CS.URL = "http://10.0.0.134:48484";
                }
                else
                {
                    CS.URL = "http://rtrdiet.ddns.net:48484";
                }
                Log.i("CYBERON", "URL: " + CS.URL);
            }

            @Override
            public void onResume(){
                super.onResume();

                if (!spinnerPosition.equals(0))
                {
                    spnr_FoodList.setSelection(spinnerPosition);
                }
//                Toast.makeText(getApplicationContext(),
//                        String.format("Resume: Value is %d", spnr_FoodList.getSelectedItemPosition()), Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onPostResume()
            {
                Integer index;
                super.onPostResume();

//                if (!initialized)
                if (WD.FirstRun)
                {
                    try {
                        new FoodListLoader().execute();
                        new GetDailyTotalCalories().execute().get();
                        new GetMeals().execute();
                        new GetWeights().execute();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    InitializeSpinner();
                    initialized = true;
                }

                spnr_FoodList.setSelection(0);
                index = spnr_FoodList.getSelectedItemPosition();
                spnr_FoodList.setSelection(spinnerPosition);
                index = spnr_FoodList.getSelectedItemPosition();
            }
            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.menu_main, menu);
                return true;
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                switch (id)
                {
                    case R.id.action_newMenuItem:
                        onClick(btn_NewMeal);
                        return true;
                    case R.id.action_status:
                        onClick(btn_Details);
                        return true;
                    case R.id.action_meals:
                        onClick(btn_TodaysMeals);
                        return true;
                    case R.id.action_weightGraph:
                        onClick(btn_Weight);
                        return true;
                }
//                if (id == R.id.action_settings) {
//                    return true;
//                    }

                super.onOptionsItemSelected(item);
                return false;
            }

            @Override
            public void onClick(View view) {
                switch (view.getId())
                {
                    case R.id.btn_Add:
                        // Add meal to database here
                        try {
                            new EnterNewMeal().execute().get();
                            new GetDailyTotalCalories().execute().get();
                            new GetMeals().execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.btn_NewMeal:
                        // Open New Meal Activity
                        startActivityForResult(new Intent(this, NewMealActivity.class), 1);
                        break;
                    case R.id.btn_TodaysMeals:
                        // Open New Meal Activity
                        try {
                            Intent intent = new Intent(this, MealsActivity.class);
//                            intent.putExtra("FoodTable", FoodTable);
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.i("CYBERON", e.getMessage());
                            e.printStackTrace();
                        }
                        new GetDailyTotalCalories().execute();
                        break;
                    case R.id.btn_Details:
                        // Open New Meal Activity
                        try {
                            Intent intent = new Intent(this, DetailsActivity.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.i("CYBERON", e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                    case R.id.btn_Weight:
                        // Open New Meal Activity
                        try {
                            Intent intent = new Intent(this, WeightGraphActivity.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.i("CYBERON", e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                }
            }

            @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                Integer calories;
                if (requestCode == 1) {
                    if(resultCode == Activity.RESULT_OK) {

                        String food = data.getStringExtra("foodName");
                        String cals = data.getStringExtra("calories");
                        if (!food.contains("CANCEL")) {
                            calories = Integer.parseInt(cals);
                            try {
                                new FoodListLoader().execute().get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            InitializeSpinner();
                            spinnerPosition = foodListAdapter.getPosition(food);
                        }
                    }
                    if (resultCode == Activity.RESULT_CANCELED) {
                        try {
                            new FoodListLoader().execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        InitializeSpinner();
                    }
                }
            }//onActivityResult

            private void InitializeSpinner() {
                foodListAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.my_spinner_layout, foodList);

                foodListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spnr_FoodList.setAdapter(foodListAdapter);
            }

            private FoodItem GetFoodItem(String food) {
                FoodItem value = null;
                FoodItem tempValue = null;
                Integer key;
                String food1;
                Iterator i = Meals.FoodTable.keySet().iterator();

                while (i.hasNext()) {
                    key = (Integer) i.next();
                    tempValue = Meals.FoodTable.get(key);
                    food1 = tempValue.food1;
                    if (food1.equals(food)) {
                        value = Meals.FoodTable.get(key);
                        break;
                    }
                }
                return value;
            }

            private double UpdateCalories(FoodItem currentItem) {
                double totCals;
                double cals;
                double qty;
                if (!et_Quantity.getText().toString().matches("")) {
                    qty = Double.parseDouble(et_Quantity.getText().toString());
                    cals = (double) currentItem.calories;
                    totCals = cals * qty;
                    mealQuantity = qty;
                    return totCals;
                }
                return 0;
            }

            private class FoodListLoader extends AsyncTask<Void, Void, Void> {
                ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
                Boolean success;
                FoodItem foodItem;
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pdLoading.setIndeterminate(true);
                    pdLoading.setCancelable(false);
                    success = false;
                    Log.i("CYBERON", "FoodListLoader");
                    Meals.FoodTable = new HashMap<Integer, FoodItem>();
                    pdLoading.setMessage("Loading Food List ...");
                    pdLoading.show();
                }
                @Override
                protected Void doInBackground(Void... params) {
                    Meals.FoodTable.clear();
                    SoapObject request = new SoapObject(CS.NAMESPACE, CS.METHOD_GET_FOOD_LIST);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    do {
                        try {

                            HttpTransportSE myHttpTransport = new HttpTransportSE(CS.URL, CS.TIMEOUT);
                            myHttpTransport.call(CS.SOAP_ACTION_GET_FOOD_LIST, envelope);

                            // To retrieve a group of records
                            SoapObject response = (SoapObject) envelope.getResponse();

                            // 0 is the first object of data
                            SoapObject root = (SoapObject) response.getProperty(0);
                            SoapObject food = (SoapObject) root.getProperty("Food");
                            //to get the data
                            foodList.clear();
                            foodList.add("");
                            for (int i = 0; i < root.getPropertyCount() - 1; i++) {
                                foodItem = new FoodItem();
                                SoapObject item = (SoapObject) root.getProperty(i);
                                String strRecNum = item.getProperty("recNum").toString();
                                Integer recNum = Integer.parseInt(strRecNum);
                                String food1 = item.getProperty("food1").toString();
                                String strCalories = item.getProperty("calories").toString();
                                Integer calories = Integer.parseInt(strCalories);
                                foodItem.recNum = recNum;
                                foodItem.food1 = food1;
                                foodItem.calories = calories;
//                            Food.add(foodItem);
                                Meals.FoodTable.put(recNum, foodItem);
                                foodList.add(food1);
                            }
                            success = true;
/*
                        // To retrieve a single item
                        private String responseData;
                        SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
                       //to get the data
                        responseData = response.toString();
*/

                        } catch (Exception e) {
//                        StackTraceElement[] stack = e.getStackTrace();
//                        String Trace = "";
//                        for(StackTraceElement line : stack)
//                        {
//                            Trace += line.toString();
//                            Trace += "\n";
//                        }
//                        Log.i("CYBERON", "Stack Trace:\n" + Trace);
                            Log.i("CYBERON", e.getMessage());
                        }
                    } while (!success);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
//                    spnr_FoodList.setAdapter(foodListAdapter);
                    pdLoading.dismiss();
                }
            }

            private class EnterNewMeal extends AsyncTask<Void, Void, Void> {
                Boolean success;
                ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    success = false;
                    pdLoading.setIndeterminate(true);
                    pdLoading.setCancelable(false);
                    Log.i("CYBERON", "EnterNewMeal");
                    pdLoading.setMessage("Adding meal to database ...");
                    pdLoading.show();
                }

                @Override
                protected Void doInBackground(Void... params) {
                    Date today;
                    String date;
                    SimpleDateFormat formatter;

                    formatter = new SimpleDateFormat("MM/dd/yyyy");
                    today = new Date();
                    date = formatter.format(today);

                    SoapObject request = new SoapObject(CS.NAMESPACE, CS.METHOD_ADD_DAILY_FOOD_ITEM);
                    request.addProperty("date", date);       // string
                    request.addProperty("foodName", foodName);   // string
                    request.addProperty("qty", mealQuantity);        // double

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);

                    MarshalDouble md = new MarshalDouble();
                    md.register(envelope);

                    do {
                        try {

                            HttpTransportSE myHttpTransport = new HttpTransportSE(CS.URL, CS.TIMEOUT);
                            myHttpTransport.call(CS.SOAP_ACTION_ADD_DAILY_FOOD_ITEM, envelope);

                        } catch (Exception e) {
//                        StackTraceElement[] stack = e.getStackTrace();
//                        String Trace = "";
//                        for(StackTraceElement line : stack)
//                        {
//                            Trace += line.toString();
//                            Trace += "\n";
//                        }
//                        Log.i("CYBERON", "Stack Trace:\n" + Trace);
                            Log.i("CYBERON", e.getMessage());
                        }
                        success = true;
                    } while(!success);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    pdLoading.dismiss();
//                    new GetDailyTotalCalories().execute();
                    spnr_FoodList.setSelection(0);
                    tv_Calories.setText("");
                    et_Quantity.setText("");
                }
            }

            private class GetDailyTotalCalories extends AsyncTask<Void, Void, Void> {
                Boolean success;
                double totalCalories;
                ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pdLoading.setIndeterminate(true);
                    pdLoading.setCancelable(false);
                    success = false;
                    Log.i("CYBERON", "GetDailyTotalCalories");
                    pdLoading.setMessage("Retreiving total calories for day ...");
                    pdLoading.show();
                }
                @Override
                protected Void doInBackground(Void... params) {
                    Date today;
                    String date;
                    SimpleDateFormat formatter;

                    formatter = new SimpleDateFormat("MM/dd/yyyy");
                    today = new Date();
                    date = formatter.format(today);
                    SoapObject request = new SoapObject(CS.NAMESPACE, CS.METHOD_GET_DAILY_TOTAL);
                    request.addProperty("date", date);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    do {
                        try {

                            HttpTransportSE myHttpTransport = new HttpTransportSE(CS.URL, CS.TIMEOUT);
                            myHttpTransport.call(CS.SOAP_ACTION_GET_DAILY_TOTAL, envelope);

                            // To retrieve a group of records
                            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

                            totalCalories = Double.parseDouble(response.toString());
                            Meals.TOTAL_CALORIES = totalCalories;
                            dailyTotalCalories = String.format("%1.1f", totalCalories);
                            success = true;
/*
                        // To retrieve a single item
                        private String responseData;
                        SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
                       //to get the data
                        responseData = response.toString();
*/

                            // 0 is the first object of data
                        } catch (XmlPullParserException e1) {
                            e1.printStackTrace();
                        } catch (SoapFault soapFault) {
                            soapFault.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (Exception e) {
//                        StackTraceElement[] stack = e.getStackTrace();
//                        String Trace = "";
//                        for(StackTraceElement line : stack)
//                        {
//                            Trace += line.toString();
//                            Trace += "\n";
//                        }
//                        Log.i("CYBERON", "Stack Trace:\n" + Trace);
                            Log.i("CYBERON", e.getMessage());
                        }
                    } while(!success);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    tv_DailyTotalCalories.setText("Total calories for today: " + dailyTotalCalories);
                    spnr_FoodList.setAdapter(foodListAdapter);
                    pdLoading.dismiss();
                }
            }

            private class GetWeights extends AsyncTask<Void, Void, Void> {
                ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
                Boolean success;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pdLoading.setIndeterminate(true);
                    pdLoading.setCancelable(false);
                    WD.FirstRun = true;
                    success = false;
                    Log.i("CYBERON", "GetWeights");
                    pdLoading.setMessage("Loading weights ...");
                    pdLoading.show();
                }
                @Override
                protected Void doInBackground(Void... params) {

                    WeightItem weight;
                    WD.GraphArray = new ArrayList<WeightItem>();

                    SoapObject request = new SoapObject(CS.NAMESPACE, CS.METHOD_GET_WEIGHT);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    do {
                        try {

                            HttpTransportSE myHttpTransport = new HttpTransportSE(CS.URL, CS.TIMEOUT);
                            myHttpTransport.call(CS.SOAP_ACTION_GET_WEIGHT, envelope);

                            // To retrieve a group of records
                            SoapObject response = (SoapObject) envelope.getResponse();
                            SoapObject foodList = (SoapObject) response.getProperty(0);
                            SoapObject mealItems = (SoapObject) response.getProperty(1);
                            SoapObject weightList = (SoapObject) response.getProperty(2);


                            // 0 is the first object of data
                            for (int i = 0; i < weightList.getPropertyCount(); i++) {
                                SoapObject item = (SoapObject) weightList.getProperty(i);

                                if (!item.getProperty("recNum").toString().equals(null)) {
                                    weight = new WeightItem();
                                    weight.recNum = Integer.parseInt(item.getProperty("recNum").toString());
                                    weight.weight1 = Float.parseFloat(item.getProperty("weight1").toString());
                                    weight.measureDate = item.getProperty("measureDate").toString();
                                    WD.GraphArray.add(weight);
                                } else {
                                    break;
                                }
                            }
                            success = true;
                        } catch (XmlPullParserException e1) {
                            e1.printStackTrace();
                        } catch (SoapFault soapFault) {
                            soapFault.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (Exception e) {
//                        StackTraceElement[] stack = e.getStackTrace();
//                        String Trace = "";
//                        for(StackTraceElement line : stack)
//                        {
//                            Trace += line.toString();
//                            Trace += "\n";
//                        }
//                        Log.i("CYBERON", "Stack Trace:\n" + Trace);
                            Log.i("CYBERON", e.getMessage());
                        }
                    } while(!success);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    weightClass.PopulateGraphArray();
                    weightClass.CalculateGraphArray();
                    pdLoading.dismiss();
                }
            }

            private class GetMeals extends AsyncTask<Void, Void, Void> {
//                double totalCalories;
//                String postString = "";
                Boolean success;

                Meal meal;
                ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pdLoading.setIndeterminate(true);
                    pdLoading.setCancelable(false);
                    success = false;
                    Log.i("CYBERON", "GetMeals");
                    pdLoading.setMessage("Loading Daily Meals ...");
                    pdLoading.show();
                }
                @Override
                protected Void doInBackground(Void... params) {

                    Meals.MealList = new ArrayList<>();
                    List<Meal> MealList = new ArrayList<>();
                    Date today;
                    String date;
                    SimpleDateFormat formatter;

                    formatter = new SimpleDateFormat("MM/dd/yyyy");
                    today = new Date();
                    date = formatter.format(today);
                    SoapObject request = new SoapObject(CS.NAMESPACE, CS.METHOD_GET_MEALS);
                    request.addProperty("date", date);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    do {
                        try {

                            HttpTransportSE myHttpTransport = new HttpTransportSE(CS.URL, CS.TIMEOUT);
                            myHttpTransport.call(CS.SOAP_ACTION_GET_MEALS, envelope);

                            // To retrieve a group of records
                            SoapObject response = (SoapObject) envelope.getResponse();
                            SoapObject foodList = (SoapObject) response.getProperty(0);
                            SoapObject mealItems = (SoapObject) response.getProperty(1);
                            SoapObject weightList = (SoapObject) response.getProperty(2);


                            // 0 is the first object of data
//                Object meals = response.getProperty(1);
//                Integer q = meals.length;
                            for (int i = 0; i < mealItems.getPropertyCount(); i++) {
                                SoapObject item = (SoapObject) mealItems.getProperty(i);

                                if (!item.getProperty("Food").toString().equals(null)) {
                                    meal = new Meal();
                                    meal.Food = item.getProperty("Food").toString();
                                    meal.Quantity = Double.parseDouble(item.getProperty("Quantity").toString());
                                    meal.Calories = Integer.parseInt(item.getProperty("Calories").toString());
                                    Meals.MealList.add(meal);
//                                postString += String.format("%s\n", meal.Food);
                                } else {
                                    break;
                                }
                            }
                            success = true;

                        } catch (XmlPullParserException e1) {
                            e1.printStackTrace();
                        } catch (SoapFault soapFault) {
                            soapFault.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (Exception e) {
//                        StackTraceElement[] stack = e.getStackTrace();
//                        String Trace = "";
//                        for(StackTraceElement line : stack)
//                        {
//                            Trace += line.toString();
//                            Trace += "\n";
//                        }
//                        Log.i("CYBERON", "Stack Trace:\n" + Trace);
                            Log.i("CYBERON", e.getMessage());
                        }
                    } while(!success);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    pdLoading.dismiss();
                }
            }
        }

