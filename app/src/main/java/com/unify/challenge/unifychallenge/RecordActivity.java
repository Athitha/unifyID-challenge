package com.unify.challenge.unifychallenge;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.frosquivel.magicalcamera.MagicalCamera;
import com.stealthcopter.steganography.BitmapHelper;
import com.stealthcopter.steganography.Steg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RecordActivity extends AppCompatActivity {
    private static final String TAG = RecordActivity.class.getSimpleName();
    private int RESIZE_PHOTO_PIXELS_PERCENTAGE = 1000;
    private MagicalCamera magicalCamera;

    private Camera mCamera;
    private TextView mtTextView;
    private ImageView mImageView;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private int counter = 0;
    private Bitmap encodedBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mtTextView = (TextView) findViewById(R.id.hint);
        mImageView = (ImageView) findViewById(R.id.image);
        mPicture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(final byte[] data, Camera camera) {
                // TODO: this code is ok
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String encoded =  Base64.encodeToString(data, Base64.DEFAULT);
                        Bitmap bitmap = BitmapHelper.createTestBitmap(2000, 2000);
                        try {
                            encodedBitmap = Steg.withInput(bitmap).encode(encoded).intoBitmap();
                            saveImage(encodedBitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                mtTextView.setText(String.format(getString(R.string.photos_remaining), 10 - ++counter));

                // Displays the last image on the UI
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                mImageView.setImageBitmap(bmp);
            }
        };
        if(!checkCameraHardware()) {
            Snackbar.make(mImageView, R.string.no_camera, Snackbar.LENGTH_INDEFINITE).show();
            return;
        }

        initCamera();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final int MILLISECONDS = 500;
                try {
                    Thread.sleep(MILLISECONDS*5); // wait 2.5 seconds before start recording
                    mCamera.takePicture(null, null, mPicture);
                    Thread.sleep(MILLISECONDS);
                    mCamera.takePicture(null, null, mPicture);
                    Thread.sleep(MILLISECONDS);
                    mCamera.takePicture(null, null, mPicture);
                    Thread.sleep(MILLISECONDS);
                    mCamera.takePicture(null, null, mPicture);
                    Thread.sleep(MILLISECONDS);
                    mCamera.takePicture(null, null, mPicture);
                    Thread.sleep(MILLISECONDS);
                    mCamera.takePicture(null, null, mPicture);
                    Thread.sleep(MILLISECONDS);
                    mCamera.takePicture(null, null, mPicture);
                    Thread.sleep(MILLISECONDS);
                    mCamera.takePicture(null, null, mPicture);
                    Thread.sleep(MILLISECONDS);
                    mCamera.takePicture(null, null, mPicture);
                    Thread.sleep(MILLISECONDS);
                    mCamera.takePicture(null, null, mPicture);
                    Thread.sleep(MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void initCamera() {
        // Create an instance of Camera
        mCamera = getCameraInstance();

        mCamera.setDisplayOrientation(90);
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    /**
     *
     * Check if this device has a camera
     * */
    private boolean checkCameraHardware() {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            if(Camera.getNumberOfCameras() == 1) {
                return false;
            }
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     *
     * A safe way to get an instance of the Camera object.
     * */
    private Camera getCameraInstance() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open;", e);
                }
            }
        }

        return cam;
    }

    private void saveImage(Bitmap b) throws IOException {
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File file = new File(path, "thanks-for-using-unify" + counter + ".jpg");
        fOut = new FileOutputStream(file);

        Bitmap pictureBitmap = b;
        pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        fOut.flush();
        fOut.close();

        MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
    }
}
