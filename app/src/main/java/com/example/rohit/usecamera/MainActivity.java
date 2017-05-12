package com.example.rohit.usecamera;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private String contentPath;
    private ImageView capturedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
        Button btnCamera = (Button) findViewById(R.id.btnCamera);

        capturedImage= (ImageView) findViewById(R.id.capturedImage);

        btnCamera.setTypeface(font);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                openCamera();
            }
        });
    }

    private File createFile() throws Exception{      //to save captured photo in a file
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String name="Image_"+timeStamp;
        File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),name+".jpg");
        contentPath ="file:"+file.getAbsolutePath();
        return file;
    }
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null){
            File photo=null;
            try {
               photo= createFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (photo!=null){
                Uri imageUri=Uri.fromFile(photo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            }
            startActivityForResult(intent, 1);
        }

    }
    private void galleryAddPic() {             // to add picture to gallery (of full quality)
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(contentPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap)data.getExtras().get("data");          //to get back thumbnail of the captured image
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                bp.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream); //compress image and put it in bytearrayoutputstream
                byte[] bytes=byteArrayOutputStream.toByteArray();                 //converting bytearrayoutputstream into bytes
                Bitmap bmp= BitmapFactory.decodeByteArray(bytes,0,bytes.length);   //NOw converting bytes again into bitmap
                capturedImage.setImageBitmap(bmp);
               galleryAddPic();
            }
        }
    }


}
