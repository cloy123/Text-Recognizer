package com.example.textrecognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.provider.MediaStore;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.drawable.BitmapDrawable;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.Locale;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import android.os.Bundle;


public class MainActivity extends AppCompatActivity {




    private ImageView imageView;
    private Button cameraButton;
    private CheckBox checkRus;
    private CheckBox checkEng;
    private Button recognizeButton;
    private Button copyButton;
    private EditText text;

    private final int Pick_Image = 1;

    private Bitmap currentImage;

    private String langForRec = "eng";

    private String DATA_PATH; //getExternalFilesDir(null) + "/TesseractSample/";
    private static final String TESSDATA = "tessdata";
    private String LangForRec = "eng";

    private final int REQUEST_PERMISSION_MANAGE_STORAGE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setOnClickListener(OpenGalleryClick);

        cameraButton = (Button)findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(OpenCameraClick);

        checkRus = (CheckBox)findViewById(R.id.checkRus);
        checkRus.setOnClickListener(CheckRusClick);
        checkRus.setChecked(false);

        checkEng = (CheckBox)findViewById(R.id.checkEng);
        checkEng.setOnClickListener(CheckEngClick);
        checkEng.setChecked(true);

        recognizeButton = (Button)findViewById(R.id.recognizeButton);
        recognizeButton.setOnClickListener(RecognizeText);

        copyButton = (Button)findViewById(R.id.copyButton);
        copyButton.setOnClickListener(CopyText);

        text = (EditText)findViewById(R.id.text);

        requestPermissions();

        DATA_PATH = getExternalFilesDir(null) + "/TesseractSample/";
        CreateProgramFiles();



    }

    private void CreateProgramFiles()
    {
        try {
            String IMGS_PATH = DATA_PATH + "imgs";
            String TESS_PATH = DATA_PATH + TESSDATA;
            File dir = new File(IMGS_PATH);
            dir.mkdirs();
            dir = new File(TESS_PATH);
            dir.mkdirs();
        }
        catch (Exception e)
        {
            Log.e("", e.getMessage());
        }

        try {
            String fileList[] = getAssets().list(TESSDATA);

            for(String fileName: fileList)
            {
                String pathToDateFile = DATA_PATH + TESSDATA + "/" + fileName;
                if (!(new File(pathToDateFile).exists()))
                {
                    InputStream in = getAssets().open(TESSDATA + "/" + fileName);
                    OutputStream out = new FileOutputStream(pathToDateFile);
                    byte[] buf = new byte[1024];
                    int len = in.read(buf);
                    while (len > 0)
                    {
                        out.write(buf, 0, len);
                        len = in.read(buf);
                    }
                    in.close();
                    out.close();
                }
            }
        }
        catch (Exception e)
        {
            Log.e("", "Unable to copy files to tessdata " + e.toString());
        }
    }



    private void startCameraActivity() {
       // try {


        //CreateProgramFiles();
           // String img_path = IMGS_PATH + "/ocr.jpg";

           // outputFileUri = Uri.fromFile(new File(img_path));

          // final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

           // takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            //    startActivityForResult(takePictureIntent, PHOTO_REQUEST_CODE);
            //}


       // } catch (Exception e) {
      //      Log.e("", e.getMessage());
     //   }
    }





    private void requestPermissions()
    {
        // проверка наличия разрешения
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_MANAGE_STORAGE);
        }
    }



    // вызывается после ответа пользователя на запрос разрешения
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_MANAGE_STORAGE: {
                // если пользователь закрыл запрос на разрешение, не дав ответа, массив grantResults будет пустым
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // разрешение было предоставлено
                    // выполните здесь необходимые операции для включения функциональности приложения, связанной с запрашиваемым разрешением
                } else {
                    // разрешение не было предоставлено
                    // выполните здесь необходимые операции для выключения функциональности приложения, связанной с запрашиваемым разрешением
                }
                return;
            }
        }
    }

    private View.OnClickListener OpenGalleryClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK:
            android.content.Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            //Тип получаемых объектов - image:
            photoPickerIntent.setType("image/*");
            //Запускаем переход с ожиданием обратного результата в виде информации об изображении:
            startActivityForResult(photoPickerIntent, Pick_Image);
        }
    };


    private View.OnClickListener OpenCameraClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startCameraActivity();
        }
    };

    private View.OnClickListener CheckRusClick = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            langForRec = "rus";
            checkEng.setChecked(false);
            checkRus.setChecked(true);
        }
    };

    private View.OnClickListener CheckEngClick = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            langForRec = "eng";
            checkEng.setChecked(true);
            checkRus.setChecked(false);
        }
    };

    private View.OnClickListener RecognizeText = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(currentImage != null) {
                try {
                    RecognizeText recognizeText = new RecognizeText(DATA_PATH, langForRec);
                    String res = recognizeText.extractText(currentImage);
                    text.setText(res);
                }
                catch (Exception e)
                {
                    text.setText(e.getMessage());
                }

            }
            else {

                Toast.makeText(getApplicationContext(), "Отсутсвует изображение", Toast.LENGTH_LONG).show();
            }

        }
    };


    private View.OnClickListener CopyText = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", text.getText());
            clipboard.setPrimaryClip(clip);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case Pick_Image:
                if(resultCode == RESULT_OK)
                {
                  try
                  {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        currentImage = BitmapFactory.decodeStream(imageStream);
                        imageView.setImageBitmap(currentImage);

                  }
                  catch(Exception e)
                  {
                      Toast.makeText(this, "Ошибка", Toast.LENGTH_LONG).show();
                  }
                }
            break;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && currentImage != null) {
            imageView.setImageBitmap(currentImage);
        }
    }




}