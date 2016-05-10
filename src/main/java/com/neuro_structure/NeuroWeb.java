package com.neuro_structure;

import com.graphics.ImageService;
import com.graphics.ItemType;

import javax.imageio.ImageIO;

import static com.graphics.ImageService.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class describes the full neuro web
 */
public class NeuroWeb implements Serializable {

    /***************************************
     * CONSTANTS
     ***************************************/
    private final int inputImageSize = 100; // size of source

    private final int imageSize = 40;
    private final int M = 10; // amount of numbers to recognize
    private final int K = 26; // amount of letters to recognize
    private final double E = -(1 / M) / 2; // weight of synapses of the second neuro-layer
    private final double MIN_OUTPUT = 0; // min value of output to be accepted

    private BufferedImage image; // source
    private List<InputNeuron> firstDigitNeuroLayer; // first neuro-layer
    private List<Neuron> secondDigitNeuroLayer; // second neuro-layer

    private List<InputNeuron> firstLetterNeuroLayer; // first neuro-layer
    private List<Neuron> secondLetterNeuroLayer; // second neuro-layer


    public NeuroWeb() {
        open();
    }

    public void generateAndLearn() {
        ImageService.generateImages("learnDigits\\", M, ItemType.DIGIT);
        ImageService.generateImages("learnLetters\\", K, ItemType.LETTER);
        File[] files = (new File("learnDigits\\")).listFiles();
        for (File file : files) {
            if (Pattern.matches("[0-9]+_[0-9]+.jpg", file.getName())) {
                try {
                    learnItem(ImageIO.read(file), Integer.parseInt(file.getName().split("_")[0]), ItemType.DIGIT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        files = (new File("learnLetters\\")).listFiles();
        for (File file : files) {
            if (Pattern.matches("[0-9]+_[0-9]+.jpg", file.getName())) {
                try {
                    learnItem(ImageIO.read(file), Integer.parseInt(file.getName().split("_")[0]), ItemType.LETTER);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        save();
    }

    public void learnItem(BufferedImage image, int answer, ItemType itemType) {
        List<InputNeuron> firstLayer = (itemType == ItemType.DIGIT) ? firstDigitNeuroLayer : firstLetterNeuroLayer; // first neuro-layer

        InputNeuron neuron = firstLayer.get(answer);

        // resize the image if necessary
        if (image.getWidth() != imageSize || image.getHeight() != imageSize) {
            try {
                image = putImageIntoWhiteSquare(image);
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
                    neuron.increaseWeight(x, y);
                } else {
                    neuron.decreaseWeight(x, y);
                }
            }
        }
        System.out.println("Learned " + answer);
    }

    public int recognizeItem(BufferedImage image, ItemType itemType) {
        List<InputNeuron> firstLayer = (itemType == ItemType.DIGIT) ? firstDigitNeuroLayer : firstLetterNeuroLayer; // first neuro-layer
        List<Neuron> secondLayer = (itemType == ItemType.DIGIT) ? secondDigitNeuroLayer : secondLetterNeuroLayer; // second neuro-layer
        final int items = (itemType == ItemType.DIGIT) ? M : K;

        image = putImageIntoWhiteSquare(image);
        try {
            image = resize(image, imageSize, imageSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < items; i++) {
            InputNeuron neuron = firstLayer.get(i);
            neuron.setOutput(0d);
            for (int x = 0; x < imageSize; x++) {
                for (int y = 0; y < imageSize; y++) {
                    if (isBlack(getPixelRGB(image, x, y))) {
                        neuron.changeOutputBy(neuron.getWeightAt(x, y));
                    } else {
                        neuron.changeOutputBy(-neuron.getWeightAt(x, y));
                    }
                }
            }
        }

        double outputs[] = new double[items];
        for (int i = 0; i < items; i++) {
            Neuron neuron = secondLayer.get(i);
            outputs[i] = firstLayer.get(i).getOutput();
            neuron.setOutput(outputs[i]);
        }

        int count = 0;
        boolean allEqual = true;
        do {
            for (int i = 0; i < items; i++) {
                Neuron neuron = secondLayer.get(i);
                outputs[i] = firstLayer.get(i).getOutput();
                neuron.setSum(0);
            }

            for (int i = 0; i < items; i++) {
                for (int j = 0; j < items; j++) {
                    if (i == j) {
                        secondLayer.get(j).changeSumBy(secondLayer.get(i).getOutput());
                    } else {
                        secondLayer.get(j).changeSumBy(secondLayer.get(i).getOutput() * this.E);
                    }
                }
            }

            for (int i = 0; i < items; i++) {
                secondLayer.get(i).setOutput(secondLayer.get(i).getSum());
                if (outputs[i] != secondLayer.get(i).getOutput())
                    allEqual = false;
            }


        } while (!allEqual && ++count < 25);

        double max = Double.MIN_VALUE;
        int index = -1;
        for (int i = 0; i < items; i++) {
            if (secondLayer.get(i).getOutput() > max) {
                max = secondLayer.get(i).getOutput();
                index = i;
            }
        }

        return (max > MIN_OUTPUT) ? index : -1;
    }

    public void initializeCleanNeurons() {
        firstDigitNeuroLayer = new ArrayList<InputNeuron>();
        secondDigitNeuroLayer = new ArrayList<Neuron>();
        firstLetterNeuroLayer = new ArrayList<InputNeuron>();
        secondLetterNeuroLayer = new ArrayList<Neuron>();
        for (int i = 0; i < M; i++) {
            firstDigitNeuroLayer.add(new InputNeuron(imageSize, imageSize));
            secondDigitNeuroLayer.add(new Neuron());
        }
        for (int i = 0; i < K; i++) {
            firstLetterNeuroLayer.add(new InputNeuron(imageSize, imageSize));
            secondLetterNeuroLayer.add(new Neuron());
        }
    }

    // deserializing the web
    public void open() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("input.nwf"));
            firstDigitNeuroLayer = (List<InputNeuron>) ois.readObject();
            firstLetterNeuroLayer = (List<InputNeuron>) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            initializeCleanNeurons();
        } catch (IOException e) {
            e.printStackTrace();
            initializeCleanNeurons();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            initializeCleanNeurons();
        }
        secondDigitNeuroLayer = new ArrayList<>();
        secondLetterNeuroLayer = new ArrayList<>();
        for (int i = 0; i < M; i++) {
            secondDigitNeuroLayer.add(new Neuron());
        }
        for (int i = 0; i < K; i++) {
            secondLetterNeuroLayer.add(new Neuron());
        }
    }

    // serializing the web
    public void save() {
        ObjectOutputStream ous = null;
        try {
            ous = new ObjectOutputStream(new FileOutputStream("input.nwf"));
            ous.writeObject(firstDigitNeuroLayer);
            ous.writeObject(firstLetterNeuroLayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getImageSize() {
        return imageSize;
    }
}