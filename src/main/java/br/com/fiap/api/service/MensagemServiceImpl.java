package br.com.fiap.api.service;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.repository.MensagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MensagemServiceImpl implements MensagemService {

    private final MensagemRepository repository;

    @Override
    public Mensagem registrarMensagem(Mensagem mensagem) {
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
        if (!mensagem.getId().equals(mensagemAtualizada.getId())) {
            throw new MensagemNotFoundException("Mensagem atualizada não apresenta o ID correto");
        }
        mensagem.setConteudo(mensagemAtualizada.getConteudo());
        return repository.save(mensagem);
    }

    @Override
    public boolean removerMensagem(UUID id) {
        buscarMensagem(id);
        repository.deleteById(id);
        return true;
    }

    @Override
    public Page<Mensagem> listarMensagens(Pageable pageable) {
        return repository.listarMensagens(pageable);
    }
}
