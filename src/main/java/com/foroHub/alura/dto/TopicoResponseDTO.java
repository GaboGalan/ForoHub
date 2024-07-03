package com.foroHub.alura.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopicoResponseDTO {

    private Long id;
    private String titulo;
    private String mensaje;
    private LocalDateTime fecha;
    private String autorNombre;

    public TopicoResponseDTO(com.foroHub.alura.model.Topico topico) {
        this.id = topico.getId();
        this.titulo = topico.getTitulo();
        this.mensaje = topico.getMensaje();
        this.fecha = topico.getFecha();
        this.autorNombre = topico.getUsuario().getNombre();
    }
}