package com.BookPoint.cupones.controller;

import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.BookPoint.cupones.model.CuponDescuento;
import com.BookPoint.cupones.service.CuponService;

@RestController
@RequestMapping("/api/cupones")
public class CuponController {
    @Autowired
    private CuponService cuponService;

    @PostMapping
    public CuponDescuento crearCupon(@RequestBody CuponDescuento cupon) {
        return cuponService.guardar(cupon);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> buscar(@PathVariable String codigo){
        Optional<CuponDescuento> cupon = cuponService.buscarPorCodigo(codigo);

        if(cupon.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cupon.get());
    }
}
