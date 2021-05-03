package com.dheerendrakumar.firebaseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ViewPostActivity extends AppCompatActivity implements AdapterView.OnItemClickListener ,AdapterView.OnItemLongClickListener{

    private ListView postListview;
    private ArrayList<String> usernames;
    private ArrayAdapter arrayAdapter;
    private FirebaseAuth firebaseAuth;
    private ImageView postimageview;
    private TextView txtdesc;
    private ArrayList<DataSnapshot> dataSnapshots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        firebaseAuth = FirebaseAuth.getInstance();
        postimageview = findViewById(R.id.postImageview);
        txtdesc = findViewById(R.id.txtDesc);
        dataSnapshots = new ArrayList<>();

        postListview = findViewById(R.id.postListview);
        usernames = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,usernames);
        postListview.setAdapter(arrayAdapter);
        postListview.setOnItemClickListener(this);
        postListview.setOnItemLongClickListener(this);

        FirebaseDatabase.getInstance().getReference().child("my_users").child(firebaseAuth.getCurrentUser().getUid()).child("receiver_post").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                dataSnapshots.add(snapshot);
                String fromwhom = (String) snapshot.child("fromWhom").getValue();
                usernames.add(fromwhom);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        DataSnapshot mydataSnapshot = dataSnapshots.get(position);
        String downloadLink = (String) mydataSnapshot.child("imageLink").getValue();

        Picasso.get().load(downloadLink).into(postimageview);
        txtdesc.setText((String)mydataSnapshot.child("desc").getValue());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("delete entry")
                .setMessage("Are you sure you want to delete this ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseStorage.getInstance().getReference().child("my_image").child((String) dataSnapshots.get(position).child("imageIdentifier").getValue()).delete();
                        FirebaseDatabase.getInstance().getReference().child("my_users").child(firebaseAuth.getCurrentUser().getUid())
                                .child("receiver_post").child(dataSnapshots.get(position).getKey()).removeValue();


                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();


        return true;
    }
}