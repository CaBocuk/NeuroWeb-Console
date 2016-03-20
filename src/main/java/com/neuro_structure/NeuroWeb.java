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
    private final double MIN_OUTPUT = 0; // min value of output to be accepted

    private BufferedImage image; // source
    private List<InputNeuron> firstNeuroLayer; // first neuro-layer
    private List<Neuron> secondNeuroLayer; // second neuro-layer


    public NeuroWeb() {
        firstNeuroLayer = new ArrayList<InputNeuron>(M);
        secondNeuroLayer = new ArrayList<Neuron>(M);
        /*try {
            image = ImageIO.read(new File("input.jpg"));
        } catch (IOException e1) {
            System.out.println("The image was read incorrectly");
        }*/
    }

    public void learnImage(BufferedImage image, int answer) {
        InputNeuron neuron = firstNeuroLayer.get(answer);

        // resize the image if necessary
        if (image.getWidth() != imageSize || image.getHeight() != imageSize) {
           // try {
                image = getCutImage(image, imageSize, imageSize);
                //image = resize(image, imageSize, imageSize);
           /* } catch (IOException e1) {
                System.out.println("The image was resized incorrectly");
            }*/
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
        System.out.println("Learned "+answer);
    }

    public int recognizeImage(BufferedImage image) {
        image = getCutImage(image, imageSize, imageSize);
        for (int i = 0; i < M; i++) {
            InputNeuron neuron = firstNeuroLayer.get(i);
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

            /*if (neuron.getOutput() >= N / 2) {
                neuron.setOutput(N / 2);
            }*/
        }

        double outputs[] = new double[M];
        for (int i = 0; i < M; i++) {
            Neuron neuron = secondNeuroLayer.get(i);
            outputs[i] = firstNeuroLayer.get(i).getOutput();
            neuron.setOutput(outputs[i]);
        }

        int count = 0;
        boolean allEqual = true;
        do {
            for (int i = 0; i < M; i++) {
                Neuron neuron = secondNeuroLayer.get(i);
                outputs[i] = firstNeuroLayer.get(i).getOutput();
                neuron.setSum(0);
            }

            for (int i = 0; i < M; i++) {
                for (int j = 0; j < M; j++) {
                    if (i == j) {
                        secondNeuroLayer.get(j).changeSumBy(secondNeuroLayer.get(i).getOutput());
                    } else {
                        secondNeuroLayer.get(j).changeSumBy(secondNeuroLayer.get(i).getOutput() * this.E);
                    }
                }
            }

            for(int i = 0; i < M; i++) {
                secondNeuroLayer.get(i).setOutput(secondNeuroLayer.get(i).getSum());
                if(outputs[i] != secondNeuroLayer.get(i).getOutput())
                    allEqual = false;
            }


        } while(!allEqual && ++count < 25);

        double max = Double.MIN_VALUE;
        int index = -1;
        for(int i = 0; i < M; i++){
            if(secondNeuroLayer.get(i).getOutput() > max){
                max = secondNeuroLayer.get(i).getOutput();
                index = i;
            }
        }

        return (max > MIN_OUTPUT) ? index : -1;
    }

    public void initializeCleanNeurons() {
        firstNeuroLayer = new ArrayList<InputNeuron>();
        secondNeuroLayer = new ArrayList<Neuron>();
        for(int i = 0; i < M; i++){
            firstNeuroLayer.add(new InputNeuron(imageSize, imageSize));
            secondNeuroLayer.add(new Neuron());
        }
    }

    // deserializing the web
    public void open() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("input.nwf"));
            firstNeuroLayer = (List<InputNeuron>) ois.readObject();
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
        for(int i = 0; i < M; i++){
            secondNeuroLayer.add(new Neuron());
        }
    }

    // serializing the web
    public void save() {
        ObjectOutputStream ous = null;
        try {
            ous = new ObjectOutputStream(new FileOutputStream("input.nwf"));
            ous.writeObject(firstNeuroLayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}