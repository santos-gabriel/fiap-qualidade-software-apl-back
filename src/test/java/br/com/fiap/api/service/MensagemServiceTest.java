package br.com.fiap.api.service;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.repository.MensagemRepository;
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
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MensagemServiceTest {

    private MensagemService mensagemService;
    @Mock
    private MensagemRepository mensagemRepository;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        mensagemService = new MensagemServiceImpl(mensagemRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class RegistarMensagem {
        @Test
        void devePermitirRegistrarMensagem() {
            // Arrange
            var mensagem = gerarMensagem();
            when(mensagemRepository.save(any(Mensagem.class)))
                    .thenAnswer(i -> i.getArgument(0));
            // Act
            var mensageRegistrada = mensagemService.registrarMensagem(mensagem);
            // Assert
            assertThat(mensageRegistrada).isInstanceOf(Mensagem.class).isNotNull();
            assertThat(mensageRegistrada.getConteudo()).isEqualTo(mensagem.getConteudo());
            assertThat(mensageRegistrada.getUsuario()).isEqualTo(mensagem.getUsuario());
            assertThat(mensagem.getId()).isNotNull();
            verify(mensagemRepository, times(1)).save(any(Mensagem.class));
        }
    }

    @Nested
    class BuscarMensagem {
        @Test
        void devePermitirBuscarMensagem() {
            // Arrange
            var id = UUID.fromString("7ed5adbc-9597-423a-9918-16aa1c79a8a9");
            var mensagem = gerarMensagem();
            mensagem.setId(id);
            when(mensagemRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(mensagem));
            // Act
            var mensagemObtida = mensagemService.buscarMensagem(id);

            //Assert
            assertThat(mensagemObtida).isEqualTo(mensagem);
            verify(mensagemRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExiste() {
            // Arrange
            var id = UUID.fromString("1c5f5969-1d19-49b7-8dc3-bc6cff9e95b7");
            when(mensagemRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.empty());
            //Assert
            assertThatThrownBy(() -> mensagemService.buscarMensagem(id)).isInstanceOf(MensagemNotFoundException.class).hasMessage("Mensagem não encontrada");
            verify(mensagemRepository, times(1)).findById(any(UUID.class));
        }
    }

    @Nested
    class AlterarMensagem {
        @Test
        void devePermitirAlterarMensagem() {
            // Arrange
            var id = UUID.fromString("9e9b18a3-6072-49e5-b7b4-6d517d5dc3e4");

            var mensagemAntiga = gerarMensagem();
            mensagemAntiga.setId(id);

            var mensagemNova = new Mensagem();
            mensagemNova.setId(mensagemAntiga.getId());
            mensagemNova.setUsuario(mensagemAntiga.getUsuario());
            mensagemNova.setConteudo("ABCD 123456465465465654654");

            when(mensagemRepository.findById(any(UUID.class))).thenReturn(Optional.of(mensagemAntiga));
            when(mensagemRepository.save(any(Mensagem.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            var mensagemObtida = mensagemService.alterarMensagem(id, mensagemNova);

            // Assert
            assertThat(mensagemObtida).isInstanceOf(Mensagem.class).isNotNull();
            assertThat(mensagemObtida.getId()).isEqualTo(mensagemNova.getId());
            assertThat(mensagemObtida.getUsuario()).isEqualTo(mensagemNova.getUsuario());
            assertThat(mensagemObtida.getConteudo()).isEqualTo(mensagemNova.getConteudo());
            verify(mensagemRepository, times(1)).findById(any(UUID.class));
            verify(mensagemRepository, times(1)).save(any(Mensagem.class));

        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
            // Arrange
            var id = UUID.fromString("caef7ed9-a628-4112-849f-07f9318e0301");
            var mensagem = gerarMensagem();
            when(mensagemRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
            // Act & Assert
            assertThatThrownBy(() -> mensagemService.alterarMensagem(id, gerarMensagem())).isInstanceOf(MensagemNotFoundException.class).hasMessage("Mensagem não encontrada");
            verify(mensagemRepository, times(1)).findById(any(UUID.class));
            verify(mensagemRepository, never()).save(any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
            // Arrange
            var id = UUID.fromString("4c2ee106-56d2-4ce2-ab78-06aff14494b1");
            var mensagemAntiga = gerarMensagem();
            mensagemAntiga.setId(id);

            var mensagemNova = gerarMensagem();
            mensagemNova.setId(UUID.fromString("a8035997-6b1e-4b7f-9e5d-89a40b661cb9"));
            mensagemNova.setConteudo("ABC 123");

            when(mensagemRepository.findById(id)).thenReturn(Optional.of(mensagemAntiga));

            // Act & Assert
            assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemNova)).isInstanceOf(MensagemNotFoundException.class).hasMessage("Mensagem atualizada não apresenta o ID correto");
            verify(mensagemRepository, times(1)).findById(any(UUID.class));
            verify(mensagemRepository, never()).save(any(Mensagem.class));
        }
    }

    @Nested
    class RemoverMensagem {
        @Test
        void devePermitirRemoverMensagem() {
            // Arrange
            var id = UUID.fromString("fa77fa5c-1c8f-471e-a96d-ff7b3086e94e");
            var mensagem = gerarMensagem();
            mensagem.setId(id);
            when(mensagemRepository.findById(id)).thenReturn(Optional.of(mensagem));
            doNothing().when(mensagemRepository).deleteById(id);

            // Act
            var mensagemFoiRemovida = mensagemService.removerMensagem(id);

            // Assert
            assertThat(mensagemFoiRemovida).isTrue();
            verify(mensagemRepository, times(1)).findById(any(UUID.class));
            verify(mensagemRepository, times(1)).deleteById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
            // Arrange
            var id = UUID.fromString("3fc6b75c-df89-427f-830b-4cbd516a0003");
            when(mensagemRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> mensagemService.removerMensagem(id)).isInstanceOf(MensagemNotFoundException.class).hasMessage("Mensagem não encontrada");
            verify(mensagemRepository, times(1)).findById(any(UUID.class));
            verify(mensagemRepository, never()).deleteById(any(UUID.class));
       }
    }

    @Nested
    class ListarMensagens {
        @Test
        void devePermitirListarMensagens() {
            // Arrange
            Page<Mensagem> listaDeMensagens = new PageImpl<>(Arrays.asList(gerarMensagem(), gerarMensagem()));
            when(mensagemRepository.listarMensagens(any(Pageable.class))).thenReturn(listaDeMensagens);

            // Act
            var resultadoObtido = mensagemService.listarMensagens(Pageable.unpaged());

            // Assert
            assertThat(resultadoObtido).hasSize(2);
            assertThat(resultadoObtido.getContent()).asList().allSatisfy(mensagem -> {
                assertThat(mensagem).isNotNull().isInstanceOf(Mensagem.class);
            });
            verify(mensagemRepository, times(1)).listarMensagens(any(Pageable.class));
        }
    }

}
