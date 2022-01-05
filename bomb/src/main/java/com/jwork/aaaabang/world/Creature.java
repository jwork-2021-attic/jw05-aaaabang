/*
 * Copyright (C) 2015 Aeranythe Echosong
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jwork.aaaabang.world;


import java.awt.Color;


/**
 *
 * @author Aeranythe Echosong
 */
public class Creature implements Runnable,java.io.Serializable{

    protected World world;
    protected int x;
    protected int y;
    protected char glyph;
    protected Color color;
    protected int maxHP;
    protected int hp;
    protected int attackValue;
    protected int defenseValue;
    public void setX(int x) {
        this.x = x;
    }
    public int x() {
        return x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int y() {
        return y;
    }
    public char glyph() {
        return this.glyph;
    }
    public Color color() {
        return this.color;
    }
    public void setColor (Color color){
        this.color = color;
    }
    public int maxHP() {
        return this.maxHP;
    }
    public int hp() {
        return this.hp;
    }
    public synchronized void modifyHP(int amount) {
        this.hp += amount;

        if (this.hp < 1) {
            world.remove(this);
        }
    }

    public boolean isAlive(){
        if (this.hp < 1) {
            world.remove(this);
            return false;
        }
        return true;
    }
    public int attackValue() {
        return this.attackValue;
    }
    

    public int defenseValue() {
        return this.defenseValue;
    }

    private int visionRadius;

    public int visionRadius() {
        return this.visionRadius;
    }

    public Tile tile(int wx, int wy) {
        return world.tile(wx, wy);
    }

    public void dig(int wx, int wy) {
        world.dig(wx, wy);
    }

    public synchronized int moveBy(int mx, int my) {
        return 0;
    }

    // public synchronized void attack(Creature other) {
    //     int damage = Math.max(0, this.attackValue() - other.defenseValue());
    //     //damage = (int) (Math.random() * damage) + 1;

    //     other.modifyHP(-damage);

    //     this.notify("You attack the '%s' for %d damage.", other.glyph, damage);
    //     other.notify("The '%s' attacks you for %d damage.", glyph, damage);
    // }

    public void getAttack(int damage) {
        
        this.modifyHP(-damage);
        this.notify("you get attacked for %d damage.", damage);
    }

    public void update() {
        this.onUpdate();
    }

    public boolean canEnter(int x, int y) {
        return world.tile(x, y).isGround();
    }

    public void notify(String message, Object... params) {
        this.onNotify(String.format(message, params));
    }

    public Creature(World world, char glyph, Color color, int maxHP, int attack, int defense, int visionRadius) {
        this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.maxHP = maxHP;
        this.hp = maxHP;
        this.attackValue = attack;
        this.defenseValue = defense;
        this.visionRadius = visionRadius;
    }

    public void onEnter(int x, int y, Tile tile) {

    }

    public void onUpdate() {
    }

    public void onNotify(String message) {
    }

    public boolean canSee(int x, int y) {
        if ((x() - x) * ( x() - x) + ( y() - y) * ( y() - y) >  visionRadius()
                *  visionRadius()) {
            return false;
        }
        // for (Point p : new Line( x(),  y(), x, y)) {
        //     if ( tile(p.x, p.y).isGround() || (p.x == x && p.y == y)) {
        //         continue;
        //     }
        //     return false;
        // }
        return true;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

    
}
