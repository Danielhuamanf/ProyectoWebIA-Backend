// com.nico.multiservicios.controller.ReporteController
package com.nico.multiservicios.controller;

import com.nico.multiservicios.dto.ReporteClienteDTO;
import com.nico.multiservicios.dto.ReporteInventarioDTO;
import com.nico.multiservicios.dto.ReporteVentaDTO;
import com.nico.multiservicios.dto.ReporteStockBajoDTO;
import com.nico.multiservicios.repository.DetalleVentaRepository;
import com.nico.multiservicios.repository.custom.ClienteRepositoryCustom;
import com.nico.multiservicios.repository.custom.ProductoRepositoryCustom;
import com.nico.multiservicios.repository.custom.VentaRepositoryCustom;
import com.nico.multiservicios.service.ExcelExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://frontend-integrador-ikmdiqdep-huahuaccapas-projects.vercel.app"
})
public class ReporteController {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    private final VentaRepositoryCustom ventaRepositoryCustom;
    private final ClienteRepositoryCustom clienteRepositoryCustom;
    private final ProductoRepositoryCustom productoRepositoryCustom;
    private final ExcelExportService excelExportService;


    @Autowired
    public ReporteController(VentaRepositoryCustom ventaRepositoryCustom,
                             ClienteRepositoryCustom clienteRepositoryCustom,
                             ProductoRepositoryCustom productoRepositoryCustom,
                             ExcelExportService excelExportService) {
        this.ventaRepositoryCustom = ventaRepositoryCustom;
        this.clienteRepositoryCustom = clienteRepositoryCustom;
        this.productoRepositoryCustom = productoRepositoryCustom;
        this.excelExportService = excelExportService;
    }

    @GetMapping("/ventas")
    public ResponseEntity<?> getReporteVentas(
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date fechaFin) {

        try {
            Map<String, Object> response = new HashMap<>();
            response.put("ventas", ventaRepositoryCustom.findVentasReporte(fechaInicio, fechaFin));
            response.put("totalVentas", ventaRepositoryCustom.getTotalVentas(fechaInicio, fechaFin));
            response.put("cantidadVentas", ventaRepositoryCustom.getCantidadVentas(fechaInicio, fechaFin));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al generar reporte de ventas: " + e.getMessage());
        }
    }

    @GetMapping("/clientes")
    public ResponseEntity<?> getReporteClientes() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("clientes", clienteRepositoryCustom.findClientesReporte());
            response.put("cantidadClientes", clienteRepositoryCustom.countClientesRegistrados());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al generar reporte de clientes: " + e.getMessage());
        }
    }

    @GetMapping("/inventario")
    public ResponseEntity<?> getReporteInventario(
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date fechaFin) {

        try {
            Map<String, Object> response = new HashMap<>();
            response.put("inventario", productoRepositoryCustom.findInventarioReporte(fechaInicio, fechaFin));
            response.put("totalInventario", productoRepositoryCustom.getTotalInventario());
            response.put("cantidadProductos", productoRepositoryCustom.getCantidadProductos());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al generar reporte de inventario: " + e.getMessage());
        }
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<?> getReporteStockBajo() {
        try {
            return ResponseEntity.ok(productoRepositoryCustom.findProductosStockBajo());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al generar reporte de stock bajo: " + e.getMessage());
        }
    }

    @GetMapping("/historial-cliente/{clienteId}")
    public ResponseEntity<?> getHistorialCliente(@PathVariable Long clienteId) {
        try {
            // Implementación similar a tu frontend
            Map<String, Object> response = new HashMap<>();
            // Aquí deberías agregar la lógica para obtener el historial del cliente
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener historial del cliente: " + e.getMessage());
        }
    }

    @GetMapping("/exportar/{tipoReporte}")
    public void exportToExcel(@PathVariable String tipoReporte,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date fechaInicio,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date fechaFin,
                              HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + tipoReporte + "_reporte.xlsx");

        Workbook workbook = excelExportService.generateExcelReport(tipoReporte, fechaInicio, fechaFin);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    //Obtiene los 5 productos mas vendidos
    @GetMapping("/top-productos")
    public List<Object[]> obtenerTopProductosVendidos() {
        return detalleVentaRepository.findTopProductosVendidos(PageRequest.of(0, 5));
    }


}