package diet.diet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MealsActivity extends AppCompatActivity {

    TextView tv_MealList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);
        tv_MealList = (TextView) findViewById(R.id.tv_MealList);
        tv_MealList.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_MealList.setTextSize(12);
        ListMeals();
    }

    private void ListMeals()
    {
        String mealsString = "";
        String food;
        Double quantity;
        Double total;
        mealsString =  "                                        TOTAL\n";
        mealsString += "QTY  MEAL                                CAL\n";
        mealsString += "---------------------------------------------\n";

        for (Integer i = 0; i <  Meals.MealList.size(); i++)
        {
            food = Meals.MealList.get(i).Food.length() > 35 ? Meals.MealList.get(i).Food.substring(0, 32) + " ..." : Meals.MealList.get(i).Food;
            quantity = Meals.MealList.get(i).Quantity;
            total = Meals.MealList.get(i).Quantity * Meals.MealList.get(i).Calories;
            mealsString += String.format("%4.1f %-34s %5.1f\n", quantity, food, total);
        }
        mealsString += "---------------------------------------------\n";
        mealsString += String.format("                       TOTAL CALORIES: %6.1f\n", Meals.TOTAL_CALORIES);
        tv_MealList.setText(mealsString);
    }
}
