package world;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.awt.Point;
import java.util.concurrent.TimeUnit;

import asciiPanel.AsciiPanel;
import configuration.Configure;

public class Bomb implements Runnable{
    private World world;
    private int x;
    protected int y;
    private int size;
    private int attackValue;
    private char glyph;
    private Color color;
    private List<Point> trace;

    public Bomb(World world, char glyph, Color color, int size,int attack){
        this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.size = size;
        this.attackValue = attack;
        this.trace = new ArrayList<>();
    }

    public char glyph() {
        return this.glyph;
    }
    public Color color() {
        return this.color;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int x() {
        return x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int y() {
        return y;
    }
    
    public void getBombTrace(){
        for(int i = -size;i <= size;i++){
            //炸弹本身
            if(i == 0)
            {
                continue;
            }
            
            //格子
            Tile tile = world.tile(x+i, y);
            if(tile != Tile.BOUNDS && tile != Tile.GOAL){
                trace.add(new Point(x+i,y));
            }
        }

        for(int i = -size;i <= size;i++){
            if(i == 0)
            {
                continue;
            }
            Tile tile = world.tile(x, y+i);
            if(tile != Tile.BOUNDS && tile != Tile.GOAL){
                trace.add(new Point(x,y+i));
            }
                
        }
            
    }
    public void attack(){
        Creature creature; 
   
        for(Point p:trace){
            world.setTile(Tile.BOMB,p.x,p.y);
            creature = world.creature(p.x,p.y);
            if(creature != null){
                creature.getAttack(attackValue); 
            }
        }
        
    }

    public  void afterBomb(){

        for(Point p:trace){
            world.setTile(Tile.FLOOR,p.x,p.y);
        }
    }

    @Override
    public  void run() {
        try {
            Creature creature = world.creature(x, y);
            getBombTrace();
            while(creature instanceof Player){
                TimeUnit.MILLISECONDS.sleep(Configure.bombTime);
                creature = world.creature(x, y);
            }
            attack();
            TimeUnit.MILLISECONDS.sleep(Configure.bombTime);
            afterBomb();
            world.remove(this);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }
}
