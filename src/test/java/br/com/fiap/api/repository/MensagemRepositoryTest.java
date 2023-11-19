package br.com.fiap.api.repository;

import br.com.fiap.api.model.Mensagem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.api.util.MensagemHelper.gerarMensagem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MensagemRepositoryTest {

    @Mock
    private MensagemRepository mensagemRepository;

    AutoCloseable openMocks;

    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class RegistrarMensagem {
        @Test
        void devePermitirRegistrarMensagem() {
            // Arrange
            var mensagem = gerarMensagem();
            when(mensagemRepository.save(any(Mensagem.class))).thenReturn(mensagem);
            // Act
            var mensagemRegistrada = mensagemRepository.save(mensagem);

            // Assert
            assertThat(mensagemRegistrada)
                    .isNotNull()
                    .isEqualTo(mensagem);
            verify(mensagemRepository, times(1)).save(any(Mensagem.class));
        }
    }

    @Nested
    class BuscarMensagem {
        @Test
        void devePermitirBuscarMensagem() {
            // Arrange
            var id = UUID.randomUUID();
            var mensagem = gerarMensagem();
            mensagem.setId(id);
            when(mensagemRepository.findById(any(UUID.class))).thenReturn(Optional.of(mensagem));

            // Act
            var mensagemRecebidaOpcional = mensagemRepository.findById(id);

            // Assert
            assertThat(mensagemRecebidaOpcional)
                    .isPresent()
                    .containsSame(mensagem);

            mensagemRecebidaOpcional.ifPresent(mensagemRecebida -> {
                assertThat(mensagemRecebida.getId()).isEqualTo(mensagem.getId());
                assertThat(mensagemRecebida.getConteudo()).isEqualTo(mensagem.getConteudo());
            });
            verify(mensagemRepository, times(1)).findById(any(UUID.class));

        }
    }

    @Nested
    class RemoverMensagem {
        @Test
        void devePermitirRemoverMensagem() {
            // Arrange
            var id = UUID.randomUUID();
            doNothing().when(mensagemRepository).deleteById(any(UUID.class));

            // Act
            mensagemRepository.deleteById(id);

            // Assert
            verify(mensagemRepository, times(1)).deleteById(any(UUID.class));
        }
    }

    @Nested
    class ListarMensagens {
        @Test
        void devePermitirListarMensagens() {
            // Arrange
            var mensagem1 = gerarMensagem();
            var mensagem2 = gerarMensagem();
            var listaMensagens = Arrays.asList(mensagem1, mensagem2);
            when(mensagemRepository.findAll()).thenReturn(listaMensagens);

            // Act
            var mensagensRecebidas = mensagemRepository.findAll();

            // Assert
            assertThat(mensagensRecebidas)
                    .hasSize(2)
                    .containsExactlyInAnyOrder(mensagem1, mensagem2);
            verify(mensagemRepository, times(1)).findAll();

        }

        @Test
        void devePermitirListarMensagens_ComPaginacao() {
            // Arrange
            Page<Mensagem> listaDeMensagens = new PageImpl<>(Arrays.asList(gerarMensagem(), gerarMensagem()));
            when(mensagemRepository.listarMensagens(any(Pageable.class))).thenReturn(listaDeMensagens);

            // Act
            var resultadoObtido = mensagemRepository.listarMensagens(Pageable.unpaged());

            // Assert
            assertThat(resultadoObtido).hasSize(2);
            assertThat(resultadoObtido.getContent()).asList().allSatisfy(mensagem -> {
                assertThat(mensagem).isNotNull().isInstanceOf(Mensagem.class);
            });
            verify(mensagemRepository, times(1)).listarMensagens(any(Pageable.class));
        }
    }

}
