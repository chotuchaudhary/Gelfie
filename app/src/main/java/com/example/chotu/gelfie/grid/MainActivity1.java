package com.example.chotu.gelfie.grid;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.chotu.gelfie.MainActivity;
import com.example.chotu.gelfie.R;
import com.example.chotu.gelfie.model.UserDetails;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity1 extends Activity implements SurfaceHolder.Callback {

    boolean recording=false;
    private MediaRecorder mediaRecorder;
    private boolean cameraFront = false;
   static   Camera camera=null;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Firebase mFirebaseChild;

    private Context myContext;
    ShutterCallback shutterCallback;
    PictureCallback jpegCallback;
    Button image;
    Button changeCamera;
    Button video;
    Button view;
    int  cameraId;
    ProgressDialog  mProgressDialog;
    StorageReference mStorageRef;
    public DatabaseReference mDatabaseRef;
    String e_mail;
    DatabaseReference mRef2;
    ArrayList<String > mArrayList=new ArrayList<>();
    DatabaseReference db;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b=getIntent().getExtras();
         e_mail=(String)b.get("email");

        db=FirebaseDatabase.getInstance().getReference();



        myContext = this;
        mStorageRef= FirebaseStorage.getInstance().getReference();



        mProgressDialog=new ProgressDialog(this);

        setContentView(R.layout.activity_main1);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();


        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        jpegCallback = new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {

               try {

                    refreshCamera();


                   Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                   if (cameraFront) {

                        bitmap = rotateBitmap(bitmap, 270);

                    } else
                        bitmap = rotateBitmap(bitmap, 90);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    data = stream.toByteArray();

                    uploadImage(data,"img_"+System.currentTimeMillis());



                }catch (Exception e) {
                  //  System.out.println("error message"+e.getMessage());
                }

            }
        };

        image=(Button)findViewById(R.id.btnCapture);
        image.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        camera.takePicture(null, null, jpegCallback);
                    }
                }

        );
        initialize();
       // System.out.println("printing for debugging"+1);




    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this,"Siging out !",Toast.LENGTH_LONG).show();
        Intent intent=new Intent(this,MainActivity.class);
        MainActivity1.this.startActivity(intent);
       // surfaceDestroyed(surfaceHolder);


        return true;
    }

    public void uploadImage(byte[] data, final String fileName)
    {
        mProgressDialog.setMessage("Uploading image....");
        mProgressDialog.show();

        StorageReference filepath=mStorageRef.child("Photos").child(fileName);
        filepath.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgressDialog.dismiss();
               final String mUrl=taskSnapshot.getDownloadUrl().toString();
                DatabaseReference db1=db.child("Users").child(e_mail);
                DatabaseReference child=db1.child(fileName);
                child.setValue(mUrl);



                Toast.makeText(MainActivity1.this,"Upload done!",Toast.LENGTH_LONG).show();

            }
        });
    }

    public void setValue(Firebase child,UserDetails user,String fileName,String mUrl)
    {
        user.putImageUrl(fileName,mUrl);
        child.setValue(user);
    }



    public  Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }







    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        setCamera(camera);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            Log.d("", "Error starting camera preview: " + e.getMessage());
        }
    }




    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.

        refreshCamera();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // open the camera
            if (camera == null)
                camera = Camera.open();
        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e);
            return;
        }

        Camera.Parameters parameters = camera.getParameters();
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            parameters.set("orientation", "portrait");
            camera.setDisplayOrientation(90);
        }
        try {
            camera.setParameters(parameters);
            camera.setPreviewDisplay(holder);
        } catch (Exception e) {
            if (camera != null)
                camera.release();
            camera = null;
            e.printStackTrace();
            //  mCamera.release();
        }

    }



    public void surfaceDestroyed(SurfaceHolder holder) {
        // stop preview and release camera
        if(camera==null)
            return;
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public void setCamera(Camera camera1) {
        //method to set a camera instance
        if(camera1!=null)
       camera = camera1;
        if(camera==null)
            return;
        Camera.Parameters parameters = camera.getParameters();
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            parameters.set("orientation", "portrait");
            camera.setDisplayOrientation(90);
        }

        camera.setParameters(parameters);
        // mCamera.setPreviewDisplay(holder);

    }

    private void releaseCamera() {
        // stop and release camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    public void initialize() {



        video=(Button)findViewById(R.id.btnVideo);
        video.setOnClickListener(videoRecordListener);
        view=(Button)findViewById(R.id.btnView);
        view.setOnClickListener(viewListener);
        changeCamera = (Button)findViewById(R.id.btnCamera);
        changeCamera.setOnClickListener(switchCameraListener);
    }

    View.OnClickListener viewListener=new View.OnClickListener(){
        public void onClick(View v)
        {
            Intent intent=new Intent();
            intent.putExtra("email",e_mail);
            intent.setClass(MainActivity1.this,Grid_view.class);
          //  overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            MainActivity1.this.startActivity(intent);


        }
    };

    View.OnClickListener switchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!(recording||recordingVideo)) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    // release the old camera instance
                    // switch camera, from the front and the back and vice versa

                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    };

    View.OnClickListener videoRecordListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recordingVideo) {
                // stop recording and release camera
                try {
                    mediaRecorder.stop(); // stop the recording
                }catch(Exception e)
                {

                }
                releaseMediaRecorder();
                video.setText("Start");

                // release the MediaRecorder object
                Toast.makeText(MainActivity1.this, "Video captured!", Toast.LENGTH_LONG).show();
                recordingVideo = false;
            } else {
                if (!prepareMediaRecorder()) {
                    Toast.makeText(MainActivity1.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                    finish();
                }
                video.setText("Stop");
                // work on UiThread for better performance
                runOnUiThread(new Runnable() {
                    public void run() {


                        try {
                            mediaRecorder.start();
                        } catch (final Exception ex) {
                            // Log.i("---","Exception in thread");
                        }
                    }
                });

                recordingVideo = true;
            }
        }
    };


    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                changeCamera.setText("   Front   ");
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                camera = Camera.open(cameraId);
                cameraFront=false;
                // mPicture = getPictureCallback();
                refreshCamera();
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                cameraFront=true;
                changeCamera.setText("   Back   ");

                camera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                refreshCamera();
            }
        }
    }

    private int findFrontFacingCamera() {
        cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
      /*  if (camera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                video.setVisibility(View.GONE);
            }

           camera = Camera.open(findBackFacingCamera());
            refreshCamera();
        }
        */
    }

    private boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }
    ///// all stuff related to video recording...................................................................
    ////---------------------------------------------------------------------------------------------------------
    ////---------------------------------------------------------------------------------------------------------


    boolean recordingVideo = false;


    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            camera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        camera.unlock();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        CamcorderProfile profile = null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            mediaRecorder.setProfile(profile= CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
        else {
            // For >= Android 3.0 devices select 720p, 480p or low quality of video
            if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_720P))
                mediaRecorder.setProfile(profile= CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
            else if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_480P))
                mediaRecorder.setProfile(profile= CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
            else
                mediaRecorder.setProfile(profile= CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
        }
        boolean  flag=false;
        File newFile=null;
        if (profile.fileFormat == MediaRecorder.OutputFormat.THREE_GPP) {
            try {
                newFile = File.createTempFile("videocapture", ".3gp", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                mediaRecorder.setOutputFile(newFile.getAbsolutePath());
            } catch (IOException e) {
                // Log.v(LOGTAG,"Couldn't create file");
                e.printStackTrace();
                finish();
            }
        } else if (profile.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {
            try {
                newFile = File.createTempFile("videocapture", ".mp4", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                mediaRecorder.setOutputFile(newFile.getAbsolutePath());
            } catch (IOException e) {
                // Log.v(LOGTAG,"Couldn't create file");
                e.printStackTrace();
                finish();
            }
        } else {
            try {
                newFile = File.createTempFile("videocapture", ".mp4", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                mediaRecorder.setOutputFile(newFile.getAbsolutePath());
            } catch (IOException e) {
                //Log.v(LOGTAG,"Couldn't create file");
                e.printStackTrace();
                finish();
            }

        }

        //recorder.setMaxDuration(50000); // 50 seconds
        //recorder.setMaxFileSize(5000000); // Approximately 5 megabytes




        try {
            mediaRecorder.prepare();

        } catch (Exception e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }
    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
      //  super.onBackPressed();
        surfaceDestroyed(surfaceHolder);
        moveTaskToBack(true);

       // overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
    }


}
