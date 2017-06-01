package com.company;

import java.io.ByteArrayOutputStream;
import sun.misc.IOUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vitaly on 24.04.17.
 */
public class SocketProcessor implements Runnable {

    private Socket s;
    private InputStream is;
    private OutputStream os;

    public SocketProcessor(Socket s) throws Throwable {
        this.s = s;
        this.is = s.getInputStream();
        this.os = s.getOutputStream();
    }

    public void run() {
        try {
            byte[] bytes = readInputHeaders();
            System.out.println(bytes.length);
            String answer = CommandHandler.handleCommand(bytes);
            writeResponse(answer);

        } catch (Throwable t) {
                System.out.println(t.getMessage());
        } finally {
            try {
                s.close();
                System.out.println("closed");
            } catch (Throwable t) {
            }
        }
        System.err.println("Client processing finished");
    }

    private void writeResponse(String s) throws Throwable {
        os.write(s.getBytes());
        os.flush();

    }

    private byte[] readInputHeaders() throws Throwable {

        DataInputStream dataInputStream = new DataInputStream(is);
        byte[] sizeBytes = new byte[4];
        dataInputStream.read(sizeBytes);

        int size = Converter.byteToInt(sizeBytes);
        byte[] bytes = new byte[size];

        int i = -1;
        while((i = i + 1) < size) bytes[i] = (byte) dataInputStream.read();

        return bytes;
    }
}


