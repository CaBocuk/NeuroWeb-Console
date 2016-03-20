package com.neuro_structure;

import java.io.Serializable;
import java.util.List;

/**
 * Class describes the single neuron structure
 */
public class Neuron implements Serializable{

    private double output;
    private double sum; // sum of weights

    public Neuron(){
        output = 0;
        sum = 0;
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public void changeSumBy(double offset){
        sum += offset;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
