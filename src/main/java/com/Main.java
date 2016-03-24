package com;

import com.graphics.ImageService;
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
        NeuroWeb web = new NeuroWeb();
        web.open();
        File folder = new File("Learn images");
        List<File> files = Arrays.asList(folder.listFiles());
        files.stream().forEach((image) -> {
            try {
                //web.learnImage(ImageIO.read(image), Integer.parseInt(image.getName().charAt(0)+""));
                web.learnImageExperiment(ImageIO.read(image), Integer.parseInt(image.getName().charAt(0)+""));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        /*folder = new File("Recognize images");
        files = Arrays.asList(folder.listFiles());
        files.stream().forEach((image) -> {
            try {
                System.out.println("For image '" + image.getName() + "' we got result " + web.recognizeImage(ImageIO.read(image)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });*/
        //web.save();
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        chooser.showOpenDialog(null);
        File img = chooser.getSelectedFile();
        try {
            BufferedImage[] images = ImageService.getMappedImage(ImageIO.read(img), 40, 40);
            StringBuffer sb = new StringBuffer();
            Arrays.asList(images).stream().forEach((image)->sb.append(web.recognizeImage(image)));
            System.out.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
