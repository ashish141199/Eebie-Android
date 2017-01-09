package com.eebie.eebie;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eebie.eebie.models.Message;
import com.eebie.eebie.models.User;

import java.util.ArrayList;
import java.util.List;




public class MessageAdapter extends BaseAdapter {
    private final List<Message> Message;
    private Activity context;

    public MessageAdapter(Activity context, List<Message> message) {
        this.context = context;
        this.Message = message;
    }


    @Override
    public int getCount() {
        if (Message != null) {
            return Message.size();
        } else {
            return 0;
        }
    }
    @Override
    public Message getItem(int position) {
        if (Message != null) {
            return Message.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Message message = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.chat_message, parent, false);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        User user = Methods.getUserInstance(parent.getContext());
        boolean isMe = message.getUsername().equals(user.getUsername());

        setAlignment(holder, isMe);
        holder.message_text.setText(message.getText());
        return convertView;

    }

    public void add(Message message) {
        Message.add(message);
    }

    public void addAll(List<Message> messages) {
        Message.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, Boolean isMe) {
        Log.i("boooooolean", Boolean.toString(isMe));
        if (!isMe) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            holder.message_text.setLayoutParams(lp);

        }else{

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            holder.message_text.setLayoutParams(lp);


        }
    }


    private ViewHolder createViewHolder(View view) {
        ViewHolder holder = new ViewHolder();
        holder.message_text = (TextView) view.findViewById(R.id.message_text);


        return holder;

    }

    private static class ViewHolder {
        public TextView message_text;

    }


}


