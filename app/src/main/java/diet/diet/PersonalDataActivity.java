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
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static diet.diet.MainActivity.returnFromActivity;

public class PersonalDataActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_Enter;
    EditText et_Name;
    EditText et_Height;
    EditText et_InitialWeight;
    EditText et_TargetWeight;
    EditText et_SSID;
    TextView tv_Warning;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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
        if (!PersonalData.Name.equals(""))
        {
            et_Name.setText(PersonalData.Name);
            et_Height.setText(String.format(Locale.US, "%.2f", PersonalData.Height));
            et_InitialWeight.setText(String.format(Locale.US, "%.0f", PersonalData.InitialWeight));
            et_TargetWeight.setText(String.format(Locale.US, "%.0f", PersonalData.TargetWeight));
            et_SSID.setText(PersonalData.SSID);
        }
    }

    @Override
    public void onClick(View v)
    {

        if (!et_Name.getText().toString().equals("DELETE"))
        {
            if (et_Name.getText().toString().equals("") || et_Height.getText().toString().equals("") || et_InitialWeight.getText().toString().equals("") || et_TargetWeight.getText().toString().equals("")) {
                tv_Warning.setText(getString(R.string.Warning2));
            } else {
                PersonalData.Name = et_Name.getText().toString();
                PersonalData.Height = Float.parseFloat(et_Height.getText().toString());
                PersonalData.InitialWeight = Double.parseDouble(et_InitialWeight.getText().toString());
                PersonalData.TargetWeight = Double.parseDouble(et_TargetWeight.getText().toString());
                PersonalData.SSID = et_SSID.getText().toString();

                try {
                    WritePersonalData();
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
        }
        else
        {
            DeletePersonalData();
        }
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void WritePersonalData()  throws IOException  //write PersonalData.txt local file
    {
        String fileName = "PersonalData.txt";
        Log.i("CYBERON", "PersonalData path: " + getApplicationContext().getCacheDir().toString());
        try {
            PrintStream fileStream = new PrintStream(new File(getCacheDir(), fileName));
            fileStream.println(PersonalData.Name);
            fileStream.println(String.format(Locale.US, "%f", PersonalData.Height));
            fileStream.println(String.format(Locale.US, "%f", PersonalData.InitialWeight));
            fileStream.println(String.format(Locale.US, "%f", PersonalData.TargetWeight));
            fileStream.println(PersonalData.SSID);
            fileStream.flush();
            fileStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.i("CYBERON", "SetPersonalData Error: " + e.toString());
        }
    }

    private void DeletePersonalData()
    {
        String filename = "PersonalData.txt";
        File file = new File(getCacheDir(), filename);
        file.delete();

        try {
            new DeletePersonalData().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("CYBERON", "SavePersonData Interrupt Exception: " + e.toString());
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.i("CYBERON", "SavePersonData Execution Exception: " + e.toString());
        }
        Intent returnIntent = new Intent(this, MainActivity.class);
        returnIntent.putExtra("true", returnFromActivity);
        returnIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(returnIntent);
        finish();
    }

    public void savePersonalData() throws IOException  // save PersonalData to database
    {
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
            Log.i("CYBERON", "SavePersonData Interrupt Exception: " + e.toString());
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.i("CYBERON", "SavePersonData Execution Exception: " + e.toString());
        }
        Intent returnIntent = new Intent(this, MainActivity.class);
        returnIntent.putExtra("true", returnFromActivity);
        returnIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(returnIntent);
        finish();
    }

    private class SetPersonalData extends AsyncTask<Void, Void, Void>
    {
        Boolean success;
        private String userName;
        private float userHeight;
        private float userInitialWeight;
        private float userTargetWeight;
        private String userSSID;

        ProgressDialog pdLoading = new ProgressDialog(PersonalDataActivity.this);

        SetPersonalData(String name, float height, float iWeight, float tWeight, String ssid)
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

    private class DeletePersonalData extends AsyncTask<Void, Void, Void> {
        HttpTransportSE myHttpTransport;
        ProgressDialog pdLoading = new ProgressDialog(PersonalDataActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("CYBERON", "DeletePersonalData");
            pdLoading.setIndeterminate(true);
            pdLoading.setCancelable(false);
            pdLoading.setMessage("Deleting Personal Data from Database");
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(CommStrings.NAMESPACE, CommStrings.METHOD_DELETE_PERSONAL_DATA);
            // TODO Move date to Service
            request.addProperty("name", PersonalData.Name);       // string

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            try {

                myHttpTransport = new HttpTransportSE(CommStrings.URL, CommStrings.TIMEOUT);
                myHttpTransport.debug = true;
                myHttpTransport.call(CommStrings.SOAP_ACTION_DELETE_PERSONAL_DATA, envelope);

            } catch (XmlPullParserException e) {
                Log.i("CYBERON", "DeletePersonalData XmlPullParserException");
                Log.i("CYBERON", e.getMessage());
                Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                e.printStackTrace();
            } catch (SoapFault e) {
                Log.i("CYBERON", "DeletePersonalData SoapFault");
                Log.i("CYBERON", e.getMessage());
                Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                e.printStackTrace();
            } catch (IOException e) {
                Log.i("CYBERON", "DeletePersonalData IOException");
                Log.i("CYBERON", e.getMessage());
                Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
                e.printStackTrace();
            } catch (Exception e) {
                Log.i("CYBERON", "DeletePersonalDatar Error");
                Log.i("CYBERON", "Message: " + e.getMessage());
                Log.i("CYBERON", "Request Dump: " + myHttpTransport.requestDump);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                Log.i("CYBERON", "Stack Trace:\n" + sw.toString());
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
