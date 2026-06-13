package com.BookPoint.cupones.controller;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;

import com.BookPoint.cupones.model.CuponDescuento;
import com.BookPoint.cupones.service.CuponService;

@RestController
@RequestMapping("/api/v1/cupones")
public class CuponController {
    @Autowired
    private CuponService cuponService;

    @PostMapping
    public ResponseEntity<EntityModel<CuponDescuento>> crearCupon(@RequestBody CuponDescuento cupon) {
        CuponDescuento nuevo = cuponService.guardar(cupon);
        EntityModel<CuponDescuento> cuponModel = EntityModel.of(nuevo,
                linkTo(methodOn(CuponController.class).buscar(nuevo.getCodigo())).withSelfRel(),
                linkTo(methodOn(CuponController.class).listar()).withRel("cupones"));
        return ResponseEntity.status(HttpStatus.CREATED).body(cuponModel);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<EntityModel<CuponDescuento>> buscar(@PathVariable String codigo) {
        return cuponService.buscarPorCodigo(codigo)
                .map(cupon -> ResponseEntity.ok(EntityModel.of(cupon,
                        linkTo(methodOn(CuponController.class).buscar(codigo)).withSelfRel(),
                        linkTo(methodOn(CuponController.class).listar()).withRel("cupones"))))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public CollectionModel<EntityModel<CuponDescuento>> listar() {
        List<CuponDescuento> cupones = cuponService.listar();
        List<EntityModel<CuponDescuento>> cuponModels = cupones.stream()
                .map(cupon -> EntityModel.of(cupon,
                        linkTo(methodOn(CuponController.class)
                                .buscar(cupon.getCodigo())).withSelfRel()))
                .toList();
        return CollectionModel.of(cuponModels,
                linkTo(methodOn(CuponController.class).listar()).withSelfRel());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<CuponDescuento>> actualizar(@PathVariable Long id,
            @RequestBody CuponDescuento cupon) {
        try {
            CuponDescuento actualizado = cuponService.actualizar(id, cupon);
            return ResponseEntity.ok(EntityModel.of(actualizado,
                    linkTo(methodOn(CuponController.class).buscar(actualizado.getCodigo())).withSelfRel(),
                    linkTo(methodOn(CuponController.class).listar()).withRel("cupones")));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCupon(@PathVariable Long id) {
        cuponService.eliminarCupon(id);
        return ResponseEntity.noContent().build();
    }
}
