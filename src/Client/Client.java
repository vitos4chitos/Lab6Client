package Client;

import com.sun.rmi.rmid.ExecOptionPermission;
import communication.Packet;
import communication.Serializer;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

import java.util.Stack;
import java.util.concurrent.ExecutionException;

public class Client implements Runnable, ClientCommands {

    private int port;
    private DatagramSocket socket;
    private static Stack<Scanner> scanners = new Stack<Scanner>();
    static Scanner in = new Scanner(System.in);
    {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    private InetAddress address;
    private InetSocketAddress socketAddress;
    private ByteBuffer buffer = ByteBuffer.allocate(4096);
    private String command;

    @Override
    public void run() {
        System.out.print("Добро пожаловать в систему. ");
        while (true){
            try {
                Packet packet;
                System.out.println("Введите команду:");
                while (true) {
                    if(scanners.size() == 0) {
                        command = in.nextLine();
                        if (command.equals("execute_script")) {
                            scripter(command);
                        } else {
                            packet = invoker(command);
                            if (packet.getMode()) {
                                break;
                            }
                        }
                    }
                    else{
                        if(in.hasNext()){
                            command = in.nextLine();
                            if (command.equals("execute_script")) {
                                scripter(command);
                            } else {
                                packet = invoker(command);
                                if (packet.getMode()) {
                                    break;
                                }
                            }
                        }
                        else{
                            scripter("over");
                        }
                    }
                }
                DatagramPacket datagramPacket = packer(packet, socketAddress);
                System.out.println("Пакет успешно сформирован, отправляем запрос на сервер...");
                socket.send(datagramPacket);
                System.out.println("Ожидаем ответа от сервера");
                DatagramPacket output = new DatagramPacket(buffer.array(), buffer.limit());
                Thread checker = new Thread(){
                    @Override
                    public void run(){
                        try {
                            Thread.sleep(5000L);
                            System.out.println("Время ожидания пакета превышено, попробуйте позже отправить запрос. " +
                                    "Отключаю клиент");
                            System.exit(0);
                        } catch (InterruptedException ignored) {

                        }
                    }
                };
                checker.start();
                socket.receive(output);
                checker.interrupt();
                packet = (Packet) Serializer.deserialize(output.getData());
                System.out.println("Ответ получен:");
                packet.giveAnswer();
                buffer.clear();
                buffer.put(new byte[4096]);
            }
            catch (IOException e){
                System.out.println("Фу, быдлокодер");
            } catch (ClassNotFoundException e) {
                System.out.println("Сорри, класс не найден");
            }
        }

    }

    public static void main(String[] args){
        System.out.println("Включаем клиент...");
        Client client = new Client();
        while(!client.setSocketAddress());
        System.out.println("Запускаем клиент...");
        client.run();
    }

    private void scripter(String command){
        if(command.equals("execute_script")){
            String line;
            File file;
            while (true) {
                try {
                    System.out.println("Введите путь до Файла");
                    file = new File(Client.in.nextLine());
                    break;
                } catch (Exception e) {
                    System.out.println("Что-то пошло не так. Попробуйте ещё раз.");
                }
            }
            line = file.getPath();
            Packet packet = new Packet(true, "execute_script", line, null);
            try{
                DatagramPacket datagramPacket = packer(packet, socketAddress);
                System.out.println("Пакет успешно сформирован, отправляем запрос на сервер...");
                socket.send(datagramPacket);
                System.out.println("Ожидаем ответа от сервера");
                DatagramPacket output = new DatagramPacket(buffer.array(), buffer.limit());
                Thread checker = new Thread(){
                    @Override
                    public void run(){
                        try {
                            Thread.sleep(5000L);
                            System.out.println("Время ожидания пакета превышено, попробуйте позже отправить запрос. " +
                                    "Отключаю клиент");
                            System.exit(0);
                        } catch (InterruptedException ignored) {
                        }
                    }
                };
                checker.start();
                socket.receive(output);
                checker.interrupt();
                packet = (Packet) Serializer.deserialize(output.getData());
                System.out.println("Ответ получен:");
                packet.giveAnswer();
                if(packet.getMode()){
                    Client.scanners.push(in);
                    in = new Scanner(file);
                }
                buffer.clear();
                buffer.put(new byte[4096]);
            }
            catch (IOException e){
                System.out.println("Фу, быдлокодер");
            } catch (ClassNotFoundException e) {
                System.out.println("Сорри, класс не найден");
            }
        }
        else{
            Packet packet = new Packet(true, "over", null, null);
            try{
                DatagramPacket datagramPacket = packer(packet, socketAddress);
                System.out.println("Пакет успешно сформирован, отправляем запрос на сервер...");
                socket.send(datagramPacket);
                System.out.println("Ожидаем ответа от сервера");
                DatagramPacket output = new DatagramPacket(buffer.array(), buffer.limit());
                socket.receive(output);
                packet = (Packet) Serializer.deserialize(output.getData());
                System.out.println("Ответ получен:");
                packet.giveAnswer();
                buffer.clear();
                buffer.put(new byte[4096]);
                in = Client.scanners.peek();
            }
            catch (IOException e){
                System.out.println("Фу, быдлокодер");
            } catch (ClassNotFoundException e) {
                System.out.println("Сорри, класс не найден");
            }
        }
    }

    private boolean setSocketAddress(){
        while (true) {
            try {
                System.out.println("Введите порт сервера");
                port = in.nextInt();
                break;
            } catch (Exception e) {
                System.out.println("Вы что-то ввели не так, попробуйте ещё раз");
            }
        }
        while (true) {
            try {
                System.out.println("Введите адресс сервера");
                while (!in.hasNext());
                String addr = in.nextLine();
                address = InetAddress.getByName(addr);
                in.nextLine();
                break;
            } catch (Exception e) {
                System.out.println("Вы что-то ввели не так, попробуйте ещё раз");
            }
        }
        try{
            System.out.println("Создаём адресс соединения");
            socketAddress = new InetSocketAddress(address, port);
            System.out.println("Адресс соединения успешно создан");
            return true;
        }
        catch (Exception e){
            System.out.println("Ваши данные были введены неверно, попробуйте ещё раз");
            return false;
        }

    }



}
