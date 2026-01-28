package com.example.chatjavafx;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class EchoServerMultihilo {
    private static final int PUERTO = 8080;
    private static final int MAX_CLIENTES = 10;
    private static final List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Puerto " + PUERTO);
        System.out.println("Esperando conexion...\n");

        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTES);

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                Socket clienteSocket = serverSocket.accept();
               //System.out.println("Conectado correctamente");

                pool.execute(new ClientHandler(clienteSocket));
            }
        } catch (IOException e) {
            System.err.println("Error server: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

               // out.println("Bienvenido! Escribe tu NOMBRE:");

//Leer nombre
                username = in.readLine();

                //push a clientes
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                System.out.println(username + " se ha unido al chat");
                broadcast(username + " se ha unido al chat!", out);

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("salir")) {
                        break;
                    }

                    System.out.println(username + ": " + message);
                    broadcast(username + ": " + message, out);
                }

            } catch (IOException e) {
                System.err.println("Error con " + username + ": " + e.getMessage());
            } finally {
//Quitar
                if (out != null) {
                    synchronized (clientWriters) {
                        clientWriters.remove(out);
                    }
                }

//Notificar
                if (username != null) {
                    System.out.println(username + " se ha desconectado");
                    broadcast(username + " se ha desconectado", null);
                }

                try {
                    socket.close();
                } catch (IOException e) {
                    // Ignorar
                }
            }
        }

        private void broadcast(String message, PrintWriter exclude) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    if (writer != exclude) {
                        writer.println(message);
                    }
                }
            }
        }
    }
}