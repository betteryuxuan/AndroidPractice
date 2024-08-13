package com.example.chatroompractice;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatroompractice.databinding.ActivityChatBinding;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private List<Msg> msgList = new ArrayList<>();
    private String username;
    private String ip;
    private String port;
    private int imageResourceId;
    private Socket socket;
    private MsgAdapter adapter;
    private OutputStream os = null;
    private DataOutputStream dos = null;
    private static ChatActivity context;
    private Runnable checkOnlineCountRunnable;
    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportActionBar().hide();

        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        ip = intent.getStringExtra("ip");
        port = intent.getStringExtra("port");
        imageResourceId = intent.getIntExtra("imageResourceId", R.drawable.userphoto1);
        Log.d("data1", username + " " + ip + " " + port + " " + imageResourceId);

        context = this;
//        initData();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.msgRecyclerView.setLayoutManager(manager);
        adapter = new MsgAdapter(msgList);
        binding.msgRecyclerView.setAdapter(adapter);

        // 初始化 ClientReaderThread用于接收消息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, Integer.parseInt(port));
                    //把适配器和消息集合传进去才能在里面增加发过来的消息
                    new ClientReaderThread(socket, adapter, msgList, binding).start();
                    os = socket.getOutputStream();
                    dos = new DataOutputStream(os);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            }
        }).start();

        setButtonAnimation(binding.btnBack);
        setButtonAnimation(binding.btnSend);
        binding.btnSend.setOnClickListener(v -> {
            String content = binding.edText.getText().toString();
            if (content.contains("|")) {
                Toast.makeText(this, "不能包含|", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.isEmpty(content)) {
                Msg msg = new Msg(content, Msg.TYPE_SENT, username, getFormattedCurrentTime(), imageResourceId);
                msgList.add(msg);
                adapter.notifyItemInserted(msgList.size() - 1);
                binding.msgRecyclerView.scrollToPosition(msgList.size() - 1);
                binding.edText.setText("");

                String msgToSend = username + "|" + getFormattedCurrentTime() + "|" + content + "|" + imageResourceId;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (dos != null) {
                                dos.writeUTF(msgToSend);
                                dos.flush();
                            } else {
                                Log.e("ChatActivity", "DataOutputStream is null");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }
        });

//        // 查询在线人数
//        checkOnlineCountRunnable = new Runnable() {
//            @Override
//            public void run() {
//                // 子线程
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            dos.writeUTF("GET_ONLINE_USER");
//                            dos.flush();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }).start();
//
//                // 在主线程中继续安排下一个查询操作
//                handler.postDelayed(this, 3000);
//            }
//        };
//
//        handler.post(checkOnlineCountRunnable);


        // 监听软键盘弹出和收起，自动滚动到底部
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                binding.getRoot().getWindowVisibleDisplayFrame(r);
                int screenHeight = binding.getRoot().getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) { // 如果键盘高度超过屏幕高度的15%
                    binding.msgRecyclerView.scrollToPosition(msgList.size() - 1);
                }
            }
        });

        // 返回键
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dos.close();
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
//                Intent intent1 = new Intent(ChatActivity.this, MainActivity.class);
//                startActivity(intent1);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (dos != null) {
                dos.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    private void initData() {
//        Msg msg1 = new Msg("你好", Msg.TYPE_RECEIVED, "杜月朋", "12:11", R.drawable.userphoto1);
//        msgList.add(msg1);
//        Msg msg2 = new Msg("今天心情怎么样", Msg.TYPE_RECEIVED, "杜月朋", "12:12", R.drawable.userphoto1);
//        msgList.add(msg2);
//        Msg msg3 = new Msg("挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de挺好de", Msg.TYPE_SENT, "小王", "12:13", R.drawable.userphoto2);
//        msgList.add(msg3);
//        Msg msg4 = new Msg("真的吗真的吗真的吗真的吗真的吗真的吗真的吗真的吗真的吗真的吗真的吗真的吗真的吗真的吗真的吗真的吗！", Msg.TYPE_RECEIVED, "杜月朋", "12:15", R.drawable.userphoto1);
//        msgList.add(msg4);
//    }

    // 封装获取当前时间并格式化的方法
    public static String getFormattedCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        return now.format(formatter);
    }

    //在读取类里可以添加信息
    public static ChatActivity getContext() {
        return context;
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
