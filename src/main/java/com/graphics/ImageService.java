package com.graphics;

import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Scope of some static methods which are designed to work with input images
 */
public class ImageService {

    private static final double MAX_BLACK = 0.3; // uses for ImageService.howBlackIsIt();
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

    // метод для определения степени "черноты" цвета
    public static double howBlackIsIt(int[] rgb) {
        double[] magicCoefficients = {0.2126, 0.7152, 0.0722}; // коэффициенты для расчета итоговой суммы
        double sum = 0;

        for (int i = 0; i < 3; i++) {
            sum += magicCoefficients[i] * rgb[i];
        }

        // если сумма ближе к нулю - цвет черный. Если ближе к 255 - цвет белый
        return sum / 255;
    }

    public static boolean isBlack(int[] rgb) {
        return howBlackIsIt(rgb) <= MAX_BLACK;
    }

    public static BufferedImage putImageIntoWhiteSquare(BufferedImage image) {
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
            g.drawImage(image, diff, 0, null);
            g.fillRect(diff + curWid, 0, curHei, curHei);
            image = newImage;
        } else {
            BufferedImage newImage = config.createCompatibleImage(curWid, curWid);
            Graphics2D g = newImage.createGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, curWid, diff);
            g.drawImage(image, 0, diff, null);
            image = newImage;
        }

        return image;
    }

    public static BufferedImage makeSomeNoise(BufferedImage image) {
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
        /*// проходим по каждому пикселю изображения
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if(isBlack(getPixelRGB(image,x,y))){
                    // если текущий пиксель черный
                    // проверяем соседние пиксели на то, являются ли они черными
                    int neighbourCounter = 0;
                    if(x > 0 && isBlack(getPixelRGB(image,x-1,y)))
                        neighbourCounter++;
                    if(x > 0 && y>0 && isBlack(getPixelRGB(image,x-1,y-1)))
                        neighbourCounter++;
                    if(y > 0 && isBlack(getPixelRGB(image,x,y-1)))
                        neighbourCounter++;
                    if(x < image.getWidth() && y > 0 && isBlack(getPixelRGB(image,x+1,y-1)))
                        neighbourCounter++;
                    if(x < image.getWidth() && isBlack(getPixelRGB(image,x+1,y)))
                        neighbourCounter++;
                    if(x < image.getWidth() && y < image.getHeight() && isBlack(getPixelRGB(image,x+1,y+1)))
                        neighbourCounter++;
                    if(y < image.getHeight() && isBlack(getPixelRGB(image,x,y+1)))
                        neighbourCounter++;
                    if(x > 0 && y < image.getHeight() && isBlack(getPixelRGB(image,x-1,y+1)))
                        neighbourCounter++;

                    if(neighbourCounter < 2){
                        // если рядом с текущим пикселем менее двух черных, признаем его шумом и окрашиваем в белый цвет.
                        image.setRGB(x,y,Color.white.getRGB());
                    }
                }
            }
        }*/
        return image;
    }

    private static Boolean[][] getImageBooleanMatrix(BufferedImage image) {
        Boolean[][] ret = new Boolean[image.getWidth()][image.getHeight()];
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                ret[x][y] = isBlack(getPixelRGB(image, x, y));
            }
        }
        return ret;
    }

    private static Map.Entry<Point, Point> getSectionCoordinates(Boolean[][] image, int xStart, int yStart, Map.Entry<Point, Point> sectionCorners) {
        if (image[xStart][yStart]) {
            if (xStart < sectionCorners.getKey().getX()) {
                sectionCorners.getKey().setLocation(xStart, sectionCorners.getKey().y);
            }
            if (yStart < sectionCorners.getKey().getY()) {
                sectionCorners.getKey().setLocation(sectionCorners.getKey().x, yStart);
            }
            if (xStart > sectionCorners.getValue().getX()) {
                sectionCorners.getValue().setLocation(xStart, sectionCorners.getValue().y);
            }
            if (yStart > sectionCorners.getValue().getY()) {
                sectionCorners.getValue().setLocation(sectionCorners.getValue().x, yStart);
            }
            image[xStart][yStart] = false;
            if (xStart > 0)
                sectionCorners = getSectionCoordinates(image, xStart - 1, yStart, sectionCorners);
            if (yStart > 0)
                sectionCorners = getSectionCoordinates(image, xStart, yStart - 1, sectionCorners);
            if (xStart + 1 < image.length)
                sectionCorners = getSectionCoordinates(image, xStart + 1, yStart, sectionCorners);
            if (yStart + 1 < image[0].length)
                sectionCorners = getSectionCoordinates(image, xStart, yStart + 1, sectionCorners);
        }
        return sectionCorners;
    }

    /**
     * @param image
     */
    public static List<BufferedImage> getMappedImage(BufferedImage image) {
        double coeff = 100. / image.getHeight();
        List<BufferedImage> ret = new ArrayList<>();
        // готовим изображение к обработке
        try {
            // ресайзинг
            image = resize(image, (int) (image.getWidth() * coeff), (int) (image.getHeight() * coeff));

            removeNoise(image); // шумоподавление
            image = findAndRemoveBorder(image); // обнаружение и удаление рамки вокруг номера
            ImageIO.write(image, "JPG", new File("test_5.jpg"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Не удалось удалить рамку из изображения");
            e.printStackTrace();
        }

        int sections = 0; // количество найденных обособленных элементов

        for (int x = 0; x < image.getWidth(); x++) { // пробег слева направо
            int curSectionMinX = 0;
            int curSectionMinY = 0;
            int blackPixelsCounter = 0;
            for (int y = 0; y < image.getHeight(); y++) { // подсчитываем количество черных пикселей в конкретном столбце
                if (isBlack(getPixelRGB(image, x, y))) {
                    blackPixelsCounter++;
                    if (curSectionMinY == 0)
                        curSectionMinY = y;
                }
            }
            if ((double) (blackPixelsCounter) / image.getHeight() > 0.05) { // проверяем, есть ли скопление пикселей
                curSectionMinX = x;

                //This entry contains top left and top right corner of image
                Map.Entry<Point, Point> sectionDimension = new AbstractMap.SimpleEntry<>(new Point(Integer.MAX_VALUE, Integer.MAX_VALUE), new Point(Integer.MIN_VALUE, Integer.MIN_VALUE));
                // проходим рекурсивно от текущего черного пикселя и ищем координаты
                // верхнего левого и правого нижнего угла прямоугольника, описывающего
                // текущий обособленный элемент
                sectionDimension = getSectionCoordinates(getImageBooleanMatrix(image), curSectionMinX, curSectionMinY, sectionDimension);

                x = sectionDimension.getValue().x;
                int x1 = sectionDimension.getKey().x;
                int y1 = sectionDimension.getKey().y;
                int w = sectionDimension.getValue().x - sectionDimension.getKey().x;
                int h = sectionDimension.getValue().y - sectionDimension.getKey().y;
                if (w > 0 && h > 0) {
                    // выделяем найденную область
                    BufferedImage subImg = image.getSubimage(x1, y1, w, h);
                    // добавляем в список найденных
                    ret.add(subImg);
                    sections++;

                    try {
                        ImageIO.write(subImg, "PNG", new File(sections + ".png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return ret;
    }

    public static BufferedImage copyImage(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private static BufferedImage findAndRemoveBorder(BufferedImage image) {
        int wid = image.getWidth();
        int hei = image.getHeight();

        Boolean[][] imageMatrix = getImageBooleanMatrix(image);

        // look for left side of frame
        for (int x = 0; x < wid / 10; x++) {
            if (isBlack(getPixelRGB(image, x, hei / 2))) {
                Map.Entry<Point, Point> sectionCorners = new AbstractMap.SimpleEntry<>(new Point(0, 0), new Point(wid, hei));
                sectionCorners = getSectionCoordinates(imageMatrix, x, hei / 2, sectionCorners);

                int borderWidth = sectionCorners.getValue().x - sectionCorners.getKey().x;
                int borderHeight = sectionCorners.getValue().y - sectionCorners.getKey().y;
                boolean isBorder = borderWidth > 0.5 * wid && borderHeight > 0.5 * hei;

                if (isBorder) {
                    Boolean[][] cur = getImageBooleanMatrix(image);
                    for (int x1 = 0; x1 < wid; x1++)
                        for (int y1 = 0; y1 < hei; y1++)
                            if (imageMatrix[x1][y1] ^ cur[x1][y1])
                                image.setRGB(x1, y1, Color.white.getRGB());
                }
            }
        }
        return image;
    }

    // resizing of image using Thumbnailator

    public static BufferedImage resize(BufferedImage image, int newWid, int newHei) throws IOException {
        return Thumbnails.of(image).size(newWid, newHei).asBufferedImage();
    }
    public static BufferedImage resizeWidth(BufferedImage image, int newWid) throws IOException {
        return Thumbnails.of(image).size(newWid, image.getHeight() * newWid / image.getHeight()).asBufferedImage();
    }
    public static BufferedImage resizeHeight(BufferedImage image, int newHei) throws IOException {
        return Thumbnails.of(image).size(image.getWidth() * newHei / image.getHeight(), newHei).asBufferedImage();
    }

    public static void generateImages(String folder, int limit, ItemType itemType) {
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (int i = 0; i < limit; i++) {
            int counter = 0;
            for (Font font : fonts) {
                final GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
                BufferedImage img = config.createCompatibleImage(200, 200);
                Graphics g = img.createGraphics();
                g.setColor(Color.white);
                g.fillRect(0, 0, 200, 200);
                g.setColor(Color.black);
                System.out.println(font.getName());
                g.setFont(new Font(font.getName(), 150, 150));
                if (itemType == ItemType.DIGIT)
                    g.drawString(i + "", 50, 150);
                else if (itemType == ItemType.LETTER)
                    g.drawString((char) (i + 'A') + "", 50, 150);

                try {
                    ImageIO.write(img, "JPG", new File(folder + i + "_" + counter + ".jpg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                counter++;
            }
        }
    }
}
