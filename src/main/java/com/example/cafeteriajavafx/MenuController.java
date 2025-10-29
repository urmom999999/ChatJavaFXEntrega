package com.example.cafeteriajavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.util.Random;

public class MenuController {

    @FXML
    private Text colaClientesText;

    @FXML
    private Text estadoCamarerosText;

    @FXML
    private TextArea estadoCafeteriaText;

    @FXML
    private Button comenzarButton;

    private boolean simulacionEnCurso = false;

    @FXML
    public void initialize() {
        estadoCafeteriaText.setEditable(false);
        estadoCafeteriaText.setWrapText(true);

        comenzarButton.setOnAction(event -> comenzarSimulacion());
    }

    private void comenzarSimulacion() {
        if (simulacionEnCurso) {
            return;
        }

        simulacionEnCurso = true;
        comenzarButton.setDisable(true);


        colaClientesText.setText("");
        estadoCamarerosText.setText("");
        estadoCafeteriaText.setText("");

        Thread simulacionThread = new Thread(() -> {
            ejecutarSimulacionCompleta();

            javafx.application.Platform.runLater(() -> {
                comenzarButton.setDisable(false);
                simulacionEnCurso = false;
            });
        });

        simulacionThread.start();
    }

    private void ejecutarSimulacionCompleta() {

        Cola cola = new Cola();
        Camarero c1 = new Camarero("Camarero 1", cola, this);
        Camarero c2 = new Camarero("Camarero 2", cola, this);
        Camarero[] camareros = {c1, c2};
        Random random = new Random();

        Cliente[] clientes = {
                new Cliente("Ramon", random.nextInt(3000)+7000, cola, camareros, this),
                new Cliente("Juanjo", random.nextInt(3000)+7000, cola, camareros, this),
                new Cliente("Pepe", random.nextInt(3000)+5000, cola, camareros, this),
                new Cliente("Manuel", random.nextInt(3000)+5000, cola, camareros, this),
                new Cliente("Ramona", random.nextInt(3000)+5000, cola, camareros, this),
                new Cliente("Larry", random.nextInt(5000)+10000, cola, camareros, this),
                new Cliente("Manuela", random.nextInt(2000)+3000, cola, camareros, this),
                new Cliente("Ana", random.nextInt(2000)+4000, cola, camareros, this)
        };

        agregarMensajeCafeteria("Comienzo!");

        for (int i = 0; i < clientes.length; i++) {
            clientes[i].start();
            try {
                Thread.sleep(random.nextInt(400) + 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Cliente cliente : clientes) {
            try {
                cliente.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        agregarMensajeCafeteria("Todos los camareros terminaron! Fin");
    }


    public void agregarClienteACola(String nombre, int tiempoEspera) {
        javafx.application.Platform.runLater(() -> {
            String textoActual = colaClientesText.getText();
            colaClientesText.setText(textoActual + "• " + nombre + " (" + (tiempoEspera/1000) + " seg)\n");
        });
    }

    public void removerClienteDeCola(String nombre) {
        javafx.application.Platform.runLater(() -> {
            String textoActual = colaClientesText.getText();
            String nuevoTexto = textoActual.replace("• " + nombre + " (", "").replaceAll("\\(.*?\\).*\n", "");
            colaClientesText.setText(nuevoTexto);
        });
    }

    public void actualizarEstadoCamarero(String nombre, boolean trabajando) {
        javafx.application.Platform.runLater(() -> {
            String textoActual = estadoCamarerosText.getText();
            if (textoActual.contains(nombre)) {
                String nuevoTexto = textoActual.replaceAll("• " + nombre + ".*\n", "");
                estadoCamarerosText.setText(nuevoTexto + "• " + nombre + ": " + (trabajando ? "OCUPADO" : "DESOCUPADO") + "\n");
            } else {
                estadoCamarerosText.setText(textoActual + "• " + nombre + ": " + (trabajando ? "OCUPADO" : "DESOCUPADO") + "\n");
            }
        });
    }

    public void agregarMensajeCafeteria(String mensaje) {
        javafx.application.Platform.runLater(() -> {
            estadoCafeteriaText.appendText("• " + mensaje + "\n");
        });
    }
}