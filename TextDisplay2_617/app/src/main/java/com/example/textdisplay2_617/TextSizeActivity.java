package com.example.textdisplay2_617;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TextSizeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_size);

        TextView tv = findViewById(R.id.tv_hello1);
        tv.setTextSize(30);
        tv.setTextColor(0xFFFF0000);

        TextView tv_code_background = findViewById(R.id.tv_hello2);
        tv_code_background.setBackgroundColor(Color.GRAY);
//        tv_code_background.setBackgroundResource(R.color.customcolor);

    }
}