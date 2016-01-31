package com.neuro_structure;

import static com.graphics.ImageService.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class describes the full neuro web
 */
public class NeuroWeb implements Serializable {

    /*************************************** CONSTANTS ***************************************/
    private final int imageSize = 100; // size of source
    private final int N = imageSize * imageSize; // amount of incomes for the web (every pixel)
    private final int M = 26; // amount of letters to recognize
    private final double E = -(1 / M) / 2; // weight of synapses of the second neuro-layer
    private final double MAX_BLACK = 0.25; // uses for ImageService.howBlackIsIt();
                                        // if greater than this value => not black.

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

    public void learnImage(BufferedImage image, char c) {

        // resize the image if necessary
        if (image.getWidth() != imageSize || image.getHeight() != imageSize) {
            try {
                image = resize(image,imageSize,imageSize);
            } catch (IOException e1) {
                System.out.println("The image was resized incorrectly");
            }
        }

        // start loop through every pixel in the image
        for (int x = 0; x < imageSize; x++) {
            for (int y = 0; y < imageSize; y++) {
                int[] rgb = getPixelRGB(image, x, y);

                // if $black is closer to 0 => black. If closer to 1 => white. Used to find dark-grey pixels also.
                double black = howBlackIsIt(rgb);
                if(black > MAX_BLACK){
                    /**
                     * TODO: Finished working here
                     */
                }
            }
        }
    }


}