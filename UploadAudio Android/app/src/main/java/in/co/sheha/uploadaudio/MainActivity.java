package in.co.sheha.uploadaudio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.deskode.recorddialog.RecordDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Bitmap;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_AUDIO = 2;
    String selectedPath = "";
    Button btnUpload;
    Uri selectedImageUri;
    TextView bn,bd;
    ImageView iv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // checks and ask for permissions for app to function
        initPermissions();




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bn= (TextView)findViewById(R.id.birdName);
        iv= (ImageView)findViewById(R.id.birdImage);
        bd=(TextView)findViewById(R.id.birdDescription);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        btnUpload = (Button)findViewById(R.id.btnUpload);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openGalleryAudio();
            }
        });

        findViewById(R.id.recordNow).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                RecordDialog audio = RecordDialog.newInstance("bird_sound_recorder");

                audio.setTitle("Record Bird Audio");

                audio.setMessage("Press to start recording..");

                audio.show(MainActivity.this.getFragmentManager(), "MAIN_ACT");

                audio.setPositiveButton("Save", new RecordDialog.ClickListener()
                {
                    @Override
                    public void OnClickListener(String s) {

                        Toast.makeText(getApplicationContext(), "Successfully saved !", Toast.LENGTH_LONG).show();

                    }
                });

            }
        });

    }

    public void openGalleryAudio(){
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Audio "), SELECT_AUDIO);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_AUDIO)
            {
                Uri selectedFileUri = data.getData();
                selectedPath = FilePath.getPath(this,selectedFileUri);
                System.out.println("SELECT_AUDIO");
                System.out.println("SELECT_AUDIO Path : " + selectedPath);

                try {
                    uploadVideo(selectedPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
       if (cursor != null) {
           cursor.moveToFirst();
           return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
       }
       return "";
    }



    @SuppressLint("SetTextI18n")
    private void uploadVideo(String yourfilepath) throws ParseException, IOException {
        ProgressDialog Reloading =new ProgressDialog(MainActivity.this);
        Reloading.setMessage("\tLoading ..");
        Reloading.setCancelable(false);
        Reloading.show();
//progresbar start


        HttpClient httpclient = new DefaultHttpClient();

        //post request to send the video
        HttpPost httppost = new HttpPost("http://192.168.8.100:8888/uploadFile");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy( policy);
        FileBody video_file1 = new FileBody(new File(yourfilepath));
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("file", video_file1);
        httppost.setEntity(reqEntity);


        // DEBUG
        System.out.println( "executing request " + httppost.getRequestLine( ) );
        HttpResponse response = httpclient.execute( httppost );
        HttpEntity resEntity = response.getEntity( );


        // DEBUG
        System.out.println( response.getStatusLine( ) );

        if (resEntity != null) {
            //end progress bar
            Reloading.dismiss();
            try {
                JSONObject bird=new JSONObject(EntityUtils.toString( resEntity ));
                String bname=bird.getString("fileName");
                String burl=bird.getString("birdPicurl");
                String bdetails=bird.getString("birdDetails");

               if (bird.getString("score").equals("1")) {
                    URL url = new URL(burl);
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    iv.setImageBitmap(bmp);

                    bn.setText(bname);
                    bd.setText(bdetails);
                   //((TextView)findViewById(R.id.birdName)).setText("dfsds");
//                System.out.println(bname);
//                System.out.println(bdetails);
//                System.out.println(burl);
               }

            } catch (JSONException e) {
                e.printStackTrace();
            }

//            JSONObject bird= null;
//            try {
//                bird = new JSONObject(EntityUtils.toString( resEntity ));
//                String bname=bird.getString("fileName");
//                String bdetails=bird.getString("birdDetails");
//                String burl=bird.getString("birdPicurl");
//                bd.setText(bname);
//                Picasso.get().load(burl.trim()).into(iv);
//                bd.setText(bdetails);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }


        }


        // end if
        if (resEntity != null) {
            resEntity.consumeContent( );
        } // end if

        httpclient.getConnectionManager( ).shutdown( );
    }

    //permission for access
    private void initPermissions() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener()
        {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                if (report.areAllPermissionsGranted())
                {
                    Toast.makeText(getApplicationContext(), "All Permissions Granted, Thanks", Toast.LENGTH_LONG)
                            .show();

                    return;
                }

                for (PermissionDeniedResponse deniedResponse : report.getDeniedPermissionResponses())
                {

                    return;
                }

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();

    }

}
