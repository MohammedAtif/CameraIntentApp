package example.zemoso.com.cameraintentapp;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment {

    private Context mContext;
    private ImageView mPreview;
    private String mCurrentPhotoPath;

    private Bitmap mThumbnail;

    private final static boolean shouldDisplayThumbnail = false;

    private final String TAG = this.getClass().getSimpleName();
    private final static int REQUEST_IMAGE_CAPTURE = 1000;

    public CameraFragment() {
        // Required empty public constructor
    }

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    //region Lifecycle Methods


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPreview = (ImageView) view.findViewById(R.id.image_preview);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                /**
                 * <h3>Check whether calling activity exists or not</h3>
                 * <p>Helps to prevent the Activity not found exception</p>
                 */
                if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(mContext,
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "data received");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(shouldDisplayThumbnail) {
                Bundle extras = data.getExtras();
                mThumbnail = (Bitmap) extras.get("data");
                mPreview.setImageBitmap(mThumbnail);
            }else{
                Glide.with(mContext)
                        .load(mCurrentPhotoPath)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(mPreview);
            }
        }
    }

    //endregion

    //region Private Methods

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i(TAG, "Storage path is "+mCurrentPhotoPath);
        return image;
    }

    @Override
    public void onStop() {
        if(mThumbnail != null){
            mThumbnail.recycle();
        }
        super.onStop();
    }

    //endregion

}
