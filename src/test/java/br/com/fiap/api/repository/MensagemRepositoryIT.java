package br.com.fiap.api.repository;

import br.com.fiap.api.model.Mensagem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class MensagemRepositoryIT {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Test
    void devePermitirCriarTabela() {
        var totalDeRegistros = mensagemRepository.count();
        assertThat(totalDeRegistros).isGreaterThan(0);
    }

    @Test
    void devePermitirRegistrarMensagem() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = gerarMensagem();
//        mensagem.setId(id);

        // Act
        var mensagemRecebida = mensagemRepository.save(mensagem);

        // Assert
        assertThat(mensagemRecebida)
                .isInstanceOf(Mensagem.class)
                .isNotNull();
        assertThat(mensagemRecebida.getId()).isEqualTo(id);
        assertThat(mensagemRecebida.getConteudo()).isEqualTo(mensagem.getConteudo());
        assertThat(mensagemRecebida.getConteudo()).isEqualTo(mensagem.getConteudo());

    }

    @Test
    void devePermitirBuscarMensagem() {
        // Arrange
//         var id = UUID.fromString("65b1bbee-c784-4457-be6d-d00b0be5c9e0");
        var id = UUID.randomUUID();
        var mensagem = gerarMensagem();
        mensagem.setId(id);
        registrarMensagem(mensagem);

        // Act
        var mensagemRecebidaOptional = mensagemRepository.findById(id);
        var teste  = mensagemRepository.findAll();

        // Assert
        assertThat(mensagemRecebidaOptional).isPresent();

        mensagemRecebidaOptional.ifPresent(mensagemRecebida -> {
            assertThat(mensagemRecebida.getId()).isEqualTo(id);
        });

    }

    @Test
    void devePermitirRemoverMensagem() {
        // Arrange
        var id = UUID.fromString("592ac344-9f12-40cd-8ed9-1fde6ad9006e");

        // Act
        mensagemRepository.deleteById(id);
        var mensagemRecebidaOptional = mensagemRepository.findById(id);

        // Assert
        assertThat(mensagemRecebidaOptional).isEmpty();

    }

    @Test
    void devePermitirListarMensagens() {
        // Act
        var resultadosObtidos = mensagemRepository.findAll();

        // Assert
        assertThat(resultadosObtidos).hasSizeGreaterThan(0);
    }

    private Mensagem gerarMensagem() {
        return Mensagem.builder()
                .usuario("Jose")
                .conteudo("conteúdo da mensagem")
                .build();
    }

    private Mensagem registrarMensagem(Mensagem mensagem) {
        return mensagemRepository.save(mensagem);
    }
}
