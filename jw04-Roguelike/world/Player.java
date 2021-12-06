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
package world;

import java.awt.Color;
import java.util.List;

/**
 *
 * @author Aeranythe Echosong
 */
public class Player extends Creature {

    private List<String> messages;

    public Player(World world, char glyph, Color color, int maxHP, int attack, int defense, int visionRadius,List<String> messages) {
        super(world, glyph, color, maxHP, attack, defense, visionRadius);
        this.messages = messages;
    }
    @Override
    public void run(){
        
    }

    @Override
    public synchronized int moveBy(int mx, int my) {
        Tile tile = world.tile(x + mx, y + my);
        if(tile == Tile.GOAL){
            onEnter(x + mx, y + my, world.tile(x + mx, y + my));
            return 1;
        }

        Creature other = world.creature(x + mx, y + my);

        if (other == null) {
            onEnter(x + mx, y + my, world.tile(x + mx, y + my));
        } else {
            this.getAttack(other.attackValue()-this.defenseValue());
        }
        return 0;
        
        
    }

    @Override
    public void getAttack(int damage) {
        
        messages.clear();

        this.modifyHP(-damage);

        this.notify("The monster attacks you for %d damage.", damage);
    }
   

    public void onEnter(int x, int y, Tile tile) {
        if (tile.isGround()) {
            setX(x);
            setY(y);
        } else if (tile.isDiggable()) {
            dig(x, y);
        }
    }

    public void onNotify(String message) {
        this.messages.add(message);
    }
}
