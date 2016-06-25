package thackbarth.com.securecameraintent;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


// code from: https://developer.android.com/training/camera/photobasics.html


public class MainActivity extends AppCompatActivity
{

    ImageView mImageView;
    File destination;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mImageView = (ImageView) findViewById(R.id.imageview);

        destination = new File(Environment
                .getExternalStorageDirectory(), "image.jpg");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                dispatchTakePictureIntent();
//                takeNonPrivateImage();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    String mCurrentPhotoPath;
    private static final String TAG = "MainActivity";
    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        //  /storage/emulated/0/Android/data/thackbarth.com.securecameraintent/files/Pictures/

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if ( !storageDir.exists() )
        {
            if (!storageDir.mkdir())
                Log.e(TAG, "createImageFile: direct create failed" );
        }
        else
        {
            Log.i(TAG, "createImageFile: directory exists");
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );


        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;


    }

    private static final int REQUEST_IMAGE = 439;
    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            // Create the File where the photo should go
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            } catch (IOException ex)
            {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "thackbarth.com.securecameraintent.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO)
        {
            setPic();
        }

        if (requestCode == REQUEST_IMAGE
                && resultCode == Activity.RESULT_OK)
        {
            try
            {
                FileInputStream in =
                        new FileInputStream(destination);
                BitmapFactory.Options options =
                        new BitmapFactory.Options();
                options.inSampleSize = 10; //Downsample by 10x
                Bitmap userImage = BitmapFactory
                        .decodeStream(in, null, options);
                mImageView.setImageBitmap(userImage);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void setPic()
    {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    public void takeNonPrivateImage()
    {
        try
        {
            Intent intent =
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //Add extra to save full-image somewhere
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(destination));
            startActivityForResult(intent, REQUEST_IMAGE);
        } catch (ActivityNotFoundException e)
        {
            //Handle if no application exists
        }
    }
}
