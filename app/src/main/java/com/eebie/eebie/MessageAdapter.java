package com.eebie.eebie;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eebie.eebie.models.Message;
import com.eebie.eebie.models.User;

import java.util.ArrayList;
import java.util.List;


public class MessageAdapter extends BaseAdapter{
    private List<Message> Message;
    private Context context;
    private LayoutInflater layoutInflater;

    public MessageAdapter(Context context, List<Message> message) {
        this.context = context;
        this.Message = message;
    }

    @Override
    public int getCount() {
        return Message.size();
    }

    @Override
    public Object getItem(int position) {
        return Message.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.chat_message, viewGroup, false);
        Message item = (Message) getItem(i);
        TextView message_text = (TextView) rowView.findViewById(R.id.message_text);
        message_text.setText(item.getText());


        return rowView;
    }


}



//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View rowView = inflater.inflate(R.layout.chat_message, viewGroup, false);
//        Message item = (Message) getItem(i);
//        TextView message_text = (TextView) rowView.findViewById(R.id.message_text);
//        message_text.setText(item.getText());
//
//
//        return rowView;
//    }


