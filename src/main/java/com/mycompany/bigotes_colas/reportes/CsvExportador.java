/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.reportes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author gbcya
 */
public class CsvExportador {

    private CsvExportador() {
        // Clase de utilidad: no se instancia
    }

    /**
     * Escribe encabezados y filas a un archivo CSV, separado por comas,
     * con comillas automaticas para valores que contengan comas o comillas.
     */
    public static void exportar(String rutaArchivo, String[] encabezados, List<String[]> filas) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo))) {
            writer.write(String.join(",", escaparFila(encabezados)));
            writer.newLine();
            for (String[] fila : filas) {
                writer.write(String.join(",", escaparFila(fila)));
                writer.newLine();
            }
        }
    }

    private static String[] escaparFila(String[] fila) {
        String[] resultado = new String[fila.length];
        for (int i = 0; i < fila.length; i++) {
            String valor = fila[i] == null ? "" : fila[i];
            if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
                valor = "\"" + valor.replace("\"", "\"\"") + "\"";
            }
            resultado[i] = valor;
        }
        return resultado;
    }
}