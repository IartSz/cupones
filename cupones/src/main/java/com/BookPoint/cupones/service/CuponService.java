package com.BookPoint.cupones.service;

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

    public Optional<CuponDescuento> buscarPorCodigo(String codigo){
        return cuponRepository.findByCodigo(codigo);
    }
}
