/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RayCastingEngine;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 *
 * @author imahone
 */
public class Initializer extends Canvas {
    int levelWidth=0, levelHeight=0;
    int size = 1;
    Driver driver;
    ArrayList<Wall> walls;
    boolean[][] checked;
    double playerStartX = 0, playerStartY = 0;

    public Initializer(Driver driver) {
        this.driver = driver;

//        JFrame frame = new JFrame("Playground");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(1000, 1000);
//        frame.setLayout(new BorderLayout());
//        frame.add(this);
//        frame.setResizable(true);
//        frame.setVisible(true);
//        frame.pack();
//        frame.setLocationRelativeTo(null);
    }

    public ArrayList<Wall> Initialize(int levelSelect) {

        System.out.println("Begining Initilization...");

        int fileCount = 0;
        BufferedImage level = null;

        try {
            fileCount = new File("src/main/java/levels").listFiles().length;
            //int fileCount = getClass().getResourceAsStream("/Levels/")).listFiles().length;
        } catch (Exception e) {
            System.out.println("No Levels Found...  (" + e.toString() + ")");
            return null;
        }

//        if (levelSelect > fileCount) {
//            System.out.println("Initialization Unsuccessful: There are only " + fileCount + " Levels, not " + levelSelect);
//            return;
//        }
        try {
            level = ImageIO.read(new File("src/main/java/levels/" + levelSelect + ".png"));
        } catch (IOException e) {
            System.out.println("Initialization Unsuccessful: Cannot Retreive Level " + levelSelect + " (" + e.toString() + ")");
            return null;
        }
        int[][] rgb = new int[level.getWidth()][level.getHeight()];
        
        for (int y = 0; y < level.getHeight(); y += 1) {
            for (int x = 0; x < level.getWidth(); x += 1) {
                rgb[x][y] = level.getRGB(x, y); // white = -1, black = -16777216, red = -65536
                //System.out.println(x + ", " + y + ", " + rgb[x][y]);
                if (rgb[x][y] == -65536) {
                    playerStartX = x;
                    playerStartY = y;
                }
            }
        }
        size = level.getWidth();
        levelWidth = level.getWidth();
        levelHeight = level.getHeight();
        
        checked = new boolean[level.getWidth()][level.getHeight()];
        walls = new ArrayList<>();
        System.out.println("Total Walls: " + getBorderingWalls((int) Math.round(playerStartX), (int) Math.round(playerStartY), rgb));
        combineWalls();
        renderWalls(level);
//        scale(100);
//        size = level.getWidth() * 100;
//        levelWidth = level.getWidth() * 100;
//        levelHeight = level.getHeight() * 100;
        rotateWalls(Math.PI/4);

        if (fileCount == 1) {
            System.out.println("Initialization Successful: " + fileCount + " Level Found, Level " + levelSelect + " Loaded");
            return walls;
        } else {
            System.out.println("Initialization Successful: " + fileCount + " Levels Found, Level " + levelSelect + " Loaded");
            return walls;
        }

    }

    int total = 0;

    public long getBorderingWalls(int x, int y, int[][] array) {
        long runningTotal = 0;
        int width = array.length;
        int height = array[0].length;

        int[][] surroundings = {{x + 1, y}, {x - 1, y}, {x, y + 1}, {x, y - 1}};
        for (int i = 0; i < 4; i += 1) {
            //System.out.println(x + ", " + y + ", " + surroundings[i][0] + ", " + surroundings[i][1] + ", " + i);

            if (surroundings[i][0] >= 0 && surroundings[i][0] < width && surroundings[i][1] >= 0 && surroundings[i][1] < height) {

                if (!checked[surroundings[i][0]][surroundings[i][1]]) {
                    //System.out.println(surroundings[i][0] + ", " + surroundings[i][1]);

                    if (array[surroundings[i][0]][surroundings[i][1]] == -1) {
                        runningTotal += 1;
                        //checked[surroundings[i][0]][surroundings[i][1]] = true;
                        if (i == 0) {//Right
                            walls.add(new Wall(x + 1, y, x + 1, y + 1, 0, false));

                        } else if (i == 1) {//Left
                            walls.add(new Wall(x, y, x, y + 1, 0, false));

                        } else if (i == 2) {//Bottom
                            walls.add(new Wall(x, y + 1, x + 1, y + 1, 0, true));

                        } else if (i == 3) {//Top
                            walls.add(new Wall(x, y, x + 1, y, 0, true));

                        } else {
                            //System.out.println("Error");
                        }
                        //repaint();
                        //System.out.println(walls.size());

                    } else if ((array[surroundings[i][0]][surroundings[i][1]] == -16777216) || (array[surroundings[i][0]][surroundings[i][1]] == -65536)) {

//                        
                        checked[surroundings[i][0]][surroundings[i][1]] = true;

                        runningTotal += getBorderingWalls(surroundings[i][0], surroundings[i][1], array);
                    } else {
                        //System.out.println("Error");
                    }

                } else {
                    //System.out.println("Already Checked");
                }

            } else {
                //System.out.println("Outside of Area");
            }
        }

        return runningTotal;

    }

    public void renderWalls(BufferedImage level) {
        final int MULT = 10;
        BufferedImage output = new BufferedImage((level.getWidth() + 1) * MULT, (level.getHeight() + 1) * MULT, BufferedImage.TYPE_INT_ARGB);
        Graphics g = output.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, output.getWidth(), output.getHeight());
//        g.setColor(Color.LIGHT_GRAY);
//        for (int y = 0; y < output.getHeight(); y += 10){
//            g.drawLine(0, y, output.getWidth(), y);
//        }
//        for (int x = 0; x < output.getWidth(); x += 10){
//            g.drawLine(x, 0, x, output.getHeight());
//        
//        }
        for (int i = 0; i < walls.size(); i += 1) {
            g.setColor(new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
            //g.setColor(Color.BLACK);
            g.drawLine((int) Math.round(walls.get(i).startX * MULT), (int) Math.round(walls.get(i).startY * MULT), (int) Math.round(walls.get(i).endX * MULT), (int) Math.round(walls.get(i).endY * MULT));
        }

        try {
            ImageIO.write(output, "PNG", new File("src/output.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void combineWalls() {
        ArrayList<Wall> longWalls = new ArrayList<>();
        boolean done = true;
        do {
            done = true;
            forLoops:
            for (int i = 0; i < walls.size(); i += 1) {
                for (int j = 0; j < walls.size(); j += 1) {
                    if ((i) != (j)) {
                        if ((walls.get(i).horizontal == walls.get(j).horizontal) && (walls.get(i).texture == walls.get(j).texture)) {
                            if ((walls.get(i).endX == walls.get(j).startX) && (walls.get(i).endY == walls.get(j).startY)) {
                                walls.add(new Wall(walls.get(i).startX, walls.get(i).startY, walls.get(j).endX, walls.get(j).endY, walls.get(i).texture, walls.get(i).horizontal));
                                if (i > j) {
                                    walls.remove(i);
                                    walls.remove(j);
                                } else {
                                    walls.remove(j);
                                    walls.remove(i);

                                }
                                //walls.addAll(longWalls);
                                done = false;
                                break forLoops;

                            }
                        }
                    }
                }
            }
        } while (!done);
        System.out.println("Final Number of Walls: " + walls.size());
    }
    
    public void rotateWalls(double angle){ //angle in radians
        double h;
        double theta;
        for (Wall wall: walls) {
            h = Math.sqrt(Math.pow(wall.startY, 2) + Math.pow(wall.startX, 2));
            theta = Math.asin(wall.startY/h);
            wall.startY = (Math.sin(theta + angle)*h);
            wall.startX = (Math.cos(theta + angle)*h) + levelWidth/2;
            
            h = Math.sqrt(Math.pow(wall.endY, 2) + Math.pow(wall.endX, 2));
            theta = Math.asin(wall.endY/h);
            wall.endY =  (Math.sin(theta + angle)*h);
            wall.endX =  (Math.cos(theta + angle)*h) + levelWidth/2;    
        }
            h = Math.sqrt(Math.pow(playerStartY, 2) + Math.pow( playerStartX, 2));
            theta = Math.asin(playerStartY/h);
            playerStartY = (Math.sin(theta + angle)*h);
            playerStartX = (Math.cos(theta + angle)*h) + levelWidth/2;
            
           
        
        
    }
    
    public void scale(int factor) {
        for (Wall wall: walls) {
            
            wall.startY *= factor;
            wall.startX *= factor;
            wall.endY *= factor;
            wall.endX *= factor;
            
        }
    }
}
