package com.nico.multiservicios.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "proveedores")
public class Proveedor {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "ruc")
        private String ruc;

        @Column(name = "tipo_proveedor")
        private String tipoProveedor;

        @Column(name = "nombre_proveedor")
        private String nombre;

        @Column(name = "codigo_postal")
        private String codigoPostal;

        @Column(name = "pais_proveedor")
        private String paisProveedor;

        private String ciudad;
        private String distrito;
        private String direccion;
        private String rubro;
        private String tipoProducto;
        private String telefono;
        private String correo;

        public Proveedor() {

        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getCorreo() {
                return correo;
        }

        public void setCorreo(String correo) {
                this.correo = correo;
        }

        public String getTelefono() {
                return telefono;
        }

        public void setTelefono(String telefono) {
                this.telefono = telefono;
        }

        public String getTipoProducto() {
                return tipoProducto;
        }

        public void setTipoProducto(String tipoProducto) {
                this.tipoProducto = tipoProducto;
        }

        public String getRubro() {
                return rubro;
        }

        public void setRubro(String rubro) {
                this.rubro = rubro;
        }

        public String getDireccion() {
                return direccion;
        }

        public void setDireccion(String direccion) {
                this.direccion = direccion;
        }

        public String getCiudad() {
                return ciudad;
        }

        public void setCiudad(String ciudad) {
                this.ciudad = ciudad;
        }

        public String getPaisProveedor() {
                return paisProveedor;
        }

        public void setPaisProveedor(String paisProveedor) {
                this.paisProveedor = paisProveedor;
        }

        public String getDistrito() {
                return distrito;
        }

        public void setDistrito(String distrito) {
                this.distrito = distrito;
        }

        public String getCodigoPostal() {
                return codigoPostal;
        }

        public void setCodigoPostal(String codigoPostal) {
                this.codigoPostal = codigoPostal;
        }

        public String getNombre() {
                return nombre;
        }

        public void setNombre(String nombre) {
                this.nombre = nombre;
        }

        public String getTipoProveedor() {
                return tipoProveedor;
        }

        public void setTipoProveedor(String tipoProveedor) {
                this.tipoProveedor = tipoProveedor;
        }

        public String getRuc() {
                return ruc;
        }

        public void setRuc(String ruc) {
                this.ruc = ruc;
        }
}
