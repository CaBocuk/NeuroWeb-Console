package com.graphics;

import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Scope of some static methods which are designed to work with input images
 */
public class ImageService {

    // getting the color of particular pixel
    public static int[] getPixelRGB(BufferedImage image, int x, int y) {
        int color = image.getRGB(x, y);
        int[] rgb = new int[3];
        rgb[0] = (color & 0x00ff0000) >> 16;
        rgb[1] = (color & 0x0000ff00) >> 8;
        rgb[2] = color & 0x000000ff;

        return rgb;
    }

    // trying to investigate how black is the color
    public static double howBlackIsIt(int[] rgb) {
        double[] magicCoefficients = {0.2126, 0.7152, 0.722}; // I don't know why they are like this.
        // Link: http://stackoverflow.com/questions/9780632/how-do-i-determine-if-a-color-is-closer-to-white-or-black

        double sum = 0;

        for (int i = 0; i < 3; i++) {
            sum += magicCoefficients[i] * rgb[i];
        }

        // if sum is closer to 0 => it seems to be black. If closer to 255 => white.
        return 1 - sum / 255;
    }

    // resizing of image using Thumbnailator
    public static BufferedImage resize(BufferedImage image, int newWid, int newHei) throws IOException {
        return Thumbnails.of(image).size(newWid, newHei).asBufferedImage();
    }
}
