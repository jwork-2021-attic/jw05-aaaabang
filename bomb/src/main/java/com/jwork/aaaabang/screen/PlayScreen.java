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
package com.jwork.aaaabang.screen;



import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


import javax.xml.namespace.QName;

import com.jwork.aaaabang.asciiPanel.AsciiPanel;
import com.jwork.aaaabang.configuration.*;
import com.jwork.aaaabang.world.*;
import java.io.*;




/**
 *
 * @author Aeranythe Echosong
 */
public class PlayScreen implements Screen {

    private World world;
    private Player player;
    private int screenWidth;
    private int screenHeight;
    private List<String> messages;
    private List<String> oldMessages;

    public PlayScreen(int keyType) {
        this.screenWidth = Configure.GameSize;
        this.screenHeight = Configure.GameSize;
        
        this.messages = new ArrayList<String>();
        this.oldMessages = new ArrayList<String>();
        createWorld();
        switch (keyType) {
            case KeyEvent.VK_ENTER:
                newGame();
                break;
            case KeyEvent.VK_SPACE:
                loadGame();
                break;
        }
        
    }
    private void loadGame(){
        try(
            // 创建一个ObjectInputStream输入流
            ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("object.txt")))
        {
            // 从输入流中读取一个Java对象，并将其强制类型转换为World类
            this.world = (World)ois.readObject();
            player = world.getPlayer();
            ExecutorService exec = Executors.newCachedThreadPool();
            for(Creature c : world.getCreatures()){
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                exec.execute(c);
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    private void newGame(){
       
        
        CreatureFactory creatureFactory = new CreatureFactory(this.world);
        createCreatures(creatureFactory);
    }

    private void createCreatures(CreatureFactory creatureFactory) {
        this.player = creatureFactory.newPlayer(this.messages);
        this.world.setPlayer(this.player);
        for (int i = 0; i < Configure.monstersCnt; i++) {
            creatureFactory.newFungus();
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createWorld() {
        world = new WorldBuilder(this.screenWidth, this.screenHeight).makeCaves().build();
    }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        // Show terrain
        for (int x = 0; x < screenWidth; x++) {
            for (int y = 0; y < screenHeight; y++) {
                int wx = x + left;
                int wy = y + top;

                if (player.canSee(wx, wy)) {
                    terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy));
                } else {
                    terminal.write((char)0, x, y, Color.DARK_GRAY);
                }
            }
        }

        

        // Show creatures
        for (Creature creature : world.getCreatures()) {
            if (creature.x() >= left && creature.x() < left + screenWidth && creature.y() >= top
                    && creature.y() < top + screenHeight) {
                if (player.canSee(creature.x(), creature.y())) {
                    terminal.write(creature.glyph(), creature.x() - left, creature.y() - top, creature.color());
                }
            }
        }
        
        for(Bomb bomb: world.getBombs()) {
            terminal.write(bomb.glyph(), bomb.x() - left, bomb.y() - top, bomb.color());
        }

        // Creatures can choose their next action now
        world.update();
    }

    private void displayMessages(AsciiPanel terminal, List<String> messages) {
        
        int top = this.screenHeight ;
        int index = messages.size()-1;
        
        if(index >= 0)
            terminal.write(messages.get(index), 1, top + 2);
        
        //this.oldMessages.addAll(messages);
        //messages.clear();
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        // Terrain and creatures
        displayTiles(terminal, getScrollX(), getScrollY());
        // Player
        terminal.write(player.glyph(), player.x() - getScrollX(), player.y() - getScrollY(), player.color());
        // Stats
        String stats = String.format("%3d/%3d hp", player.hp(), player.maxHP());
        terminal.write(stats, 1, this.screenHeight+1);
        // Messages
        displayMessages(terminal, this.messages);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        int flag = 0;
        switch (key.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                flag = player.moveBy(-1, 0);
                break;
            case KeyEvent.VK_RIGHT:
                flag = player.moveBy(1, 0);
                break;
            case KeyEvent.VK_UP:
                flag = player.moveBy(0, -1);
                break;
            case KeyEvent.VK_DOWN:
                flag = player.moveBy(0, 1);
                break;
            case KeyEvent.VK_SPACE:
                player.putBomb();
                break;
            case KeyEvent.VK_ENTER:
                saveGame();
            
        }
        if(flag == 1){    
            return new WinScreen();
        }
        else if(flag == -1){
            return new LoseScreen();
        }
        else{
            return this;
        }
    }

    public void saveGame(){
        try(
            // 创建一个ObjectOutputStream输出流
            ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("object.txt")))
        {
            // 将player对象写入输出流
            //oos.writeObject(player);
            oos.writeObject(world);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public int getScrollX() {
        return Math.max(0, Math.min(player.x() - screenWidth / 2, world.width() - screenWidth));
    }

    public int getScrollY() {
        return Math.max(0, Math.min(player.y() - screenHeight / 2, world.height() - screenHeight));
    }

}
