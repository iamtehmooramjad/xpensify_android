package com.dev175.xpensify.Adapter;



import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.dev175.xpensify.Common.Common;
import com.dev175.xpensify.Model.Contact;
import com.dev175.xpensify.R;
import java.util.ArrayList;
import java.util.Locale;

public class ContactListAdapter extends ArrayAdapter<Contact> {

    private final ArrayList<Contact> list;
    private final Activity context;
    private ArrayList<Contact> arrayList;


    public ContactListAdapter(Activity context, ArrayList<Contact> list) {
        super(context, R.layout.item_contact_list, list);
        this.context = context;
        this.list = list;
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(list);

    }

    static class ViewHolder {
        protected ImageView userImg;
        protected CheckBox checked;
        protected TextView name;
        protected TextView phoneNumber;
        protected TextView status;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.item_contact_list, null);
            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.userImg = (ImageView) view.findViewById(R.id.userImg);
            viewHolder.name = (TextView) view.findViewById(R.id.contactName);
            viewHolder.phoneNumber = (TextView) view.findViewById(R.id.contactNumber);
            viewHolder.status = (TextView) view.findViewById(R.id.contactStatus);
            viewHolder.checked = (CheckBox) view.findViewById(R.id.cbContact);

            viewHolder.checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked)
                {
                    Contact c = (Contact) viewHolder.checked.getTag();
                    c.setChecked(buttonView.isChecked());

                    if (isChecked)
                    {
                        if (!Common.groupContactsList.contains(c)) {
                            Common.groupContactsList.add(c);
                        }
                    }
                    else if (!isChecked)
                    {
                        Common.groupContactsList.remove(c);
                    }
                }
            });


            view.setTag(viewHolder);
            viewHolder.checked.setTag(list.get(position));
        }
        else
        {
            view = convertView;
            ((ViewHolder) view.getTag()).checked.setTag(list.get(position));
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(list.get(position).getName());
        holder.phoneNumber.setText(list.get(position).getPhoneNumber());
        holder.status.setText(list.get(position).getStatus());
        holder.checked.setChecked(list.get(position).getChecked());
        if (list.get(position).getStatus()!="Available")
        {
            holder.status.setTextColor(Color.RED);
            holder.checked.setVisibility(View.GONE);
            holder.checked.setEnabled(false);


        }
        else
        {
            holder.checked.setVisibility(View.VISIBLE);
            holder.checked.setEnabled(true);
            holder.status.setTextColor(Color.parseColor("#326c32"));
        }


        return view;
    }



    //Filter
    public void filter(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        list.clear();

        if (charText.length()==0)
        {
            list.addAll(arrayList);
        }
        else
        {
            for (Contact contact : arrayList){
                if (contact.getName().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    list.add(contact);
                }
            }
        }

        notifyDataSetChanged();
    }

}
