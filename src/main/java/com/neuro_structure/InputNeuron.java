package com.neuro_structure;

import java.io.Serializable;
import java.util.List;

/**
 * Class describes the single neuron structure from first neuro-layer of our neuro-web.
 */
public class InputNeuron implements Serializable{
    private double weights[][]; // Weight coefficients of synapses
    private double output;

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
