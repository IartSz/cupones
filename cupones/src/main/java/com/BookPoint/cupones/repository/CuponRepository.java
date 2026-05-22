package com.BookPoint.cupones.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.BookPoint.cupones.model.CuponDescuento;

public interface CuponRepository extends JpaRepository<CuponDescuento, Long>{
    Optional<CuponDescuento> findByCodigo(String codigo);
}
