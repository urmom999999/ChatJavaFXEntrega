package com.example.cafeteriajavafx;

public class Cliente extends Thread{
private String nombre;
private int tiempoEspera;
private long llegada;
private boolean servido;
private Cola cola;
private Camarero[] camareros;
    private MenuController controller;
    //Servido?
//llegada?

public Cliente(String nombre, int tiempoEspera,Cola cola,Camarero[] camareros,MenuController controller){
    this.nombre=nombre;
    this.tiempoEspera=tiempoEspera;
    this.servido=false;
    this.cola=cola;
    this.camareros=camareros;
    this.controller = controller;
}

    public String getnombre() {
        return nombre;
    }
public int getTiempoEspera (){
    return tiempoEspera;
}
public boolean getServido(){
    return servido;
}
public void siServido(){
        this.servido=servido;
}
    public void servir() {
        this.servido = true;
    }
//Para que el camarero no este checkeando cada pocos milisegundos por un nuevo cliente, el cliente activa el camarero
    private void activarCamarero(){
    for (Camarero camarero: camareros){
        if(!camarero.estaTrabajando()){
            camarero.activar();
            System.out.println("El camarero "+ camarero.getNombre() +" fue llamado por el cliente "+nombre+ " y empezó a trabajar");
            break;
        }

    }
    }

@Override
public void run(){
    //ENTRAR, ANUNCIAR ENTRADA -----HORA-----
    System.out.println(nombre+ " entró en la cafetería y pidió un café. Máximo de espera: "+ tiempoEspera/1000+" segundos");
    if (controller != null) {
        controller.agregarClienteACola(nombre, tiempoEspera);
        controller.agregarMensajeCafeteria(nombre + " entró en la cafetería");
    }
//Si camarero no esta trabajando
    if (cola.getClientesEnCola()==0) {
        activarCamarero();
    }
    //Si esta trabajando añadir a la cola sin mas
    cola.agregarCliente(this);
    //ESPERAR DETERMINADO TIEMPO
    try {
        Thread.sleep(tiempoEspera);
//IRSE SI SUPERA EL TIEPO Thread.sleep?
        if(!servido){

            if (controller != null) controller.agregarMensajeCafeteria(nombre+ " esperó demasiado y se fue.");
            System.out.println(nombre+ " esperó demasiado y se fue.");
        } else {

            if (controller != null) controller.agregarMensajeCafeteria(nombre+ " recibió su café!");
            System.out.println(nombre+ " recibió su café!");
        }


        if (controller != null) controller.removerClienteDeCola(nombre);

    } catch (InterruptedException e) {
        System.out.println(nombre+ " murió");
    }
    //CONFIRMAR CAFE RECIVIDO?
    //System.out.println(nombre+ " recivió su café!.");
}
}
