package com.example.chatjavafx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;


public class ChatController {
    public String serverName;
    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;
    @FXML
    private TextField messageServer;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected = false;
    private Thread receiveThread;
    private String username;
    private ArrayList arrayNames;
    private Date time;
    private Boolean kickMessage=false;
    @FXML
    private void initialize() {
        // Inicializar el chatArea
        chatArea.setWrapText(true);
        Platform.runLater(() -> {
            appendToChat("Bienvenido al chat");

            //appendToChat(serverNameTest);

            messageField.requestFocus();
        });
//CONEXION CON SERVER!
/*
        Platform.runLater(() -> {
            connectToServer();
        });*/
    }
    @FXML
    private void createServer() {
        String messageTest = messageServer.getText().trim();
        if (messageTest.isEmpty()) {
            Platform.runLater(() -> {
                appendToChat("ERROR! Escribe el nombre del nuevo servidor");
            });

        }
        else{
            serverName = messageTest;
            connectToServer();


            //RUN SERVER DESDE AQUI??
            //serverTest400
           //EchoServerMultihilo;

            //serverTest400.run();
        }
    }
    @FXML
    private void checkServer() {
        //LOGICA PARA COMPROBAR SI EL TEXT AREA ES IGUAL A NOMBRE?
        // messageServer
        String messageTest = messageServer.getText().trim();
        //for(int i = 0; i <10; i++){
        // if(messageTest== messageServer[i]){
        //serverName=messageServer[i]
        //connectToServer(serverName);
        // break;
        // }}
        //
    }

    @FXML
    private void connectToServer() {
        //serverName;

        if (connected) return;
        String messageTest = messageServer.getText().trim();
        if(messageTest.isEmpty()){
            Platform.runLater(() -> {
                appendToChat("Error, escribe el nombre del servidor");
                //appendToChat(serverNameTest);
                messageField.requestFocus();
            });
        return;}
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
                    //appendToChat(serverNameTest);
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
        //checkKickMessage();
        if (!message.isEmpty()) {
            try {
                out.println(message);

//primer input
                if (username == null) {
                    username = message;
                    appendToChat("Nombre: " + username);

                }
                else if(kickMessage){
                    appendToChat("Kick message enviado");
                }else {

                    appendToChat(username + ": " + message);
                    //appendToChat(Date);
                }


                messageField.clear();


            } catch (Exception e) {
                connected = false;
                connectToServer(); //reintentar
            }
        }
    }
private <message> void checkKickMessage(){

        //message[i]

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