package com.example.jesperenemark.friendsapp.Controller;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import com.example.jesperenemark.friendsapp.BE.Friend;
import com.example.jesperenemark.friendsapp.DAO.AppProvider;
import com.example.jesperenemark.friendsapp.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class DetailsActivity extends AppCompatActivity {

    // Widgets
    Button buttonAdd;
    Button backbtn;
    Button openPhone;
    Button openMail;
    Button btngetLocation;


    String homeLocation;
    Button buttonInsert;
    Button buttonDelete;
    EditText firstNameText,lastNameText, addressText, mailText, birthDateText, phoneText;
    ImageView imageV;


    // Database provider
    AppProvider appProvider;

   // Camera01
   private final static String LOGTAG = "Camera01";
   private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
   private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 200;
   private static final int MY_PERMISSIONS_REQUEST__ACCESS_FINE_LOCATION = 200;
    File mFile;
    ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        setTitle("PersonDetails");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        appProvider = new AppProvider(this);
        mImage = (ImageView) findViewById(R.id.imageView);
        buttonAdd = (Button) findViewById(R.id.btnSave);
        buttonInsert = (Button) findViewById(R.id.btnSave);
        buttonDelete = (Button) findViewById(R.id.btnDelete);
        openPhone = (Button) findViewById(R.id.btnOpenPhone);
        openMail = (Button) findViewById(R.id.btnOpenMail);
        btngetLocation = (Button) findViewById(R.id.btnSetHome);
        init();
        //displayInfo();
        delete();
        OpenPhoneView();
        OpenMailView();
        SetHome();
        backbtn = (Button) findViewById(R.id.btnBack);
        clickBack();

        findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DetailsActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
                else {
                    onClickTakePics();
                }

            }
        });

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DetailsActivity.this.onClickAdd();
            }
        });

    }

    private void SetHome() {
        btngetLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);
                if (ContextCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DetailsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST__ACCESS_FINE_LOCATION);
                }
                else {
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    String latitude = String.valueOf(location.getLatitude());
                    String longtitude = String.valueOf(location.getLongitude());
                    homeLocation = latitude + ", " + longtitude;
                    Log.d("DetailActivity", "Friend's location: " + homeLocation);
                }
            }
        });
    }

    private void onClickTakePics()
    {
        mFile = getOutputMediaFile();
        if(mFile == null) {
            Toast.makeText(this, "Could not create file...", Toast.LENGTH_LONG).show();
            return;
        }
        // Create intent to take a picture
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
        Log.d("File is not null: ", "file uri = " + Uri.fromFile(mFile).toString());
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private File getOutputMediaFile(){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Camera01");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("mediaStorage: ", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String postfix = "jpg";
        String prefix = "IMG";

        File mediaFile = new File(mediaStorageDir.getPath() +
                File.separator + prefix +
                "_"+ timeStamp + "." + postfix);

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                showPictureTaken(mFile);

            } else
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Canceled...", Toast.LENGTH_LONG).show();
                return;

            } else
                Toast.makeText(this, "Picture NOT taken - unknown error...", Toast.LENGTH_LONG).show();
        }
    }

    private void showPictureTaken(File f) {
        mImage.setImageURI(Uri.fromFile(f));
        mImage.setBackgroundColor(Color.BLACK);
        mImage.setRotation(0);
        scaleImage();
    }
    private void scaleImage()
    {
        final Display display = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        final float screenWidth = p.x/2;
        final float screenHeight = p.y/2; //m_takeBtn.getHeight());
        mImage.setMaxHeight((int)screenHeight);
        mImage.setMaxWidth((int)screenWidth);
    }

    public void init() {
        firstNameText = (EditText) findViewById(R.id.person_firstName);
        lastNameText = (EditText) findViewById(R.id.person_lastName);
        addressText = (EditText) findViewById(R.id.person_address);
        mailText = (EditText) findViewById(R.id.person_email);
        birthDateText = (EditText) findViewById(R.id.person_web);
        phoneText = (EditText) findViewById(R.id.person_phone);
    }

    public void onClickAdd() {
        String firstname = firstNameText.getText().toString();
        String lastname = lastNameText.getText().toString();
        String address = addressText.getText().toString();
        String mail = mailText.getText().toString();
        String birthdate = birthDateText.getText().toString();
        String phone = phoneText.getText().toString();

        appProvider.addPerson(new Friend(0,firstname, lastname, address, mail, phone, Uri.fromFile(mFile).toString(),homeLocation));
        firstNameText.setText("");
        lastNameText.setText("");
        addressText.setText("");
        mailText.setText("");
        phoneText.setText("");
        Toast.makeText(this, "FirstName: " + firstname + "LastName: " + lastname + "Address: " + address + "Mail: " + mail +
                "Phone: "  + phone + "PhotoDirectory: " + Uri.fromFile(mFile).toString(), Toast.LENGTH_LONG).show();
    }

    public void OpenPhoneView() {
        openPhone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL, null);
                startActivity(i);
            }
        });
    }
    public void OpenMailView() {
        openMail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                startActivity(intent);
            }
            });
    }

    public void clickBack() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void delete() {
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                int index = getIntent().getExtras().getInt("index");

                Friend current = appProvider.getAll().get(index);

                appProvider.deleteById(current.Id);
                Toast.makeText(DetailsActivity.this, "Friend deleted" + current, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

        public void displayInfo() {
        int index = getIntent().getExtras().getInt("index");

          Friend current = appProvider.getAll().get(index);
          EditText txtName = (EditText) findViewById(R.id.person_firstName);
          txtName.setText(current.FirstName);
     }


}






