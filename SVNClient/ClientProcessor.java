package com.company;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vitaly on 04.05.17.
 */
public class ClientProcessor implements Runnable{

    private Socket socket;
    private InputStream sin;
    private OutputStream sout;
    private String command;

    private int serverPort = 5683; // здесь обязательно нужно указать порт к которому привязывается сервер.
    private String address = "127.0.0.1";
    // создаем объект который отображает вышеописанный IP-адрес.


    public ClientProcessor (String command) throws UnknownHostException, IOException {
        InetAddress ipAddress = InetAddress.getByName(address);
        this.socket = new Socket(ipAddress, serverPort);
        this.sin = socket.getInputStream();
        this.sout = socket.getOutputStream();
        this.command = command;
    }
    public void run(){
        try {
            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);

            byte[] sended = PacketMaker.makePacket(this.command);
            out.write(Converter.intToByte(sended.length));
            out.write(sended);
            out.flush(); // заставляем поток закончить передачу данных.
            //        out.close();
            int i;
            ByteOutputStream byteOutputStream = new ByteOutputStream();
            List<Integer> integers = new ArrayList<>();
            while ((i = in.read()) != -1)
                byteOutputStream.write(i);

            byte[] byteAnsw = byteOutputStream.getBytes();
            System.out.println(new String(byteAnsw));
            out.close();
            socket.close();
        } catch (Throwable e) {
            System.out.println("Command was crashed");
        }
    }
}
