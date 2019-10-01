/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RayCastingEngine;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics;
import javax.swing.JFrame;

/**
 *
 * @author imahone
 */
public class Display extends Canvas {

    Driver driver;
    Input input;
    int width, height;

    public Display(Driver driver, int width, int height) {
        this.driver = driver;
        this.width = width;
        this.height = height;
        JFrame frame = new JFrame("Ray Casting");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLayout(new BorderLayout());
        frame.add(this, BorderLayout.CENTER);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.pack();
        frame.setLocationRelativeTo(null);
        this.input = new Input(this);
    }

    public void paint(Graphics g) {
        g.drawImage(driver.image, 0, 0, this);
        g.drawImage(driver.minimap, 3 * width / 4, 0, this);
    }
}
