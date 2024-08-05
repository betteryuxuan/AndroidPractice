package com.example.chatroompractice;

import android.util.Log;

import com.example.chatroompractice.databinding.ActivityChatBinding;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientReaderThread extends Thread {
    private Socket socket;
    private MsgAdapter adapter;
    private List<Msg> msgList;
    private ActivityChatBinding binding;
    private InputStream is;
    private DataInputStream dis;

    public ClientReaderThread(Socket socket, MsgAdapter adapter, List<Msg> msgList, ActivityChatBinding binding) {
        this.socket = socket;
        this.adapter = adapter;
        this.msgList = msgList;
        this.binding = binding;
    }

    @Override
    public void run() {
        try {
            is = socket.getInputStream();
            dis = new DataInputStream(is);
            String msg;
            while (!socket.isClosed()) {
                try {
                    // 接收到了消息
                    msg = dis.readUTF();
                    Log.d("ClientReaderThread", "Received message: " + msg);
                    if (msg.startsWith("ONLINE_USER_COUNT:")) {
                        String countString = msg.substring(msg.indexOf(":") + 1);
                        int countInt = Integer.parseInt(countString);
                        // 更新UI需要主线程里进行
                        ((ChatActivity) ChatActivity.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.tvOnLineUser.setText("在线人数：" + countInt);
                                Log.d("在线人数", "Online user count updated: " + countInt);
                            }
                        });
                    } else {
                        String[] split = msg.split("\\|");
                        String username = split[0];
                        String time = split[1];
                        String content = split[2];
                        int avatarId = Integer.parseInt(split[3]);
                        Msg msg1 = new Msg(content, Msg.TYPE_RECEIVED, username, time, avatarId);

                        ((ChatActivity) ChatActivity.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                msgList.add(msg1);
                                adapter.notifyItemInserted(msgList.size() - 1);
                                binding.msgRecyclerView.scrollToPosition(msgList.size() - 1);
                            }
                        });
                    }
                } catch (IOException e) {
                    dis.close();
                    socket.close();
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // 确保在结束线程时关闭资源
            try {
                if (dis != null) {
                    dis.close();
                }
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                Log.e("ClientReaderThread", "IOException while closing resources", e);
            }
        }
    }
}