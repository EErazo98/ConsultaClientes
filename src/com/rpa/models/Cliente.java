/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rpa.models;

import java.time.LocalDate;

/**
 *
 * @author diaze
 */
public class Cliente {

    private int id;
    private String nombre;
    private double saldo;
    private LocalDate ultimaFecha;
    private String tipoCliente;

    public Cliente() {

    }

    public Cliente(int id, String nombre, double saldo, LocalDate ultimaFecha, String tipoCliente) {
        this.id = id;
        this.nombre = nombre;
        this.saldo = saldo;
        this.ultimaFecha = ultimaFecha;
        this.tipoCliente = tipoCliente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public LocalDate getUltimaFecha() {
        return ultimaFecha;
    }

    public void setUltimaFecha(LocalDate ultimaFecha) {
        this.ultimaFecha = ultimaFecha;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

}
