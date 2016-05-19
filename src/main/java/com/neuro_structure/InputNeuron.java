package com.neuro_structure;

import java.io.Serializable;
import java.util.List;

/**
 * Class describes the single neuron structure from first neuro-layer of our neuro-web.
 */
public class InputNeuron implements Serializable{
    private double weights[][]; // Weight coefficients of synapses
    private double output;

    private int learnedImages;

    private final double LEARN_MAX = 1d;

    public InputNeuron(double weights[][]){
        this.weights = weights;
        output = 0d;
    }

    public InputNeuron(int imageX, int imageY){
        weights = new double[imageX][imageY];
        output = 0d;
    }

    public void changeWeightBy(int x, int y, double value){
        weights[x][y] += value;
    }

    public double getWeightAt(int x, int y){
        return weights[x][y];
    }

    public int getLearnedImages() {
        return learnedImages;
    }

    public void imageLearned(){
        learnedImages++;
    }

    public void increaseWeight(int x, int y){ // функция, увеличивающая значение нейрона
        double weight = weights[x][y];
        weight *= learnedImages++;
        weight += LEARN_MAX; // значение LEARN_MAX = 1 и означает максимальную вероятность пикселя быть черным
        weight /= learnedImages;
        weights[x][y] = weight;
    }

    public void decreaseWeight(int x, int y){
        double weight = weights[x][y];
        weight *= learnedImages++;
        weight -= LEARN_MAX;
        weight /= learnedImages;
        weights[x][y] = weight;
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public void changeOutputBy(double offset){
        output += offset;
    }
}
