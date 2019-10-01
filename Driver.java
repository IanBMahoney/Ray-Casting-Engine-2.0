/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RayCastingEngine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author imahone
 */
public class Driver {

    Display display;
    Input input;
    Initializer init;
    Boolean running = false;
    int time, timeAtLastCheck;
    int frames, fps;
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    BufferedImage minimap = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    ;
    final int HORIZON = 400;
    final double RADIUS = 30;
    int width, height;
    double[] origin = new double[3]; //x, y, theta

    final boolean MINIMAP = true;

    double vel = .2, vela = 1, xVar, yVar;

    ArrayList<Wall> walls = new ArrayList<>();
    ArrayList<Wall> currentWalls = new ArrayList<>();

    public Driver() {
        System.out.println("Starting Driver...");

        width = 1000;
        height = 800;

        this.display = new Display(this, width, height);
        this.input = display.input;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        minimap = new BufferedImage(height / 4, height / 4, BufferedImage.TYPE_INT_ARGB);

        init = new Initializer(this);

        walls = init.Initialize(7);

        origin[0] = init.playerStartX;
        origin[1] = init.playerStartY;

        Thread t1 = new Thread(input);
        t1.start();
        display.addKeyListener(input);
        display.requestFocus();
        timeAtLastCheck = (int) System.currentTimeMillis();
        running = true;
        run();

        // new TestDriver(walls);
    }

    public void run() {
        while (running) {
            time = (int) (System.currentTimeMillis());
            if ((time - timeAtLastCheck) >= 1000) {
                fps = frames / ((time - timeAtLastCheck) / 1000);
                //System.out.println("FPS: " + fps);
                frames = 0;
                timeAtLastCheck = time;
            }
            tick();
            render();
            keyInput();
            frames += 1;
        }
    }

    public void tick() {
        try {
            Thread.sleep(10);
        } catch (Exception e) {

        }
    }

    public void render() {
        Graphics g = image.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.WHITE);
        g.fillRect(0, height / 2, width, height / 2);
        g.drawString(" X: " + origin[0] + " Y: " + origin[1] + " Angle: " + origin[2] + " FPS: " + fps, 10, 20);
        if ((frames % 1 == 0) && MINIMAP) {
            try {
                g = minimap.getGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, minimap.getWidth(), minimap.getHeight());
//                double multiplier = (double) minimap.getHeight() * 6 / 8 / init.size;
                double multiplier = (double) minimap.getHeight() * 7 / 8 / (RADIUS * 2);
                g.setColor(Color.BLACK);
                for (Wall wall : walls) {
                    try {
                        if (circleIntersect(origin[0], origin[1], RADIUS, wall.startX, wall.startY, wall.endX, wall.endY, wall.texture) != null) {
                            currentWalls.add(circleIntersect(origin[0], origin[1], RADIUS, wall.startX, wall.startY, wall.endX, wall.endY, wall.texture));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < currentWalls.size(); i += 1) {

                    g.drawLine((int) Math.round((currentWalls.get(i).startX - origin[0]) * multiplier + (minimap.getHeight() / 2)), (int) Math.round((currentWalls.get(i).startY - origin[1]) * multiplier + (minimap.getHeight() / 2)), (int) Math.round((currentWalls.get(i).endX - origin[0]) * multiplier + (minimap.getHeight() / 2)), (int) Math.round((currentWalls.get(i).endY - origin[1]) * multiplier + (minimap.getHeight() / 2)));
                }
                g.setColor(Color.red);
                g.drawArc((int) Math.round(origin[0] * multiplier), (int) Math.round(origin[1] * multiplier), (int) Math.round(RADIUS * 2 * multiplier), (int) Math.round(RADIUS * 2 * multiplier), 0, 360);

                int[] xPoints = {
                    (int) Math.round((10 * Math.cos(Math.toRadians(origin[2]))) + (origin[0] * multiplier)),
                    (int) Math.round((5 * Math.cos(Math.toRadians(origin[2] + 130))) + (origin[0] * multiplier)),
                    (int) Math.round((5 * Math.cos(Math.toRadians(origin[2] - 130))) + (origin[0] * multiplier))
                };

                int[] yPoints = {
                    (int) Math.round((10 * Math.sin(Math.toRadians(origin[2]))) + (origin[1] * multiplier)),
                    (int) Math.round((5 * Math.sin(Math.toRadians(origin[2] + 130))) + (origin[1] * multiplier)),
                    (int) Math.round((5 * Math.sin(Math.toRadians(origin[2] - 130))) + (origin[1] * multiplier))
                };
                g.drawPolygon(xPoints, yPoints, 3);
            } catch (Exception e) {

            }
        }
        display.repaint();
        currentWalls.clear();
        g.dispose();
    }

    public void keyInput() {

        if (input.d) {
            origin[2] += vela;
        }
        if (input.a) {
            origin[2] -= vela;
        }
        if (origin[2] >= 360) {
            origin[2] -= 360;
        }
        if (origin[2] < 0) {
            origin[2] += 360;
        }

        int[][] signs = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        if (input.w) {
            origin[0] += signs[(int) ((origin[2]) / 360)][0] * vel * Math.cos(Math.toRadians(origin[2]));
            origin[1] += signs[(int) ((origin[2]) / 360)][1] * vel * Math.sin(Math.toRadians(origin[2]));
        }
        if (input.s) {
            origin[0] -= signs[(int) ((origin[2]) / 360)][0] * vel * Math.cos(Math.toRadians(origin[2]));
            origin[1] -= signs[(int) ((origin[2]) / 360)][1] * vel * Math.sin(Math.toRadians(origin[2]));
        }

    }

    public Wall circleIntersect(double h, double k, double r, double x1, double y1, double x2, double y2, int texture) {
        if (x1 > x2) {
            double temp = x2;
            x2 = x1;
            x1 = temp;
            temp = y2;
            y2 = y1;
            y1 = temp;
        }

        boolean e1 = false, e2 = false;
        double lA = y1 - y2;
        double lB = x2 - x1;
        double lC = 1 * (x1 * y2 - x2 * y1);
//        System.out.println(x1 + ", " + y1 + ", " + x2 + ", " + y2);
//        System.out.println(lA + ", " + lB + ", " + lC);

        double t = Math.abs(lA * h + lB * k + lC) / (Math.sqrt(lA * lA + lB * lB));

        if (t < r) {

            double cA = -2 * h;
            double cB = -2 * k;
            double cC = h * h + k * k - r * r;

            double qA = 1 + (lA * lA) / (lB * lB);
            double qB = cA + ((2 * lC * lA) / (lB * lB)) - (lA * cB / lB);
            double qC = ((lC * lC) / (lB * lB)) - (cB * lC / lB) + cC;

            double xI1 = (-qB + Math.sqrt(qB * qB - 4 * qA * qC)) / (2 * qA);
            double xI2 = (-qB - Math.sqrt(qB * qB - 4 * qA * qC)) / (2 * qA);

            double yI1 = (-lA * xI1 - lC) / lB;
            double yI2 = (-lA * xI2 - lC) / lB;

            if (xI1 > xI2) {
                double temp = xI2;
                xI2 = xI1;
                xI1 = temp;
                temp = yI2;
                yI2 = yI1;
                yI1 = temp;
            }

            if (((!(xI1 > x1 && xI1 < x2)) && ((Math.sqrt(Math.pow(x1 - h, 2)) + Math.sqrt(Math.pow(y1 - k, 2))) > r)) && ((!(xI2 > x1 && xI2 < x2)) && ((Math.sqrt(Math.pow(x2 - h, 2)) + Math.sqrt(Math.pow(y2 - k, 2))) > r))) {
                //System.out.println("False");
                return null;
            }

//            if (x1<xI1) {
//                xI1 = x1;
//                yI1 = y1;
//            } 
//            if (x2<xI2){
//                xI2 = x2;
//                yI2 = y2;
//            }
            if ((Math.sqrt(Math.pow(x1 - h, 2) + Math.pow(y1 - k, 2))) < r) {
                xI1 = x1;
                yI1 = y1;
            }
            if ((Math.sqrt(Math.pow(x2 - h, 2) + Math.pow(y2 - k, 2))) < r) {
                xI2 = x2;
                yI2 = y2;
            }
            //System.out.println("True");
            return new Wall(xI1, yI1, xI2, yI2, texture, true);
            //System.out.println(xI1 + ", " + yI1 + ", " + xI2 + ", " + yI2);
        } else if (t == r) {

        } else {
            //System.out.println("false");
        }
        //System.out.println("FALSE");
        return null;
    }

    public static void main(String args[]) {
        new Driver();
    }
}
