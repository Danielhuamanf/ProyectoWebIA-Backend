package com.nico.multiservicios.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
@Entity
@Table (name = "pedidos")//añadiendo test
public class Pedido {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_pedido")
        private Long id;

        @Column(name = "id_proveedor")
        private Long proveedorId;

        @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
        @JsonIgnoreProperties("pedido")
        private List<DetallePedido> detallePedido;

        private String estado;
        private String metodoPago;
        private LocalDate fechaEntrega;
        private Integer total;

        public Pedido() {
        }

        public Pedido(Long id, Long proveedorId, List<DetallePedido> detallePedido, String estado, String metodoPago, LocalDate fechaEntrega, Integer total) {
                this.id = id;
                this.proveedorId = proveedorId;
                this.detallePedido = detallePedido;
                this.estado = estado;
                this.metodoPago = metodoPago;
                this.fechaEntrega = fechaEntrega;
                this.total = total;
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public Long getProveedorId() {
                return proveedorId;
        }

        public void setProveedorId(Long proveedorId) {
                this.proveedorId = proveedorId;
        }

        public List<DetallePedido> getDetallePedido() {
                return detallePedido;
        }

        public void setDetallePedido(List<DetallePedido> detallePedido) {
                this.detallePedido = detallePedido;
        }

        public String getEstado() {
                return estado;
        }

        public void setEstado(String estado) {
                this.estado = estado;
        }

        public String getMetodoPago() {
                return metodoPago;
        }

        public void setMetodoPago(String metodoPago) {
                this.metodoPago = metodoPago;
        }

        public LocalDate getFechaEntrega() {
                return fechaEntrega;
        }

        public void setFechaEntrega(LocalDate fechaEntrega) {
                this.fechaEntrega = fechaEntrega;
        }

        public Integer getTotal() {
                return total;
        }

        public void setTotal(Integer total) {
                this.total = total;
        }
}
