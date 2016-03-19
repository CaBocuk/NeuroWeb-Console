package com.neuro_structure;

import static com.graphics.ImageService.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class describes the full neuro web
 */
public class NeuroWeb implements Serializable {

    /***************************************
     * CONSTANTS
     ***************************************/
    private final int inputImageSize = 100; // size of source
    private final int imageSize = 40;
    private final int N = imageSize * imageSize; // amount of incomes for the web (every pixel)
    private final int M = 10; // amount of numbers to recognize
    private final double E = -(1 / M) / 2; // weight of synapses of the second neuro-layer

    private BufferedImage image; // source
    private List<InputNeuron> firstNeuroLayer; // first neuro-layer
    private List<Neuron> secondNeuroLayer; // second neuro-layer


    public NeuroWeb() {
        firstNeuroLayer = new ArrayList<InputNeuron>(M);
        secondNeuroLayer = new ArrayList<Neuron>(M);
        try {
            image = ImageIO.read(new File("input.jpg"));
        } catch (IOException e1) {
            System.out.println("The image was read incorrectly");
        }
    }

    public void learnImage(BufferedImage image, int answer) {
        InputNeuron neuron = firstNeuroLayer.get(answer);

        // resize the image if necessary
        if (image.getWidth() != imageSize || image.getHeight() != imageSize) {
            try {
                image = resize(image, imageSize, imageSize);
            } catch (IOException e1) {
                System.out.println("The image was resized incorrectly");
            }
        }

        // start loop through every pixel in the image
        for (int x = 0; x < imageSize; x++) {
            for (int y = 0; y < imageSize; y++) {
                int[] rgb = getPixelRGB(image, x, y);

                if (isBlack(rgb)) {
                    neuron.changeWeightBy(x, y, 0.5);
                } else {
                    neuron.changeWeightBy(x, y, -0.5);
                }
            }
        }
    }

    public void initializeNeurons() {

    }

    // deserializing the web
    public void open() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("input.nwf"));

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }

    // serializing the web
    public void save() {

    }
}