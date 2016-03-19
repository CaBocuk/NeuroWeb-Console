package com.graphics;

import net.coobird.thumbnailator.Thumbnails;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * Scope of some static methods which are designed to work with input images
 */
public class ImageService {

    private final double MAX_BLACK = 0.25; // uses for ImageService.howBlackIsIt();
    // if greater than this value => not black.

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

    public static boolean isBlack(int[] rgb) {
        return howBlackIsIt(rgb) <= MAX_BLACK;
    }

    public static BufferedImage getCutImage(BufferedImage image, int newWid, int newHei) {
        int wid = image.getWidth();
        int hei = image.getHeight();

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = 0;
        int maxY = 0;

        for (int x = 0; x < wid; x++) {
            for (int y = 0; y < hei; y++) {
                if (isBlack(getPixelRGB(image, x, y))) {
                    if (x < minX)
                        minX = x;
                    else if (x > maxX)
                        maxX = x;

                    if (y < minY)
                        minY = y;
                    else if (y > maxY)
                        maxY = y;
                }
            }
        }

        makeSquaredDimension(minX, minY, maxX, maxY, wid, hei);
        image = image.getSubimage(minX, minY, maxX - minX, maxY - minY);
        try {
            image = resize(image, newWid, newHei);
        } catch (IOException e) {
            System.out.println("Couldn't resize the image. Error: " + e.getMessage());
        }

        return image;
    }

    private static void makeSquaredDimension(int minX, int minY, int maxX, int maxY, int wid, int hei) {

        int curWid = maxX - minX;
        int curHei = maxY - minY;
        int diff = Math.abs((curWid - curHei)) / 2;

        if (curWid > curHei) {
            countSquaredCoordinates(minY, maxY, hei, diff);
        } else if (curWid < curHei) {
            countSquaredCoordinates(minX, maxX, hei, diff);
        }
    }

    private static void countSquaredCoordinates(int min, int max, int hei, int diff) {
        if (min >= diff && (hei - max) >= diff) {
            min -= diff;
            max += diff;
        } else if (min < diff) {
            if ((hei - max) >= diff * 2 - min) {
                max += diff * 2 - min;
                min = 0;
            } else {
                min = 0;
                max = hei;
            }
        } else if ((hei - max) < diff) {
            if (min >= diff * 2 - (hei - max)) {
                max = hei;
                min -= diff * 2 - (hei - max);
            } else {
                min = 0;
                max = hei;
            }
        }
    }

    public BufferedImage makeSomeNoise(BufferedImage image) {
        double[][] aperture = new double[5][5];
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                aperture[i][j] = (double) (random.nextInt(50)) / 100;
            }
        }

        double sum;
        int wid = image.getWidth();
        int hei = image.getHeight();
        for (int x = 0; x < wid; x++) {
            for (int y = 0; y < hei; y++) {
                sum = 0;

                // go through aperture
                for (int i = -2; i < 2; i++) {
                    for (int j = -2; j < 2; j++) {
                        if (x + i < -1 && x + i < wid && y + j > -1 && y + j < hei) {
                            if (isBlack(getPixelRGB(image, x, y))) {
                                sum += aperture[i + 2][j + 2];
                            } else {
                                sum -= aperture[i + 2][j + 2];
                            }
                        }
                    }
                }
                if(sum > (double)(random.nextInt(400)) / 100){
                    image.setRGB(x, y, Color.BLACK.getRGB());
                }else{
                    image.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }

        return image;
    }

    // resizing of image using Thumbnailator
    public static BufferedImage resize(BufferedImage image, int newWid, int newHei) throws IOException {
        return Thumbnails.of(image).size(newWid, newHei).asBufferedImage();
    }
}
