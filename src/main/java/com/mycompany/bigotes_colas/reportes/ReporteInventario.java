/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.reportes;
 
import com.mycompany.bigotes_colas.model.Producto;
 
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

public class ReporteInventario {
 
    private ReporteInventario() {
        // Clase de utilidad: no se instancia
    }
 
    public static void generarPDF(List<Producto> productos, String rutaArchivo) throws DRException, IOException {
        DRDataSource dataSource = new DRDataSource("codigo", "nombre", "categoria", "stock", "vence");
        for (Producto p : productos) {
            dataSource.add(
                    p.getCodigo(),
                    p.getNombre(),
                    p.getCategoria(),
                    p.getStock(),
                    p.getVence() != null ? p.getVence().toString() : "-"
            );
        }
 
        JasperReportBuilder reporte = report();
        reporte
                .title(cmp.text("Bigotes & Colas - Reporte de Inventario")
                        .setStyle(stl.style().bold().setFontSize(16)))
                .columns(
                        col.column("Codigo", "codigo", type.stringType()),
                        col.column("Producto", "nombre", type.stringType()),
                        col.column("Categoria", "categoria", type.stringType()),
                        col.column("Stock", "stock", type.integerType()),
                        col.column("Vence", "vence", type.stringType())
                )
                .setDataSource(dataSource);
 
        try (FileOutputStream out = new FileOutputStream(rutaArchivo)) {
            reporte.toPdf(out);
        }
    }
}
 
