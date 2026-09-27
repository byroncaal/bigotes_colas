/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.reportes;
 
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
 
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
 
import static net.sf.dynamicreports.report.builder.DynamicReports.*;
/**
 *
 * @author gbcya
 */

public class ReporteHistorial {
 
    private ReporteHistorial() {
        // Clase de utilidad: no se instancia
    }
 
    /**
     * @param historial            lista de citas/consultas de la mascota
     * @param nombresVeterinarios  mapa idVeterinario -> nombre, para mostrar el nombre en vez del id
     * @param nombreMascota        para el titulo del reporte
     * @param rutaArchivo          donde guardar el PDF
     */
    public static void generarPDF(List<com.mycompany.bigotes_colas.model.CitaConsulta> historial,
                                   Map<Integer, String> nombresVeterinarios,
                                   String nombreMascota,
                                   String rutaArchivo) throws DRException, IOException {
 
        DRDataSource dataSource = new DRDataSource("fecha", "motivo", "diagnostico", "veterinario");
        for (com.mycompany.bigotes_colas.model.CitaConsulta c : historial) {
            dataSource.add(
                    c.getFecha() != null ? c.getFecha().toString() : "-",
                    c.getMotivo(),
                    c.getDiagnostico() != null ? c.getDiagnostico() : "-",
                    nombresVeterinarios.getOrDefault(c.getIdVeterinario(), "Desconocido")
            );
        }
 
        JasperReportBuilder reporte = report();
        reporte
                .title(cmp.text("Bigotes & Colas - Historial clinico de " + nombreMascota)
                        .setStyle(stl.style().bold().setFontSize(16)))
                .columns(
                        col.column("Fecha", "fecha", type.stringType()),
                        col.column("Motivo", "motivo", type.stringType()),
                        col.column("Diagnostico", "diagnostico", type.stringType()),
                        col.column("Veterinario", "veterinario", type.stringType())
                )
                .setDataSource(dataSource);
 
        try (FileOutputStream out = new FileOutputStream(rutaArchivo)) {
            reporte.toPdf(out);
        }
    }
}
