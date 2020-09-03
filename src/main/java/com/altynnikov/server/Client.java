package com.altynnikov.server;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Client {

    private static String path = "";

    public static void main(String[] args) {
        try(Socket fromserver = new Socket("localhost", 4444)) {
            DataOutputStream out = new DataOutputStream(fromserver.getOutputStream());

            while (true){
                DataInputStream in = new DataInputStream(fromserver.getInputStream());
                String request = in.readUTF();
                String[] metaData = request.split(" ");
                if (metaData[0].equals("file")){
                    byte[] file = new byte[Integer.parseInt(metaData[1])];
                    in.read(file);
                    if (path == null){
                        System.out.println("Введите путь сохранения");
                        path = new BufferedReader(new InputStreamReader(System.in)).readLine();
                    }
                    FileOutputStream writeOnFile = new FileOutputStream(path + "//ticket" + new Random().nextInt(100) + ".pdf");
                    writeOnFile.write(file);
                    writeOnFile.close();
                }else if(metaData[0].equals("SeanceInfo")){
                    byte[] getString = new byte[Integer.parseInt(metaData[1])];
                    in.read(getString);
                    System.out.println(new String(getString));
                    out.writeUTF(new BufferedReader(new InputStreamReader(System.in)).readLine());
                }else if(request.contains("Этот билет уже забронирован")){
                    System.out.println("Этот билет уже забронирован");
                }else if (request.contains("illegalInput")){
                    System.out.println(in.readUTF());
                }else if(request.contains("exit")){
                    System.exit(0);
                }else {
                    System.out.println(request);
                    out.writeUTF(new BufferedReader(new InputStreamReader(System.in)).readLine());
                    out.flush();
                }
            }

            }catch (IOException e){
                e.printStackTrace();
            }
    }
}
