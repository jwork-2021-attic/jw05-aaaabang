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

import com.jwork.aaaabang.asciiPanel.AsciiPanel;
import java.awt.event.KeyEvent;
import java.io.IOException;
/**
 *
 * @author Aeranythe Echosong
 */
public class StartScreen extends RestartScreen {

    private int choice = 0;

    @Override
    public void displayOutput(AsciiPanel terminal) {
        
        terminal.write("Welcome to Bomb Game.", 2, 0);
        terminal.write("Press ENTER to continue...", 2, 1);

        //箭头选择;
        switch(choice){
            case 0://new game
                terminal.write((char)26,3,10);
                break;
            case 1://last game
                terminal.write((char)26,3,12);
                break;
            case 2://multiple
                terminal.write((char)26,3,14);
                break;
            default:
                break;
        }


        terminal.write("New Game",5,10);
        terminal.write("Last Game",5,12);
        terminal.write("Multiple Players",5,14);
        
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        switch (key.getKeyCode()) {
            case KeyEvent.VK_DOWN:
                choice = (choice+1)%3;
                return this;
            case KeyEvent.VK_ENTER:
                try {
                    return new PlayScreen(choice);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            default:
                return this;
        }
    }

}
