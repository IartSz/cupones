package com.BookPoint.cupones.controller;

import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> crearCupon(@RequestBody CuponDescuento cupon) {
        try {
            CuponDescuento nuevo = cuponService.guardar(cupon);
            if (nuevo == null) {
                return new ResponseEntity<>("No se pudo crear el cupón", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Error al crear el cupón", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> buscar(@PathVariable String codigo) {
        try {
            Optional<CuponDescuento> cupon = cuponService.buscarPorCodigo(codigo);
            if (cupon.isEmpty()) {
                return new ResponseEntity<>("Cupón no encontrado", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(cupon.get(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Error al buscar cupón", HttpStatus.CONFLICT);
        }
    }
}
