        package diet.diet;

        import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static diet.diet.R.layout.activity_main;

        public class MainActivity extends AppCompatActivity implements View.OnClickListener {

            static Integer spinnerPosition = 0;
            static Boolean trigger = false;

            DiagVars DV;
            Weight weightClass;

            private static final Integer TIMEOUT = 50000;

            // Food Table
            private static final String METHOD_GET_FOOD_LIST = "GetFoodList";
            final static String SOAP_ACTION_GET_FOOD_LIST = "http://tempuri.org/IDietService/GetFoodList";

            // Add Daily Meal
            private static final String METHOD_ADD_DAILY_FOOD_ITEM = "AddDailyFoodItem";
            final static String SOAP_ACTION_ADD_DAILY_FOOD_ITEM = "http://tempuri.org/IDietService/AddDailyFoodItem";

            // Get Daily total calories
            private static final String METHOD_GET_DAILY_TOTAL = "GetDailyTotal";
            final static String SOAP_ACTION_GET_DAILY_TOTAL = "http://tempuri.org/IDietService/GetDailyTotal";

            // Weight Table
            private static final String METHOD_GET_WEIGHT = "GetWeight";
            final static String SOAP_ACTION_GET_WEIGHT = "http://tempuri.org/IDietService/GetWeight";

            // Daily Table
            private static final String METHOD_GET_MEALS = "GetMeals";
            final static String SOAP_ACTION_GET_MEALS = "http://tempuri.org/IDietService/GetMeals";

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
            TextView tv_QuantityLabel;
            TextView tv_LoadProgress;
            Spinner spnr_FoodList;
            EditText et_Quantity;
            ImageView iv_Connection;
            /**
             * ATTENTION: This was auto-generated to implement the App Indexing API.
             * See https://g.co/AppIndexing/AndroidStudio for more information.
             */
            private GoogleApiClient client;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(activity_main);

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
                tv_QuantityLabel = (TextView) findViewById(R.id.tv_QuantityLabel);
                tv_LoadProgress = (TextView) findViewById(R.id.tv_LoadProgress);

                et_Quantity = (EditText) findViewById(R.id.et_Quantity);

                iv_Connection = (ImageView) findViewById((R.id.iv_Connection));

                tv_CaloriesLabel.setMaxLines(1);
                tv_CaloriesLabel.setMaxWidth(300);
                tv_CaloriesLabel.setHeight(100);
                tv_CaloriesLabel.setTextSize(18);

                tv_Calories.setHeight(100);
                tv_Calories.setTextSize(18);

                tv_DailyTotalCalories.setHeight(120);
                tv_DailyTotalCalories.setTextSize(22);

                tv_LoadProgress.setTextColor(Color.parseColor("#FFFF00"));
                tv_LoadProgress.setText("");
                spnr_FoodList.setMinimumHeight(60);

                btn_Add.setText("Start");
                btn_NewMeal.setVisibility(View.INVISIBLE);
                btn_TodaysMeals.setVisibility(View.INVISIBLE);
                btn_Details.setVisibility(View.INVISIBLE);
                btn_Weight.setVisibility(View.INVISIBLE);
                spnr_FoodList.setVisibility(View.INVISIBLE);
                et_Quantity.setVisibility(View.INVISIBLE);
                tv_DailyTotalCalories.setVisibility(View.INVISIBLE);
                tv_CaloriesLabel.setVisibility(View.INVISIBLE);
                tv_QuantityLabel.setVisibility(View.INVISIBLE);

//                tv_LoadProgress.setVisibility(View.INVISIBLE);

                spnr_FoodList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // your code here
                        FoodItem currentItem;
                        Integer index;
                        String food;

                        if (!spinnerPosition.equals(0) && trigger) {
                            spnr_FoodList.setSelection(spinnerPosition);
                            spinnerPosition = -1;
                            trigger = false;
                        } else {
                            trigger = true;
                        }

                        index = spnr_FoodList.getSelectedItemPosition();
                        food = spnr_FoodList.getSelectedItem().toString();

                        if (!index.equals(0)) {
                            et_Quantity.setText("1");
                            et_Quantity.setSelection(et_Quantity.getText().length());
                            currentItem = GetFoodItem(food);
                            tv_Calories.setText(currentItem.calories.toString());
                            foodName = currentItem.food1;
                            UpdateCalories(currentItem);
                        } else {
                            tv_Calories.setText("");
                            et_Quantity.setText("");
                            foodName = null;
                            mealQuantity = 0.0;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });

                et_Quantity.addTextChangedListener(new TextWatcher() {

                    public void afterTextChanged(Editable s) {
                        Double totCals;

                        FoodItem selectedItem;

                        if (!et_Quantity.getText().toString().matches("")) {
                            selectedItem = GetFoodItem(spnr_FoodList.getSelectedItem().toString());
                            totCals = UpdateCalories(selectedItem);
                            tv_Calories.setText(String.format(Locale.US, "%5.1f", totCals));
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

                WifiManager wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

//                *****************************************************************************
//                *****************************************************************************
//                This next bit requires you to create a custom class as follows:

//                public class URLStrings {
//                    public static final String LAN_SSID = "<your LAN SSID>";
                // This is used when you're on your own WiFi
//                    public static final String LAN_URL = "http://<IIS Server LAN IP>:<PORT>";
                // This is when you're not on your home WIFI
//                    public static final String WAN_URL = "http://<PUBLIC URL>:<PORT>";
//                }
//                *****************************************************************************
//                *****************************************************************************

                if (wifiInfo.getSSID().contains(URLStrings.LAN_SSID)) {
                    CommStrings.URL = URLStrings.LAN_URL;
                    iv_Connection.setImageResource(R.drawable.wifi);
                } else {
                    CommStrings.URL = URLStrings.WAN_URL;
                    iv_Connection.setImageResource(R.drawable.celldata);
                }
                Log.i("CYBERON", "SSID: " + wifiInfo.getSSID());
                Log.i("CYBERON", "URL: " + CommStrings.URL);

                DV.oR_hits = 0;
                DV.oPR_hits = 0;
                Log.i("CYBERON", "MainActivity setting WeightData.FirstRun: True");
                WeightData.FirstRun = true;

                // ATTENTION: This was auto-generated to implement the App Indexing API.
                // See https://g.co/AppIndexing/AndroidStudio for more information.
                client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
            }   // end onCreate

            @Override
            public void onResume() {
                super.onResume();
                String hitCounts = String.format(Locale.US, "onResume Hits: %s WeightData.FirstRun: %s", (++DV.oR_hits).toString(), (WeightData.FirstRun) ? "TRUE" : "FALSE");
                Log.i("CYBERON", hitCounts);

                if (!spinnerPosition.equals(0)) {
                    spnr_FoodList.setSelection(spinnerPosition);
                }
            }

            @Override
            protected void onPostResume() {
                super.onPostResume();

                String hitCounts = String.format(Locale.US, "onPostResume Hits: %s WeightData.FirstRun: %s", (++DV.oPR_hits).toString(), (WeightData.FirstRun) ? "TRUE" : "FALSE");
                Log.i("CYBERON", hitCounts);

//                if (WeightData.FirstRun)
//                {
//                    Log.i("CYBERON", "Loading Database ...");
//                    try {
//                        tv_LoadProgress.setText("Loading FoodList ...");
//                        new FoodListLoader().execute().get();
//                        tv_LoadProgress.setText("Loading DailyTotalCalories ...");
//                        new GetDailyTotalCalories().execute().get();
//                        tv_LoadProgress.setText("Loading Meals ...");
//                        new GetMeals().execute().get();
//                        tv_LoadProgress.setText("Loading Weights ...");
//                        new GetWeights().execute().get();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                    InitializeSpinner();
//                    WeightData.FirstRun = false;
//                }
//
//                spnr_FoodList.setSelection(0);
//                spnr_FoodList.setSelection(spinnerPosition);
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
                switch (id) {
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

                super.onOptionsItemSelected(item);
                return false;
            }

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_Add:
                        // Add meal to database here
                        if (WeightData.FirstRun) {
                            Log.i("CYBERON", "Loading Database ...");
                            new FoodListLoader().execute();
                            new GetDailyTotalCalories().execute();
                            new GetMeals().execute();
                            new GetWeights().execute();
                            InitializeSpinner();
                            WeightData.FirstRun = false;

                            spnr_FoodList.setSelection(0);
                            spnr_FoodList.setSelection(spinnerPosition);

                            btn_Add.setText("Add Meal");
                            btn_NewMeal.setVisibility(View.VISIBLE);
                            btn_TodaysMeals.setVisibility(View.VISIBLE);
                            btn_Details.setVisibility(View.VISIBLE);
                            btn_Weight.setVisibility(View.VISIBLE);
                            spnr_FoodList.setVisibility(View.VISIBLE);
                            et_Quantity.setVisibility(View.VISIBLE);
                            tv_DailyTotalCalories.setVisibility(View.VISIBLE);
                            tv_CaloriesLabel.setVisibility(View.VISIBLE);
                            tv_QuantityLabel.setVisibility(View.VISIBLE);
                        } else if (foodName != null && mealQuantity != 0.0) {
                            try {
                                new EnterNewMeal().execute().get();
                                new GetDailyTotalCalories().execute().get();
                                new GetMeals().execute().get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
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
                    if (resultCode == Activity.RESULT_OK) {

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
            }

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
                double totCals = 0;
                double cals;
                double qty;
                try {
                    if (!et_Quantity.getText().toString().matches("")) {
                        qty = Double.parseDouble(et_Quantity.getText().toString());
                        cals = (double) currentItem.calories;
                        totCals = cals * qty;
                        mealQuantity = qty;
                    }
                } catch (NumberFormatException e) {
                    totCals = 0;
                } finally {
                    return totCals;
                }
            }

            /**
             * ATTENTION: This was auto-generated to implement the App Indexing API.
             * See https://g.co/AppIndexing/AndroidStudio for more information.
             */
            public Action getIndexApiAction() {
                Thing object = new Thing.Builder()
                        .setName("Main Page") // TODO: Define a title for the content shown.
                        // TODO: Make sure this auto-generated URL is correct.
                        .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                        .build();
                return new Action.Builder(Action.TYPE_VIEW)
                        .setObject(object)
                        .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                        .build();
            }

            @Override
            public void onStart() {
                super.onStart();

                // ATTENTION: This was auto-generated to implement the App Indexing API.
                // See https://g.co/AppIndexing/AndroidStudio for more information.
                client.connect();
                AppIndex.AppIndexApi.start(client, getIndexApiAction());
            }

            @Override
            public void onStop() {
                super.onStop();

                // ATTENTION: This was auto-generated to implement the App Indexing API.
                // See https://g.co/AppIndexing/AndroidStudio for more information.
                AppIndex.AppIndexApi.end(client, getIndexApiAction());
                client.disconnect();
            }

//            ASYNC Tasks

            private class FoodListLoader extends AsyncTask<Void, Void, Void> {
                HttpTransportSE myHttpTransport = null;
                ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
                FoodItem foodItem;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
//                    pdLoading.setIndeterminate(true);
//                    pdLoading.setCancelable(false);
                    Log.i("CYBERON", "FoodListLoader");
                    Meals.FoodTable = new HashMap<Integer, FoodItem>();
                    pdLoading.setMessage("Loading Food List ...");
                    pdLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    WindowManager.LayoutParams wmlp = pdLoading.getWindow().getAttributes();
                    wmlp.gravity = Gravity.TOP;
                    wmlp.y = 50;
                    pdLoading.show();
                }

                @Override
                protected Void doInBackground(Void... params) {
                    Meals.FoodTable.clear();
                    SoapObject request = new SoapObject(CommStrings.NAMESPACE, CommStrings.METHOD_GET_FOOD_LIST);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.implicitTypes = true;
                    envelope.setOutputSoapObject(request);
                    do {
                        try {
                            myHttpTransport = new HttpTransportSE(CommStrings.URL, CommStrings.TIMEOUT);
                            myHttpTransport.debug = true;
                            myHttpTransport.call(CommStrings.SOAP_ACTION_GET_FOOD_LIST, envelope);

                            // 0 is the first object of data
                            SoapObject response = (SoapObject) envelope.getResponse();
                            SoapObject foodlist = (SoapObject) response.getProperty(0);
                            SoapObject mealItems = (SoapObject) response.getProperty(1);
                            SoapObject weightList = (SoapObject) response.getProperty(2);

                            SoapObject root = (SoapObject) response.getProperty(0);
                            //to get the data
                            foodList.clear();
                            foodList.add("");
                            for (int i = 0; i < foodlist.getPropertyCount() - 1; i++) {
                                foodItem = new FoodItem();
                                SoapObject item = (SoapObject) foodlist.getProperty(i);
                                String strRecNum = item.getProperty("recNum").toString();
                                Integer recNum = Integer.parseInt(strRecNum);
                                String food1 = item.getProperty("food1").toString();
                                String strCalories = item.getProperty("calories").toString();
                                Integer calories = Integer.parseInt(strCalories);
                                foodItem.recNum = recNum;
                                foodItem.food1 = food1;
                                foodItem.calories = calories;
                                Meals.FoodTable.put(recNum, foodItem);
                                foodList.add(food1);
                            }
                        } catch (XmlPullParserException e) {
                            Log.i("CYBERON", "FoodListLoader XmlPullParserException");
                            Log.i("CYBERON", "e.getMessage:\n" + e.getMessage());
                            Log.i("CYBERON", "envelope.bodyin:\n" + (String) envelope.bodyIn);
                            Log.i("CYBERON", "\nmyHttpTransport.responseDump:\n" + myHttpTransport.responseDump);
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e.printStackTrace(pw);
                            Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                        } catch (SoapFault e) {
                            Log.i("CYBERON", "FoodListLoader SoapFault");
                            Log.i("CYBERON", e.getMessage());
                            Log.i("CYBERON", "Response Dump: " + myHttpTransport.responseDump);
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e.printStackTrace(pw);
                            Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                            e.printStackTrace();
                        } catch (IOException e) {
                            Log.i("CYBERON", "FoodListLoader IOException");
                            Log.i("CYBERON", e.getMessage());
                            Log.i("CYBERON", "Response Dump: " + myHttpTransport.responseDump);
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e.printStackTrace(pw);
                            Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                            e.printStackTrace();
                        } catch (Exception e) {
                            Log.i("CYBERON", "FoodListLoader Error");
                            Log.i("CYBERON", "Message: " + e.getMessage());
                            Log.i("CYBERON", "Response Dump: " + myHttpTransport.responseDump);
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e.printStackTrace(pw);
                            Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                            e.printStackTrace();
                        }
                    } while (foodList.isEmpty());
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    pdLoading.dismiss();
                }
            }

            private class GetDailyTotalCalories extends AsyncTask<Void, Void, Void> {
                HttpTransportSE myHttpTransport = null;
                ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
                double totalCalories;
                boolean success = false;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pdLoading.setIndeterminate(true);
                    pdLoading.setCancelable(false);
                    Log.i("CYBERON", "GetDailyTotalCalories");
                    pdLoading.setMessage("Retreiving total calories for day ...");
                    pdLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    WindowManager.LayoutParams wmlp = pdLoading.getWindow().getAttributes();
                    wmlp.gravity = Gravity.TOP;
                    wmlp.y = 550;
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
                    SoapObject request = new SoapObject(CommStrings.NAMESPACE, CommStrings.METHOD_GET_DAILY_TOTAL);
                    request.addProperty("date", date);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    do {
                        try {
                            myHttpTransport = new HttpTransportSE(CommStrings.URL, CommStrings.TIMEOUT);
                            myHttpTransport.debug = true;
                            myHttpTransport.call(CommStrings.SOAP_ACTION_GET_DAILY_TOTAL, envelope);

                            // To retrieve a group of records
                            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

                            totalCalories = Double.parseDouble(response.toString());
                            Meals.TOTAL_CALORIES = totalCalories;
                            dailyTotalCalories = String.format(Locale.US, "%1.1f", totalCalories);
                            success = true; // communications successful

                            // 0 is the first object of data
                        } catch (XmlPullParserException e) {
                            Log.i("CYBERON", "GetDailyTotalCalories XmlPullParserException");
                            Log.i("CYBERON", e.getMessage());
                            Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e.printStackTrace(pw);
                            Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                            e.printStackTrace();
                        } catch (SoapFault e) {
                            Log.i("CYBERON", "GetDailyTotalCalories SoapFault");
                            Log.i("CYBERON", e.getMessage());
                            Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e.printStackTrace(pw);
                            Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                            e.printStackTrace();
                        } catch (IOException e) {
                            Log.i("CYBERON", "GetDailyTotalCalories IOException");
                            Log.i("CYBERON", e.getMessage());
                            Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e.printStackTrace(pw);
                            Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                            e.printStackTrace();
                        } catch (Exception e) {
                            Log.i("CYBERON", "GetDailyTotalCalories Exception");
                            Log.i("CYBERON", e.getMessage());
                            Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e.printStackTrace(pw);
                            Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                            e.printStackTrace();
                        }
                    } while (!success);
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

            private class GetMeals extends AsyncTask<Void, Void, Void> {
                HttpTransportSE myHttpTransport = null;
                ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
                Meal meal;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pdLoading.setIndeterminate(true);
                    pdLoading.setCancelable(false);
                    Log.i("CYBERON", "GetMeals");
                    pdLoading.setMessage("Loading Daily Meals ...");
                    pdLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    WindowManager.LayoutParams wmlp = pdLoading.getWindow().getAttributes();
                    wmlp.gravity = Gravity.TOP;
                    wmlp.y = 1050;
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
                    SoapObject request = new SoapObject(CommStrings.NAMESPACE, CommStrings.METHOD_GET_MEALS);
                    request.addProperty("date", date);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    try {

                        myHttpTransport = new HttpTransportSE(CommStrings.URL, CommStrings.TIMEOUT);
                        myHttpTransport.debug = true;
                        myHttpTransport.call(CommStrings.SOAP_ACTION_GET_MEALS, envelope);

                        // To retrieve a group of records
                        SoapObject response = (SoapObject) envelope.getResponse();
                        SoapObject foodList = (SoapObject) response.getProperty(0);
                        SoapObject mealItems = (SoapObject) response.getProperty(1);
                        SoapObject weightList = (SoapObject) response.getProperty(2);


                        // 0 is the first object of data
                        for (int i = 0; i < mealItems.getPropertyCount(); i++) {
                            SoapObject item = (SoapObject) mealItems.getProperty(i);

                            if (!item.getProperty("Food").toString().equals(null)) {
                                meal = new Meal();
                                meal.Food = item.getProperty("Food").toString();
                                meal.Quantity = Double.parseDouble(item.getProperty("Quantity").toString());
                                meal.Calories = Integer.parseInt(item.getProperty("Calories").toString());
                                Meals.MealList.add(meal);
                            } else {
                                break;
                            }
                        }
                    } catch (XmlPullParserException e) {
                        Log.i("CYBERON", "GetMeals XmlPullParserException");
                        Log.i("CYBERON", e.getMessage());
                        Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                    } catch (SoapFault e) {
                        Log.i("CYBERON", "GetMeals SoapFault");
                        Log.i("CYBERON", e.getMessage());
                        Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.i("CYBERON", "GetMeals IOException");
                        Log.i("CYBERON", e.getMessage());
                        Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                        e.printStackTrace();
                    } catch (Exception e) {
                        Log.i("CYBERON", "GetMeals Exception");
                        Log.i("CYBERON", e.getMessage());
                        Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    pdLoading.dismiss();
                }
            }

            private class GetWeights extends AsyncTask<Void, Void, Void> {
                HttpTransportSE myHttpTransport = null;
                ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pdLoading.setIndeterminate(true);
                    pdLoading.setCancelable(false);
                    WeightData.FirstRun = true;
                    Log.i("CYBERON", "GetWeights");
                    pdLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    WindowManager.LayoutParams wmlp = pdLoading.getWindow().getAttributes();
                    wmlp.gravity = Gravity.TOP;
                    wmlp.y = 1550;
                    pdLoading.setMessage("Loading weights ...");
                    pdLoading.show();
                }

                @Override
                protected Void doInBackground(Void... params) {

                    WeightItem weight;
                    WeightData.GraphArray = new ArrayList<WeightItem>();

                    SoapObject request = new SoapObject(CommStrings.NAMESPACE, CommStrings.METHOD_GET_WEIGHT);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    do {
                        try {

                            myHttpTransport = new HttpTransportSE(CommStrings.URL, CommStrings.TIMEOUT);
                            myHttpTransport.debug = true;
                            myHttpTransport.call(CommStrings.SOAP_ACTION_GET_WEIGHT, envelope);

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
                                    WeightData.GraphArray.add(weight);
                                } else {
                                    break;
                                }
                            }
                        } catch (XmlPullParserException e) {
                            Log.i("CYBERON", "GetWeights XmlPullParserException");
                            Log.i("CYBERON", e.getMessage());
                            Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e.printStackTrace(pw);
                            Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                            e.printStackTrace();
                        } catch (SoapFault e) {
                            Log.i("CYBERON", "GetWeights SoapFault");
                            Log.i("CYBERON", e.getMessage());
                            Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e.printStackTrace(pw);
                            Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                            e.printStackTrace();
                        } catch (IOException e) {
                            Log.i("CYBERON", "GetWeights IOException");
                            Log.i("CYBERON", e.getMessage());
                            Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e.printStackTrace(pw);
                            Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                            e.printStackTrace();
                        } catch (Exception e) {
                            Log.i("CYBERON", "GetWeights Exception");
                            Log.i("CYBERON", e.getMessage());
                            Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e.printStackTrace(pw);
                            Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                            e.printStackTrace();
                        }
                    } while (WeightData.GraphArray.isEmpty());
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

            private class EnterNewMeal extends AsyncTask<Void, Void, Void> {
                HttpTransportSE myHttpTransport;
                ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    Log.i("CYBERON", "EnterNewMeal");
                    pdLoading.setIndeterminate(true);
                    pdLoading.setCancelable(false);
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

                    SoapObject request = new SoapObject(CommStrings.NAMESPACE, CommStrings.METHOD_ADD_DAILY_FOOD_ITEM);
                    request.addProperty("date", date);       // string
                    request.addProperty("foodName", foodName);   // string
                    request.addProperty("qty", mealQuantity);        // double

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);

                    MarshalDouble md = new MarshalDouble();
                    md.register(envelope);

                    try {

                        myHttpTransport = new HttpTransportSE(CommStrings.URL, CommStrings.TIMEOUT);
                        myHttpTransport.debug = true;
                        myHttpTransport.call(CommStrings.SOAP_ACTION_ADD_DAILY_FOOD_ITEM, envelope);

                    } catch (XmlPullParserException e) {
                        Log.i("CYBERON", "FoodListLoader XmlPullParserException");
                        Log.i("CYBERON", e.getMessage());
                        Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                        e.printStackTrace();
                    } catch (SoapFault e) {
                        Log.i("CYBERON", "FoodListLoader SoapFault");
                        Log.i("CYBERON", e.getMessage());
                        Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.i("CYBERON", "FoodListLoader IOException");
                        Log.i("CYBERON", e.getMessage());
                        Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                        e.printStackTrace();
                    } catch (Exception e) {
                        Log.i("CYBERON", "FoodListLoader Error");
                        Log.i("CYBERON", "Message: " + e.getMessage());
                        Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                        e.printStackTrace();
                    }
                    foodName = null;
                    mealQuantity = 0.0;
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    pdLoading.dismiss();
                    spnr_FoodList.setSelection(0);
                    tv_Calories.setText("");
                    et_Quantity.setText("");
                }
            }
        }

