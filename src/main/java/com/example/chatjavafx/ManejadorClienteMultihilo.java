package com.example.chatjavafx;


import java.io.*;
import java.net.*;
import java.util.concurrent.*;
/**
 * ManejadorClienteMultihilo
 * ------------------------
 * Clase que implementa Runnable para manejar la comunicación con un
 * único cliente en un hilo separado. Este patrón permite que el servidor
 * acepte múltiples clientes y atienda cada uno concurrentemente.
 *
 * Responsabilidades:
 * - Leer mensajes enviados por el cliente a través del socket.
 * - Responder con un mensaje "ECHO" para cada línea recibida.
 * - Detectar la palabra especial "salir" para terminar la conexión.
 * - Gestionar correctamente los recursos (streams y socket).
 */
public class ManejadorClienteMultihilo implements Runnable {
    private final Socket socket;
    private final int numeroCliente;
    /**
     * Constructor
     * @param socket Socket ya conectado al cliente
     * @param numeroCliente Número identificador del cliente (para logs)
     */
    public ManejadorClienteMultihilo(Socket socket, int numeroCliente) {
        this.socket = socket;
        this.numeroCliente = numeroCliente;
    }
    /**
     * Punto de entrada del hilo: gestiona la comunicación con el cliente.
     *
     * Implementación clave:
     * - Usamos try-with-resources para asegurar el cierre de los streams
     *   (BufferedReader y PrintWriter). El socket se cierra en el finally
     *   porque cerrar los streams no siempre cierra el socket en todas las
     *   implementaciones o si ocurre una excepción antes de crear los streams.
     * - PrintWriter se crea con autoflush=true para que cada println se envíe
     *   inmediatamente sin necesidad de llamar a flush() explícitamente.
     */
    @Override
    public void run() {
        try (
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                PrintWriter salida = new PrintWriter(
                        socket.getOutputStream(), true
                )
        ) {
            Boolean firstInput=true;
            String mensaje;

            while ((mensaje = entrada.readLine()) != null) {
                if(firstInput){salida.println("¡Bienvenido!");
                    firstInput = false;} else {

                System.out.println("[Usuario " + numeroCliente + "] " + mensaje);
                if (mensaje.equalsIgnoreCase("salir")) {
                    break;
                }
//QUITAR DESPUES
                salida.println("Usuario#"+numeroCliente+": " + mensaje);
            }
        }} catch (IOException e) {
            System.err.println("Error " + numeroCliente + ": " +
                    e.getMessage());
        } finally {
//cerrar socket!!
            try {
                socket.close();
                System.out.println("Cliente " + numeroCliente + " desconectado");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
