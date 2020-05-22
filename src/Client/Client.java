package Client;

import communication.Packet;
import communication.Serializer;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

import java.util.Stack;

public class Client implements Runnable, ClientCommands {

    private int port = 1340;
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
    {
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    private InetSocketAddress socketAddress = new InetSocketAddress(address, port);
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
                    command = in.nextLine();
                    if(command.equals("execute_script") || command.equals("over")) {
                        scripter(command);
                    }
                    else {
                        packet = invoker(command);
                        if (packet.getMode()){
                            break;
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
        System.out.println("Запускаем клиент...");
        client.run();
    }

    public void scripter(String command){
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

}
