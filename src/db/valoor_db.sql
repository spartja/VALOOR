
-- =====================================================
-- VALOOR - Base de datos (MySQL 8)
-- Sistema de información para gestión y venta de artículos deportivos
-- =====================================================
 
DROP DATABASE IF EXISTS valoor;
CREATE DATABASE valoor CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE valoor;
 
-- 1. USUARIO (incluye el rol: ADMIN o CLIENTE)
CREATE TABLE usuario (
    id_usuario     INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    email          VARCHAR(120) NOT NULL UNIQUE,
    contrasena       VARCHAR(255) NOT NULL,            -- contraseña cifrada (BCrypt)
    telefono       VARCHAR(20),
    direccion      VARCHAR(200),
    rol            ENUM('ADMIN','CLIENTE') NOT NULL DEFAULT 'CLIENTE',
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
 
-- 2. DEPORTE (fútbol, tenis, natación...)
CREATE TABLE deporte (
    id_deporte INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(60) NOT NULL UNIQUE
);
 
-- 3. CATEGORIA (equipo, calzado, ropa)
CREATE TABLE categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(60) NOT NULL UNIQUE
);
 
-- 4. PRODUCTO (N a 1 con deporte y categoría)
CREATE TABLE producto (
    id_producto  INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(120) NOT NULL,
    descripcion  TEXT,
    marca        VARCHAR(60),
    precio       DECIMAL(10,2) NOT NULL CHECK (precio >= 0),
    imagen_url   VARCHAR(255),
    activo       BOOLEAN NOT NULL DEFAULT TRUE,
    id_deporte   INT NOT NULL,
    id_categoria INT NOT NULL,
    FOREIGN KEY (id_deporte)   REFERENCES deporte(id_deporte),
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria),
    INDEX idx_producto_filtros (id_deporte, id_categoria, marca, precio)
);
 
-- 5. INVENTARIO (existencias por producto y talla)
CREATE TABLE inventario (
    id_inventario INT AUTO_INCREMENT PRIMARY KEY,
    id_producto   INT NOT NULL,
    talla         VARCHAR(10) NOT NULL DEFAULT 'UNICA',   -- S, M, L, 40, 42, UNICA...
    stock         INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
    stock_minimo  INT NOT NULL DEFAULT 5,                  -- para alerta de bajo stock
    UNIQUE (id_producto, talla),
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);
 
-- 6. PEDIDO (N a 1 con usuario)
CREATE TABLE pedido (
    id_pedido      INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario     INT NOT NULL,
    fecha          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado         ENUM('PENDIENTE','CONFIRMADO','ENVIADO','ENTREGADO','CANCELADO')
                   NOT NULL DEFAULT 'PENDIENTE',
    metodo_pago    VARCHAR(30) NOT NULL DEFAULT 'REFERENCIA',  -- sin pago real en v1
    direccion_envio VARCHAR(200),
    total          DECIMAL(12,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);
 
-- 7. DETALLE_PEDIDO (N a 1 con pedido y producto)
CREATE TABLE detalle_pedido (
    id_detalle      INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido       INT NOT NULL,
    id_producto     INT NOT NULL,
    talla           VARCHAR(10) NOT NULL,
    cantidad        INT NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(10,2) NOT NULL,       -- precio al momento de la compra
    subtotal        DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (id_pedido)   REFERENCES pedido(id_pedido) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);
 