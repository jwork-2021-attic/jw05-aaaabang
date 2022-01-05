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

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.*;

import com.jwork.aaaabang.configuration.Configure;

/**
 *
 * @author Aeranythe Echosong
 */
public class CreatureFactory implements java.io.Serializable{

    private transient World world;
    transient ExecutorService exec;

    public CreatureFactory(World world) {
        this.world = world;
        exec = Executors.newCachedThreadPool();
    }

    public Player newPlayer(List<String> messages) {
        Player player = new Player(this.world, (char)2, Configure.playeColor, 100, 0, 5, 9,messages);

        world.addAtEmptyLocation(player);
        
        //exec.execute(player);
        return player;
    }

    public Monster newFungus() {
        Monster fungus = new Monster(this.world, (char)1, Configure.monsterColor, Configure.monsterHp, 10, 0, 6,this);
        world.addAtEmptyLocation(fungus);
        
        exec.execute(fungus);
        return fungus;
    }
}
