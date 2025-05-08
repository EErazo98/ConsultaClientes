/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rpa.controllers;

import com.rpa.models.Cliente;
import com.rpa.util.LoggerUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author diaze
 */
public class ClienteController {

    private List<Cliente> clientes = new ArrayList<>();
    private List<Cliente> clientesAlmacenados = new ArrayList<>();
    private List<Cliente> clientesBusqueda = new ArrayList<>();

    public boolean cargarClientesDesdeArchivo(File archivo) {
        clientes.clear();
        boolean huboErrores = false;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                Cliente cliente = validarLinea(linea);
                if (cliente != null) {
                    clientes.add(cliente);
                } else {
                    huboErrores = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return huboErrores;
    }

    public void ordenarPorSaldoDescendente() {
        for (int i = 0; i < clientes.size() - 1; i++) {
            for (int j = 0; j < clientes.size() - i - 1; j++) {
                Cliente actual = clientes.get(j);
                Cliente siguiente = clientes.get(j + 1);
                if (actual.getSaldo() < siguiente.getSaldo()) {
                    clientes.set(j, siguiente);
                    clientes.set(j + 1, actual);
                }
            }
        }
    }

    public void ordenarclientesAlmacenados() {
        for (int i = 0; i < clientesAlmacenados.size() - 1; i++) {
            for (int j = 0; j < clientesAlmacenados.size() - i - 1; j++) {
                Cliente actual = clientesAlmacenados.get(j);
                Cliente siguiente = clientesAlmacenados.get(j + 1);
                if (actual.getSaldo() < siguiente.getSaldo()) {
                    clientesAlmacenados.set(j, siguiente);
                    clientesAlmacenados.set(j + 1, actual);
                }
            }
        }
    }

    public void ordenarclientesAlmacenadosBusqueda() {
        for (int i = 0; i < clientesBusqueda.size() - 1; i++) {
            for (int j = 0; j < clientesBusqueda.size() - i - 1; j++) {
                Cliente actual = clientesBusqueda.get(j);
                Cliente siguiente = clientesBusqueda.get(j + 1);
                if (actual.getSaldo() < siguiente.getSaldo()) {
                    clientesBusqueda.set(j, siguiente);
                    clientesBusqueda.set(j + 1, actual);
                }
            }
        }
    }

    public void buscarClientes(LocalDate fechaMax, String tipoCliente) {
        clientesBusqueda.clear();

        for (Cliente c : clientesAlmacenados) {
            if (c.getUltimaFecha().isBefore(fechaMax.plusDays(1))
                    && c.getTipoCliente().equalsIgnoreCase(tipoCliente)) {
                clientesBusqueda.add(c);
            }
        }
    }

    public Map<String, Double> calcularPromediosBusquedaPorTipo() {
        Map<String, List<Double>> saldosPorTipo = new HashMap<>();

        for (Cliente c : clientesAlmacenados) {
            String tipo = c.getTipoCliente();
            saldosPorTipo.computeIfAbsent(tipo, k -> new ArrayList<>()).add(c.getSaldo());
        }

        Map<String, Double> promedios = new HashMap<>();
        for (String tipo : Arrays.asList("Vip", "Premium", "Regular")) {
            List<Double> saldos = saldosPorTipo.getOrDefault(tipo, new ArrayList<>());
            double promedio = saldos.isEmpty() ? 0.0
                    : saldos.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            promedios.put(tipo, promedio);
        }

        return promedios;
    }

    public void exportarClientes(List<Cliente> lista, File destino) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(destino))) {
            for (Cliente c : lista) {
                String linea = String.format("%d,%s,%.2f,%s,%s",
                        c.getId(),
                        c.getNombre(),
                        c.getSaldo(),
                        c.getUltimaFecha().toString(),
                        c.getTipoCliente()
                );
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            LoggerUtil.registrarErrorCatch("exportarClientes", "Error al exportar archivo: " + e.getMessage());
        }
    }

    private boolean idExiste(int id) {
        return clientesAlmacenados.stream().anyMatch(c -> c.getId() == id);
    }

    private Cliente validarLinea(String linea) {
        List<String> errores = new ArrayList<>();
        String[] partes = linea.split(",");

        if (linea.trim().isEmpty()) {
            LoggerUtil.registrarError(linea, "Línea vacía.");
            return null;
        }

        if (partes.length != 5) {
            LoggerUtil.registrarError(linea, "Número incorrecto de campos (esperados: 5).");
            return null;
        }

        Integer id = null;
        String nombre = partes[1].trim();
        Double saldo = null;
        LocalDate fecha = null;
        String tipo = partes[4].trim();

        try {
            id = Integer.parseInt(partes[0].trim());
            if (idExiste(id)) {
                errores.add("ID duplicado: " + id);
            }
        } catch (NumberFormatException e) {
            errores.add("ID no es un número entero.");
        }

        if (nombre.isEmpty()) {
            errores.add("Nombre vacío.");
        }

        try {
            saldo = Double.parseDouble(partes[2].trim());
            if (saldo < 0) {
                errores.add("Saldo no puede ser negativo.");
            }
        } catch (NumberFormatException e) {
            errores.add("Saldo no es un número válido.");
        }

        try {
            fecha = LocalDate.parse(partes[3].trim());
        } catch (DateTimeParseException e) {
            errores.add("Fecha inválida. Formato esperado: YYYY-MM-DD.");
        }

        if (tipo.isEmpty()) {
            errores.add("Tipo de cliente vacío.");
        } else {
            String tipoValido = tipo.toLowerCase();
            if (!tipoValido.equals("vip") && !tipoValido.equals("premium") && !tipoValido.equals("regular")) {
                errores.add("Tipo de cliente inválido: " + tipo + ". Solo se permite: Vip, Premium o Regular.");
            }
        }

        if (!errores.isEmpty()) {
            for (String error : errores) {
                LoggerUtil.registrarError(linea, error);
            }
            return null;
        }

        return new Cliente(id, nombre, saldo, fecha, tipo);
    }

    public void almacenarClientes() {
        clientesAlmacenados.addAll(clientes);
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public List<Cliente> getClientesAlmacenados() {
        return clientesAlmacenados;
    }

    public List<Cliente> getClientesBusqueda() {
        return clientesBusqueda;
    }
}
