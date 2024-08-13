package com.example.chatroompractice;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatroompractice.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportActionBar().hide();

        setButtonAnimation(binding.btnLogin);
        setButtonAnimation(binding.etUsername);
        setButtonAnimation(binding.etIp);
        setButtonAnimation(binding.etPort);

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.etUsername.getText().toString())) {
                    Toast.makeText(MainActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(binding.etIp.getText().toString())) {
                    Toast.makeText(MainActivity.this, "请输入IP地址", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(binding.etPort.getText().toString())) {
                    Toast.makeText(MainActivity.this, "请输入端口号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (binding.etUsername.getText().toString().contains("|")) {
                    Toast.makeText(MainActivity.this, "用户名不能包含|", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
                intent.putExtra("username", binding.etUsername.getText().toString());
                intent.putExtra("ip", binding.etIp.getText().toString());
                intent.putExtra("port", binding.etPort.getText().toString());
                startActivity(intent);
//                finish();
            }
        });
    }

    // 动画
    private void setButtonAnimation(View view) {
        final Animation scaleDown = AnimationUtils.loadAnimation(this, R.anim.button_scale);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.startAnimation(scaleDown);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.clearAnimation();
                        break;
                }
                // 返回false,系统将继续传递事件
                return false;
            }
        });
    }
}