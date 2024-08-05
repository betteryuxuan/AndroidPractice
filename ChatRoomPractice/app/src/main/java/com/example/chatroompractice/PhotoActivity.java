package com.example.chatroompractice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatroompractice.databinding.ActivityPhotoBinding;

public class PhotoActivity extends AppCompatActivity {
    private ActivityPhotoBinding binding;
    private int selectedImageId = R.drawable.userphoto1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoBinding.inflate(getLayoutInflater());

        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.photo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getSupportActionBar().hide();

        binding.img1.setOnClickListener(v -> selectedImageId = R.drawable.userphoto1);
        binding.img2.setOnClickListener(v -> selectedImageId = R.drawable.userphoto2);
        binding.img3.setOnClickListener(v -> selectedImageId = R.drawable.userphoto3);
        binding.img4.setOnClickListener(v -> selectedImageId = R.drawable.userphoto4);
        binding.img5.setOnClickListener(v -> selectedImageId = R.drawable.userphoto5);
        binding.img6.setOnClickListener(v -> selectedImageId = R.drawable.userphoto6);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String ip = intent.getStringExtra("ip");
        String port = intent.getStringExtra("port");

        setButtonAnimation(binding.btnConfirm);
        setButtonAnimation(binding.img1);
        setButtonAnimation(binding.img2);
        setButtonAnimation(binding.img3);
        setButtonAnimation(binding.img4);
        setButtonAnimation(binding.img5);
        setButtonAnimation(binding.img6);

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PhotoActivity1", "Selected Image ID: " + selectedImageId);
                Intent intent = new Intent(PhotoActivity.this, ChatActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("ip", ip);
                intent.putExtra("port", port);
                intent.putExtra("imageResourceId", selectedImageId);
                startActivity(intent);
                finish();
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