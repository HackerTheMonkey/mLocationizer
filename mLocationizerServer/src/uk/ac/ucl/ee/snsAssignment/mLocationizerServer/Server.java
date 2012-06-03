package uk.ac.ucl.ee.snsAssignment.mLocationizerServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    private static final int port = 1234;

    public static void main(String args[])
    {    	
        ServerSocket serverSocket = null;
        try
        {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        
        while(true)
        {
           try
           {
               Socket socket = serverSocket.accept();
               RequestProcessor requestProcessor = new RequestProcessor(socket);
               Thread processingThread = new Thread(requestProcessor);
               processingThread.start();
           }
           catch(Exception e)
           {
               e.printStackTrace();
           }
        }
    }
}