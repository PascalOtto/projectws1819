package de.uniks.liverisk.event;

import java.io.*;
import java.net.Socket;
import java.util.LinkedHashMap;

public class CallAgent implements Runnable
{
   private SocketMan socketMan;
   private Socket socket;

   public CallAgent(SocketMan socketMan, Socket socket)
   {
      this.socketMan = socketMan;
      this.socket = socket;
   }

   public void run()
   {
      try
      {
         while (true) {
            InputStream inputStream = socket.getInputStream();
            ObjectInputStream inputStreamReader = new ObjectInputStream(inputStream);
            LinkedHashMap<String, String> in = (LinkedHashMap<String, String>) inputStreamReader.readObject();
            socketMan.receiveMessage(in);
         }
      }
      catch (Exception e)
      {
         System.out.println("closed");
      }

      socketMan.removeSocket(socket);
   }
}
