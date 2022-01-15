package com.jwork.aaaabang.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.jwork.aaaabang.configuration.Configure;
import com.jwork.aaaabang.screen.PlayScreen;


public class Client implements Runnable{
    PlayScreen playScreen;
    int id;

    private int numClient;
    private int[] seeds;
    boolean connected;
    int winSign;

    InetSocketAddress hostAddress;
    SocketChannel clientChannel;
    private ByteBuffer buffer;

    public Client(PlayScreen playScreen) throws IOException {
        this.playScreen = playScreen;
        this.id = -1;//还未确认编号的客户端
        numClient = -1;
        winSign = 0;

        buffer = ByteBuffer.allocate(1024);
        hostAddress = new InetSocketAddress("localhost", 9001);
        clientChannel = SocketChannel.open(hostAddress);
        //System.out.println("Client... connected");
        // clientChannel = SocketChannel.open();
        // clientChannel.configureBlocking(false);
        // connected = clientChannel.connect(hostAddress);
    }

    public int getID(){
        return this.id;
    }
    //客户端是否连接成功
    public boolean isConnected() throws IOException{
        return clientChannel.finishConnect();
    }

    public void read() throws IOException { // 将server发送的消息读入并进行处理
        buffer.clear();
        int numRead = -1;
        numRead = clientChannel.read(buffer);
        
        if (numRead == 0 || numRead == -1) {
            ////System.out.println("Client Read Nothing");
            return;
        }

        byte[] data = new byte[numRead];
        System.arraycopy(buffer.array(), 0, data, 0, numRead);
        buffer.clear();

        String info = new String(data);
        System.out.println("Got from Server: " + info);
        parseInfo(info);
        
    }

    private  void parseInfo(String info) throws IOException {
        String[] temp = info.split(":");
        if (temp[0].equals("numClient")) { // 收到当前客户端数量，可设置编号
            numClient = Integer.parseInt(temp[1]);
            if(this.id == -1){
                this.id = numClient-1;
                write("Ask for seed:");
            }
        } else if (temp[0].equals("Seeds")) {
            seeds = new int[8];
            for(int i = 0;i < 8;i++){
                this.seeds[i] = Integer.parseInt(temp[i+1]);
            }
        }else if(temp[0].equals("Action")){
            int id = Integer.parseInt(temp[1]);
            int keyCode = Integer.parseInt(temp[2]);
            //System.out.println("ID: " + id + " Keycode:"+keyCode);
            this.winSign = playScreen.playerAction(id,keyCode);
        }
    }

    public int getWinSign(){
        return winSign;
    }
    public  int getNumClient(){
        return numClient;
    }

    public void start(){
        
        while (id == -1 || seeds == null) {
            //System.out.println("Client... started");
            try {
                read();
                //write("Ask for seed:");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //  catch (InterruptedException e) {
            //     // TODO Auto-generated catch block
            //     e.printStackTrace();
            // }
            
        }
    }
    public void write(String string) throws IOException {
        buffer.clear();
        byte[] data = string.getBytes();
        buffer.put(data);
        buffer.flip();
        clientChannel.write(buffer); // 将buffer中的数据写入channel中
        String s = new String(data);
        System.out.println("Write to Server: " + s);
        buffer.clear();
    }

    @Override
    public void run() {

        // TODO Auto-generated method stub
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(Configure.refreshTime);
                this.read();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        
    }

    public int[] getSeeds() {
        return seeds;
    }


}
