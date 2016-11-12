package com.example.chotu.gelfie.grid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.chotu.gelfie.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Grid_view extends Activity {
    ArrayList<String> imageArrayList;
    ArrayList<String> urlArrayList;
    String email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_grid1);
        Bundle b=getIntent().getExtras();
        email=(String)b.get("email");

        getFromFirebase();

    }

    public void getFromFirebase(){
        urlArrayList=new ArrayList<>();
        imageArrayList=new ArrayList<>();
        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("Users").child(email);

        db.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){

                for(DataSnapshot dsp:dataSnapshot.getChildren())
                {
                    urlArrayList.add(dsp.getValue(String.class));
                    imageArrayList.add(dsp.getKey());

                }


              callAdapter();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void callAdapter()
    {
        GridView gridView = (GridView)findViewById(R.id.gridview);
        gridView.setAdapter(new MyAdapter(this,imageArrayList,urlArrayList));
        // Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fly_in_from_center);
        // gridView.setAnimation(anim);
        // anim.start();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // Sending image id to FullScreenActivity
                Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
                // passing array index
                i.putExtra("id", urlArrayList.get(position));
                i.putExtra("position",position+"");
                i.putExtra("path",imageArrayList.get(position));
                Grid_view.this.startActivityForResult(i,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                int  image=Integer.parseInt(data.getStringExtra("position"));
                String fileName=imageArrayList.get(image);
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();

// Create a reference to the file to delete
                StorageReference desertRef = storageRef.child("Photos/"+fileName);

// Delete the file
                desertRef.delete();
                imageArrayList.remove(image);
                urlArrayList.remove(image);
              //  System.out.println("image name which is deleted is --->"+image);
                getFromFirebase();
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
     /*   if (id == R.id.action_settings) {
            return true;
        }
       */
        return super.onOptionsItemSelected(item);
    }

}

