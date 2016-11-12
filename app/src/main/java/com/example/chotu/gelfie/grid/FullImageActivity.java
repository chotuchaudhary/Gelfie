package com.example.chotu.gelfie.grid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chotu.gelfie.MainActivity;
import com.example.chotu.gelfie.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by CHOTU on 10/31/2016.
 */
public  class FullImageActivity extends Activity {
    String path="";
    String position="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);

        // get intent data
        Intent i = getIntent();

        // Selected image id
        String url = i.getExtras().getString("id");// url of images
        path=i.getExtras().getString("path");// path of this images in database....
        System.out.println(path);
        position= i.getExtras().getString("position");


        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
        Glide.with(this).load(url).into(imageView);

    }


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        Toast.makeText(this,"Deleted !",Toast.LENGTH_LONG).show();
        deleteImageUrl();
        Intent intent=new Intent();
        intent.putExtra("position",position);
        setResult(Activity.RESULT_OK,intent);
        finish();
       // Intent intent=new Intent(this,Grid_view.class);
        //intent.putExtra("email",path.substring(0,path.indexOf("/")));
        //FullImageActivity.this.startActivity(intent);



        return true;
    }

    void deleteImageUrl()
    {
        DatabaseReference db= FirebaseDatabase.getInstance().getReference();
        DatabaseReference db1=db.child("Users").child(path);
        db1.removeValue();

    }

    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();
        Intent intent=new Intent();
      //  intent.putExtra("image",path.substring(path.indexOf("/")+1));
        setResult(Activity.RESULT_CANCELED,intent);
        finish();

        // overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
    }


}