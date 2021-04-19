import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;


import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class LinesComponent extends JComponent{

  private LinkedList<Line> lines = new LinkedList<Line>();
  private LinkedList<Pixel> pixels = new LinkedList<Pixel>();

  private static class Line {
    int x0;
    int x1;
    int y0;
    int y1;

    public Line(int x0, int x1, int y0, int y1) {
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
    }
  }

    private static class Pixel {
      final int x;
      final int y;

      public Pixel(int x, int y) {
          this.x = x;
          this.y = y;
      }
    }
    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      for (Pixel p : pixels) {
        g.setColor(Color.black);
        g.drawLine(p.x, p.y, p.x, p.y);
      }
    }

    public void addPixel(int x, int y) {
      pixels.add(new Pixel(x, y));
      repaint();
    }

    private void basicAlgorithm(int x0, int y0, int x1, int y1) {
      float dx = x1-x0;
      float dy = y1-y0;
      float x = x0;
      float y = y0;
      float m = dy / dx;
      float steps;
      if (Math.abs(dx) >= Math.abs(dy)) {
        steps = dx;
      } else {
        steps = dy;
      }
      dx = dx/steps;
      dy = dy/steps;
      for (int j = 0; j < Math.abs(steps); j++) {
        addPixel(Math.round(x), Math.round(y));
        x += dx;
        y += dy;
      }
    }

    private int inputLines(String file) {
      try {
        File text = new File(file);
        Scanner scan = new Scanner(text);
        int linesRead = 0;
        int x0 = 0;
        int x1 = 0;
        int y0 = 0;
        int y1 = 0;
        while(scan.hasNextInt()) {
          linesRead++;
          for (int i = 0; i < 4; i++) {
            switch (i) {
              case 0:
              x0 = scan.nextInt();
              break;
              case 1:
              x1 = scan.nextInt();
              break;
              case 2:
              y0 = scan.nextInt();
              break;
              case 3:
              y1 = scan.nextInt();
              break;
            }
          }
          lines.add(new Line(x0, x1, y0, y1));
          basicAlgorithm(x0, x1, y0, y1);
        }
        scan.close();
        return linesRead;
      } catch(FileNotFoundException e) {
        System.out.println("File not found");
        return 0;
      }
    }

    private void applyTransformation(String file) {
      try {
        File text = new File(file);
        Scanner scan = new Scanner(text);
        int linesRead = 0;
        double[][] matrix = new double[3][3];
        while(scan.hasNextDouble()) {
          for (int i = 0; i < 3; i++) {
            matrix[linesRead][i] = scan.nextDouble();
          }
          linesRead++;
        }
        for (Line l : lines) {
          int[] newline = new int[4];
          for (int c = 0; c < 2; c++) {
            int[] line = new int[3];
            if (c == 0) {
              line[0] = l.x0;
              line[1] = l.y0;
            } else {
              line[0] = l.x1;
              line[1] = l.y1;
            }
            line[2] = 1;
            double[] product = new double[3];
            for (int j = 0; j < 3; j++) {
              for (int k = 0; k < 3; k++) {
                product[j] += line[k] * matrix[k][j];
              }
            }
            if (c == 0) {
              newline[0] = (int)product[0];
              newline[1] = (int)product[1];
            } else {
              newline[2] = (int)product[0];
              newline[3] = (int)product[1];
            }
          }
          l.x0 = newline[0];
          l.x1 = newline[1];
          l.y0 = newline[2];
          l.y1 = newline[3];
          basicAlgorithm(l.x0, l.x1, l.y0, l.y1);
        }
      } catch(FileNotFoundException e) {
        System.out.println("File not found");
      }
    }

    private void displayPixels(int x0, int x1, int y0, int y1) {
      LinkedList<Pixel> toRemove = new LinkedList<Pixel>();
      for (Pixel p : pixels) {
        if (p.x < x0 || p.x > x1 || p.y < y0 || p.y > y1) {
          toRemove.add(p);
        }
      }
      pixels.removeAll(toRemove);
      repaint();
    }

    private void outputLines() {
      try {
        File output = new File("output.txt");
        output.createNewFile();
        FileWriter writer = new FileWriter("output.txt");
        for (Line l : lines) {
          writer.write(l.x0 + " " + l.x1 + " " + l.y0 + " " + l.y1 + "\n");
        }
        writer.close();
      } catch (IOException e) {
        System.out.println("File output failed");
      }
    }

    private void translate(int x, int y) {
      try {
        File output = new File("transform.txt");
        output.createNewFile();
        FileWriter writer = new FileWriter("transform.txt");
        writer.write("1 0 0\n0 1 0\n" + x + " " + y + " 1");
        writer.close();
      } catch (IOException e) {
        System.out.println("File output failed");
      }
    }
    private void basicScale(double x, double y) {
      try {
        File output = new File("transform.txt");
        output.createNewFile();
        FileWriter writer = new FileWriter("transform.txt");
        writer.write(x + " 0 0\n0 " + y + " 0\n0 0 1");
        writer.close();
      } catch (IOException e) {
        System.out.println("File output failed");
      }
    }
    private void rotate(int angle) {
      try {
        File output = new File("transform.txt");
        output.createNewFile();
        FileWriter writer = new FileWriter("transform.txt");
        writer.write(Math.cos(Math.toRadians(angle)) + " " + (-1 * Math.sin(Math.toRadians(angle))) + " 0\n" + Math.sin(Math.toRadians(angle)) + " " + Math.cos(Math.toRadians(angle)) + " 0\n 0 0 1");
        writer.close();
      } catch (IOException e) {
        System.out.println("File output failed");
      }
    }
    private void scale(double sx, double sy, int x, int y) {
      try {
        File output = new File("transform.txt");
        output.createNewFile();
        FileWriter writer = new FileWriter("transform.txt");
        writer.write(sx + " 0 0\n0 " + sy + " 0\n" + (-1 * x) + " " + (-1 * y) + " 1");
        writer.close();
      } catch (IOException e) {
        System.out.println("File output failed");
      }
    }

public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setSize(1400, 800);
    final LinesComponent comp = new LinesComponent();
    comp.setPreferredSize(new Dimension(1400,600));
    frame.getContentPane().add(comp, BorderLayout.CENTER);
    JPanel panel = new JPanel();
    panel.setPreferredSize(new Dimension(1400,200));
    JTextField field = new JTextField(50);
    field.setPreferredSize(new Dimension(300, 100));
    field.setFont(new Font("Calibri", Font.PLAIN, 24));
    JButton newLineButton = new JButton("New Line");
    newLineButton.setPreferredSize(new Dimension(160,80));
    newLineButton.setFont(new Font("Calibri", Font.PLAIN, 24));
    JButton fileInputButton = new JButton("Input File");
    fileInputButton.setPreferredSize(new Dimension(160,80));
    fileInputButton.setFont(new Font("Calibri", Font.PLAIN, 24));
    JButton applyTransformButton = new JButton("Apply Transformation");
    applyTransformButton.setPreferredSize(new Dimension(160,80));
    applyTransformButton.setFont(new Font("Calibri", Font.PLAIN, 24));
    JButton displayPixelsButton = new JButton("Display Pixels");
    displayPixelsButton.setPreferredSize(new Dimension(160,80));
    displayPixelsButton.setFont(new Font("Calibri", Font.PLAIN, 24));
    JButton fileOutputButton = new JButton("Output File");
    fileOutputButton.setPreferredSize(new Dimension(160,80));
    fileOutputButton.setFont(new Font("Calibri", Font.PLAIN, 24));
    JButton translateButton = new JButton("Translate");
    translateButton.setPreferredSize(new Dimension(160,80));
    translateButton.setFont(new Font("Calibri", Font.PLAIN, 24));
    JButton basicScaleButton = new JButton("Basic Scale");
    basicScaleButton.setPreferredSize(new Dimension(160,80));
    basicScaleButton.setFont(new Font("Calibri", Font.PLAIN, 24));
    JButton rotateButton = new JButton("Rotate");
    rotateButton.setPreferredSize(new Dimension(160,80));
    rotateButton.setFont(new Font("Calibri", Font.PLAIN, 24));
    JButton scaleButton = new JButton("Scale");
    scaleButton.setPreferredSize(new Dimension(160,80));
    scaleButton.setFont(new Font("Calibri", Font.PLAIN, 24));

    panel.add(field);
    panel.add(newLineButton);
    panel.add(fileInputButton);
    panel.add(applyTransformButton);
    panel.add(displayPixelsButton);
    panel.add(fileOutputButton);
    panel.add(translateButton);
    panel.add(basicScaleButton);
    panel.add(rotateButton);
    panel.add(scaleButton);
    frame.getContentPane().add(panel, BorderLayout.SOUTH);
    newLineButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            int x0 = (int) (Math.random()*1400);
            int x1 = (int) (Math.random()*1400);
            int y0 = (int) (Math.random()*600);
            int y1 = (int) (Math.random()*600);
            comp.lines.add(new Line(x0, x1, y0, y1));
            comp.basicAlgorithm(x0, x1, y0, y1);
        }
    });
    fileInputButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            comp.inputLines(field.getText());
        }
    });
    applyTransformButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            comp.applyTransformation(field.getText());
        }
    });
    displayPixelsButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            String numbers = field.getText();
            String n[] = numbers.split(" ");
            int x0 = Integer.parseInt(n[0]);
            int x1 = Integer.parseInt(n[1]);
            int y0 = Integer.parseInt(n[2]);
            int y1 = Integer.parseInt(n[3]);
            comp.displayPixels(x0, x1, y0, y1);
        }
    });
    fileOutputButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          comp.outputLines();
        }
    });
    translateButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          String numbers = field.getText();
          String n[] = numbers.split(" ");
          int x = Integer.parseInt(n[0]);
          int y = Integer.parseInt(n[1]);
          comp.translate(x, y);
        }
    });
    basicScaleButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          String numbers = field.getText();
          String n[] = numbers.split(" ");
          Double x = Double.parseDouble(n[0]);
          Double y = Double.parseDouble(n[1]);
          comp.basicScale(x, y);
        }
    });
    rotateButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          String n = field.getText();
          int angle = Integer.parseInt(n);
          comp.rotate(angle);
        }
    });
    scaleButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          String numbers = field.getText();
          String n[] = numbers.split(" ");
          double sx = Double.parseDouble(n[0]);
          double sy = Double.parseDouble(n[1]);
          int x = Integer.parseInt(n[2]);
          int y = Integer.parseInt(n[3]);
          comp.scale(sx, sy, x, y);
        }
    });
    frame.pack();
    frame.setVisible(true);
}

}
