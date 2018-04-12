package model;

import java.awt.Color;
import java.awt.Graphics2D;

public class DrawableParticle {
  public DrawableParticle(int x, int y,int r, Color c) {
    this.x = x;
    this.y = y;
    this.r= r;
    this.c = c;
  }
  int x;
  int y;
  int r;
  Color c;
  public void draw(Graphics2D g){
    g.setColor(c);
    g.drawOval(x-r, y-r, r*2, r*2);
  }
}
