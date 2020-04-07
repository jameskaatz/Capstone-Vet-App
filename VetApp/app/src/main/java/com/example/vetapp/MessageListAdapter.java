package com.example.vetapp;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

class MessageListAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<MessageListAdapter.MessageListViewHolder>{

    private ArrayList<Message> messageList;
    private User currentUser;

    public MessageListAdapter(ArrayList<Message> messages, User me) {
        messageList = messages;
        currentUser = me;
    }

    @NonNull
    @Override
    public MessageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(lp);

        MessageListViewHolder mlvh = new MessageListViewHolder(itemView);
        return mlvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListViewHolder holder, int position) {
        Message m = messageList.get(position);

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(m.getTimestamp());
        String date = DateFormat.format("dd-MM-yyyy HH:mm", cal).toString();

        if(m.getSender().equals(currentUser.getUid())) {
            //sender is current user
            LinearLayout.LayoutParams rparam = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            );
            holder.receivedMessage.setLayoutParams(rparam);

            LinearLayout.LayoutParams sparam = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    3
            );
            holder.sentMessage.setLayoutParams(sparam);

            holder.receivedContent.setText("");
            holder.receivedTimestamp.setText("");
            holder.sentContent.setText(m.getContent());
            holder.sentTimestamp.setText(date);
        } else {
            //sender is not current user
            LinearLayout.LayoutParams rparam = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    3
            );
            holder.receivedMessage.setLayoutParams(rparam);

            LinearLayout.LayoutParams sparam = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            );
            holder.sentMessage.setLayoutParams(sparam);

            holder.receivedContent.setText(m.getContent());
            holder.receivedTimestamp.setText(date);
            holder.sentContent.setText("");
            holder.sentTimestamp.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageListViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout receivedMessage;
        public TextView receivedContent;
        public TextView receivedTimestamp;

        public LinearLayout sentMessage;
        public TextView sentContent;
        public TextView sentTimestamp;

        public MessageListViewHolder(View view) {
            super(view);

            receivedMessage = view.findViewById(R.id.receivedMessage);
            receivedContent = view.findViewById(R.id.receivedContent);
            receivedTimestamp = view.findViewById(R.id.receivedTimestamp);

            sentMessage = view.findViewById(R.id.sentMessage);
            sentContent = view.findViewById(R.id.sentContent);
            sentTimestamp = view.findViewById(R.id.sentTimestamp);
        }
    }
}
