package com.jwork.aaaabang.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.jwork.aaaabang.configuration.Configure;

public class Server implements Runnable{
    private Selector selector;
    private InetSocketAddress listenAddress;
    private ServerSocketChannel serverChannel;
    private ByteBuffer buffer;

    private int numClient;
    private int[] seeds;

    public Server(String address, int port) throws IOException{
        
        numClient = 0;
        seeds = Configure.generateSeed();

        buffer = ByteBuffer.allocate(1024);
        listenAddress = new InetSocketAddress(address, port);
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(listenAddress);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run(){
        //System.out.println("Sever is running");
        while (true) {
            
            int readyCount;
            try {
                TimeUnit.MILLISECONDS.sleep(33);
                
                readyCount = selector.select();
                if (readyCount == 0) {
                    continue;
                }
                // process selected keys...
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator iterator = readyKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = (SelectionKey) iterator.next();
                    // Remove key from set so we don't process it twice
                    iterator.remove();
                    // operate on the channel...
                    
                    if (key.isAcceptable()) { // Accept client connections
                        this.accept(key);
                        TimeUnit.MILLISECONDS.sleep(33);
                        String msg = "numClient" + ":" + numClient;
                        byte[] data = msg.getBytes();
                        writeToAll(data);
                    } else if (key.isReadable()) { // Read from client
                        this.read(key);
                    } else if (key.isWritable()) {
                        
                    }
                
                }
         
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        
        int numRead = -1;
        buffer.clear();
        numRead = channel.read(buffer); // 从channel中将数据读入buffer

        if (numRead == -1) { // 如果没读取到
            Socket socket = channel.socket();
            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            //System.out.println("Connection closed by client: " + remoteAddr);
            channel.close();
            key.cancel();
            return;
        }

        byte[] data = new byte[numRead];
        System.arraycopy(buffer.array(), 0, data, 0, numRead); // 将buffer中的数据放到存入本地data变量
        String info = new String(data);
        System.out.println("Got: " + info);
        buffer.clear();
        parseInfo(info);
        
    }

    private void parseInfo(String info) throws IOException {
        String[] temp = info.split(":");
        ////System.out.println("HandleInfo:" + info);
        if (temp[0].equals("Ask for seed")) { // 建立连接时，发送numClient
            String msg = new String();
            ////System.out.println("first:" + msg);
            msg = "Seeds:";
            for(int i = 0;i < 8;i++){
                msg += seeds[i] + ":";
            }
            System.out.println("seeds:" + msg);
            byte[] data = msg.getBytes();
            writeToAll(data);
        } else { // 连接已建立，发送操作
            byte[] data = info.getBytes();
            writeToAll(data);
        }
    }

    private void accept(SelectionKey key) throws IOException{
        //System.out.println("receive client");
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        Socket socket = channel.socket();
        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        //System.out.println("Connected to: " + remoteAddr);

        /*
         * Register channel with selector for further IO (record it for read/write
         * operations, here we have used read operation)
         */
        channel.register(this.selector, SelectionKey.OP_READ); 
        ++numClient;
    }

    void write(SelectionKey key, byte[] data) throws IOException {
        if (key.channel() instanceof SocketChannel) {
            SocketChannel channel = (SocketChannel) key.channel();

            buffer.clear();
            buffer.put(data); // 先将要写的数据放入buffer中
            buffer.flip();
            
            channel.write(buffer); // 再将buffer中的数据写入channel中
            buffer.clear();
        }
    }
    
    private void writeToAll(byte[] data) throws IOException{
        Set<SelectionKey> allKeys = selector.keys();
        Iterator iterator = allKeys.iterator();
        // //System.out.println(allKeys.size());
        SelectionKey key = (SelectionKey) iterator.next();
        write(key, data);

        while (iterator.hasNext()) {
            key = (SelectionKey) iterator.next();
            write(key, data);
        }
    }


    public static void main(String[] args) throws IOException {
        
        Server server = new Server("localhost", 9001);
        new Thread(server).start();
    }
}
