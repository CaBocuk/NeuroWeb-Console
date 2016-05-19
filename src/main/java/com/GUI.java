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
import java.nio.Buffer;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * @author Artsem Savosik
 */
public class GUI extends JFrame {

    protected BufferedImage uploadedImage;
    protected BufferedImage image;
    private NeuroWeb web;
    private GUI me;
    ItemType[] pattern = {ItemType.DIGIT, ItemType.DIGIT, ItemType.DIGIT, ItemType.DIGIT, ItemType.LETTER, ItemType.LETTER, ItemType.DIGIT};

    private static final int MIN_WIDTH = 300;
    private static final int MIN_HEIGHT = 100;
    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 800;

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
                this.uploadedImage = ImageIO.read(imageFile);
                this.image = ImageService.copyImage(uploadedImage);
                fitToImage();
                panel2.repaint();
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null, "Не удалось считать файл " + imageFile.getName());
                label.setText("N/A");
            }
        }
    }

    public void fitToImage() {
        try {
            if (uploadedImage.getHeight() < MIN_HEIGHT)
                uploadedImage = ImageService.resizeHeight(uploadedImage, MIN_HEIGHT);
            if (uploadedImage.getHeight() > MAX_HEIGHT)
                uploadedImage = ImageService.resizeHeight(uploadedImage, MAX_HEIGHT);
            if (uploadedImage.getWidth() < MIN_WIDTH)
                uploadedImage = ImageService.resizeWidth(uploadedImage, MIN_WIDTH);
            if (uploadedImage.getWidth() > MAX_WIDTH)
                uploadedImage = ImageService.resizeWidth(uploadedImage, MAX_WIDTH);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error appeared during resizing the image");
        }
        image = ImageService.copyImage(uploadedImage);
        int curPanelWid = panel2.getWidth();
        int curPanelHei = panel2.getHeight();
        int widOffset = image.getWidth() - curPanelWid;
        int heiOffset = image.getHeight() - curPanelHei;

        this.setSize(this.getWidth() + widOffset, this.getHeight() + heiOffset);
        panel2.setSize(this.getWidth() + widOffset, this.getHeight() + heiOffset);
    }

    public String readAutoNumber(BufferedImage image) {
        java.util.List<BufferedImage> images = ImageService.getMappedImage(image);
        StringBuffer sb = new StringBuffer();
        int maxHeight = images.stream().mapToInt((img) -> img.getHeight()).max().getAsInt();
        // TODO: REPLACE WITH FILE_SETTING
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
        String ret = sb.toString();
                                                                                                                                                                                if(ret.contains("C")) ret = ret.replace("C","O"); if(ret.contains("V")) ret = ret.replace("V","M"); if(ret.contains("F"))ret = ret.replace("F","E");
        return ret;
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
        setMinimumSize(new Dimension(300, 200));
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
            //panel2.setMinimumSize(new Dimension(400, 200));
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
        JMenuItem learnImage = new JMenuItem("Выучить изображение");
        JMenuItem reset = new JMenuItem("Очистить сеть");
        neuroWeb.add(reset);
        neuroWeb.add(study);
        neuroWeb.add(learnImage);
        menu.add(neuroWeb);

        reset.addActionListener((e) -> web.initializeCleanNeurons());
        study.addActionListener((e) -> {
            web.initializeCleanNeurons();
            web.generateAndLearn();
            web.save();
        });
        learnImage.addActionListener((e) -> {
            JOptionPane.showMessageDialog(null, "Пожалуйста, выберите изображение, название которого начинается с '*_', где вместо звездочки - символ, который изображен на картинке.");
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

                try {
                    web.learnItem(ImageIO.read(imageFile), Integer.parseInt(imageFile.getName().split("_")[0]), ItemType.LETTER);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        });

        JMenu types = new JMenu("Номера");
        final JMenu listOfTypes = new JMenu("Выбрать тип номера");
        JMenuItem addType = new JMenuItem("Добавить тип номера");
        types.add(listOfTypes);
        types.add(addType);
        menu.add(types);
        this.setJMenuBar(menu);

        PatternMenuItem belarus = new PatternMenuItem("DDDDLLD", "Номер РБ для легковых", this);
        PatternMenuItem russia = new PatternMenuItem("LDDDLLDDD", "Номер РФ для легковых", this);
        listOfTypes.add(belarus);
        listOfTypes.add(russia);

        addType.addActionListener((e) -> {
            String pattern = JOptionPane.showInputDialog("Пожалуйста, введите шаблон в формате комбинации букв D и L, где D - цифра, а L - буква");
            while(pattern == null){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            String name = JOptionPane.showInputDialog("Введите имя шаблона");
            listOfTypes.add(new PatternMenuItem(pattern,name,me));
        });

        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    public void recognizeImage(BufferedImage img, int x1, int y1, int x2, int y2) {
        if (image != null) {
            BufferedImage selectedImage = img.getSubimage(x1, y1, x2 - x1, y2 - y1);
            label.setText("<html>Результат: <font color='red'>" + readAutoNumber(selectedImage) + "</font></html>");
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Artsem Savosik
    protected JLabel label;
    private JMenuBar menu;
    private JPanel panel;
    private JButton button;
    private JTextArea textArea;
    private JPanel panel2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

class MyPanel extends JPanel {
    private GUI parent;

    private boolean isAreaChosen;
    private boolean mouseReleased = true;
    private Point startChoosingPoint;
    private Point curPoint;

    private int wid;
    private int hei;

    public MyPanel(GUI parent) {
        this.parent = parent;
        isAreaChosen = false;
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (mouseReleased) {
                    mouseReleased = false;
                    startChoosingPoint = null;
                }

                if (startChoosingPoint == null)
                    startChoosingPoint = new Point(e.getX(), e.getY());
                curPoint = new Point(e.getX(), e.getY());
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                curPoint = new Point(e.getX(), e.getY());
                mouseReleased = true;
                repaint();
                grabFocus();
            }
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    isAreaChosen = true;
                    parent.recognizeImage(parent.uploadedImage, startChoosingPoint.x, startChoosingPoint.y, curPoint.x, curPoint.y);
                    startChoosingPoint = null;
                    curPoint = null;
                    parent.image = ImageService.copyImage(parent.uploadedImage);
                    repaint();
                }

            }
        });
    }

    public void paint(Graphics g) {
        wid = getWidth();
        hei = getHeight();
        if (parent.image != null) {
            parent.image = ImageService.copyImage(parent.uploadedImage);
            if (startChoosingPoint != null && curPoint != null && !startChoosingPoint.equals(curPoint)) {
                for (int x = startChoosingPoint.x; x != curPoint.x; x += 1 * Math.signum(curPoint.x - startChoosingPoint.x)) {
                    for (int y = startChoosingPoint.y; y != curPoint.y; y += 1 * Math.signum(curPoint.y - startChoosingPoint.y)) {
                        Color color = new Color(parent.image.getRGB(x, y));
                        Color newColor = new Color(0, 0, color.getBlue());
                        parent.image.setRGB(x, y, newColor.getRGB());
                    }
                }
            }

            g.clearRect(0, 0, wid, hei);
            int iwid = parent.image.getWidth();
            int ihei = parent.image.getHeight();
            g.drawImage(parent.image, 0, (hei - ihei * wid / iwid) / 2, wid, ihei * wid / iwid, null);
        }
    }
}

class PatternMenuItem extends JMenuItem{
    private String pattern;

    public PatternMenuItem(String pattern, String name, GUI parent){
        super(name);
        this.pattern = pattern;
        this.addActionListener((e) -> {
            parent.pattern = getPattern();
        });
    }

    public ItemType[] getPattern(){
        ItemType[] ret = new ItemType[pattern.length()];
        for(int i = 0; i < pattern.length(); i++){
            switch(pattern.charAt((i))) {
                case 'D':
                    ret[i] = ItemType.DIGIT;
                    break;
                case 'L':
                    ret[i] = ItemType.LETTER;
                    break;
            }
        }
        return ret;
    }
}