package diet.diet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.concurrent.ExecutionException;

public class NewMealActivity extends AppCompatActivity implements View.OnClickListener {

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

        tv_Warning = (TextView) findViewById(R.id.tv_Warning);
        et_FoodName = (EditText) findViewById(R.id.et_FoodName);
        et_Calories = (EditText) findViewById(R.id.et_Calories);
        btn_AddToMealsList = (Button) findViewById(R.id.btn_AddToMealsList);
        btn_AddToMealsList.setOnClickListener(this);
        btn_Cancel = (Button) findViewById(R.id.btn_Cancel);
        btn_Cancel.setOnClickListener(this);

        tv_Warning.setTextColor(Color.RED);
        tv_Warning.setText("");

//        et_Calories.setKeyListener(DigitsKeyListener.getInstance(true, true));
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
                    tv_Warning.setText(getString(R.string.Warning));
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
                Intent returnIntent = new Intent();
                returnIntent.putExtra("foodName", "CANCEL");
                returnIntent.putExtra("calories", "CANCEL");
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
            pdLoading.setMessage("Sending...");
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params) {

            SoapObject request = new SoapObject(CommStrings.NAMESPACE, CommStrings.METHOD_ADD_NEW_FOOD);
            request.addProperty("foodName", foodName);   // String
            request.addProperty("calories", calories);   // Integer

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            MarshalDouble md = new MarshalDouble();
            md.register(envelope);

            do {
                try {

                    HttpTransportSE myHttpTransport = new HttpTransportSE(CommStrings.URL, CommStrings.TIMEOUT);
                    myHttpTransport.call(CommStrings.SOAP_ACTION_ADD_NEW_FOOD, envelope);
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
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
