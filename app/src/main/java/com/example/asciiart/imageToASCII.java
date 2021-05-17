package com.example.asciiart;

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;

public class imageToASCII {

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
        char luminToChar(byte lumin, int shades, char[] ASCIIchars) {
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
        char[][] imageToAscii(byte[] greyArray, int imageWidth, int imageHeight, int shades, char[] ASCIIchars) {
            char[][] asciiArray = new char [imageHeight][imageWidth];

            int count = 0;
            for (int row = 0; row < imageHeight; row++) {
                for (int col = 0; col < imageWidth; col++) {
                    asciiArray[row][col] = luminToChar(greyArray[count], shades, ASCIIchars);
                    count++;
                }
            }

            return asciiArray;
        }

    }
