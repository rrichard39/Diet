package diet.diet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutionException;

public class PersonalDataActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_Enter;
    EditText et_Name;
    EditText et_Height;
    EditText et_InitialWeight;
    EditText et_TargetWeight;
    EditText et_SSID;
    TextView tv_Warning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btn_Enter = (Button) findViewById(R.id.btn_Enter);
        btn_Enter.setOnClickListener(this);

        et_Name = (EditText) findViewById(R.id.et_Name);
        et_Height = (EditText) findViewById(R.id.et_Height);
        et_InitialWeight = (EditText) findViewById(R.id.et_InitialWeight);
        et_TargetWeight = (EditText) findViewById(R.id.et_TargetWeight);
        et_SSID = (EditText) findViewById(R.id.et_SSID);
        tv_Warning = (TextView) findViewById(R.id.tv_Warning);
        tv_Warning.setTextColor(Color.RED);
        tv_Warning.setText("");

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        }
//    );
        if (PersonalData.Name != "")
        {
            et_Name.setText(PersonalData.Name);
            et_Height.setText(String.format("%.2f", PersonalData.Height));
            et_InitialWeight.setText(String.format("%.0f", PersonalData.InitialWeight));
            et_TargetWeight.setText(String.format("%.0f", PersonalData.TargetWeight));
            et_SSID.setText(PersonalData.SSID);
        }
    }

    @Override
    public void onClick(View v) {

        if (et_Name.getText().toString().equals("") || et_Height.getText().toString().equals("") || et_InitialWeight.getText().toString().equals("") || et_TargetWeight.getText().toString().equals(""))
        {
            tv_Warning.setText(getString(R.string.Warning2));
        }
        else
        {
            PersonalData.Name = et_Name.getText().toString();
            PersonalData.Height = Float.parseFloat(et_Height.getText().toString());
            PersonalData.InitialWeight = Double.parseDouble(et_InitialWeight.getText().toString());
            PersonalData.TargetWeight = Double.parseDouble(et_TargetWeight.getText().toString());
            PersonalData.SSID = et_SSID.getText().toString();

            try {
                SetSSID(PersonalData.SSID);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("CYBERON", "SSID file creation failed: " + e.toString());
            }

            try {
                savePersonalData();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("CYBERON", "Data file creation failed");
            }
        }
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void SetSSID(String ssid)  throws IOException
    {
        String filePath = getFilesDir().getAbsolutePath() + File.separator + "SSID.txt";
        Log.i("CYBERON", "SetSSID path: " + filePath);
        PrintStream fileStream = new PrintStream(new File(filePath));
        fileStream.println(ssid);
        fileStream.flush();
        fileStream.close();
    }

    public void savePersonalData() throws IOException {
        Intent returnIntent = new Intent();

        try {
            new SetPersonalData(
                    et_Name.getText().toString(),
                    Float.parseFloat(et_Height.getText().toString()),
                    Float.parseFloat(et_InitialWeight.getText().toString()),
                    Float.parseFloat(et_TargetWeight.getText().toString()),
                    et_SSID.getText().toString()
                    ).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // return result value
        setResult(Activity.RESULT_OK, returnIntent);
    }

    private class SetPersonalData extends AsyncTask<Void, Void, Void> {
        Boolean success;
        private String userName;
        private float userHeight;
        private float userInitialWeight;
        private float userTargetWeight;
        private String userSSID;

        ProgressDialog pdLoading = new ProgressDialog(PersonalDataActivity.this);

        public SetPersonalData(String name, float height, float iWeight, float tWeight, String ssid)
        {
            this.userName = name;
            this.userHeight = height;
            this.userInitialWeight = iWeight;
            this.userTargetWeight = tWeight;
            this.userSSID = ssid;
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

            SoapObject request = new SoapObject(CommStrings.NAMESPACE, CommStrings.METHOD_ADD_PERSONAL_DATA);
            request.addProperty("name", this.userName);
            request.addProperty("height", this.userHeight);
            request.addProperty("iWeight", this.userInitialWeight);
            request.addProperty("tWeight", this.userTargetWeight);
            request.addProperty("ssid", this.userSSID);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            MarshalFloat md = new MarshalFloat();
            md.register(envelope);

            do {
                try {

                    HttpTransportSE myHttpTransport = new HttpTransportSE(CommStrings.URL, CommStrings.TIMEOUT);
                    myHttpTransport.call(CommStrings.SOAP_ACTION_ADD_PERSONAL_DATA, envelope);
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
