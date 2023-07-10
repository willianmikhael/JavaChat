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

    private int secretNumber;
    private boolean gameRunning;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started. Waiting for a client to connect...");

        clientSocket = serverSocket.accept();

        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        consoleReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Client connected.");

        // Allow the server to manually input the secret number
        System.out.println("Enter the secret number (between 1 and 10):");
        String secretNumberInput = consoleReader.readLine();
        secretNumber = Integer.parseInt(secretNumberInput);
        gameRunning = true;

        // Start a separate thread to listen for client guesses
        Thread guessListener = new Thread(this::listenForGuesses);
        guessListener.start();

        // Process client guesses
        String clientGuess;
        while (gameRunning && (clientGuess = in.readLine()) != null) {
            processGuess(clientGuess);
        }

        stop();
    }

    private void listenForGuesses() {
        try {
            String clientGuess;
            while ((clientGuess = in.readLine()) != null) {
                processGuess(clientGuess);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processGuess(String clientGuess) {
        int guess = Integer.parseInt(clientGuess);
        System.out.println("Client guessed: " + guess);

        String response;
        if (guess == secretNumber) {
            response = "Correct! You guessed the number.";
            gameRunning = false;
        } else if (guess < secretNumber) {
            response = "Wrong guess. The secret number is higher.";
        } else {
            response = "Wrong guess. The secret number is lower.";
        }

        out.println(response);

        if (gameRunning) {
            System.out.println("Waiting for the client's next guess...");
        } else {
            stop();
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
        }
    }
}
