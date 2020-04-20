package com.francesco.voicephotos.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.francesco.voicephotos.R;
import com.francesco.voicephotos.models.Photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //professionally create beautifully organized albums
    // take your photo, tell the description and have

    private ImageView button_Capture, button_Gallery, button_Settings, button_multiple_shots;
    private ImageButton end_session;
    private TextureView textureView;
    private String photo_name;
    private String TAG = "MainActivity: ";
    String currentPhotoPath;
    Boolean directoryCreated;
    ProgressBar progress_loader;
    private SensorManager sensorManager;
    Sensor accelerometer;
    private int rotation, mod;
    private int orientation;

    //Check state orientation of output image
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static{
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_270,180);
    }

    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;


    //Save to FILE
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private static final String ACCELEROMETER_ROTATION = "1";

    private ArrayList<String> multiple_shots;

    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice=null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textureView = (TextureView)findViewById(R.id.textureView);
        //From Java 1.4 , you can use keyword 'assert' to check expression true or false
        assert textureView != null;
       // enableRotation(getApplicationContext(), true);
        textureView.setSurfaceTextureListener(textureListener);
        button_Capture = (ImageView) findViewById(R.id.btnCapture);
        end_session = findViewById(R.id.button_multiple_shot_end_session);
         multiple_shots = new ArrayList<String>();

        progress_loader = (ProgressBar) findViewById(R.id.progress_loader);
        button_Capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture(0);
            }
        });
        button_Gallery = (ImageView) findViewById(R.id.gallery);
        button_Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        button_Settings = (ImageView) findViewById(R.id.settings);
        button_multiple_shots = findViewById(R.id.btnCaptureMultiple);
        button_multiple_shots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_Capture.setVisibility(View.GONE);
                end_session.setVisibility(View.VISIBLE);

                takePicture(1);

                // scatta tutte le foto che vuoi


                end_session.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        button_Capture.setVisibility(View.VISIBLE);
                        end_session.setVisibility(View.GONE);
                        Intent intent = new Intent(MainActivity.this, SavePhoto.class);
                        intent.putExtra("PHOTO_MOD", "1");
                        intent.putStringArrayListExtra("LIST_PHOTO_NAMES", multiple_shots);

                    }
                });
            }
        });

        orientation = 0;


        directoryCreated = false;
        initializeSeonsors();
    }

    private void initializeSeonsors() {
        rotation =0;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void openGallery() {
        progress_loader.setVisibility(View.VISIBLE);
        Intent intent = new Intent(MainActivity.this, Gallery.class);
        startActivity(intent);
        progress_loader.setVisibility(View.GONE);

    }


    private void takePicture(final int mod) {
        if(cameraDevice == null)
            return;
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if(characteristics != null)
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        .getOutputSizes(ImageFormat.JPEG);

            //Capture image with custom size
            int width = 640;
            int height = 480;
            if(jpegSizes != null && jpegSizes.length > 0)
            {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            final ImageReader reader = ImageReader.newInstance(width,height, ImageFormat.JPEG,1);
            List<Surface> outputSurface = new ArrayList<>(2);
            outputSurface.add(reader.getSurface());
            outputSurface.add(new Surface(textureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            //Check orientation base on device
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotation));

            photo_name = createImageName();

            if (!directoryCreated){createFolder();}


            file = new File(Environment.getExternalStorageDirectory()+"/VoicePhotos/"+ photo_name +".jpg");

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        {

                            if (image != null)
                                image.close();
                        }

                    }

                }

                // qui l'immagine in byte viene salvata nel path specificato

                private void save(byte[] bytes) throws IOException {
                    OutputStream outputStream = null;
                    try{
                        outputStream = new FileOutputStream(file);



                        outputStream.write(bytes);
                    }finally {
                        if(outputStream != null)
                            outputStream.close();
                    }
                }
            };

            reader.setOnImageAvailableListener(readerListener,mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    //Toast.makeText(MainActivity.this, "Saved " + file, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onCaptureCompleted SAVED PHOTO : "  +file.toString());
                    //createCameraPreview();

                    // salva la foto,
                    // passa come intent l'URI della foto salvata
                    // carica la foto nella pagina successiva

                    if (mod ==0 ){
                        Intent intent = new Intent(MainActivity.this, SavePhoto.class);

                        intent.putExtra("PHOTO_MOD", "0");
                        intent.putExtra("PHOTO_PATH", file.toString());
                        intent.putExtra("PHOTO_NAME", photo_name.toString());
                        intent.putExtra("PHOTO_ORIENTATION", String.valueOf(orientation));

                        Log.d(TAG, "onCaptureCompleted: IMAGE_PATH PASSING: " + file);
                        Log.d(TAG, "onCaptureCompleted: intent:" +intent);
                        Toast.makeText(MainActivity.this, R.string.elab_ph, Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }else if (mod ==1){

                        Log.d(TAG, "onCaptureCompleted: mod = 1 ");
                        // passo una lista di stringhe con i nomi delle foto scattate in questa sessione


                        multiple_shots.add(photo_name);


                        takePicture(1);

                    }

                }
            };

            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try{
                        cameraCaptureSession.capture(captureBuilder.build(),captureListener,mBackgroundHandler);

                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            },mBackgroundHandler);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createFolder() {
        directoryCreated = true;
        Log.d(TAG, "createFolder: ");


        String folderPath = Environment.getExternalStorageDirectory() + "/VoicePhotos";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            File wallpaperDirectory = new File(folderPath);
            wallpaperDirectory.mkdirs();
            Log.d(TAG, "createFolder: creato folder");
        }


    }

    private String createImageName() {
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
        return timeStamp;
    }


    // fa in modo che la camera sia subito accesa
    private void createCameraPreview() {
        try{
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert  texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(),imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if(cameraDevice == null)
                        return;
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MainActivity.this, "Changed", Toast.LENGTH_SHORT).show();
                }
            },null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if(cameraDevice == null)
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
        try{
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),null,mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            //Check realtime permission if run higher API 23
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_SETTINGS

                },REQUEST_CAMERA_PERMISSION);

                return;
            }
            manager.openCamera(cameraId,stateCallback,null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       if(requestCode == REQUEST_CAMERA_PERMISSION)
       {
           if(grantResults[0] != PackageManager.PERMISSION_GRANTED)
           {
               Toast.makeText(this, "You can't use camera without permission", Toast.LENGTH_SHORT).show();
                finish();
           }
       }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if(textureView.isAvailable())
            openCamera();
        else
            textureView.setSurfaceTextureListener(textureListener);
    }

    @Override
    protected void onPause() {
        stopBackgroundThread();
        super.onPause();
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try{
            mBackgroundThread.join();
            mBackgroundThread= null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "X:" +event.values[0] + " Y:"  +event.values[1] + " Z:"+event.values[2]);
        //quando la X è maggiore di 6 o minore di -6 allora ruota le icone di 90, quando è minore di 6 allora torna a ruotare
        Long duration = 100L;
        Float rotationCW = -90f;
        Float rotationACW = 90f;
        if (event.values[0]>8){

            button_multiple_shots.animate().rotation(rotationACW).setDuration(duration).start();
            button_Capture.animate().rotation(rotationACW).setDuration(duration).start();
            button_Gallery.animate().rotation(rotationACW).setDuration(duration).start();
            button_Settings.animate().rotation(rotationACW).setDuration(duration).start();
            orientation = -90;

        }else if(event.values[0]<-8){

            button_multiple_shots.animate().rotation(rotationCW).setDuration(duration).start();
            button_Capture.animate().rotation(rotationCW).setDuration(duration).start();
            button_Gallery.animate().rotation(rotationCW).setDuration(duration).start();
            button_Settings.animate().rotation(rotationCW).setDuration(duration).start();
            orientation = 90;


        }else{
            button_multiple_shots.animate().rotation(0).setDuration(duration).start();
            button_Capture.animate().rotation(0).setDuration(duration).start();
            button_Gallery.animate().rotation(0).setDuration(duration).start();
            button_Settings.animate().rotation(0).setDuration(duration).start();
            orientation = 0;

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }






}
