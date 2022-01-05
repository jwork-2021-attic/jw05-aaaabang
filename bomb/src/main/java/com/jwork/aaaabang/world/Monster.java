/*
 * Copyright (C) 2015 Winterstorm
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

import java.util.Random;
import java.util.concurrent.*;

import javax.sound.sampled.Port;

import com.jwork.aaaabang.configuration.Configure;

import java.awt.Color;
import java.awt.Point;

/**
 *
 * @author Winterstorm
 */
public class Monster extends Creature{

    private CreatureFactory factory;
    private int spreadcount = 0;

    public static int spores = 5;
    public static double spreadchance = 0.01;

    public Monster(World world, char glyph, Color color, int maxHP, int attack, int defense, int visionRadius, CreatureFactory factory) {
        super(world, glyph, color, maxHP, attack, defense, visionRadius);
        this.factory = factory;
    }

    @Override
    public void run(){
        while(hp > 1){
            
            try {
                Point playerPos = isSeePlayer();
                if(playerPos == null)
                    randomStep();
                else
                    rush(playerPos);
                TimeUnit.SECONDS.sleep(Configure.monsterSpeed);
            } catch (InterruptedException e) {
                
                e.printStackTrace();
            }
        }
    }

    private void rush(Point playerPos) {
        int px = playerPos.x;
        int py = playerPos.y;

        int disx = px - x;
        int disy = py - y;
        Random r = new Random();
        int res = r.nextInt(2);
        // if(disy == 0){
        //     if(disx > 0){
        //         moveBy(1, 0);
        //     }else if(disx < 0){
        //         moveBy(-1,0);
        //     }
        // }
        // if(disx == 0){
        //     if(disy > 0){
        //         moveBy(0, 1);
        //     }else if(disy < 0){
        //         moveBy(0,-1);
        //     }
        // }
        switch(res){
            case 0:
                if(disx > 0){
                    moveBy(1, 0);
                }else if(disx < 0){
                    moveBy(-1,0);
                }
                return;
            case 1:
                if(disy > 0){
                    moveBy(0, 1);
                }else if(disy < 0){
                    moveBy(0,-1);
                }
                return;
        }
    }

    public void onUpdate() {
        // if (this.spreadcount < MonsterAI.spores && Math.random() < MonsterAI.spreadchance) {
        //     spread();
        // }
    }

    @Override
    public synchronized void onEnter(int x, int y, Tile tile) {
        if (tile.isGround()) {
            setX(x);
            setY(y);
        } else if (tile.isDiggable()) {
            dig(x, y);
        }

        
    }

    @Override
    public int moveBy(int mx, int my) {
        Creature other = world.creature(x + mx, y + my);

        if (other == null) {
            onEnter(x + mx, y + my, world.tile(x + mx, y + my));
        } else {
            other.getAttack(this.attackValue()-other.defenseValue());
        }
        return 0;
    }

    public void randomStep(){
        Random r = new Random();
        int res = r.nextInt(4);
        switch(res){
            case 0:
                this.moveBy(-1, 0);
                //System.out.println(this + " move: " + "left");
                break;    
            case 1:
                this.moveBy(1, 0);
                //System.out.println(this + " move: " + "right");
                break;
            case 2:
                this.moveBy(0, -1);
                //System.out.println(this + " move: " + "up");
                break;
            case 3:
                this.moveBy(0, 1);
                //System.out.println(this + " move: " + "down");
                break;
        }
    }

    public Point isSeePlayer(){
        Tile[][] tiles = this.world.getTiles();

        

        for (int dwidth = -1; dwidth < 2; dwidth++) {
            for (int dheight = -1; dheight < 2; dheight++) {
                if (this.x+ dwidth < 0 || this.x + dwidth >= Configure.GameSize || this.y + dheight < 0
                        || this.y + dheight >= Configure.GameSize) {
                    continue;
                } 
                int index_x = this.x + dwidth;
                int index_y = this.y + dheight;
                if(this.canSee(index_x,index_y)){
                    Creature creature = this.world.creature(index_x, index_y);
                    if(creature instanceof Player)
                        return new Point(index_x, index_y);
                }

            }
        }
        
        return null;
    }

}
