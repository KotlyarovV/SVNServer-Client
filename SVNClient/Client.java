
import com.company.ClientProcessor;
import com.company.Converter;
import com.company.PacketMaker;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Client {

    public static void main(String[] ar) throws IOException {

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = keyboard.readLine(); // ждем пока пользователь введет что-то и нажмет кнопку Enter.
            new Thread(new ClientProcessor(line)).start();
        }
    }
}