package com.foroHub.alura.controller;

import com.foroHub.alura.dto.TopicoDTO;
import com.foroHub.alura.dto.TopicoResponseDTO;
import com.foroHub.alura.dto.TopicoUpdateDTO;
import com.foroHub.alura.model.Topico;
import com.foroHub.alura.model.Usuario;
import com.foroHub.alura.repository.TopicoRepository;
import com.foroHub.alura.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> crearTopico(@Valid @RequestBody TopicoDTO topicoDTO) {
        // Verificar si el tópico ya existe
        Optional<Topico> topicoExistente = topicoRepository.findByTituloAndMensaje(topicoDTO.getTitulo(), topicoDTO.getMensaje());
        if (topicoExistente.isPresent()) {
            return ResponseEntity.badRequest().body("Tópico duplicado: ya existe un tópico con el mismo título y mensaje.");
        }

        // Buscar el usuario
        Optional<Usuario> usuario = usuarioRepository.findById(topicoDTO.getUsuarioId());
        if (!usuario.isPresent()) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        // Crear y guardar el nuevo tópico
        Topico nuevoTopico = new Topico();
        nuevoTopico.setTitulo(topicoDTO.getTitulo());
        nuevoTopico.setMensaje(topicoDTO.getMensaje());
        nuevoTopico.setUsuario(usuario.get());
        topicoRepository.save(nuevoTopico);

        return ResponseEntity.ok(nuevoTopico);
    }

    @GetMapping
    public Page<TopicoResponseDTO> listarTopicos(@PageableDefault(size = 10) Pageable paginacion) {
        return topicoRepository.findAll(paginacion).map(TopicoResponseDTO::new);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicoResponseDTO> obtenerTopicoPorId(@PathVariable Long id) {
        Optional<Topico> topico = topicoRepository.findById(id);
        if (topico.isPresent()) {
            return ResponseEntity.ok(new TopicoResponseDTO(topico.get()));
        } else {
            return ResponseEntity.status(404).body(new TopicoResponseDTO(null, "No encontrado", null, null, null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTopico(@PathVariable Long id, @Valid @RequestBody TopicoUpdateDTO topicoUpdateDTO) {
        // Verificar si el tópico existe
        Optional<Topico> topicoExistente = topicoRepository.findById(id);
        if (!topicoExistente.isPresent()) {
            return ResponseEntity.status(404).body("No encontrado");
        }

        Topico topico = topicoExistente.get();
        // Verificar si el nuevo título y mensaje ya existen en otro tópico
        Optional<Topico> topicoDuplicado = topicoRepository.findByTituloAndMensaje(topicoUpdateDTO.getTitulo(), topicoUpdateDTO.getMensaje());
        if (topicoDuplicado.isPresent() && !topicoDuplicado.get().getId().equals(id)) {
            return ResponseEntity.badRequest().body("Tópico duplicado: ya existe un tópico con el mismo título y mensaje.");
        }

        // Actualizar los datos del tópico
        topico.setTitulo(topicoUpdateDTO.getTitulo());
        topico.setMensaje(topicoUpdateDTO.getMensaje());
        topicoRepository.save(topico);

        return ResponseEntity.ok(new TopicoResponseDTO(topico));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarTopico(@PathVariable Long id) {
        // Verificar si el tópico existe
        Optional<Topico> topicoExistente = topicoRepository.findById(id);
        if (!topicoExistente.isPresent()) {
            return ResponseEntity.status(404).body("No encontrado");
        }

        // Eliminar el tópico
        topicoRepository.deleteById(id);
        return ResponseEntity.ok("Tópico eliminado exitosamente");
    }
}