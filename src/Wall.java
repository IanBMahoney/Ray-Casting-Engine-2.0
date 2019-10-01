/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RayCastingEngine;

/**
 *
 * @author imahone
 */
public class Wall {

    double startX, startY, endX, endY;
    int texture;
    boolean horizontal;

    public Wall(double startX, double startY, double endX, double endY, int texture, boolean horizontal) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.texture = texture;
        this.horizontal = horizontal;
    }
}
