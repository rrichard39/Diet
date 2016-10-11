package diet.diet;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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
            mealsString += String.format("%4.1f %-32s %+6.1f\n", quantity, food, total);
        }
        mealsString += "---------------------------------------------\n";
        mealsString += String.format("                       TOTAL CALORIES: %6.1f\n", Meals.TOTAL_CALORIES);
        tv_MealList.setText(mealsString);
    }
}
