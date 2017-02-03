package diet.diet;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class WeightGraphActivity extends AppCompatActivity implements View.OnClickListener {

    Weight weightClass;

    Button btn_AddWeight;
    EditText et_Weight;
    LineChartView chart;

    float newWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_graph);

        weightClass = new Weight();
        et_Weight = (EditText) findViewById(R.id.et_Weight);
        btn_AddWeight = (Button) findViewById(R.id.btn_AddWeight);
        btn_AddWeight.setOnClickListener(this);

        chart = (LineChartView) findViewById(R.id.chart);
        et_Weight.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus) {
                    et_Weight.setText("");
                }
            }
        }
        );

        PopulateGraph();
    }

    public void PopulateGraph()
    {
        WeightData.GraphWeight = new ArrayList<>();
        WeightData.GraphTarget = new ArrayList<>();
        WeightData.GraphActual = new ArrayList<>();
        WeightData.GraphFirstLabel = new ArrayList<>();
        WeightData.GraphLastLabel = new ArrayList<>();

        List<AxisValue> axisLabelsForX = new ArrayList<>();
        List<AxisValue> axisLabelsForY = new ArrayList<>();

        String converter;
        Integer index = 0;

        try {
            for (Integer i = 0; i < WeightData.GraphArray.size(); i++)
            {
                index = i;

                converter = WeightData.GraphArray.get(i).actual.toString();
                WeightData.GraphActual.add(new PointValue(i, Float.parseFloat(converter)));

                if (WeightData.GraphArray.get(i).goal > 0 && WeightData.GraphArray.get(i).goal >= PersonalData.TargetWeight) {
                    WeightData.GraphTarget.add(new PointValue(i, WeightData.GraphArray.get(i).goal));
                }

                if (WeightData.GraphArray.get(i).weight1 > 0)
                {
                    WeightData.GraphWeight.add(new PointValue(i, WeightData.GraphArray.get(i).weight1));
                    if (i == 0)
                    {
                        WeightData.GraphFirstLabel.add(new PointValue(i, WeightData.GraphArray.get(i).weight1));
                    }
                    if (WeightData.GraphArray.get(i + 1).weight1 == 0)
                    {
                        WeightData.GraphLastLabel.add(new PointValue(i, WeightData.GraphArray.get(i).weight1));
                    }
                }

                converter = i.toString();
                axisLabelsForX.add(new AxisValue(Float.parseFloat(converter)).setLabel(WeightData.GraphArray.get(i).measureDate));
                axisLabelsForY.add(new AxisValue(Float.parseFloat(converter)).setLabel(String.format(Locale.US, "%5.1f", WeightData.GraphArray.get(i).actual)));
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
            Line weightLine = new Line(WeightData.GraphWeight).setColor(Color.BLUE).setCubic(false);
            Line goalLine = new Line(WeightData.GraphTarget).setColor(Color.GREEN).setCubic(false);
            Line actualLine = new Line(WeightData.GraphActual).setColor(Color.RED).setCubic(false);
            Line labelFirstLine = new Line(WeightData.GraphFirstLabel).setColor(Color.BLUE).setCubic(false);
            Line labelLastLine = new Line(WeightData.GraphLastLabel).setColor(Color.BLUE).setCubic(false);


            weightLine.setStrokeWidth(3);
            goalLine.setStrokeWidth(3);
            actualLine.setStrokeWidth(3);
            labelFirstLine.setStrokeWidth(1);
            labelLastLine.setStrokeWidth(1);

            weightLine.setHasPoints(false);
            goalLine.setHasPoints(false);
            actualLine.setHasPoints(false);
            labelFirstLine.setHasPoints(false);
            labelLastLine.setHasPoints(false);

            goalLine.setHasLabels(true);
            goalLine.setHasLines(true);

            labelFirstLine.setHasLabels(true);
            labelFirstLine.setHasLines(false);

            labelLastLine.setHasLabels(true);
            labelLastLine.setHasLines(false);

            SimpleLineChartValueFormatter formatter = new SimpleLineChartValueFormatter();
            formatter.setDecimalDigitsNumber(1);
            labelFirstLine.setFormatter(formatter);
            labelLastLine.setFormatter(formatter);

            List<Line> lines = new ArrayList<>();
            lines.add(weightLine);
            lines.add(goalLine);
            lines.add(actualLine);
            lines.add(labelFirstLine);
            lines.add(labelLastLine);

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
        et_Weight.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_AddWeight:
                if (!et_Weight.getText().toString().equals(""))
                {
                    newWeight = Float.parseFloat(et_Weight.getText().toString());
                    try {
                        new EnterNewWeight().execute().get();
                        new GetWeights().execute().get();
                        weightClass.PopulateGraphArray();
                        weightClass.CalculateGraphArray();
                        PopulateGraph();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }

    private class EnterNewWeight extends AsyncTask<Void, Void, Void> {
        Boolean success;
        ProgressDialog pdLoading = new ProgressDialog(WeightGraphActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setIndeterminate(true);
            pdLoading.setCancelable(false);
            success = false;
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

            SoapObject request = new SoapObject(CommStrings.NAMESPACE, CommStrings.METHOD_ADD_WEIGHT);
            request.addProperty("date", date);                          // string
            request.addProperty("newWeight", newWeight);                // float

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            MarshalFloat mf = new MarshalFloat();
            mf.register(envelope);

            do {
                try {

                    HttpTransportSE myHttpTransport = new HttpTransportSE(CommStrings.URL, CommStrings.TIMEOUT);
                    myHttpTransport.call(CommStrings.SOAP_ACTION_ADD_WEIGHT, envelope);
                    success = true;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }while (!success);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeightData.LastWeight = newWeight;
            pdLoading.dismiss();
            et_Weight.setText("");
        }
    }

    private class GetWeights extends AsyncTask<Void, Void, Void> {
        Boolean success;
        ProgressDialog pdLoading = new ProgressDialog(WeightGraphActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setIndeterminate(true);
            pdLoading.setCancelable(false);
            success = false;
            WeightData.FirstRun = true;
            pdLoading.setMessage("Loading weights ...");
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params) {

            WeightItem weight;
            WeightData.GraphArray = new ArrayList<>();

            SoapObject request = new SoapObject(CommStrings.NAMESPACE, CommStrings.METHOD_GET_WEIGHT);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            do
            {
                try {

                    HttpTransportSE myHttpTransport = new HttpTransportSE(CommStrings.URL, CommStrings.TIMEOUT);
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
                    success = true;
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (SoapFault e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
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
