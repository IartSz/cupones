package com.BookPoint.cupones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.BookPoint.cupones.model.CuponDescuento;
import com.BookPoint.cupones.service.CuponService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CuponController.class)
@ActiveProfiles("test")
public class CuponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockitoBean
    private CuponService cuponService;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void testListarCupones() throws Exception {
        CuponDescuento c1 = new CuponDescuento(1L, "TEST10", 10.0, true, LocalDate.now());
        CuponDescuento c2 = new CuponDescuento(2L, "TEST20", 20.0, true, LocalDate.now());

        Mockito.when(cuponService.listar()).thenReturn(Arrays.asList(c1, c2));

        mockMvc.perform(get("/api/v1/cupones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.cuponDescuentoList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.cuponDescuentoList[0].codigo", is("TEST10")))
                .andExpect(jsonPath("$._embedded.cuponDescuentoList[1].codigo", is("TEST20")))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    void testGuardarCupon() throws Exception{
        CuponDescuento nuevo = new CuponDescuento(null, "TEST30", 30.0, true, LocalDate.now());
        CuponDescuento guardado = new CuponDescuento(2L, "TEST30", 30.0, true, LocalDate.now());

        Mockito.when(cuponService.guardar(any(CuponDescuento.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/v1/cupones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.codigo", is("TEST30")))
                .andExpect(jsonPath("$.porcentajeDescuento", is(30.0)))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.cupones").exists());
    }

    @Test
    void testBuscarCupon() throws Exception {
        CuponDescuento c = new CuponDescuento(1L, "TEST10", 10.0, true, LocalDate.now());

        Mockito.when(cuponService.buscarPorCodigo("TEST10")).thenReturn(Optional.of(c));

        mockMvc.perform(get("/api/v1/cupones/TEST10"))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.codigo", is("TEST10")))
                .andExpect(jsonPath("$.porcentajeDescuento", is(10.0)))

                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.cupones").exists());
    }

    @Test
    void testBuscarCuponNoExistente() throws Exception {
        Mockito.when(cuponService.buscarPorCodigo("NOEXISTE")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/cupones/NOEXISTE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testActualizarCupon() throws Exception {
        CuponDescuento actualizado = new CuponDescuento(1L, "TEST10", 15.0, true, LocalDate.now());

        Mockito.when(cuponService.actualizar(eq(1L), any(CuponDescuento.class)))
                    .thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/cupones/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.codigo", is("TEST10")))
                .andExpect(jsonPath("$.porcentajeDescuento", is(15.0)))

                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.cupones").exists());
    }

    @Test
    void testActualizarCuponNoExistente() throws Exception {
        CuponDescuento cuponDescuento = new CuponDescuento(99L, "NOEXISTE", 15.0, true, LocalDate.now());

        Mockito.when(cuponService.actualizar(eq(99L), any(CuponDescuento.class)))
                    .thenThrow(new RuntimeException("Cupón no encontrado"));

        mockMvc.perform(put("/api/v1/cupones/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuponDescuento)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarCupon() throws Exception {
        Mockito.doNothing().when(cuponService).eliminarCupon(1L);

        mockMvc.perform(delete("/api/v1/cupones/1"))
                .andExpect(status().isNoContent());
    }

    
}
