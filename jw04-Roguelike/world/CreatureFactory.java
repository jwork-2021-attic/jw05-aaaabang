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

import java.util.List;
import java.util.concurrent.*;

import asciiPanel.AsciiPanel;

/**
 *
 * @author Aeranythe Echosong
 */
public class CreatureFactory {

    private World world;
    ExecutorService exec;

    public CreatureFactory(World world) {
        this.world = world;
        exec = Executors.newCachedThreadPool();
    }

    public Creature newPlayer(List<String> messages) {
        Player player = new Player(this.world, (char)2, AsciiPanel.brightWhite, 100, 0, 5, 9,messages);

        world.setEntry(player);
        
        //exec.execute(player);
        return player;
    }

    public Creature newFungus() {
        Monster fungus = new Monster(this.world, (char)1, AsciiPanel.green, 10, 10, 0, 0,this);
        world.addAtEmptyLocation(fungus);
        
        exec.execute(fungus);
        return fungus;
    }
}
