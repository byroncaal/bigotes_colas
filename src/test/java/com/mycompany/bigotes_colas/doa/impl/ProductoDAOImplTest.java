package com.mycompany.bigotes_colas.doa.impl;

import com.mycompany.bigotes_colas.doa.ProductoDAO;
import com.mycompany.bigotes_colas.model.Producto;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ProductoDAOImplTest
 * Punto de Control 2: "CRUD de Producto pasa JUnit contra la BD remota".
 *
 * IMPORTANTE: estas son pruebas de INTEGRACION, no unitarias -- se conectan
 * de verdad a la base de datos remota (usando ConexionBD), no a un mock.
 * Necesitas internet y que ConexionBD.java ya tenga tus credenciales reales.
 *
 * Se ejecutan en orden (Crear -> Listar -> Buscar -> Actualizar -> Eliminar)
 * porque cada prueba depende del resultado de la anterior.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductoDAOImplTest {

    private final ProductoDAO productoDAO = new ProductoDAOImpl();
    private static int idCreado;

    @Test
    @Order(1)
    @DisplayName("Crear un producto nuevo se guarda con un id generado")
    void testCrear() throws SQLException {
        Producto p = new Producto("TEST-001", "Producto de prueba JUnit", "Medicamento",
                10.50, 5, LocalDate.now().plusMonths(6));

        productoDAO.crear(p);
        idCreado = p.getIdProducto();

        assertTrue(idCreado > 0, "El id generado deberia ser mayor a 0");
    }

    @Test
    @Order(2)
    @DisplayName("Listar todos incluye el producto recien creado")
    void testListarTodos() throws SQLException {
        List<Producto> lista = productoDAO.listarTodos();

        boolean existe = lista.stream().anyMatch(p -> "TEST-001".equals(p.getCodigo()));
        assertTrue(existe, "El producto de prueba deberia aparecer en la lista completa");
    }

    @Test
    @Order(3)
    @DisplayName("Buscar por texto encuentra el producto de prueba")
    void testBuscar() throws SQLException {
        List<Producto> resultado = productoDAO.buscar("TEST-001");

        assertFalse(resultado.isEmpty(), "La busqueda deberia encontrar al menos 1 resultado");
        assertEquals("Producto de prueba JUnit", resultado.get(0).getNombre());
    }

    @Test
    @Order(4)
    @DisplayName("Actualizar cambia nombre y precio correctamente")
    void testActualizar() throws SQLException {
        Producto p = productoDAO.buscar("TEST-001").get(0);
        p.setNombre("Producto de prueba JUnit (editado)");
        p.setPrecio(15.00);

        productoDAO.actualizar(p);

        Producto actualizado = productoDAO.buscar("TEST-001").get(0);
        assertEquals("Producto de prueba JUnit (editado)", actualizado.getNombre());
        assertEquals(15.00, actualizado.getPrecio(), 0.001);
    }

    @Test
    @Order(5)
    @DisplayName("Eliminar el producto lo remueve de la BD")
    void testEliminar() throws SQLException {
        productoDAO.eliminar(idCreado);

        List<Producto> resultado = productoDAO.buscar("TEST-001");
        assertTrue(resultado.isEmpty(), "El producto ya no deberia existir despues de eliminarlo");
    }
}