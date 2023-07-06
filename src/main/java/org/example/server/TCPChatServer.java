package org.example.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPChatServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader consoleReader;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started. Waiting for a client to connect...");

        clientSocket = serverSocket.accept();

        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        consoleReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Client connected. Start typing your messages.");

        // Start a separate thread to listen for client messages
        Thread messageListener = new Thread(this::listenForMessages);
        messageListener.start();

        // Send messages to the client
        String userInput;
        while ((userInput = consoleReader.readLine()) != null) {
            out.println(userInput);
        }
    }

    private void listenForMessages() {
        try {
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                System.out.println("Client: " + clientMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException ex) {
            System.out.println("Error while closing the connection.");
        }
    }

    public static void main(String[] args) {
        TCPChatServer server = new TCPChatServer();
        try {
            server.start(12345);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            server.stop();
        }
    }
}


