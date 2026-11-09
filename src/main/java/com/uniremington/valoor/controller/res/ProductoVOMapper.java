package com.uniremington.valoor.controller.res;

import org.springframework.stereotype.Component;

import com.uniremington.valoor.model.entity.ProductoEntity;
import com.uniremington.valoor.model.vo.ProductoVO;

@Component
public class ProductoVOMapper {

    public ProductoVO toVO(ProductoEntity producto) {
        return new ProductoVO(
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getMarca(),
                producto.getPrecio(),
                producto.getImagenUrl(),
                producto.getActivo(),
                producto.getDeporte().getIdDeporte(),
                producto.getCategoria().getIdCategoria());
    }

    public void updateEntity(ProductoEntity producto, ProductoVO vo) {
        producto.setNombre(vo.getNombre());
        producto.setDescripcion(vo.getDescripcion());
        producto.setMarca(vo.getMarca());
        producto.setPrecio(vo.getPrecio());
        producto.setImagenUrl(vo.getImagenUrl());
        producto.setActivo(vo.getActivo());
    }
}
