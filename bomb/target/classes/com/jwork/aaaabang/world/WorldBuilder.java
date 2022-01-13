package com.jwork.aaaabang.world;

import java.util.Random;

/*
 * Copyright (C) 2015 s-zhouj
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
/**
 *
 * @author s-zhouj
 */
public class WorldBuilder {

    private int width;
    private int height;
    private Tile[][] tiles;

    public WorldBuilder(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
    }

    public World build() {
        return new World(tiles);
    }

    public World genrateMap(){
        tiles = new Tile[width][height];
        Random rand = new Random(4018);
        int wallNum = 5;
        for (int width = 0; width < this.width; width++) {
            for (int height = 0; height < this.height; height++) {
                if(width == 0 || width == this.width-1 || height == 0 || height == this.height - 1){
                    tiles[width][height] = Tile.BOUNDS;
                    continue;
                }
                if (rand.nextInt(wallNum)==1)
                    tiles[width][height] = Tile.WALL;
                else
                    tiles[width][height] = Tile.FLOOR;
            }
        }

        return new World(tiles);
    }
    private WorldBuilder randomizeTiles() {
        for (int width = 0; width < this.width; width++) {
            for (int height = 0; height < this.height; height++) {
                if(width == 0 || width == this.width-1 || height == 0 || height == this.height - 1){
                    tiles[width][height] = Tile.BOUNDS;
                    continue;
                }
                Random rand = new Random();
                //以45%的概率生成墙
                if(rand.nextInt(20) < 9) {
                    tiles[width][height] = Tile.WALL;
                }
                else
                {
                    tiles[width][height] = Tile.FLOOR;
                }
            }
        }
        tiles[width-2][height-2] = Tile.GOAL;
        return this;
    }

    private  WorldBuilder smooth(int factor){
        Tile[][] newtemp = new Tile[width][height];
        if (factor > 1) {
                smooth(factor - 1);
            }
        for (int width = 0; width < this.width; width++) {
            for (int height = 0; height < this.height; height++) {
                if(width == 0 || width == this.width-1 || height == 0 || height == this.height - 1){
                    newtemp[width][height] = Tile.BOUNDS;
                    continue;
                }
                
                //Surrounding walls and floor
                int surrwalls = 0;
                int surrfloor = 0;
                //Check the tiles in a 3x3 area around center tile
                for (int dwidth = -1; dwidth < 2; dwidth++) {
                    for (int dheight = -1; dheight < 2; dheight++) {
                        if (width + dwidth < 0 || width + dwidth >= this.width || height + dheight < 0
                                || height + dheight >= this.height) {
                            continue;
                        } else if (tiles[width + dwidth][height + dheight] == Tile.FLOOR) {
                            surrfloor++;
                        } else if (tiles[width + dwidth][height + dheight] != Tile.FLOOR) {
                            surrwalls++;
                        }
                    }
                }
                

                Tile replacement = tiles[width][height];
                if (replacement == Tile.WALL){                        // 取得元素
                    replacement = (surrwalls >= 5)? Tile.WALL : Tile.FLOOR; // 如果当前元素是墙，那么周围超过 5 个墙就还保持为墙 
                }
                else
                    replacement = (surrwalls >= 5) ? Tile.WALL : Tile.FLOOR;

                // if (surrwalls < surrfloor) {
                //     replacement = Tile.FLOOR;
                // } else {
                //     replacement = Tile.WALL;
                // }
                newtemp[width][height] = replacement;
            }
            
        }
        tiles = newtemp;
        newtemp[width-2][height-2] = Tile.GOAL;
        
        return this;
        
    }



    public WorldBuilder makeCaves() {
        return randomizeTiles().smooth(4);
    }
}
