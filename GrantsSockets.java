package grantssockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

///// EchoServer
public class GrantsSockets
{

    public static void main(String[] args) throws IOException
    {
        while(1==1)
        {
            Listener listener = new Listener();
            listener.listen("4444");
        }
    }

        

    

} // End of GrantsSockets

