/*
 * Created by JFormDesigner on Mon Mar 28 21:05:14 MSK 2016
 */

package com;

import com.graphics.ImageService;
import com.graphics.ItemType;
import com.neuro_structure.NeuroWeb;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * @author Artsem Savosik
 */
public class GUI extends JFrame {

    protected BufferedImage image;
    private NeuroWeb web;

    public GUI() {
        initComponents();
        image = null;
        web = new NeuroWeb();
    }

    private void chooseFile(ActionEvent e) {
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        fc.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".jpg") || f.getName().endsWith(".jpeg") || f.getName().endsWith(".png") ||
                        f.getName().endsWith(".bmp");
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        fc.showOpenDialog(this);
        File imageFile = fc.getSelectedFile();
        if (imageFile != null) {
            textArea.setText(imageFile.getName());
            try {
                this.image = ImageIO.read(imageFile);
                panel2.repaint();
                label.setText("<html>Результат: <font color='red'>" + readAutoNumber(this.image)+"</font></html>");
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null, "Не удалось считать файл " + imageFile.getName());
                label.setText("N/A");
            }
        }
    }

    public String readAutoNumber(BufferedImage image) {
        java.util.List<BufferedImage> images = ImageService.getMappedImage(image);
        StringBuffer sb = new StringBuffer();
        int maxHeight = images.stream().mapToInt((img) -> img.getHeight()).max().getAsInt();
        // TODO: REPLACE WITH FILE_SETTING
        ItemType[] pattern = {ItemType.DIGIT, ItemType.DIGIT, ItemType.DIGIT, ItemType.DIGIT, ItemType.LETTER, ItemType.LETTER, ItemType.DIGIT};
        //ItemType[] pattern = {ItemType.LETTER, ItemType.DIGIT, ItemType.DIGIT, ItemType.DIGIT, ItemType.LETTER, ItemType.LETTER, ItemType.DIGIT, ItemType.DIGIT};
        int patternCounter = 0;
        for (int i = 0; i < images.size(); i++) {
            if (patternCounter == pattern.length) {
                //JOptionPane.showMessageDialog(null, "An error appeared during recognizing the number. Please check the pattern.");
                break;
            }
            ItemType curType = pattern[patternCounter];
            BufferedImage img = images.get(i);
            if (img.getHeight() > maxHeight / 2)
                try {
                    img = ImageService.resize(ImageService.putImageIntoWhiteSquare(img), web.getImageSize(), web.getImageSize());
                    int result = web.recognizeItem(img, curType);
                    if (curType == ItemType.DIGIT)
                        sb.append(result);
                    else if (curType == ItemType.LETTER)
                        sb.append((char) ('A' + result));
                    patternCounter++;
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Couldn't resize the image.");
                }
        }

        return sb.toString();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Artsem Savosik
        label = new JLabel();
        panel = new JPanel();
        menu = new JMenuBar();
        button = new JButton();
        textArea = new JTextArea();
        panel2 = new MyPanel(this);

        //======== this ========
        setMinimumSize(new Dimension(420, 300));
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //---- label ----
        label.setText("N/A");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(label, BorderLayout.SOUTH);

        //======== panel ========
        {

            // JFormDesigner evaluation mark
            panel.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                            "", javax.swing.border.TitledBorder.CENTER,
                            javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                            java.awt.Color.red), panel.getBorder()));
            panel.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent e) {
                    if ("border".equals(e.getPropertyName())) throw new RuntimeException();
                }
            });

            panel.setLayout(new FlowLayout());

            //---- button ----
            button.setText("Обзор");
            button.addActionListener(e -> chooseFile(e));
            panel.add(button);

            //---- textArea ----
            textArea.setText("Пожалуйста, выберите файл");
            textArea.setEditable(false);
            panel.add(textArea);
        }
        contentPane.add(panel, BorderLayout.NORTH);

        //======== panel2 ========
        {
            panel2.setPreferredSize(new Dimension(400, 200));
            panel2.setMinimumSize(new Dimension(400, 200));
            panel2.setLayout(null);

            { // compute preferred size
                Dimension preferredSize = new Dimension();
                for (int i = 0; i < panel2.getComponentCount(); i++) {
                    Rectangle bounds = panel2.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel2.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel2.setMinimumSize(preferredSize);
                panel2.setPreferredSize(preferredSize);
            }
        }
        contentPane.add(panel2, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());

        //======== menu ========
        JMenu neuroWeb = new JMenu("Нейросеть");
        JMenuItem study = new JMenuItem("Переобучить сеть");
        JMenuItem learnImage = new JMenuItem("Очистить сеть");
        neuroWeb.add(study);
        neuroWeb.add(learnImage);
        menu.add(neuroWeb);

        JMenu types = new JMenu("Номера");
        JMenuItem listOfTypes = new JMenuItem("Выбрать тип номера");
        JMenuItem addType = new JMenuItem("Добавить тип номера");
        types.add(study);
        types.add(addType);
        menu.add(types);
        this.setJMenuBar(menu);


        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Artsem Savosik
    private JLabel label;
    private JMenuBar menu;
    private JPanel panel;
    private JButton button;
    private JTextArea textArea;
    private JPanel panel2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

class MyPanel extends JPanel {
    private GUI parent;
    int wid;
    int hei;

    public MyPanel(GUI parent) {
        this.parent = parent;
    }

    public void paint(Graphics g) {
        wid = getWidth();
        hei = getHeight();

        g.clearRect(0, 0, wid, hei);
        if (parent.image != null) {
            int iwid = parent.image.getWidth();
            int ihei = parent.image.getHeight();
            g.drawImage(parent.image, 0, (hei - ihei * wid / iwid) / 2, wid, ihei * wid / iwid, null);
        }
    }
}