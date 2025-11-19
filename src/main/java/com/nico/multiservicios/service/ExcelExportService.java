// com.nico.multiservicios.service.ExcelExportService
package com.nico.multiservicios.service;

import com.nico.multiservicios.dto.*;
import com.nico.multiservicios.repository.custom.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class ExcelExportService {

    private final VentaRepositoryCustom ventaRepositoryCustom;
    private final ClienteRepositoryCustom clienteRepositoryCustom;
    private final ProductoRepositoryCustom productoRepositoryCustom;

    @Autowired
    public ExcelExportService(VentaRepositoryCustom ventaRepositoryCustom,
                              ClienteRepositoryCustom clienteRepositoryCustom,
                              ProductoRepositoryCustom productoRepositoryCustom) {
        this.ventaRepositoryCustom = ventaRepositoryCustom;
        this.clienteRepositoryCustom = clienteRepositoryCustom;
        this.productoRepositoryCustom = productoRepositoryCustom;
    }

    public Workbook generateExcelReport(String tipoReporte, Date fechaInicio, Date fechaFin) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        switch (tipoReporte.toLowerCase()) {
            case "ventas":
                createVentasSheet(workbook, fechaInicio, fechaFin);
                break;
            case "clientes":
                createClientesSheet(workbook);
                break;
            case "inventario":
                createInventarioSheet(workbook, fechaInicio, fechaFin);
                break;
            case "stockbajo":
                createStockBajoSheet(workbook);
                break;
            default:
                throw new IllegalArgumentException("Tipo de reporte no válido: " + tipoReporte);
        }

        return workbook;
    }

    private void createVentasSheet(Workbook workbook, Date fechaInicio, Date fechaFin) {
        Sheet sheet = workbook.createSheet("Ventas");

        // Crear estilo para encabezados
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        String[] ventasHeaders = {"ID", "Cliente", "Total", "Método de Pago", "Fecha"};
        for (int i = 0; i < ventasHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(ventasHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        // Llenar datos
        List<ReporteVentaDTO> ventas = ventaRepositoryCustom.findVentasReporte(fechaInicio, fechaFin);
        int rowNum = 1;
        for (ReporteVentaDTO venta : ventas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(venta.getId());
            row.createCell(1).setCellValue(venta.getCliente());
            row.createCell(2).setCellValue(venta.getTotal().doubleValue());
            row.createCell(3).setCellValue(venta.getMetodo().toString());
            row.createCell(4).setCellValue(venta.getFecha());
        }

        // Autoajustar columnas
        for (int i = 0; i < ventasHeaders.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createClientesSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Clientes");

        // Estilo para encabezados
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Encabezados
        Row headerRow = sheet.createRow(0);
        String[] clientesHeaders = {"RUC", "Nombre", "Método de Pago", "Última Compra", "Compras Totales"};
        for (int i = 0; i < clientesHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(clientesHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        List<ReporteClienteDTO> clientes = clienteRepositoryCustom.findClientesReporte();
        int rowNum = 1;
        for (ReporteClienteDTO cliente : clientes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(cliente.getRuc());
            row.createCell(1).setCellValue(cliente.getNombre());
            row.createCell(2).setCellValue(cliente.getMetodoPago() != null ? cliente.getMetodoPago().toString() : "N/A");
            row.createCell(3).setCellValue(cliente.getFechaUltimaCompra());
            row.createCell(4).setCellValue(cliente.getComprasTotales());
        }

        // Autoajustar
        for (int i = 0; i < clientesHeaders.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createInventarioSheet(Workbook workbook, Date fechaInicio, Date fechaFin) {
        Sheet sheet = workbook.createSheet("Inventario");

        // Estilo encabezados
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Encabezados
        Row headerRow = sheet.createRow(0);
        String[] inventarioHeaders = {"ID", "Producto", "Precio Venta", "Stock", "Categoría", "Proveedor", "Fecha Adquisición"};
        for (int i = 0; i < inventarioHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(inventarioHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        List<ReporteInventarioDTO> inventario = productoRepositoryCustom.findInventarioReporte(fechaInicio, fechaFin);
        int rowNum = 1;
        for (ReporteInventarioDTO item : inventario) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getId());
            row.createCell(1).setCellValue(item.getProducto());
            row.createCell(2).setCellValue(item.getPrecioVenta());
            row.createCell(3).setCellValue(item.getStock());
            row.createCell(4).setCellValue(item.getCategoria());
            row.createCell(5).setCellValue(item.getProveedor());
            row.createCell(6).setCellValue(item.getFecha());
        }

        // Autoajustar
        for (int i = 0; i < inventarioHeaders.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createStockBajoSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Stock Bajo");

        // Estilo encabezados
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Encabezados
        Row headerRow = sheet.createRow(0);
        String[] stockHeaders = {"ID", "Producto", "Precio Venta", "Stock Actual", "Stock Mínimo", "Proveedor"};
        for (int i = 0; i < stockHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(stockHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        List<ReporteStockBajoDTO> stockBajo = productoRepositoryCustom.findProductosStockBajo();
        int rowNum = 1;
        for (ReporteStockBajoDTO item : stockBajo) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getId());
            row.createCell(1).setCellValue(item.getProducto());
            row.createCell(2).setCellValue(item.getPrecioVenta());
            row.createCell(3).setCellValue(item.getStock());
            row.createCell(4).setCellValue(item.getStockMinimo());
            row.createCell(5).setCellValue(item.getProveedor());
        }

        // Autoajustar
        for (int i = 0; i < stockHeaders.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}