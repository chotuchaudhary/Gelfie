package com.example.chotu.gelfie.grid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chotu.gelfie.MainActivity;
import com.example.chotu.gelfie.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileInputStream;
import java.io.FileOutputStream;


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
       // System.out.println(path);
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


        switch (item.getItemId()) {
            case R.id.delete:
                Toast.makeText(this,"Deleted !",Toast.LENGTH_LONG).show();
                deleteImageUrl();
                Intent intent=new Intent();
                intent.putExtra("position",position);
                setResult(Activity.RESULT_OK,intent);
                finish();
                return true;
            case R.id.download:
                ProgressDialog progressDialog=new ProgressDialog(this);
                progressDialog.setMessage("downloading images ...");
                progressDialog.show();
                downloadImage();
                progressDialog.dismiss();

                Toast.makeText(this,"successfully downloaded",Toast.LENGTH_LONG).show();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


       // Intent intent=new Intent(this,Grid_view.class);
        //intent.putExtra("email",path.substring(0,path.indexOf("/")));
        //FullImageActivity.this.startActivity(intent);




    }

    void downloadImage()
    {
        StorageReference storageRef= FirebaseStorage.getInstance().getReference("/Photos/"+path)  ;
        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
               try
               {
                   FileOutputStream fos=new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/"+path+".jpg");
                   fos.write(bytes);

               }catch(Exception e)
               {

               }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
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