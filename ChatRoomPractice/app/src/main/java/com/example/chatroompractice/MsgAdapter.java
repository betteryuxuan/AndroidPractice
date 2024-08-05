package com.example.chatroompractice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<Msg> mMsgList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView leftTime;
        private final TextView rightTime;
        TextView leftName;
        TextView rightName;
        ImageView rightImg;
        ImageView leftImg;
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;

        public ViewHolder(View v) {
            super(v);
            leftLayout = (LinearLayout) v.findViewById(R.id.left_layout);
            rightLayout = (LinearLayout) v.findViewById(R.id.right_layout);
            leftMsg = (TextView) v.findViewById(R.id.left_message);
            rightMsg = (TextView) v.findViewById(R.id.right_message);
            leftImg = (ImageView) v.findViewById(R.id.left_image);
            rightImg = (ImageView) v.findViewById(R.id.right_image);
            leftName = (TextView) v.findViewById(R.id.left_username);
            rightName = (TextView) v.findViewById(R.id.right_username);
            leftTime = v.findViewById(R.id.left_time);
            rightTime = v.findViewById(R.id.right_time);

        }
    }

    public MsgAdapter(List<Msg> mMsgList) {
        this.mMsgList = mMsgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        if (msg.getType() == Msg.TYPE_RECEIVED) {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftImg.setVisibility(View.VISIBLE);
            holder.rightImg.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
            holder.leftName.setText(msg.getUsername());
            holder.leftTime.setText(msg.getTime());
            holder.leftImg.setImageResource(msg.getImageId());
        } else if (msg.getType() == Msg.TYPE_SENT) {
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftImg.setVisibility(View.GONE);
            holder.rightImg.setVisibility(View.VISIBLE);
            holder.rightMsg.setText(msg.getContent());
            holder.rightName.setText(msg.getUsername());
            holder.rightTime.setText(msg.getTime());
            holder.rightImg.setImageResource(msg.getImageId());
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }
}
