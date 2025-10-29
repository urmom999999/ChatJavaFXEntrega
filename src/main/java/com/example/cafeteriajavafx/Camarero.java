package com.example.cafeteriajavafx;
public class Camarero extends Thread {
    public String nombre;
    private Cola cola;
    private boolean trabajando;
    private boolean activo;
    private MenuController controller;

    public Camarero(String nombre,Cola cola,MenuController controller){
        this.nombre=nombre;
        this.cola=cola;
        this.activo=false;
        this.trabajando=false;
        this.controller = controller;
    }

    public String getNombre() {
        return nombre;
    }
    public boolean estaTrabajando() {
        return trabajando;
    }
public void activar(){
        this.activo=true;
        //Error si no esta iniciado correctamente por primera vez
    if (!this.isAlive()) {
        this.start();
    }
}
    public void prepararCafe(Cliente cliente) throws InterruptedException {
//ANUNCIAR COMIEZO PREPARANDO EL CAFE, THREAD.SLEEP
        //Si servido es true el cliente fue servido o se fue
        if(cliente.getServido()){
            return;
        }
        if (controller != null) {
            controller.actualizarEstadoCamarero(nombre, true);
            controller.agregarMensajeCafeteria(nombre + " empezó a preparar café para " + cliente.getnombre());
        }

        System.out.println(nombre + " empezó a preparar el café para " + cliente.getnombre());
        int preparacion = (int) (Math.random() * 2000) + 1000;
        //SI EL CLIENTE SE MARCHA DETENER EL PROCESO
        Thread.sleep(preparacion);
        //ENTREGAR SI ESTA A TIEMPO

if(cliente.isAlive() && !cliente.getServido()){
    cliente.servir();
    if (controller != null) {
        controller.agregarMensajeCafeteria(nombre + " terminó de preparar el café para " + cliente.getnombre());
        controller.actualizarEstadoCamarero(nombre, false);
    }
    System.out.println(nombre + " terminó de preparar el café para "+ cliente.getnombre()+ " y tardó " + preparacion/1000 + "segundos!");
}
else{
    if (controller != null) {
        controller.agregarMensajeCafeteria(nombre + " terminó pero " + cliente.getnombre() + " se marchó");
        controller.actualizarEstadoCamarero(nombre, false);
    }
        System.out.println(nombre + " terminó de preparar el café, pero el cliente se marchó ya");}
    }
    //thread.sleep

    @Override
    public void run() {
        System.out.println(nombre + " comenzó a trabajar.");
        if (controller != null) {
            controller.agregarMensajeCafeteria(nombre + " comenzó a trabajar");
            controller.actualizarEstadoCamarero(nombre, true);
        }

while (activo|| cola.hayMasClientes()){
    trabajando=true;
    while (cola.hayMasClientes()) {
        try {
            Cliente cliente = cola.siguienteCliente();
            if (cliente != null) {
                prepararCafe(cliente);
            }
        } catch (InterruptedException e) {
            System.out.println(nombre + " ERROR");
            activo = false;
            break;
        }
    }
    try {
        Thread.sleep(100);
    } catch (InterruptedException e) {
        break;
    }
    //DEJAR DE TRABAJAR
    trabajando = false;
    activo = false;
}

        if (controller != null) {
            controller.agregarMensajeCafeteria(nombre + " terminó de trabajar");
            controller.actualizarEstadoCamarero(nombre, false);
        }
        System.out.println(nombre + " terminó de trabajar.");
    }
}
