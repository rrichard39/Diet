package diet.diet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class PersonalDataActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_Enter;
    EditText et_Name;
    EditText et_Height;
    EditText et_InitialWeight;
    EditText et_TargetWeight;
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
    }

    @Override
    public void onClick(View v) {
        if (et_Name.getText().toString().equals("") || et_Height.getText().toString().equals("") || et_InitialWeight.getText().toString().equals("") || et_TargetWeight.getText().toString().equals(""))
        {
            tv_Warning.setText(getString(R.string.Warning2));
        }
        else
        {
            PersonalData.name = et_Name.getText().toString();
            PersonalData.height = Float.parseFloat(et_Height.getText().toString());
            PersonalData.InitialWeight = Double.parseDouble(et_InitialWeight.getText().toString());
            PersonalData.TargetWeight = Double.parseDouble(et_TargetWeight.getText().toString());

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

    public void savePersonalData() throws IOException {
        String dirPath = getFilesDir().getAbsolutePath() + File.separator + "DietData";
        File d = new File(dirPath);
        String filePath = getFilesDir().getAbsolutePath() + File.separator + "DietData" + File.separator + "DietData.txt";
        if (!d.exists())
        {
            d.mkdirs();
        }
        PrintStream fileStream = new PrintStream(new File(filePath));
        fileStream.println(et_Name.getText().toString());
        fileStream.println(et_Height.getText().toString());
        fileStream.println(et_InitialWeight.getText().toString());
        fileStream.println(et_TargetWeight.getText().toString());
        fileStream.flush();
        fileStream.close();
    }
}
