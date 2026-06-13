package com.BookPoint.cupones.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.BookPoint.cupones.model.CuponDescuento;
import com.BookPoint.cupones.repository.CuponRepository;

@Service
public class CuponService {
    @Autowired
    private CuponRepository cuponRepository;

    public CuponDescuento guardar(CuponDescuento cupon){
        return cuponRepository.save(cupon);
    }

    public List<CuponDescuento> listar(){
        return cuponRepository.findAll();
    }

    public void eliminarCupon(Long id){
        cuponRepository.deleteById(id);
    }

    public Optional<CuponDescuento> buscarPorCodigo(String codigo){
        return cuponRepository.findByCodigo(codigo);
    }

    public CuponDescuento actualizar(Long id, CuponDescuento cupondescuento){
        CuponDescuento existente = cuponRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Cupón no encontrado"));
        
        existente.setCodigo(cupondescuento.getCodigo());
        existente.setPorcentajeDescuento(cupondescuento.getPorcentajeDescuento());

        return cuponRepository.save(existente);
    }
}
