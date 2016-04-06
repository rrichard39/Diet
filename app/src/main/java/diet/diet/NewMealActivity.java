package diet.diet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class NewMealActivity extends AppCompatActivity implements View.OnClickListener {

    CommStrings CS;
    TextView tv_Warning;
    EditText et_FoodName;
    EditText et_Calories;
    Button btn_AddToMealsList;
    Button btn_Cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_meal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        tv_Warning = (TextView) findViewById(R.id.tv_Warning);
        et_FoodName = (EditText) findViewById(R.id.et_FoodName);
        et_Calories = (EditText) findViewById(R.id.et_Calories);
        btn_AddToMealsList = (Button) findViewById(R.id.btn_AddToMealsList);
        btn_AddToMealsList.setOnClickListener(this);
        btn_Cancel = (Button) findViewById(R.id.btn_Cancel);
        btn_Cancel.setOnClickListener(this);

        tv_Warning.setTextColor(Color.RED);
        tv_Warning.setText("");
    }

    @Override
    public void onClick(View view) {
        String foodName;
        Integer calories;
        switch (view.getId())
        {
            case R.id.btn_AddToMealsList:
                if (et_FoodName.getText().toString().equals("") || et_Calories.getText().toString().equals(""))
                {
                    tv_Warning.setText("Please fill in both fields");
                }
                else
                {
                    foodName = et_FoodName.getText().toString();
                    calories = Integer.parseInt(et_Calories.getText().toString());
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("foodName", et_FoodName.getText().toString());
                    returnIntent.putExtra("calories", et_Calories.getText().toString());
                    // return result value
                    setResult(Activity.RESULT_OK, returnIntent);
                    // or, if no result
                    // setResult(Activity.RESULT_CANCELED, returnIntent);

                    try {
                        new AddNewFood(foodName, calories).execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
                break;
            case R.id.btn_Cancel:
                foodName = "CANCEL";
                calories = 0;
                Intent returnIntent = new Intent();
                returnIntent.putExtra("foodName", et_FoodName.getText().toString());
                returnIntent.putExtra("calories", et_Calories.getText().toString());
                // return result value
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
        }
    }

    private class AddNewFood extends AsyncTask<Void, Void, Void> {
        Boolean success;
        private String foodName;
        private Integer calories;
        ProgressDialog pdLoading = new ProgressDialog(NewMealActivity.this);

        public AddNewFood(String fd, Integer cals)
        {
            this.foodName = fd;
            this.calories = cals;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setIndeterminate(true);
            pdLoading.setCancelable(false);
            success = false;
            Log.i("CYBERON", "NewMealActivity: AddNewFood");
            pdLoading.setMessage("Sending...");
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params) {

            SoapObject request = new SoapObject(CS.NAMESPACE, CS.METHOD_ADD_NEW_FOOD);
            request.addProperty("foodName", foodName);   // String
            request.addProperty("calories", calories);   // Integer

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            MarshalDouble md = new MarshalDouble();
            md.register(envelope);

            do {
                try {

                    HttpTransportSE myHttpTransport = new HttpTransportSE(CS.URL, CS.TIMEOUT);
                    myHttpTransport.call(CS.SOAP_ACTION_ADD_NEW_FOOD, envelope);
                    success = true;
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
