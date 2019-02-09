package de.uniks.liverisk.event;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{

   private SocketMan socketMan;

   public static void main(String[] args)
   {
      new Server().run();
   }


   private void run()
   {
      try
      {
         ServerSocket serverSocket = new ServerSocket(42424);

         socketMan = new SocketMan();

         while (true)
         {
            Socket newSocket = serverSocket.accept();
            socketMan.addNewSocket(newSocket);

         }

      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }










}
