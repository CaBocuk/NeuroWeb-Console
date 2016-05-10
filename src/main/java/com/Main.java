package com;

import com.graphics.ImageService;
import com.graphics.ItemType;
import com.neuro_structure.NeuroWeb;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                GUI frame = new GUI();
                frame.setVisible(true);
            }
        });
    }
}
