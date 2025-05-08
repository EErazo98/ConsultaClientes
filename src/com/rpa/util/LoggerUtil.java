/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rpa.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author diaze
 */
public class LoggerUtil {

    private static final String LOG_FILE = "errores.log";

    public static void registrarError(String linea, String mensaje) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String log = String.format("[%s] %s -> %s", timestamp, mensaje, linea);
            bw.write(log);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en errores.log: " + e.getMessage());
        }
    }

    public static void registrarErrorCatch(String metodo, String mensaje) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String log = String.format("[%s] [%s] %s", timestamp, metodo, mensaje);
            bw.write(log);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en errores.log: " + e.getMessage());
        }
    }

}
