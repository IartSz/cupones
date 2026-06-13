package com.BookPoint.cupones.service;

import com.BookPoint.cupones.model.CuponDescuento;
import com.BookPoint.cupones.repository.CuponRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuponServiceTest {
    @Mock
    private CuponRepository cuponRepository;

    @InjectMocks
    private CuponService cuponService;

    @Test
    void testGuardar() {
        CuponDescuento cupon = new CuponDescuento(null, "TEST10", 10.0, true, LocalDate.now());
        CuponDescuento guardado = new CuponDescuento(1L, "TEST10", 10.0, true, LocalDate.now());


        when(cuponRepository.save(cupon)).thenReturn(guardado);

        CuponDescuento resultado = cuponService.guardar(cupon);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCupon());
        assertEquals("TEST10", resultado.getCodigo());
        assertEquals(10.0, resultado.getPorcentajeDescuento());
        assertTrue(resultado.getActivo());
        verify(cuponRepository, times(1)).save(cupon);
    }

    @Test
    void testListar() {
        CuponDescuento cupon1 = new CuponDescuento(1L, "TEST10", 10.0, true, LocalDate.now());
        CuponDescuento cupon2 = new CuponDescuento(2L, "TEST20", 20.0, true, LocalDate.now());
        when(cuponRepository.findAll()).thenReturn(Arrays.asList(cupon1, cupon2));
        List<CuponDescuento> resultado = cuponService.listar();

        assertEquals(2, resultado.size());
        assertEquals("TEST10", resultado.get(0).getCodigo());
        assertEquals("TEST20", resultado.get(1).getCodigo());

        verify(cuponRepository, times(1)).findAll();
    }

    @Test
    void buscarPorCodigo() {
        CuponDescuento cupon = new CuponDescuento(1L, "TEST10", 10.0, true, LocalDate.now());
        
        when(cuponRepository.findByCodigo("TEST10")).thenReturn(Optional.of(cupon));
        Optional<CuponDescuento> resultado = cuponService.buscarPorCodigo("TEST10");

        assertTrue(resultado.isPresent());
        assertEquals("TEST10", resultado.get().getCodigo());
        assertEquals(10.0, resultado.get().getPorcentajeDescuento());
        assertTrue(resultado.get().getActivo());

        verify(cuponRepository, times(1)).findByCodigo("TEST10");
    }

    @Test
    void buscarPorCodigoNoEncontrado() {
        when(cuponRepository.findByCodigo("NOEXISTE")).thenReturn(Optional.empty());
        Optional<CuponDescuento> resultado = cuponService.buscarPorCodigo("NOEXISTE");

        assertFalse(resultado.isPresent());

        verify(cuponRepository, times(1)).findByCodigo("NOEXISTE");
    }

    @Test
    void actualizar() {
        CuponDescuento existente = new CuponDescuento(1L, "TEST10", 10.0, true, LocalDate.now());
        CuponDescuento datosNuevos = new CuponDescuento(null, "TEST15", 15.0, true, LocalDate.now());
        CuponDescuento actualizado = new CuponDescuento(1L, "TEST15", 15.0, true, LocalDate.now());

        when(cuponRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(cuponRepository.save(existente)).thenReturn(actualizado);

        CuponDescuento resultado = cuponService.actualizar(1L, datosNuevos);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCupon());
        assertEquals("TEST15", resultado.getCodigo());
        assertEquals(15.0, resultado.getPorcentajeDescuento());
        assertTrue(resultado.getActivo());

        verify(cuponRepository, times(1)).findById(1L);
        verify(cuponRepository, times(1)).save(existente);
    }

    @Test
       void testActualizarCuponNoExistente(){
            CuponDescuento datosNuevos = new CuponDescuento(null, "TEST15", 15.0, true, LocalDate.now());
            when(cuponRepository.findById(99L)).thenReturn(Optional.empty());
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cuponService.actualizar(99L, datosNuevos));
                
            assertEquals("Cupón no encontrado", exception.getMessage());

            verify(cuponRepository, times(1)).findById(99L);
            verify(cuponRepository, times(0)).save(any(CuponDescuento.class));
    }

    @Test
    void eliminarCupon() {
        doNothing().when(cuponRepository).deleteById(1L);
        cuponService.eliminarCupon(1L);
        verify(cuponRepository, times(1)).deleteById(1L);
    }

}
