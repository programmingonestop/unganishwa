package com.kenyadevelopers.unganishwa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        boolean isFirstRun=getSharedPreferences("PREFERENCE",MODE_PRIVATE)
                .getBoolean("isFirstRun",true);

        if(isFirstRun)
        {
            final CheckBox checkbox=(CheckBox)findViewById(R.id.checkbox);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        Intent intent=new Intent(StartActivity.this,UnganishwaActivity.class);
                        startActivity(intent);
                        checkbox.setChecked(false);
                    }
                }
            });
        }
        else
            {
                Intent intent=new Intent(StartActivity.this,UnganishwaActivity.class);
                startActivity(intent);
            }
    }
}
