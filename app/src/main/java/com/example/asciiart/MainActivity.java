package com.example.asciiart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    // ratio makes sense for monospace font (approx 2:1 character height to width)
    public static final double constHeightChange = 0.46;
    public static final char[] ASCIIchars = {'@','%','#','*','+','=','-',':','.',' '};

    // One Button
    Button BSelectImage;

    // One Preview Image
    ImageView IVPreviewImage;

    // constant to compare the activity result code
    int SELECT_PICTURE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // views
        final ConstraintLayout layout1 = (ConstraintLayout) findViewById(R.id.view1);
        final ConstraintLayout layout2 = (ConstraintLayout) findViewById(R.id.view2);

        // hide second view
        layout2.setVisibility(View.GONE);

        // register the UI widgets with their appropriate IDs
        BSelectImage = findViewById(R.id.BSelectImage);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);

        // handle the Choose Image button to trigger
        // the image chooser function
        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        Button BtoASCII = (Button) findViewById(R.id.BASCII);

        BtoASCII.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide first view
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                toASCII();
            }

        });
    }

    // this function is triggered when the Select Image Button is clicked
    void imageChooser() {

        // create an instance of the intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // this function is triggered when user selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    IVPreviewImage.setImageURI(selectedImageUri);
                }
            }
        }
    }

    public void toASCII() {
        // get functions from class
//        imageToASCII imgToASCII = new imageToASCII();

        // get user input ASCII width
        EditText editTextWidth = (EditText) findViewById(R.id.editTextNumber);
        int imageWidth = Integer.parseInt(editTextWidth.getText().toString());

        // uploaded image into bitmap
        IVPreviewImage = findViewById(R.id.IVPreviewImage);
        IVPreviewImage.buildDrawingCache(true);
        Bitmap bitmap = IVPreviewImage.getDrawingCache(true);
        BitmapDrawable drawable = (BitmapDrawable)IVPreviewImage.getDrawable();
        bitmap = drawable.getBitmap();

        // maintain aspect ratio when scaling
        int imageHeight = (int) (bitmap.getHeight() * imageWidth * constHeightChange)/bitmap.getWidth();

        // Resize the image to a imageWidth x ? thumbnail (changing aspect ratio; change height by constHeightChange).
        byte[] resizedImage = resizeHeightChange(bitmap, imageWidth, imageHeight);
//
        // create greyscale resized byte array
        byte[] greyArray = grayscaleArray(resizedImage);

        // create 2D char array containing ASCII image
        char[][] asciiImg = imageToAscii(greyArray, imageWidth, imageHeight, ASCIIchars.length, ASCIIchars);
        TextView output = (TextView)findViewById(R.id.textViewASCII);

        output.append(greyArray.length + "\n" + imageWidth + "\n" + imageHeight);

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
//    char luminToChar(byte lumin, int shades, char[] ASCIIchars) {
//        int shadeRange = (int) Math.ceil(255 / shades);
//        int currentShade = shadeRange;
//        int currentIndex = 0;
//
//        while (currentShade <= 255) {
//            if (lumin <= currentShade) {
//                return ASCIIchars[currentIndex];
//            }
//            currentShade += shadeRange;
//            currentIndex++;
//        }
//        return ' ';
//    }

    // Converts image (greyscale array) to ASCII characters
    char[][] imageToAscii(byte[] greyArray, int imageWidth, int imageHeight, int shades, char[] ASCIIchars) {
        char[][] asciiArray = new char [imageHeight][imageWidth];

        int count = 0;
        for (int row = 0; row < imageHeight; row++) {
            for (int col = 0; col < imageWidth; col++) {

                char greyChar = ' ';
                int shadeRange = (int) Math.ceil(255 / shades);
                int currentShade = shadeRange, currentIndex = 0;

                while (currentShade <= 255) {
//                    if (greyArray[count] <= currentShade) {
//                        greyChar = ASCIIchars[currentIndex];
//                    }
                    currentShade += shadeRange;
                    currentIndex++;
                }

                asciiArray[row][col] = greyChar;
                // asciiArray[row][col] = luminToChar(greyArray[count], shades, ASCIIchars);
                count++;
            }
        }

        return asciiArray;
    }

}