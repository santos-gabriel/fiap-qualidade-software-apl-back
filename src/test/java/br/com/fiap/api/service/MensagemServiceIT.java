package br.com.fiap.api.service;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.repository.MensagemRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static br.com.fiap.api.util.MensagemHelper.gerarMensagem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@ActiveProfiles("test")
class MensagemServiceIT {

    @Autowired
    private MensagemRepository mensagemRepository;
    @Autowired
    private MensagemService mensagemService;

    @Nested
    class RegistrarMensagem {
        @Test
        void devePermitirRegistrarMensagem() {
            // Arrange
            var mensagem = gerarMensagem();
            // Act
            var resultadoObtido = mensagemService.registrarMensagem(mensagem);

            // Assert
            assertThat(resultadoObtido).isNotNull().isInstanceOf(Mensagem.class);
            assertThat(resultadoObtido.getId()).isNotNull();
            assertThat(resultadoObtido.getDataCriacao()).isNotNull();
            assertThat(resultadoObtido.getGostei()).isZero();
        }
    }

    @Nested
    class BuscarMensagem {
        @Test
        void devePermitirBuscarMensagem() {
            // Arrange
            var mensagem = gerarMensagem();
            var mensagemSalva = mensagemService.registrarMensagem(mensagem);

            // Act
            var resultadoObtido = mensagemService.buscarMensagem(mensagemSalva.getId());

            // Assert
            assertThat(resultadoObtido).isNotNull().isInstanceOf(Mensagem.class);
            assertThat(resultadoObtido.getId()).isNotNull().isEqualTo(mensagemSalva.getId());
            assertThat(resultadoObtido.getUsuario()).isNotNull().isEqualTo(mensagemSalva.getUsuario());
            assertThat(resultadoObtido.getConteudo()).isNotNull().isEqualTo(mensagemSalva.getConteudo());
            assertThat(resultadoObtido.getDataCriacao()).isNotNull().isEqualTo(mensagemSalva.getDataCriacao());
            assertThat(resultadoObtido.getGostei()).isEqualTo(mensagemSalva.getGostei());
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExiste() {
            var id = UUID.randomUUID();
            assertThatThrownBy(() -> mensagemService.buscarMensagem(id)).isInstanceOf(MensagemNotFoundException.class).hasMessage("Mensagem não encontrada");
        }
    }

    @Nested
    class AlterarMensagem {

        @Test
        void devePermitirAlterarMensagem() {
            var id = mensagemService.listarMensagens(Pageable.unpaged()).stream().findFirst().get().getId();

            var mensagem = Mensagem.builder()
                    .id(id)
                    .conteudo("TESTAED")
                    .build();

            var resultadoObtido = mensagemService.alterarMensagem(id, mensagem);

            assertThat(resultadoObtido.getId()).isEqualTo(mensagem.getId());
            assertThat(resultadoObtido.getConteudo()).isEqualTo(mensagem.getConteudo());
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
            var id = UUID.randomUUID();
            var mensagem = gerarMensagem();

            assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagem)).isInstanceOf(MensagemNotFoundException.class).hasMessage("Mensagem não encontrada");

        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
            var mensagem = gerarMensagem();
            var mensagemAntiga = mensagemService.registrarMensagem(mensagem);
            var mensagemNova = new Mensagem();
            mensagemNova.setId(UUID.randomUUID());
            mensagemNova.setConteudo("asdfasdfasfdadsfasf");

            assertThatThrownBy(() -> mensagemService.alterarMensagem(mensagemAntiga.getId(), mensagemNova)).isInstanceOf(MensagemNotFoundException.class).hasMessage("Mensagem atualizada não apresenta o ID correto");

        }
    }

    @Nested
    class RemoverMensagem {

        @Test
        void devePermitirRemoverMensagem() {
            var mensagem = mensagemService.registrarMensagem(gerarMensagem());
            var resultadoObtido = mensagemService.removerMensagem(mensagem.getId());
            assertThat(resultadoObtido).isTrue();
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
            var mensagem = mensagemService.registrarMensagem(gerarMensagem());
            assertThatThrownBy(() -> mensagemService.removerMensagem(UUID.randomUUID())).isInstanceOf(MensagemNotFoundException.class).hasMessage("Mensagem não encontrada");
        }
    }

    @Nested
    class ListarMensagens {
        @Test
        void devePermitirListarMensagens() {
            var mensagens = mensagemService.listarMensagens(Pageable.unpaged());
            assertThat(mensagens).isInstanceOf(Page.class);
            assertThat(mensagens).hasSizeGreaterThan(0);
            assertThat(mensagens.getContent()).asList().allSatisfy(mensagem -> {
                assertThat(mensagem).isNotNull().isInstanceOf(Mensagem.class);
            });
        }
    }

}
