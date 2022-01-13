package com.jwork.aaaabang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.*;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.*;


import com.jwork.aaaabang.configuration.Configure;
import com.jwork.aaaabang.screen.*;
import com.jwork.aaaabang.world.*;

public class ScreenTest {

    Screen screen;

    @Before
    public void setUP(){
        screen = new StartScreen();
        
    }

    @Test
    public void testScreen(){
        try {
            screen = new PlayScreen(KeyEvent.VK_ENTER);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }//new game
        assertTrue(screen instanceof PlayScreen);

     
        screen = new WinScreen();
        screen = new LoseScreen();
    }

    @Test
    public void testSave() throws IOException{
        screen = new PlayScreen(KeyEvent.VK_ENTER);//new game
        assertTrue(screen instanceof PlayScreen);
        ((PlayScreen) screen).saveGame();
        screen = new PlayScreen(KeyEvent.VK_SPACE);//load old game
    }

    
    
}
