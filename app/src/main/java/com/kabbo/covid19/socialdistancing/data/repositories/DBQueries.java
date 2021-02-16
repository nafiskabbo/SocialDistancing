package com.kabbo.covid19.socialdistancing.data.repositories;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kabbo.covid19.socialdistancing.data.models.Contact;
import com.kabbo.covid19.socialdistancing.ui.activities.ContactListActivity;
import com.kabbo.covid19.socialdistancing.ui.adapters.ContactAdapter;

import java.util.ArrayList;
import java.util.List;

public class DBQueries {

    ///////////////// LISTS
    public static List<Contact> contactList = new ArrayList<>();
    public static String name, email, location, time;
    ///////////////// LISTS

    public static void loadContactList(final Context context, final RecyclerView recyclerView, final Dialog loadingDialog) {
        contactList.clear();
        ////// TODO : fetching data from Database and loading CONTACT_LIST in ContactListActivity

        FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA")
                .document("CONTACT_LIST")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            long list_size = task.getResult().getLong("list_size");

                            if (list_size > 0) {

                                for (long i = (list_size - 1); i >= 0; i--) {

                                    name = task.getResult().getString("contact_name_" + String.valueOf(i));
                                    email = task.getResult().getString("contact_email_" + String.valueOf(i));
                                    location = task.getResult().getString("contact_location_" + String.valueOf(i));
                                    time = task.getResult().getString("contact_time_" + String.valueOf(i));

                                    contactList.add(new Contact(
                                            name, email, location, time
                                    ));

                                }
                                ////// TODO : after getting all data, loading into adapter
                                ContactListActivity.adapter = new ContactAdapter(contactList);
                                recyclerView.setAdapter(ContactListActivity.adapter);
                                ContactListActivity.adapter.notifyDataSetChanged();

                            }


                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();

                    }
                });

    }

    public static void setTotalCount(final Context context, final TextView totalCount) {

        FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA")
                .document("CONTACT_LIST")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            totalCount.setText("You met " + task.getResult().getLong("list_size") + " people till now..");

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public static void clearData() {
        contactList.clear();
    }
}
