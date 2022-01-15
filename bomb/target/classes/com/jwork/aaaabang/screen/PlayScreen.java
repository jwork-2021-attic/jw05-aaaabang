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

import javax.swing.plaf.basic.BasicTreeUI.TreeCancelEditingAction;
import javax.xml.namespace.QName;

import com.jwork.aaaabang.asciiPanel.AsciiPanel;
import com.jwork.aaaabang.configuration.*;
import com.jwork.aaaabang.nio.Client;
import com.jwork.aaaabang.nio.Server;
import com.jwork.aaaabang.thread.CreatureThread;
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

    private boolean gameStart;
    /*multiple game */
    private boolean multiple;
    private int numClient;
    private Client client;
    private Server server;
    private List<Player> players;
    public PlayScreen(int choice) throws IOException {
        this.screenWidth = Configure.GameSize;
        this.screenHeight = Configure.GameSize;
        
        this.messages = new ArrayList<String>();
        this.oldMessages = new ArrayList<String>();
        this.multiple = false;
        this.gameStart = false;
        createWorld();
        

        switch (choice) {
            case 0:
                newGame();
                break;
            case 1:
                loadGame();
                break;
            case 2:
                multiple = true;
                multipleGame();
                break;
        }
        
    }

    private void multipleGame() throws IOException {
        //启动客户端
        client = new Client(this);
        client.start();
        
        new Thread(client).start();
        int[] seeds = client.getSeeds();
        //生成玩家
        CreatureFactory creatureFactory = new CreatureFactory(this.world,multiple,seeds);
        Player player1 = creatureFactory.newPlayer(0);
        Player player2 = creatureFactory.newPlayer(1);
        Player player3 = creatureFactory.newPlayer(2);
        players = world.getPlayers();
        //生成怪兽
        // new Thread(new Runnable() {
        //     public void run(){
        //         createMonsters(creatureFactory);
        //     }
        // }).start();
        createMonsters(creatureFactory);
        //numClient = client.getNumClient();
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
        CreatureFactory creatureFactory = new CreatureFactory(this.world,multiple,null);
        this.player = creatureFactory.newPlayer(this.messages);
        this.world.setPlayer(this.player);
        createMonsters(creatureFactory);
    }

    private void createMonsters(CreatureFactory creatureFactory) {
        
        for (int i = 0; i < Configure.monstersCnt; i++) {
            creatureFactory.newFungus();
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createWorld() {
        world = new WorldBuilder(this.screenWidth, this.screenHeight).genrateMap();//makeCaves().build();
    }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        
        // Show terrain
        for (int x = 0; x < screenWidth; x++) {
            for (int y = 0; y < screenHeight; y++) {
                int wx = x + left;
                int wy = y + top;

                terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy));
                
            }
        }

        // Show creatures
        for (Creature creature : world.getCreatures()) {
            if (creature.x() >= 1 && creature.x() < screenWidth-1 && creature.y() >= 1
                    && creature.y() < screenHeight-1) {
                    terminal.write(creature.glyph(), creature.x(), creature.y(), creature.color());  
            }
        }
        
        for(Bomb bomb: world.getBombs()) {
            terminal.write(bomb.glyph(), bomb.x() - left, bomb.y() - top, bomb.color());
        }

        // Creatures can choose their next action now
        world.update();
    }

    private void displayMessages(AsciiPanel terminal, List<String> messages) {
        
        //show usage
        terminal.write("Enter - save game",screenWidth,2);
        

        int top = this.screenHeight ;
        int index = messages.size()-1;
        
        if(index >= 0)
            terminal.write(messages.get(index), 1, top + 2);
        
        //this.oldMessages.addAll(messages);
        //messages.clear();
    }

    private boolean startGame(){
        if(gameStart == false){
            new Thread(new CreatureThread(this.world)).start();
            gameStart = true;
            return true;//刚开始
        }
        return false;//已经开始了
    }
    @Override
    public void displayOutput(AsciiPanel terminal) {
        
        //if()
        if(multiple == false || numClient >= 3){
            if(startGame() && multiple){
                player = players.get(client.getID());
                //System.out.println("id:" + client.getID());
            }
            
            // Terrain and creatures
            displayTiles(terminal, getScrollX(), getScrollY());
            // Player
            // terminal.write(player.glyph(), player.x() - getScrollX(), player.y() - getScrollY(), player.color());
            // Stats
            String stats = String.format("%3d/%3d hp", player.hp(), player.maxHP());
            terminal.write(stats, 1, this.screenHeight+1);
            // Messages
            displayMessages(terminal, this.messages);
        }
        else{
            terminal.write("Please wait for all other players to load in...",2,10);
            numClient = client.getNumClient();
        }
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        if(multiple == false){
            return sigleMode(key);
        }
        try {
            return multipleMode(key);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return this;
    }

    private Screen multipleMode(KeyEvent key) throws IOException{
        int flag = 0;
        int id = client.getID();
        String msg = new String();
        msg = "Action:" + id + ":" + key.getKeyCode()+ ":";
        System.out.println(msg);
        client.write(msg);

        flag = client.getWinSign();
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

    public int playerAction(int id,int keyCode){
        int flag = 0;
        Player player1 = players.get(id);
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                flag = player1.moveBy(-1, 0);
                //System.out.println(id + "move left");
                break;
            case KeyEvent.VK_RIGHT:
                flag = player1.moveBy(1, 0);
                //System.out.println(id + "move right");
                break;
            case KeyEvent.VK_UP:
                flag = player1.moveBy(0, -1);
                //System.out.println(id + "move up");
                break;
            case KeyEvent.VK_DOWN:
                flag = player1.moveBy(0, 1);
                //System.out.println(id + "move down");
                break;
            case KeyEvent.VK_SPACE:
                player1.putBomb();
                //System.out.println(id + "put bomb");
                break;
            
        }
        return flag;

    }

    private Screen sigleMode(KeyEvent key){
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
        //return Math.max(0, Math.min(player.x() - screenWidth / 2, world.width() - screenWidth));
        return 0;
    }

    public int getScrollY() {
        //return Math.max(0, Math.min(player.y() - screenHeight / 2, world.height() - screenHeight));
        return 0;
    }

}
