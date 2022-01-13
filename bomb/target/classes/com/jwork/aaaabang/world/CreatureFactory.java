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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import javax.lang.model.util.ElementScanner6;

import com.jwork.aaaabang.configuration.Configure;

/**
 *
 * @author Aeranythe Echosong
 */
public class CreatureFactory implements java.io.Serializable{

    private transient World world;
    //transient ExecutorService exec;
    private boolean multiple;
    static int seedIndex = 0;
    private int[] seeds;
    public CreatureFactory(World world,boolean multiple,int[] seeds) {
        this.world = world;
        this.multiple = multiple;
        this.seeds = seeds;
        //exec = Executors.newCachedThreadPool();
    }

    public int getSeed(int index){
        return seeds[index];
    }

    public Player newPlayer(List<String> messages) {
        Player player = new Player(this.world, (char)2, Configure.playerColor, 100, 0, 5, 9,messages);

        world.addAtEmptyLocation(player);
       
        
        return player;
    }

    public Monster newFungus() {
        Monster fungus;
        if(multiple){
            fungus = new Monster(this.world, (char)1, Configure.monsterColor, Configure.monsterHp, 10, 0, 6,this,seeds[seedIndex]);
            world.addAtEmptyLocation(fungus,seeds[seedIndex]);
            ++seedIndex;
        }
        else{    
            fungus = new Monster(this.world, (char)1, Configure.monsterColor, Configure.monsterHp, 10, 0, 6,this);
            world.addAtEmptyLocation(fungus);
        }
        //exec.execute(fungus);
        return fungus;
    }

    public Player newPlayer(int id) {
        Player player;
        List<String> messages = new ArrayList<>();
        if(id == 0){
            player = new Player(this.world, (char)2, Configure.playerColor, 100, 0, 5, 9,messages,id);
            player.setXY(1, 1);
        }
        else if(id == 1){
            player = new Player(this.world, (char)2, Configure.player1Color, 100, 0, 5, 9,messages,id);
            player.setXY(1, 3);
        }
        else if(id == 2){
            player = new Player(this.world, (char)2, Configure.player2Color, 100, 0, 5, 9,messages,id);
            player.setXY(1, 5);
        }
        else
            return null;
        
        world.setPlayer(player, id);
        
        //exec.execute(player);
        return player;
    }
}
