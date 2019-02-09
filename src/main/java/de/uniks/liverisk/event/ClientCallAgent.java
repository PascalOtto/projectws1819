package de.uniks.liverisk.event;

import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientCallAgent
{
    private Socket socket;
    Client client;

    public ClientCallAgent(Socket socket, Client client)
    {
        this.client = client;
        this.socket = socket;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> doReadMessages());
    }

    private void doReadMessages()
    {
        while (socket.isClosed() == false) {
            try {

                while (true) {
                    InputStream inputStream = socket.getInputStream();
                    ObjectInputStream inputStreamReader = new ObjectInputStream(inputStream);
                    LinkedHashMap<String, String> in = (LinkedHashMap<String, String>) inputStreamReader.readObject();
                    client.processMessage(in);
                }
            } catch (Exception e) {
                if(e.getClass() == EOFException.class) {
                }
                e.printStackTrace();
            }
        }
    }
}
