/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grantssockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author toshiba
 */
public class Listener
{
    public void listen(String portNumber) throws IOException
    {
    
    


        try (
                ServerSocket serverSocket
                = new ServerSocket(Integer.parseInt(portNumber));
                Socket clientSocket = serverSocket.accept();
                PrintWriter out
                = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));)
        {
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                out.println(inputLine);
            }
        } catch (IOException e)
        {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    } // End of listen
    
}
