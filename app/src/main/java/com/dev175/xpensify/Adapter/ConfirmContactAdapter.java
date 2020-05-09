package com.dev175.xpensify.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.dev175.xpensify.Model.Contact;
import com.dev175.xpensify.R;
import java.util.ArrayList;

public class ConfirmContactAdapter extends ArrayAdapter<Contact> {


    private final ArrayList<Contact> list;
    private final Activity context;

    public ConfirmContactAdapter(Activity context, ArrayList<Contact> list) {
        super(context, R.layout.item_contact_list, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected ImageView userImg;
        protected TextView name;
        protected TextView phoneNumber;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        final ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if (view==null)
        {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_confirm_contact,null);

            //Locate views in contact_item.xml
            holder.userImg = view.findViewById(R.id.userImgCon);
            holder.name = view.findViewById(R.id.contactNameCon);
            holder.phoneNumber = view.findViewById(R.id.contactNumberCon);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }

        //Set the results into Textviews
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.name.setText(list.get(position).getName());
        viewHolder.phoneNumber.setText(list.get(position).getPhoneNumber());

        return view;
    }

}
