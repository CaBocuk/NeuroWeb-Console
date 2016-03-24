package com.graphics;

import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Scope of some static methods which are designed to work with input images
 */
public class ImageService {

    private static final double MAX_BLACK = 0.1; // uses for ImageService.howBlackIsIt();
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
        double[] magicCoefficients = {0.2126, 0.7152, 0.0722}; // I don't know why they are like this.
        // Link: http://stackoverflow.com/questions/9780632/how-do-i-determine-if-a-color-is-closer-to-white-or-black

        double sum = 0;

        for (int i = 0; i < 3; i++) {
            sum += magicCoefficients[i] * rgb[i];
        }

        // if sum is closer to 0 => it seems to be black. If closer to 255 => white.
        return sum / 255;
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

        /*int ret[] = makeSquaredDimension(minX, minY, maxX, maxY, wid, hei);
        minX = ret[0];
        minY = ret[1];
        maxX = ret[2];
        maxY = ret[3];*/

        int curWid = maxX - minX;
        int curHei = maxY - minY;
        image = image.getSubimage(minX, minY, curWid, curHei);

        final GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        int diff = Math.abs((curWid - curHei)) / 2;
        if (curHei > curWid) {
            BufferedImage newImage = config.createCompatibleImage(curHei, curHei);
            Graphics2D g = newImage.createGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, diff, curHei);
            g.drawImage(image, diff + 1, 0, null);
            g.fillRect(diff + curWid + 1, 0, curHei, curHei);
            image = newImage;
        } else {
            BufferedImage newImage = config.createCompatibleImage(curWid, curWid);
            Graphics2D g = newImage.createGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, curWid, diff);
            g.drawImage(image, 0, diff + 1, null);
            g.fillRect(0, diff + curHei + 1, curWid, curWid);
            image = newImage;
        }

        try {
            image = resize(image, newWid, newHei);
        } catch (IOException e) {
            System.out.println("Couldn't resize the image. Error: " + e.getMessage());
        }

        return image;
    }

    private static int[] makeSquaredDimension(int minX, int minY, int maxX, int maxY, int wid, int hei) {
        int ret[] = new int[4];
        int curWid = maxX - minX;
        int curHei = maxY - minY;
        int diff = Math.abs((curWid - curHei)) / 2;

        if (curWid > curHei) {
            int a[] = countSquaredCoordinates(minY, maxY, hei, diff);
            ret[0] = minX;
            ret[1] = a[0];
            ret[2] = maxY;
            ret[3] = a[1];
            if (Math.abs((curWid - curHei)) % 2 == 1) {
                if (ret[1] > 0)
                    ret[1]--;
                else
                    ret[3]++;
            }

        } else if (curWid < curHei) {
            int a[] = countSquaredCoordinates(minX, maxX, hei, diff);
            ret[0] = a[0];
            ret[1] = minY;
            ret[2] = a[1];
            ret[3] = maxY;
            if (Math.abs((curWid - curHei)) % 2 == 1) {
                if (ret[1] > 0)
                    ret[0]--;
                else
                    ret[2]++;
            }
        }
        return ret;
    }

    private static int[] countSquaredCoordinates(int min, int max, int hei, int diff) {
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
                min -= diff * 2 - (hei - max);
                max = hei;
            } else {
                min = 0;
                max = hei;
            }
        }
        int ret[] = {min, max};
        return ret;
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
                if (sum > (double) (random.nextInt(400)) / 100) {
                    image.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }

        return image;
    }

    public static BufferedImage removeNoise(BufferedImage image) {
        //TODO
        return null;
    }

    /**
     * TODO REFACTORING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     *
     * @param image
     * @param newWid
     * @param newHei
     */
    public static BufferedImage[] getMappedImage(BufferedImage image, int newWid, int newHei) {
        BufferedImage[] ret = new BufferedImage[5];
        image = cutTheFrame(image);
        ItemType sectionTypes[] = {ItemType.IGNORE, ItemType.IGNORE, ItemType.NUMBER, ItemType.NUMBER, ItemType.NUMBER, ItemType.NUMBER, ItemType.LETTER, ItemType.LETTER, ItemType.IGNORE, ItemType.NUMBER};

        int sections = 0;
        int numbers = 0;
        boolean enteredSection = false;
        int curSectionMinX = 0;
        for (int x = 0; x < image.getWidth() && sections < 10; x++) {
            int blacks = 0;
            for (int y = 0; y < image.getHeight(); y++) {
                if (isBlack(getPixelRGB(image, x, y))) {
                    blacks++;
                }
            }
            if ((double) (blacks) / image.getHeight() > 0.05 && !enteredSection) {
                enteredSection = true;
                curSectionMinX = x;
            }
            if ((double) (blacks) / image.getHeight() <= 0.05 && enteredSection) {
                enteredSection = false;
                if (sectionTypes[sections].equals(ItemType.NUMBER)){
                    BufferedImage subImg = image.getSubimage(curSectionMinX, 0, x - curSectionMinX, image.getHeight());
                    subImg = getCutImage(subImg, newWid, newHei);
                    ret[numbers] = subImg;
                    try {
                        ImageIO.write(subImg, "PNG", new File(numbers+".png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    numbers++;
                }
                sections++;
            }
        }

        return ret;
    }

    private static BufferedImage cutTheFrame(BufferedImage image) {
        int wid = image.getWidth();
        int hei = image.getHeight();

        int minX = 0;
        int minY = 0;
        int maxX = wid;
        int maxY = hei;

        boolean foundFrame = false;

        // look for left side of frame
        for (int x = 0; x < wid / 10; x++) {
            if (isBlack(getPixelRGB(image, x, hei / 2))) {
                foundFrame = true;
            } else if (!isBlack(getPixelRGB(image, x, hei / 2)) && foundFrame) {
                minX = x + 5;
                break;
            }
        }

        foundFrame = false;
        // look for upper of the frame
        for (int y = hei / 2; y > 0; y--) {
            if (isBlack(getPixelRGB(image, minX, y))) {
                minY = y + 5;
                break;
            }
        }

        for (int x = wid / 2; x < wid; x++) {
            if (isBlack(getPixelRGB(image, x, minY))) {
                maxX = x - 5;
                break;
            }
        }

        for (int y = hei / 2; y < hei; y++) {
            if (isBlack(getPixelRGB(image, maxX, y))) {
                maxY = y - 5;

            }
        }

        return image.getSubimage(minX, minY, maxX - minX, maxY - minY);
    }

    // resizing of image using Thumbnailator
    public static BufferedImage resize(BufferedImage image, int newWid, int newHei) throws IOException {
        return Thumbnails.of(image).size(newWid, newHei).asBufferedImage();
    }
}
