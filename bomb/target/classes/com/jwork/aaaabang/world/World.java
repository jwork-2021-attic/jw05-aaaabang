package com.jwork.aaaabang.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
/**
 *
 * @author Aeranythe Echosong
 */
public class World implements java.io.Serializable{

    private Tile[][] tiles;
    private int width;
    private int height;
    private List<Creature> creatures;
    private List<Bomb> bombs;
    private Player player;
    private List<Player> players;
    public static final int TILE_TYPES = 2;

    public void setPlayer(Player player){
        this.player = player;
    }

    public void setPlayer(Player player,int id){
        this.player = player;
        players.add(id, player);
        creatures.add(player);
    }

    public Player getPlayer(){
        return this.player;
    }
    public List<Player> getPlayers(){
        return this.players;
    }
    public Player getPlayers(int id){
        return players.get(id);
    }
    public World(Tile[][] tiles) {
        this.tiles = tiles;
        this.width = tiles.length;
        this.height = tiles[0].length;
        this.creatures = new ArrayList<>();
        this.bombs = new ArrayList<>();
        this.players = new ArrayList<>();
    }

    public Tile tile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return Tile.BOUNDS;
        } else {
            return tiles[x][y];
        }
    }
    public Tile[][] getTiles(){
        return tiles;
    }
    public void setTile(Tile tile,int x,int y){
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return ;
        }

        tiles[x][y] = tile;
    }
    public char glyph(int x, int y) {
        return tiles[x][y].glyph();
    }

    public Color color(int x, int y) {
        return tiles[x][y].color();
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void dig(int x, int y) {
        if (tile(x, y).isDiggable()) {
            tiles[x][y] = Tile.FLOOR;
        }
    }

    public void addAtEmptyLocation(Creature creature) {
        int x;
        int y;
        
        do {
            x = (int) (Math.random() * this.width);
            y = (int) (Math.random() * this.height);
            
        } while ( this.creature(x, y) != null);

        tiles[x][y] = Tile.FLOOR;
        creature.setX(x);
        creature.setY(y);

        this.creatures.add(creature);
    }

    public void addAtEmptyLocation(Creature creature,int seed) {
        int x;
        int y;
        Random rand = new Random(seed);
        do {
            // x = (int) (rand.nextFloat() * this.width);
            // y = (int) (rand.nextFloat() * this.height);
            x = rand.nextInt(this.width);
            y = rand.nextInt(this.height);
        } while (this.tile(x,y) == Tile.BOUNDS || this.creature(x, y) != null);

        tiles[x][y] = Tile.FLOOR;
        creature.setX(x);
        creature.setY(y);

        this.creatures.add(creature);
    }

    public void setEntry(Creature creature){
        int x = 1;
        int y = 1;
        tiles[x][y] = Tile.FLOOR;
        // Tile entry = tiles[x][y];

        creature.setX(x);
        creature.setY(y);

        this.creatures.add(creature);
    }


    public synchronized Creature creature(int x, int y) {
        for (Creature c : this.creatures) {
            if (c.x() == x && c.y() == y) {
                return c;
            }
        }
        return null;
    }

    public List<Creature> getCreatures() {
        return this.creatures;
    }

    public void remove(Creature target) {
        this.creatures.remove(target);
    }

    public void remove(Bomb target) {
        this.bombs.remove(target);
    }

    public void update() {
        ArrayList<Creature> toUpdate = new ArrayList<>(this.creatures);

        for (Creature creature : toUpdate) {
            creature.update();
        }
    }

    public boolean isAvailablePos(int x,int y){
        Creature creature = this.creature(x,y);
        Tile tile = this.tile(x,y);
        if(creature != null)
            return false;
        if(tile == Tile.BOUNDS || tile == Tile.WALL)
            return false;
            
        return true;    
    }

    public List<Bomb> getBombs() {
        return this.bombs;
    }

    public void addBomb(Bomb bomb){
        this.bombs.add(bomb);
    }
}
