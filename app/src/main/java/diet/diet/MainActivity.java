        package diet.diet;

        import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static diet.diet.R.layout.activity_main;

        public class MainActivity extends AppCompatActivity implements View.OnClickListener {

            static Integer spinnerPosition = 0;
            static Boolean trigger = false;
            public static String returnFromActivity = "false";
            Weight weightClass;
            static Meals MealList = new Meals();

            List<String> foodList = new ArrayList<>();  // for Spinner

            ArrayAdapter<String> foodListAdapter;

            String foodName;
            double mealQuantity;
            String dailyTotalCalories;
            String connectionID;
            String buildDate;

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
            TextView tv_SSID;
            Spinner spnr_FoodList;
            EditText et_Quantity;
            ImageView iv_Connection;
            /**
             * ATTENTION: This was auto-generated to implement the App Indexing API.
             * See https://g.co/AppIndexing/AndroidStudio for more information.
             */
            private GoogleApiClient client;

            @Override
            protected void onCreate(Bundle savedInstanceState)
            {
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
                tv_SSID = (TextView) findViewById(R.id.tv_SSID);

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

                tv_SSID.setTextSize(18);

                spnr_FoodList.setMinimumHeight(60);

                spnr_FoodList.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener()
                        {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
                            {
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
                                    tv_Calories.setText(String.format(Locale.US, "%d", currentItem.calories));
                                    foodName = currentItem.food;
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

                        }
                );

                et_Quantity.addTextChangedListener(new TextWatcher()
                {
                    public void afterTextChanged(Editable s)
                    {
                        Double totCals;

                        FoodItem selectedItem;

                        if (!et_Quantity.getText().toString().matches("")) {
                            selectedItem = GetFoodItem(spnr_FoodList.getSelectedItem().toString());
                            totCals = UpdateCalories(selectedItem);
                            tv_Calories.setText(String.format(Locale.US, "%5.1f", totCals));
                        }
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {
                    }
                }
                );

                setSupportActionBar(toolbar);

                SetUpCommumications();

                // ATTENTION: This was auto-generated to implement the App Indexing API.
                // See https://g.co/AppIndexing/AndroidStudio for more information.
                client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


                if (!ReadPersonalData())
                {
                    WeightData.FirstRun = true;
                    SetStartScreen();
                }
                else
                {
                    WeightData.FirstRun = false;
                    LoadDatabase();
                    SetMainScreen();
                }
                returnFromActivity = "false";

                Log.i("CYBERON", "OnCreate Complete");
            }   // end onCreate

            @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data)
            {
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == 1) // NewMealActivity
                {
                    if (resultCode == Activity.RESULT_OK)
                    {

                        String food = data.getStringExtra("foodName");
                        String cals = data.getStringExtra("calories");
                        if (!food.contains("CANCEL")) {
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
                        String food = data.getStringExtra("foodName");
                        String cals = data.getStringExtra("calories");
                        try {
                            new FoodListLoader().execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        InitializeFoodList();
                        InitializeSpinner();
                        tv_DailyTotalCalories.setText("Total calories for today: " + Double.toString(MealList.TOTAL_CALORIES));
                    }
                }

                if (requestCode == 2)           // PersonalDataActivity
                {
//                    InitializeFoodList();
//                    InitializeSpinner();
                }

                if (requestCode == 3)           // MealsActivity
                {
//                    InitializeFoodList();
//                    InitializeSpinner();
                }

                if (requestCode == 4)           // DetailsActivity
                {
//                    InitializeFoodList();
//                    InitializeSpinner();
                }

                if (requestCode == 5)           // WeightGraphActivity
                {
//                    InitializeFoodList();
//                    InitializeSpinner();

                }
            }

            @Override
            public void onResume()
            {
                super.onResume();
                Log.i("CYBERON", "OnResume Starting");

//                if (ReadPersonalData())     // Retrieve from file
//                {
//                    try {
//                        new GetPersonalData().execute().get(); // Retrieve from database
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                }

                if (!PersonalData.Name.equals(""))
                {
                    WeightData.FirstRun = false;
                    Log.i("CYBERON", "OnResume clearing FirstRun");
//                    SetMainScreen();
                }
                InitializeFoodList();
                InitializeSpinner();
                if (!spinnerPosition.equals(0)) {
                    spnr_FoodList.setSelection(spinnerPosition);
                }

                tv_DailyTotalCalories.setText("Total calories for today: " + Double.toString(MealList.TOTAL_CALORIES));
            }

            @Override
            public void onPostResume()
            {
                super.onPostResume();
//                LoadDatabase();
            }

            @Override
            protected void onDestroy()
            {
                super.onDestroy();
            }

            @Override
            public boolean onCreateOptionsMenu(Menu menu)
            {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.menu_main, menu);
                return true;
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item)
            {
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
                    case R.id.action_PersonalData:
                        try {
                            startActivityForResult(new Intent(this, PersonalDataActivity.class), 2);
                        } catch (Exception e) {
                            Log.i("CYBERON", e.getMessage());
                            e.printStackTrace();
                        }
                        return true;
                    case R.id.about: {
                        String appName = getResources().getString(R.string.app_name);

                        String author = "Author: ";
                        author += BuildConfig.AUTHOR;

                        String version = "Version: ";
                        version += Integer.toString(BuildConfig.MAJORver);
                        version += ".";
                        version += Integer.toString(BuildConfig.MINORver);
                        version += ".";
                        version += Integer.toString(BuildConfig.BUGFIXver);
                        version += ".";
                        version += Integer.toString(BuildConfig.VERSION_CODE);

                        String buildDate = "Build Date: ";
                        try{
                            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(BuildConfig.TIMESTAMP);
                            buildDate += formatter.format(calendar.getTime());
                        }catch(Exception e){
                        }

                        String message = String.format("%s\n\n%s\n%s\n%s", appName, author, version, buildDate);

                        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);

                        // set the message to display
                        alertbox.setMessage(message);

                        // add a neutral button to the alert box and assign a click listener
                        alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

                            // click listener on the alert box
                            public void onClick(DialogInterface arg0, int arg1) {
                                // the button was clicked

                            }
                        });

                        // show it
                        alertbox.show();
                    }
                }

                super.onOptionsItemSelected(item);
                return false;
            }

            @Override
            public void onClick(View view)
            {
                switch (view.getId()) {
                    case R.id.btn_Add:
                        if (WeightData.FirstRun)        // Button set to Start
                        {
                            try {
                                new GetPersonalData().execute().get();  // Retrieve from database
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }

                            if (PersonalData.Name.equals(""))
                            {
                                startActivityForResult(new Intent(this, PersonalDataActivity.class), 2);
                            }

                            Log.i("CYBERON", "Loading Database ...");
                            LoadDatabase();
                            WeightData.FirstRun = false;

                            SetMainScreen();
                        }

                        else if (!foodName.equals(null) && mealQuantity != 0.0)   // Button set to AddNewMeal
                        {
                            AddNewMeal();
                        }
                        break;
                    case R.id.btn_NewMeal:              // resultCode = 1
                        returnFromActivity = "true";
                        // Open NewMealActivity
                        Intent intent1 = new Intent(this, NewMealActivity.class);
                        startActivityForResult(intent1, 1);
                        break;
                    case R.id.btn_TodaysMeals:          // resultCode = 3
                        returnFromActivity = "true";
                        // Open MealActivity
                        Intent intent3 = new Intent(this, MealsActivity.class);
                        startActivityForResult(intent3, 3);
                        new GetDailyTotalCalories().execute();
                        break;
                    case R.id.btn_Details:              // resultCode = 4
                        returnFromActivity = "true";
                        // Open DetailsActivity
                        Intent intent4 = new Intent(this, DetailsActivity.class);
                        startActivityForResult(intent4, 4);
                        WeightData.FirstRun = false;

                        break;
                    case R.id.btn_Weight:               // resultCode = 5
                        returnFromActivity = "true";
                        // Open New Meal Activity
                        Intent intent5 = new Intent(this, WeightGraphActivity.class);
                        startActivityForResult(intent5, 5);
                        break;
                }
            }

            // FirstRun Methods
            // ----------------------------
            private void SetSSID(String ssid)  throws IOException
            {
                try {
                    FileWriter out = new FileWriter(new File(this.getFilesDir(), "SSID.txt"));
                    out.write(ssid);
                    out.close();
                } catch (IOException e) {
                    Log.i("CYBERON", "SetSSID Error: " + e.toString());
                }
           }

            private boolean ReadPersonalData()
            {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                String[] data = new String[5];
                int index = 0;
                BufferedReader in;
                Boolean result;

                String fileName = "PersonalData.txt";

                File filePath = new File(getApplicationContext().getCacheDir(), fileName);
                if(filePath.exists() && !filePath.isDirectory())
                {
                    try {
                        in = new BufferedReader(new FileReader(filePath));
                        while ((line = in.readLine()) != null)
                        {
                            data[index] = line;
                            index++;
                        }

                        if (data[0] != null) {
                            PersonalData.Name = data[0];
                            PersonalData.Height = Float.parseFloat(data[1]);
                            PersonalData.InitialWeight = Double.parseDouble(data[2]);
                            PersonalData.TargetWeight = Double.parseDouble(data[3]);
                            PersonalData.SSID = data[4];
                        }
                    } catch (FileNotFoundException e) {
                        Log.i("CYBERON", "ReadPersonalData Error FNF: " + e.toString());
                    } catch (IOException e) {
                        Log.i("CYBERON", "ReadPersonalData Error IO: " + e.toString());
                    }
                }

                if (data[0] == null)
                {
                    result = false;
                }
                else
                {
                    result = true;
                }


                return result;
            }

            private Boolean TestSSID()
            {
                if(!PersonalData.Name.equals(""))
                {
                    return true;
                }
                else
                {
                    return false;
                }
//                String filePath = getFilesDir().getAbsolutePath() + File.separator + "DietData" + File.separator + "FirstRun.txt";
//                File f = new File(filePath);
//                if (f.exists())
//                {
//                    return true;
//                }
//                else
//                {
//                    return false;
//                }
            }

            private void DeleteSSIDFile()
            {
                String filePath = getFilesDir().getAbsolutePath() + File.separator + "SSID.txt";
                File file = new File(filePath);
                file.delete();
                filePath = getFilesDir().getAbsolutePath() + File.separator + "SSSID.txt";
                file = new File(filePath);
                file.delete();
            }
            // ----------------------------

            // MainActivity Screen Setup Methods
            // ----------------------------
            private void SetMainScreen()
            {
                spnr_FoodList.setSelection(0);
                spnr_FoodList.setSelection(spinnerPosition);

                btn_Add.setText(R.string.AddMeal);
                btn_NewMeal.setVisibility(View.VISIBLE);
                btn_TodaysMeals.setVisibility(View.VISIBLE);
                btn_Details.setVisibility(View.VISIBLE);
                btn_Weight.setVisibility(View.VISIBLE);
                spnr_FoodList.setVisibility(View.VISIBLE);
                et_Quantity.setVisibility(View.VISIBLE);
                tv_DailyTotalCalories.setVisibility(View.VISIBLE);
                tv_CaloriesLabel.setVisibility(View.VISIBLE);
                tv_QuantityLabel.setVisibility(View.VISIBLE);
                tv_SSID.setVisibility(View.VISIBLE);
            }

            private void SetStartScreen()
            {
                btn_Add.setText(R.string.Start);
                btn_NewMeal.setVisibility(View.INVISIBLE);
                btn_TodaysMeals.setVisibility(View.INVISIBLE);
                btn_Details.setVisibility(View.INVISIBLE);
                btn_Weight.setVisibility(View.INVISIBLE);
                spnr_FoodList.setVisibility(View.INVISIBLE);
                et_Quantity.setVisibility(View.INVISIBLE);
                tv_DailyTotalCalories.setVisibility(View.INVISIBLE);
                tv_CaloriesLabel.setVisibility(View.INVISIBLE);
                tv_QuantityLabel.setVisibility(View.INVISIBLE);
                tv_SSID.setVisibility(View.VISIBLE);
            }
            // ----------------------------

            private void AddNewMeal()
            {
                Meal meal = new Meal();
                FoodItem thisMeal;
                Date today;
                String date;
                SimpleDateFormat formatter;

                formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                today = new Date();
                date = formatter.format(today);

                if (MealList.DATE == null)
                {
                    MealList.DATE = today;
                }
                
                if (MealList.MealList.isEmpty())
                {
                    MealList.DATE = today;
                }

                if (!MealList.DATE.equals(today))
                {
                    MealList.DATE = today;
                    MealList.MealList.clear();
                }

                thisMeal = GetFoodItem(foodName);
                meal.Food = foodName;
                meal.Calories = thisMeal.calories;
                meal.Quantity = mealQuantity;
                MealList.MealList.add(meal);

                MealList.TOTAL_CALORIES = MealList.TOTAL_CALORIES + (thisMeal.calories * mealQuantity);
                tv_DailyTotalCalories.setText("Total calories for today: " + Double.toString(MealList.TOTAL_CALORIES));
                new EnterNewMeal().execute();
//                try {
//                    new EnterNewMeal().execute();
////                    new GetDailyTotalCalories().execute().get();
////                    new GetMeals().execute().get();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
            }

            private void LoadDatabase()
            {
                if (MealList.FoodTable.isEmpty())
                {
                    new FoodListLoader().execute();
                }
                if (MealList.MealList.isEmpty())
                {
                    new GetDailyTotalCalories().execute();
                    new GetMeals().execute();
                }
                if (WeightData.GraphArray.isEmpty())
                {
                    new GetWeights().execute();
                }
                if (PersonalData.Name.equals(""))
                {
                    new GetPersonalData().execute();
                }
                InitializeSpinner();
            }

            private void SetUpCommumications()
            {
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
                // test for any WiFi
                ReadPersonalData();
                if (String.valueOf(wifiInfo.getSupplicantState()).equals("DISCONNECTED") || String.valueOf(wifiInfo.getSupplicantState()).equals("SCANNING"))
                {
                    CommStrings.URL = URLStrings.WAN_URL;
                    iv_Connection.setImageResource(R.drawable.celldata);
                    connectionID = "Data Usage";
                    tv_SSID.setText(connectionID);
                }
                else
                {
                    if (wifiInfo.getSSID().contains(PersonalData.SSID))
                    {
                        CommStrings.URL = URLStrings.LAN_URL;
                    }
                    else
                    {
                        CommStrings.URL = URLStrings.WAN_URL;
                    }
                    iv_Connection.setImageResource(R.drawable.wifi);
                    connectionID = wifiInfo.getSSID();
                    tv_SSID.setText(connectionID);
                }
                Log.i("CYBERON", "WiFi SSID: " + wifiInfo.getSSID());
                Log.i("CYBERON", "URL: " + CommStrings.URL);
            }

//            private boolean ReadPersonalData()
//            {
//                String filePath = getFilesDir().getAbsolutePath() + File.separator + "DietData" + File.separator + "DietData.txt";
//
//                InputStream instream = null;
//                try {
//                    // open the file for reading
//                    instream = new FileInputStream(filePath);
//                    // if file the available for reading
//                    if (instream != null) {
//                        // prepare the file for reading
//                        InputStreamReader inputreader = new InputStreamReader(instream);
//                        BufferedReader buffreader = new BufferedReader(inputreader);
//
//                        String line;
//
//                        line = buffreader.readLine();
//                        PersonalData.Name = line;
//                        line = buffreader.readLine();
//                        PersonalData.Height = Float.parseFloat(line);
//                        line = buffreader.readLine();
//                        PersonalData.InitialWeight = Double.parseDouble(line);
//                        line = buffreader.readLine();
//                        PersonalData.TargetWeight = Double.parseDouble(line);
//                        line = buffreader.readLine();
//                        PersonalData.SSID = line;
//
//                        instream.close();
//                    }
//                }
//                catch (Exception ex)
//                {
//                    // print stack trace.
//                    return false;
//                }
//                return true;
//            }

            private void InitializeSpinner()
            {
                foodListAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.my_spinner_layout, foodList);

                foodListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spnr_FoodList.setAdapter(foodListAdapter);
            }

            private  void InitializeFoodList()
            {
                FoodItem foodItem;
                Integer key;

                foodList.clear();
                foodList.add("");
                if (!WeightData.FirstRun) {
                    Iterator i = MealList.FoodTable.keySet().iterator();
                    while (i.hasNext()) {
                        key = (Integer) i.next();
                        foodItem = MealList.FoodTable.get(key);
                        foodList.add(foodItem.food);
                        Collections.sort(foodList);
                    }
                }
            }

            private FoodItem GetFoodItem(String foodSelection) {
                FoodItem value = null;
                FoodItem tempValue;
                Integer key;
                String food;
                Iterator i = MealList.FoodTable.keySet().iterator();

                while (i.hasNext()) {
                    key = (Integer) i.next();
                    tempValue = MealList.FoodTable.get(key);
                    food = tempValue.food;
                    if (food.equals(foodSelection)) {
                        value = MealList.FoodTable.get(key);
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
                    totCals = 0;}
                return totCals;
            }

            /**
             * ATTENTION: This was auto-generated to implement the App Indexing API.
             * See https://g.co/AppIndexing/AndroidStudio for more information.
             */
            public Action getIndexApiAction() {
                Thing object = new Thing.Builder()
                        .setName("Diet")
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
//                    MealList.FoodTable = new HashMap<Integer, FoodItem>();
                    pdLoading.setMessage("Loading Food List ...");
                    pdLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    WindowManager.LayoutParams wmlp = pdLoading.getWindow().getAttributes();
                    wmlp.gravity = Gravity.TOP;
                    wmlp.y = 50;
                    pdLoading.show();
                }

                @Override
                protected Void doInBackground(Void... params) {
                    MealList.FoodTable.clear();
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
                            SoapObject personalData = (SoapObject) response.getProperty(2);
                            SoapObject weightList = (SoapObject) response.getProperty(3);

                            //to get the data

                            foodList.clear();
                            foodList.add("");
                            for (int i = 0; i < foodlist.getPropertyCount() - 1; i++) {
                                foodItem = new FoodItem();
                                SoapObject item = (SoapObject) foodlist.getProperty(i);
                                String strRecNum = item.getProperty("recNum").toString();
                                Integer recNum = Integer.parseInt(strRecNum);
                                String food = item.getProperty("food1").toString();
                                String strCalories = item.getProperty("calories").toString();
                                Integer calories = Integer.parseInt(strCalories);
                                foodItem.recNum = recNum;
                                foodItem.food = food;
                                foodItem.calories = calories;
                                MealList.FoodTable.put(recNum, foodItem);
                                foodList.add(food);
                            }
                        } catch (XmlPullParserException e) {
                            Log.i("CYBERON", "FoodListLoader XmlPullParserException");
                            Log.i("CYBERON", "e.getMessage:\n" + e.getMessage());
                            Log.i("CYBERON", "envelope.bodyin:\n" + envelope.bodyIn);
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

                    formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
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
                            MealList.TOTAL_CALORIES = totalCalories;
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

//                    MealList.MealList = new ArrayList<>();

                    Date today;
                    String date;
                    SimpleDateFormat formatter;

                    formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
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
                        SoapObject personalData = (SoapObject) response.getProperty(2);
                        SoapObject weightList = (SoapObject) response.getProperty(3);


                        // 0 is the first object of data
                        for (int i = 0; i < mealItems.getPropertyCount(); i++) {
                            SoapObject item = (SoapObject) mealItems.getProperty(i);

                            if (!item.getProperty("Food").toString().equals(null)) {
                                meal = new Meal();
                                meal.Food = item.getProperty("Food").toString();
                                meal.Quantity = Double.parseDouble(item.getProperty("Quantity").toString());
                                meal.Calories = Integer.parseInt(item.getProperty("Calories").toString());
                                MealList.MealList.add(meal);
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
//                    WeightData.GraphArray = new ArrayList<WeightItem>();

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
                            SoapObject personalData = (SoapObject) response.getProperty(2);
                            SoapObject weightList = (SoapObject) response.getProperty(3);


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
                    pdLoading.setMessage("Adding daily meal to Today's Meals ...");
                    pdLoading.show();
                }

                @Override
                protected Void doInBackground(Void... params) {
                    Date today;
                    String date;
                    SimpleDateFormat formatter;

                    formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                    today = new Date();
                    date = formatter.format(today);

                    SoapObject request = new SoapObject(CommStrings.NAMESPACE, CommStrings.METHOD_ADD_DAILY_FOOD_ITEM);
                    // TODO Move date to Service
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

            private class GetPersonalData extends AsyncTask<Void, Void, Void> {
                HttpTransportSE myHttpTransport = null;
                ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pdLoading.setIndeterminate(true);
                    pdLoading.setCancelable(false);
                    Log.i("CYBERON", "PersonalData");
                    pdLoading.setMessage("Loading Personal Data ...");
                    pdLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    WindowManager.LayoutParams wmlp = pdLoading.getWindow().getAttributes();
                    wmlp.gravity = Gravity.TOP;
                    wmlp.y = 1050;
                    pdLoading.show();
                }
                @Override
                protected Void doInBackground(Void... params) {

                    SoapObject request = new SoapObject(CommStrings.NAMESPACE, CommStrings.METHOD_GET_PERSONAL_DATA);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    try {

                        myHttpTransport = new HttpTransportSE(CommStrings.URL, CommStrings.TIMEOUT);
                        myHttpTransport.debug = true;
                        myHttpTransport.call(CommStrings.SOAP_ACTION_GET_PERSONAL_DATA, envelope);

                        // To retrieve a group of records
                        SoapObject response = (SoapObject) envelope.getResponse();
                        SoapObject foodList = (SoapObject) response.getProperty(0);
                        SoapObject mealItems = (SoapObject) response.getProperty(1);
                        SoapObject personalData = (SoapObject) response.getProperty(2);
                        SoapObject weightList = (SoapObject) response.getProperty(3);


                        // 0 is the first object of data
                        for (int i = 0; i < personalData.getPropertyCount(); i++) {
                            SoapObject item = (SoapObject)personalData.getProperty(i);

                            if (!item.getProperty("name").toString().equals(""))
                            {
                                PersonalData.Name = item.getProperty("name").toString();
                                PersonalData.Height = Float.parseFloat(item.getProperty("height").toString());
                                PersonalData.InitialWeight = Float.parseFloat(item.getProperty("initialWeight").toString());
                                PersonalData.TargetWeight = Float.parseFloat(item.getProperty("targetWeight").toString());
                                PersonalData.SSID = item.getProperty("SSID").toString();
                            } else
                            {
                                break;
                            }
                        }
                    } catch (XmlPullParserException e) {
                        Log.i("CYBERON", "PersonalData XmlPullParserException");
                        Log.i("CYBERON", e.getMessage());
                        Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                    } catch (SoapFault e) {
                        Log.i("CYBERON", "PersonalData SoapFault");
                        Log.i("CYBERON", e.getMessage());
                        Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.i("CYBERON", "PersonalData IOException");
                        Log.i("CYBERON", e.getMessage());
                        Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                        e.printStackTrace();
                    } catch (Exception e) {
                        Log.i("CYBERON", "PersonalData Exception");
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
        }

