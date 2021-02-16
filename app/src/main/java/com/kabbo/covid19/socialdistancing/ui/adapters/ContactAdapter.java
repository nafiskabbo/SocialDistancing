package com.kabbo.covid19.socialdistancing.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kabbo.covid19.socialdistancing.data.models.Contact;
import com.kabbo.covid19.socialdistancing.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    List<Contact> contactList;

    public ContactAdapter(List<Contact> contactList) {
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_contact_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = contactList.get(position).getName();
        String email = contactList.get(position).getEmail();
        String location = contactList.get(position).getLocation();
        String time = contactList.get(position).getTime();

        holder.setData(name, email, location, time);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView contactName, contactEmail, contactLocation, contactTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //// TODO: initializing data in activity
            contactName = itemView.findViewById(R.id.contact_name);
            contactEmail = itemView.findViewById(R.id.contact_email);
            contactLocation = itemView.findViewById(R.id.contact_location);
            contactTime = itemView.findViewById(R.id.contact_time);
        }

        private void setData(String name, String email, String location, String time) {
            //// TODO: setting data in activity
            contactName.setText("You met " + name);
            contactEmail.setText("Email : " + email);
            contactLocation.setText("Location : " + location);
            contactTime.setText(time);

        }

    }
}

