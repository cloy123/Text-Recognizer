package com.example.textrecognizer;

import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.util.concurrent.Callable;

public class RecognizeText {

    private String DATA_PATH;
    private  String lang;
    private static final String TAG = MainActivity.class.getSimpleName();
    private  TessBaseAPI tessBaseAPI;

    public String extractText(Bitmap bitmap) {
        try
        {
            tessBaseAPI = new TessBaseAPI();

        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            if(tessBaseAPI == null)
            {
                Log.e(TAG, "TessBaseAPI is null. TessFactory not returning tess object.");
            }
        }

        tessBaseAPI.init(DATA_PATH, lang);

        //       //EXTRA SETTINGS
//        //For example if we only want to detect numbers
//        tessBaseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890");
//
//        //blackList Example
//        tessBaseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-qwertyuiop[]}{POIU" +
//                "YTRWQasdASDfghFGHjklJKLl;L:'\"\\|~`xcvXCVbnmBNM,./<>?");

        Log.d(TAG, "Training file loaded");
        tessBaseAPI.setImage(bitmap);
        String extractedText = "empty result";
        try {
            extractedText = tessBaseAPI.getUTF8Text();
        } catch (Exception e) {
            Log.e(TAG, "Error in recognizing text.");
        }
        tessBaseAPI.end();
        return extractedText;

    }


    public RecognizeText(String dataPath, String langForRec)
    {
        DATA_PATH = dataPath;
        lang = langForRec;
    }
}


