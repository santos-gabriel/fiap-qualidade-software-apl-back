package br.com.fiap.api.service;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.repository.MensagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MensagemServiceImpl implements MensagemService {

    private final MensagemRepository repository;

    @Override
    public Mensagem registrarMensagem(Mensagem mensagem) {
//        if (Objects.isNull(mensagem)){
//
//        }
        mensagem.setId(UUID.randomUUID());
        return repository.save(mensagem);
    }

    @Override
    public Mensagem buscarMensagem(UUID id) {
        return repository.findById(id).orElseThrow(() -> new MensagemNotFoundException("Mensagem não encontrada"));
    }

    @Override
    public Mensagem alterarMensagem(UUID id, Mensagem mensagemAtualizada) {
        var mensagem = buscarMensagem(id);
        if (!mensagem.getId().equals(id)) {
            throw new MensagemNotFoundException("Mensagem atualizada não apresenta o ID correto");
        }
        mensagem.setConteudo(mensagemAtualizada.getConteudo());
        return repository.save(mensagem);
    }

    @Override
    public boolean removerMensagem(UUID id) {
        return false;
    }
}
