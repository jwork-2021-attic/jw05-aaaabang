package com.jwork.aaaabang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.*;
import java.util.*;


import com.jwork.aaaabang.configuration.Configure;
import com.jwork.aaaabang.world.*;

/**
 * Unit test for simple App.
 */
public class PlayerTest 
{
    World world;
    Player player;
    int screenWidth;
    int screenHeight;
    List<String> messages;
    /**
     * Rigorous Test :-)
     */

    @Before
    public void setUP(){
        Tile[][] tiles = new Tile[Configure.GameSize][Configure.GameSize];
        for(int i = 0;i < Configure.GameSize;i++)
            for(int j = 0;j < Configure.GameSize;j++){
                if(i == 0 || i == Configure.GameSize-1 || j == 0 || j == Configure.GameSize - 1){
                    tiles[i][j] = Tile.BOUNDS;
                    continue;
                }
                tiles[i][j] = Tile.FLOOR;
            }

        world = new World(tiles);
        this.messages = new ArrayList<String>();
        this.player = new Player(this.world, (char)2, Configure.playeColor, 100, 0, 5, 9,messages);
        world.setPlayer(player);
        

        int size = Configure.GameSize;
        int x = size/2, y= size/2;
        player.setX(x);
        player.setY(y);
    }

    @Test
    public void testMove() throws Exception
    {
        assertEquals(player, world.getPlayer());
        int x = player.x();
        int y = player.y();

        //turn left
        player.moveBy(-1, 0);
        assertEquals(player.x(), --x);
        assertEquals(player.y(), y);

        //turn right
        player.moveBy(1, 0);
        assertEquals(player.x(), ++x);
        assertEquals(player.y(), y);

        //turn up
        player.moveBy(0, -1);
        assertEquals(player.y(), --y);
        assertEquals(player.x(), x);

        //turn down
        player.moveBy(0, 1);
        assertEquals(player.y(), ++y);
        assertEquals(player.x(), x);

        
    }

    @Test
    public void testBomb(){
        Bomb bomb = player.putBomb();
        player.moveBy(0, 1);

        boolean flag = false;
        List<Bomb> bombs = world.getBombs();
        for(Bomb b : bombs){
            if(b == bomb)
            {
                flag = true;
                break;
            }
        }
        assertTrue(flag);
    }

    @Test
    public void testGetAttacked(){
        assertEquals(player.maxHP(), 100); 
        assertEquals(player.maxHP(), player.hp());
        player.getAttack(20);
        assertEquals(player.hp(), 80);

        player.modifyHP(20);
        assertEquals(player.hp(), 100);
    }

    
}


