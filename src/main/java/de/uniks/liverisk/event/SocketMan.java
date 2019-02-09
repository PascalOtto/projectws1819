package de.uniks.liverisk.event;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketMan {

    private final ExecutorService executor;
    private final ExecutorService agentsExecutor;

    private ServerGameController sgc = new ServerGameController(this);

    public SocketMan()
    {
        executor = Executors.newSingleThreadExecutor();
        agentsExecutor = Executors.newCachedThreadPool();
    }

    private ArrayList<Socket> socketList = new ArrayList<>();

    public int getSocketCount() {return socketList.size();}

    public void addNewSocket(Socket socket)
    {
        executor.execute(() -> doAddSocket(socket));
    }

    // internal executed only by a single thread
    private void doAddSocket(Socket socket)
    {
        if(socketList.size() >= sgc.getMaxPlayer()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        socketList.add(socket);
        sgc.playerConnected();
        CallAgent callAgent = new CallAgent(this, socket);

        agentsExecutor.execute(callAgent);
    }

    public void newMessage(LinkedHashMap<String, String> map)
    {
        executor.execute(() -> doNewMessage(map));
    }
    public void newMessage(LinkedHashMap<String, String> map, int i)
    {
        executor.execute(() -> doNewMessage(map, i));
    }

    private void doNewMessage(LinkedHashMap<String, String> map) {
        for (Socket socket : socketList)
        {
            try
            {

                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(outputStream);
                objOut.writeObject(map);
                objOut.flush();
            }
            catch (IOException e)
            {
                // kill socket
                removeSocket(socket);
            }
        }
    }

    private void doNewMessage(LinkedHashMap<String, String> map, int i) {
            Socket socket = socketList.get(i);
            try
            {

                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(outputStream);
                objOut.writeObject(map);
                objOut.flush();
            }
            catch (IOException e)
            {
                System.out.println(e.getStackTrace());
                // kill socket
                removeSocket(socket);
            }

    }

    public void receiveMessage(LinkedHashMap<String, String> map) {
        executor.execute(()-> doReceiveMessage(map));
    }

    private void doReceiveMessage(LinkedHashMap<String, String> map) {
        sgc.receive(map);
    }

    public void removeSocket(Socket socket)
    {
        System.out.println("remove");
        socketList.remove(socket);
        sgc.playerDisconnected();
        if(socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

