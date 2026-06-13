package com.BookPoint.cupones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.BookPoint.cupones.model.CuponDescuento;
import com.BookPoint.cupones.repository.CuponRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CuponControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CuponRepository cuponRepository;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void cleanDb() {
        cuponRepository.deleteAll();
    }

    @Test
    void testCrearYObtenerCupon() throws Exception{
        CuponDescuento cupon = new CuponDescuento(null, "TEST10", 10.0, true, LocalDate.now());

        mockMvc.perform(post("/api/v1/cupones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cupon)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("TEST10"))
                .andExpect(jsonPath("$.porcentajeDescuento").value(10.0))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.fechaExpiracion").exists());
        
        mockMvc.perform(get("/api/v1/cupones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.cuponDescuentoList[0].codigo").value("TEST10"))
                .andExpect(jsonPath("$._embedded.cuponDescuentoList[0].porcentajeDescuento").value(10.0))
                .andExpect(jsonPath("$._embedded.cuponDescuentoList[0].activo").value(true))
                .andExpect(jsonPath("$._embedded.cuponDescuentoList[0].fechaExpiracion").exists())

                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    void testEliminarCupon() throws Exception {
        CuponDescuento cupon = new CuponDescuento(null, "TEST20", 20.0, true, LocalDate.now());
        CuponDescuento guardado = cuponRepository.save(cupon);

        mockMvc.perform(delete("/api/v1/cupones/{id}", guardado.getIdCupon()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/cupones/" + guardado.getCodigo()))
                .andExpect(status().isNotFound());    
    }

    @Test
    void testActualizarCupon() throws Exception{
        CuponDescuento cupon = new CuponDescuento(null, "TEST10", 10.0, true, LocalDate.now());
        CuponDescuento guardado = cuponRepository.save(cupon);

        CuponDescuento actualizado = new CuponDescuento(null, "TEST20", 20.0, true, LocalDate.now());

        mockMvc.perform(put("/api/v1/cupones/" + guardado.getIdCupon())
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(actualizado)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.porcentajeDescuento").value(20.0))
               .andExpect(jsonPath("$._links").exists());
               
    }
    
}
