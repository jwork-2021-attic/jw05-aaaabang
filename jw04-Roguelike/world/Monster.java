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
package world;

import java.util.Random;
import java.util.concurrent.*;

import java.awt.Color;
import configuration.*;

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
                randomStep();
                TimeUnit.SECONDS.sleep(Configure.monsterSpeed);
            } catch (InterruptedException e) {
                
                e.printStackTrace();
            }
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


}
