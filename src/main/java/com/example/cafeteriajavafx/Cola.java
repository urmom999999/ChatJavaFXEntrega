package com.example.cafeteriajavafx;

import java.util.ArrayList;
import java.util.List;


public class Cola{
    private List<Cliente> clientesEsperando;
    private int indiceActual;

    public Cola(){
        this.clientesEsperando=new ArrayList<>();
        this.indiceActual=0;
    }

    public void agregarCliente(Cliente cliente){
        clientesEsperando.add(cliente);
        System.out.println(cliente.getnombre()+" se unió a la cola de espera");

    };
public Cliente siguienteCliente(){
    if (indiceActual < clientesEsperando.size()) {
        Cliente cliente = clientesEsperando.get(indiceActual);
        indiceActual++;
        return cliente;
    }
    return null;
}

    public boolean hayMasClientes() {
        return indiceActual < clientesEsperando.size();
    }

    public int getClientesEnCola() {
        return clientesEsperando.size() - indiceActual;
    };
}
