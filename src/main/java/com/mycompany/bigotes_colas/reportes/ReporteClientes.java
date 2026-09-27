/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.reportes;
 
import com.mycompany.bigotes_colas.model.Cliente;
 
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
 
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
 
import static net.sf.dynamicreports.report.builder.DynamicReports.*;

/**
 *
 * @author gbcya
 */
public class ReporteClientes {
 
    private ReporteClientes() {
        // Clase de utilidad: no se instancia
    }
 
    public static void generarPDF(List<Cliente> clientes, String rutaArchivo) throws DRException, IOException {
        DRDataSource dataSource = new DRDataSource("dpi", "nombre", "telefono", "direccion");
        for (Cliente c : clientes) {
            dataSource.add(c.getDpi(), c.getNombre(), c.getTelefono(), c.getDireccion());
        }
 
        JasperReportBuilder reporte = report();
        reporte
                .title(cmp.text("Bigotes & Colas - Reporte de Clientes")
                        .setStyle(stl.style().bold().setFontSize(16)))
                .columns(
                        col.column("DPI", "dpi", type.stringType()),
                        col.column("Nombre", "nombre", type.stringType()),
                        col.column("Telefono", "telefono", type.stringType()),
                        col.column("Direccion", "direccion", type.stringType())
                )
                .setDataSource(dataSource);
 
        try (FileOutputStream out = new FileOutputStream(rutaArchivo)) {
            reporte.toPdf(out);
        }
    }
}
