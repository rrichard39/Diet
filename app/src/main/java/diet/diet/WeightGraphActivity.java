package diet.diet;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class WeightGraphActivity extends AppCompatActivity implements View.OnClickListener {

    CommStrings CS;
    WeightData WD;
    Weight weightClass;

    Button btn_AddWeight;
    EditText et_Weight;
    LineChartView chart;

    Double newWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_graph);

        weightClass = new Weight();
        et_Weight = (EditText) findViewById(R.id.et_Weight);
        btn_AddWeight = (Button) findViewById(R.id.btn_AddWeight);
        btn_AddWeight.setOnClickListener(this);

        chart = (LineChartView) findViewById(R.id.chart);
        et_Weight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_Weight.setText("");
                }
            }
        });

        PopulateGraph();
    }

    public void PopulateGraph()
    {
        WD.GraphWeight = new ArrayList<PointValue>();
        WD.GraphGoal = new ArrayList<PointValue>();
        WD.GraphActual = new ArrayList<PointValue>();

        List<AxisValue> axisLabelsForX = new ArrayList<AxisValue>();
        List<AxisValue> axisLabelsForY = new ArrayList<AxisValue>();

        String converter;
        Integer index = 0;

        try {
            for (Integer i = 0; i < WD.GraphArray.size(); i++)
            {
                index = i;

                converter = WD.GraphArray.get(i).actual.toString();
                WD.GraphActual.add(new PointValue(i, Float.parseFloat(converter)));

                if (WD.GraphArray.get(i).goal > 0) {
                    WD.GraphGoal.add(new PointValue(i, WD.GraphArray.get(i).goal));
                }

                if (WD.GraphArray.get(i).weight1 > 0)
                {
                    WD.GraphWeight.add(new PointValue(i, WD.GraphArray.get(i).weight1));
                }

                converter = i.toString();
                axisLabelsForX.add(new AxisValue(Float.parseFloat(converter)).setLabel(WD.GraphArray.get(i).measureDate));
                axisLabelsForY.add(new AxisValue(Float.parseFloat(converter)).setLabel(String.format("%5.1f", WD.GraphArray.get(i).actual)));
            }
        } catch (NumberFormatException e) {
            Log.i("CYBERON", "WeightActivity: " + e.getMessage());
            Log.i("CYBERON", "WeightActivity: index = " + index.toString());
            e.printStackTrace();
        }

        try {
            Axis axisXLabels = new Axis(axisLabelsForX);
            Axis axisYLabels = new Axis(axisLabelsForY);

            //In most cased you can call data model methods in builder-pattern-like manner.
            Line weightLine = new Line(WD.GraphWeight).setColor(Color.BLUE).setCubic(false);
            Line goalLine = new Line(WD.GraphGoal).setColor(Color.GREEN).setCubic(false);
            Line actualLine = new Line(WD.GraphActual).setColor(Color.RED).setCubic(false);

            weightLine.setStrokeWidth(3);
            goalLine.setStrokeWidth(3);
            actualLine.setStrokeWidth(3);

            weightLine.setHasPoints(false);
            goalLine.setHasPoints(false);
            actualLine.setHasPoints(false);

            goalLine.setHasLabels(true);
            goalLine.setHasLines(true);

            List<Line> lines = new ArrayList<Line>();
            lines.add(weightLine);
            lines.add(goalLine);
            lines.add(actualLine);

            LineChartData data = new LineChartData();
            data.setLines(lines);
//        chart.setValueSelectionEnabled(true);

            axisXLabels.setTextColor(Color.BLACK);
            axisXLabels.setName("Date");
            axisXLabels.setHasLines(true);
            axisXLabels.setLineColor(Color.BLACK);
            axisXLabels.setMaxLabelChars(10);
            axisXLabels.setHasTiltedLabels(true);

            axisYLabels.setTextColor(Color.BLACK);
            axisYLabels.setName("Weight");
            axisYLabels.setHasLines(true);
            axisYLabels.setLineColor(Color.BLACK);
            axisYLabels.setMaxLabelChars(5);
            axisYLabels.setHasTiltedLabels(false);
            axisYLabels.setValues(axisLabelsForY);
            axisYLabels.setTextSize(12);
            axisYLabels.setAutoGenerated(true);     // THis makes the Y axis labels work

            data.setAxisYLeft(axisYLabels);
            data.setAxisXBottom(axisXLabels);

            chart.setLineChartData(data);
        } catch (Exception e) {
            Log.i("CYBERON", "Chart Setup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_AddWeight:
                Log.i("CYBERON", "btn_AddWeight pressed");
                newWeight = Double.parseDouble(et_Weight.getText().toString());
                try {
                    new EnterNewWeight().execute().get();
                    new GetWeights().execute().get();
                    Log.i("CYBERON", "OK 1");
                    weightClass.PopulateGraphArray();
                    Log.i("CYBERON", "OK 2");
                    weightClass.CalculateGraphArray();
                    Log.i("CYBERON", "OK 3");
                    PopulateGraph();
                    Log.i("CYBERON", "OK 4");
                } catch (InterruptedException e) {
                    Log.i("CYBERON", "1 WeightGraphActivity Error");
                    Log.i("CYBERON", e.getMessage());
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    Log.i("CYBERON", "2 WeightGraphActivity Error");
                    Log.i("CYBERON", e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.i("CYBERON", "6 WeightGraphActivity Error");
                    Log.i("CYBERON", e.getMessage());
                }

                break;
        }
    }

    private class EnterNewWeight extends AsyncTask<Void, Void, Void> {
        Boolean success;
        //                FoodItem foodItem;
        ProgressDialog pdLoading = new ProgressDialog(WeightGraphActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setIndeterminate(true);
            pdLoading.setCancelable(false);
            success = false;
            Log.i("CYBERON", "WeightGraphActivity: EnterWeight");
            pdLoading.setMessage("Adding weight to database ...");
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

            SoapObject request = new SoapObject(CS.NAMESPACE, CS.METHOD_ADD_WEIGHT);
            request.addProperty("date", date);       // string
            request.addProperty("newWeight", newWeight);        // double

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            MarshalDouble md = new MarshalDouble();
            md.register(envelope);

            do {
                try {

                    HttpTransportSE myHttpTransport = new HttpTransportSE(CS.URL, CS.TIMEOUT);
                    myHttpTransport.call(CS.SOAP_ACTION_ADD_WEIGHT, envelope);
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
            }while (!success);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.i("CYBERON", "I ran");
            WD.LastWeight = Float.parseFloat(et_Weight.getText().toString());
            pdLoading.dismiss();
//                    new GetDailyTotalCalories().execute();
            et_Weight.setText("");
        }
    }

    private class GetWeights extends AsyncTask<Void, Void, Void> {
        Boolean success;
        ProgressDialog pdLoading = new ProgressDialog(WeightGraphActivity.this);
        double totalCalories;
        String postString = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setIndeterminate(true);
            pdLoading.setCancelable(false);
            success = false;
            WD.FirstRun = true;
            Log.i("CYBERON", "WeightGraphActivity: GetWeights");
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
            do
            {
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
                } catch (XmlPullParserException e) {
                    Log.i("CYBERON", "3 WeightGraphActivity Error");
                    Log.i("CYBERON", e.getMessage());
                    e.printStackTrace();
                } catch (SoapFault e) {
                    Log.i("CYBERON", "4 WeightGraphActivity Error");
                    Log.i("CYBERON", e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i("CYBERON", "5 WeightGraphActivity Error");
                    Log.i("CYBERON", e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
//                        StackTraceElement[] stack = e.getStackTrace();
//                        String Trace = "";
//                        for(StackTraceElement line : stack)
//                        {
//                            Trace += line.toString();
//                            Trace += "\n";
//                        }
//                        Log.i("CYBERON", "Stack Trace:\n" + Trace);
                    Log.i("CYBERON", "6 WeightGraphActivity Error");
                    Log.i("CYBERON", e.getMessage());
                }
            } while (!success);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pdLoading.dismiss();
        }
    }
}
