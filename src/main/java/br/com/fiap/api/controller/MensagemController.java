package br.com.fiap.api.controller;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.service.MensagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("mensagens")
@RequiredArgsConstructor
public class MensagemController {

    private final MensagemService mensagemService;

    @PostMapping
    public ResponseEntity<Mensagem> registrarMensagem(@RequestBody Mensagem mensagem) {
        var mensagemRegistrada = mensagemService.registrarMensagem(mensagem);
        return new ResponseEntity<>(mensagemRegistrada, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarMensagem(@PathVariable String id) {
        try {
            var mensagemObtida = mensagemService.buscarMensagem(UUID.fromString(id));
            return new ResponseEntity<>(mensagemObtida, HttpStatus.OK);
        } catch (MensagemNotFoundException mensagemNotFoundException) {
            return new ResponseEntity<>("ID Inválido", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> alterarMensagem(@PathVariable String id, @RequestBody Mensagem mensagem) {
        try {
            var mensagemAtualizada = mensagemService.alterarMensagem(UUID.fromString(id), mensagem);
            return new ResponseEntity<>(mensagemAtualizada, HttpStatus.ACCEPTED);
        } catch (MensagemNotFoundException mensagemNotFoundException) {
            return new ResponseEntity<>("ID Inválido", HttpStatus.BAD_REQUEST);
        }

    }

}
