package com.company;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws Throwable {

            ServerSocket ss = new ServerSocket(5683);
            while (true) {
                Socket s = ss.accept();
                System.err.println("Client accepted");
                new Thread(new SocketProcessor(s)).start();
            }
    }
}
