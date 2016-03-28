/*
 * Created by JFormDesigner on Mon Mar 28 21:05:14 MSK 2016
 */

package com;

import com.graphics.ImageService;
import com.neuro_structure.NeuroWeb;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
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
        if(imageFile != null) {
            textArea1.setText(imageFile.getName());
            try {
                this.image = ImageIO.read(imageFile);
                panel2.repaint();
                label1.setText("The result is: " + web.readAutoNumber(this.image));
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null, "Couldn't read the file " + imageFile.getName());
                label1.setText("N/A");
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Artsem Savosik
        label1 = new JLabel();
        panel1 = new JPanel();
        button1 = new JButton();
        textArea1 = new JTextArea();
        panel2 = new MyPanel(this);

        //======== this ========
        setMinimumSize(new Dimension(420, 300));
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //---- label1 ----
        label1.setText("N/A");
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(label1, BorderLayout.SOUTH);

        //======== panel1 ========
        {

            // JFormDesigner evaluation mark
            panel1.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                            "", javax.swing.border.TitledBorder.CENTER,
                            javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                            java.awt.Color.red), panel1.getBorder()));
            panel1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent e) {
                    if ("border".equals(e.getPropertyName())) throw new RuntimeException();
                }
            });

            panel1.setLayout(new FlowLayout());

            //---- button1 ----
            button1.setText("Choose an image");
            button1.addActionListener(e -> chooseFile(e));
            panel1.add(button1);

            //---- textArea1 ----
            textArea1.setText("Please, choose a file");
            textArea1.setEditable(false);
            panel1.add(textArea1);
        }
        contentPane.add(panel1, BorderLayout.NORTH);

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
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Artsem Savosik
    private JLabel label1;
    private JPanel panel1;
    private JButton button1;
    private JTextArea textArea1;
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