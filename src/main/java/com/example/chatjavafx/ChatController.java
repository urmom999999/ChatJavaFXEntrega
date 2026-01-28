package com.example.chatjavafx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import java.io.*;
import java.net.*;

public class ChatController {

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected = false;
    private Thread receiveThread;
    private String username;

    @FXML
    private void initialize() {
        // Inicializar el chatArea
        chatArea.setWrapText(true);

//CONEXION CON SERVER!
        Platform.runLater(() -> {
            connectToServer();
        });
    }

    private void connectToServer() {
        if (connected) return;

        new Thread(() -> {
            try {

                socket = new Socket("localhost", 8080);
                socket.setSoTimeout(30000);

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                connected = true;

                Platform.runLater(() -> {
                    appendToChat("Conectado");
                    appendToChat("Escribe tu NOMBRE de usuario para empezar:");
                    messageField.requestFocus();
                });

                receiveThread = new Thread(this::receiveMessages);
                receiveThread.setDaemon(true);
                receiveThread.start();

            } catch (ConnectException e) {
                Platform.runLater(() -> {
                    appendToChat("Error servidor");
                });

                Platform.runLater(() -> {
                    try {
                        Thread.sleep(5000);
                        connectToServer();
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                });

            } catch (IOException e) {
                Platform.runLater(() -> {
                    appendToChat("Error de conexion " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void sendMessage() {
        if (!connected) {
            appendToChat("Reconectando...");
            connectToServer();
            return;
        }

        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            try {
                out.println(message);

//primer input
                if (username == null) {
                    username = message;
                    appendToChat("Nombre: " + username);
                } else {
                    appendToChat(username + ": " + message);
                }

                messageField.clear();


            } catch (Exception e) {
                connected = false;
                connectToServer(); //reintentar
            }
        }
    }

    private void receiveMessages() {
        try {
            String serverMessage;
            while (connected && (serverMessage = in.readLine()) != null) {
                final String message = serverMessage;
                Platform.runLater(() -> appendToChat(message));
            }
        } catch (IOException e) {
            if (connected) {
                Platform.runLater(() -> {
                    connected = false;
                    connectToServer();
                });
            }
        }
    }

    private void disconnect() {
        if (!connected) return;

        connected = false;

        try {
            if (out != null) {
                out.println("salir");
                out.close();
            }
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
        }

    }

    private void appendToChat(String message) {
        chatArea.appendText(message + "\n");
        chatArea.setScrollTop(Double.MAX_VALUE);
    }

    public void shutdown() {
        disconnect();
    }
}