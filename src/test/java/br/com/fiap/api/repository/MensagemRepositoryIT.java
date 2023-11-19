package br.com.fiap.api.repository;

import br.com.fiap.api.model.Mensagem;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.UUID;

import static br.com.fiap.api.util.MensagemHelper.gerarMensagem;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class MensagemRepositoryIT {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Test
    void devePermitirCriarTabela() {
        var totalDeRegistros = mensagemRepository.count();
        assertThat(totalDeRegistros).isPositive();
    }

    @Nested
    class RegistrarMensagem {
        @Test
        void devePermitirRegistrarMensagem() {
            // Arrange
            var id = UUID.randomUUID();
            var mensagem = gerarMensagem();
            mensagem.setId(id);

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
    }

    @Nested
    class BuscarMensagem {
        @Test
        void devePermitirBuscarMensagem() {
            // Arrange
            //         var id = UUID.fromString("af87b174-0343-48fd-9069-46ca2e3df9e7");
            var id = UUID.randomUUID();

            var mensagem = gerarMensagem();
            mensagem.setId(id);
            registrarMensagem(mensagem);

            // Act
            var mensagemRecebidaOptional = mensagemRepository.findById(id);
            var teste = mensagemRepository.findAll();

            // Assert
            assertThat(mensagemRecebidaOptional).isPresent();

            mensagemRecebidaOptional.ifPresent(mensagemRecebida -> {
                assertThat(mensagemRecebida.getId()).isEqualTo(id);
            });

        }
    }

    @Nested
    class RemoverMensagem {
        @Test
        void devePermitirRemoverMensagem() {
            // Arrange
            // var id = UUID.fromString("592ac344-9f12-40cd-8ed9-1fde6ad9006e");
            var id = UUID.randomUUID();
            var mensagem = gerarMensagem();
            mensagem.setId(id);
            registrarMensagem(mensagem);

            // Act
            mensagemRepository.deleteById(id);
            var mensagemRecebidaOptional = mensagemRepository.findById(id);

            // Assert
            assertThat(mensagemRecebidaOptional).isEmpty();

        }
    }

    @Nested
    class ListarMensagens {
        @Test
        void devePermitirListarMensagens() {
            // Act
            var resultadosObtidos = mensagemRepository.findAll();

            // Assert
            assertThat(resultadosObtidos).hasSizeGreaterThan(0);
        }

        @Test
        void devePermitirListarMensagens_ComPaginacao() {
            // Act
            var resultadosObtidos = mensagemRepository.listarMensagens(Pageable.unpaged());

            // Assert
            assertThat(resultadosObtidos).hasSizeGreaterThan(0);
            assertThat(resultadosObtidos.getContent()).asList().allSatisfy(mensagem -> {
                assertThat(mensagem).isNotNull().isInstanceOf(Mensagem.class);
            });
        }
    }

    private Mensagem registrarMensagem(Mensagem mensagem) {
        return mensagemRepository.save(mensagem);
    }
}
