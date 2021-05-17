package com.example.asciiart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;

public class ASCIIActivity extends AppCompatActivity {

    // ratio makes sense for monospace font (approx 2:1 character height to width)
    public static final double constHeightChange = 0.46;
    public static final char[] ASCIIchars = {'@','%','#','*','+','=','-',':','.',' '};

    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_s_c_i_i);

//        // get image byte array and image width
//        Intent intent = getIntent();
//        byte[] byteArray = intent.getByteArrayExtra(MainActivity.EXTRA_BYTES);
//        int imageWidth = intent.getIntExtra(MainActivity.EXTRA_INT, 100);
//
//        //convert byteArray to bitmap
//        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//
//        // maintain aspect ratio when scaling
//        int imageHeight = (int) (bmp.getHeight() * imageWidth * constHeightChange)/bmp.getWidth();
//
//        // Resize the image to a imageWidth x ? thumbnail (changing aspect ratio; change height by constHeightChange).
//        byte[] resizedImage = resizeHeightChange(bmp, imageWidth, imageHeight);

//        // create greyscale resized byte array
//        byte[] greyArray = grayscaleArray(resizedImage);
//
//        // create 2D char array containing ASCII image
//        char[][] asciiImg = imageToAscii(greyArray, imageWidth, imageHeight, ASCIIchars.length);
//        TextView output = (TextView)findViewById(R.id.textViewASCII);
//
//        for (int row = 0; row < imageHeight; row++) {
//            String str = "";
//            for (int col = 0; col < imageWidth; col++) {
//                str+=asciiImg[row][col];
//            }
//            str+="\n";
//            output.append(str);
//        }

    }

    byte[] resizeHeightChange(Bitmap bmp, int imageWidth, int imageHeight) {
        // scale bitmap
        Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, imageWidth, imageHeight, true);

        // bitmap into byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaledBmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

        return stream.toByteArray();
    }


    // convert byte array to greyscale
    byte[] grayscaleArray(byte[] byteArray) {
        byte[] greyByteArray = new byte [(int) (byteArray.length / 3) + 1];

        int count = 0;
        for (int i = 0; i < greyByteArray.length; i += 4) {
            int r  =  byteArray[i] & 0xff; // red
            int g2 = (byteArray[i + 1] & 0xff) << 1; // 2 * green
            int b  =  byteArray[i + 2] & 0xff; // blue

            // Calculate green-favouring average
            greyByteArray[count] = (byte) ((r + g2 + b) / 4);
            count++;
        }

        return greyByteArray;
    }

    // Converts pixel luminance to ASCII character
    // (range of luminance per character split evenly by number of shades)
    char luminToChar(byte lumin, int shades) {
        int shadeRange = (int) Math.ceil(255 / shades);
        int currentShade = shadeRange;
        int currentIndex = 0;

        while (currentShade <= 255) {
            if (lumin <= currentShade) {
                return ASCIIchars[currentIndex];
            }
            currentShade += shadeRange;
            currentIndex++;
        }
        return ' ';
    }

    // Converts image (greyscale array) to ASCII characters
    char[][] imageToAscii(byte[] greyArray, int imageWidth, int imageHeight, int shades) {
        char[][] asciiArray = new char [imageHeight][imageWidth];

        int count = 0;
        for (int row = 0; row < imageHeight; row++) {
            for (int col = 0; col < imageWidth; col++) {
                asciiArray[row][col] = luminToChar(greyArray[count], shades);
                count++;
            }
        }

        return asciiArray;
    }

}