package org.example.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPChatClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader consoleReader;

    public void start(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        consoleReader = new BufferedReader(new InputStreamReader(System.in));

        // Start a separate thread to listen for server responses
        Thread responseListener = new Thread(this::listenForResponses);
        responseListener.start();

        System.out.println("Enter your guesses (between 1 and 10):");

        // Send guesses to the server
        String userInput;
        while ((userInput = consoleReader.readLine()) != null) {
            out.println(userInput);
        }
    }

    private void listenForResponses() {
        try {
            String serverResponse;
            while ((serverResponse = in.readLine()) != null) {
                System.out.println("Server: " + serverResponse);

                if (serverResponse.startsWith("Correct")) {
                    break;
                }
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
        } catch (IOException ex) {
            System.out.println("Error while closing the connection.");
        }
    }

    public static void main(String[] args) {
        TCPChatClient client = new TCPChatClient();
        try {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Enter the server IP address: ");
            String serverIP = consoleReader.readLine();

            System.out.println("Enter the server port: ");
            int serverPort = Integer.parseInt(consoleReader.readLine());

            client.start(serverIP, serverPort);

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            client.stop();
        }
    }
}
