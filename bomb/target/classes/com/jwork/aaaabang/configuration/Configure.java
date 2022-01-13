package com.jwork.aaaabang.configuration;

import java.awt.Color;
import java.util.Random;

import com.jwork.aaaabang.asciiPanel.AsciiPanel;
public class Configure {
    public static long refreshTime = 50;//MILLISECONDS
    public static int GameSize = 25;

    public static int worldWidth = 50;
    public static int worldHeight = 50;

    // public static Color TILE_FLColor = AsciiPanel.black;
    // public static Color TILE_WLColor = AsciiPanel.brightBlack;
    // public static Color TILE_BNColor = AsciiPanel.magenta;
    // public static Color TILE_GLColor = AsciiPanel.brightRed;

    public static Color playerColor = AsciiPanel.brightYellow;
    public static Color player1Color = AsciiPanel.brightBlue;
    public static Color player2Color = AsciiPanel.brightRed;

    public static int monstersCnt = 4;
    public static Color monsterColor = AsciiPanel.cyan;
    public static double monsterSpeed = 1.4;//Senconds
    public static int monsterHp = 10;

    public static int bombAttack = 10;
    public static long bombTime = 1*1000;//MILLISECONDS

    public static int[] seeds = new int[8];

    public static int[] generateSeed(){
        for (int i = 0; i < 8; i++) {
            seeds[i] = new Random().nextInt(45656);
        }
        return seeds;
    }


}
