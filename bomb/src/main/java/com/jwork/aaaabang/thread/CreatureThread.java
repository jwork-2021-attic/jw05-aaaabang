package com.jwork.aaaabang.thread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jwork.aaaabang.world.Creature;
import com.jwork.aaaabang.world.World;

public class CreatureThread implements Runnable{
    
    private ExecutorService exec;
    private List<Creature> creatures;

    public CreatureThread(World world){
        exec = Executors.newCachedThreadPool();
        creatures = world.getCreatures();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        for(Creature creature:creatures)
        {
            exec.execute(creature);
        }
        
    }
    
}
