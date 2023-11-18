package br.com.fiap.api.service;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.repository.MensagemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

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

    @Test
    void devePermitirBuscarMenssagem() {
        // Arrange
        var id = UUID.randomUUID();
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
    void deveGerarExcecao_QuandoBuscarMenssagem_IdNaoExiste() {
        // Arrange
        var id = UUID.randomUUID();
        when(mensagemRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());
        //Assert
        assertThatThrownBy(() -> mensagemService.buscarMensagem(id)).isInstanceOf(MensagemNotFoundException.class).hasMessage("Mensagem não encontrada");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void devePermitirAlterarMensagem() {
        // Arrange
        var id = UUID.randomUUID();

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
    void deveGerarExecao_QuandoAlterarMensagem_IdNaoApresentaMensagem() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = gerarMensagem();
        when(mensagemRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> mensagemService.alterarMensagem(id, gerarMensagem())).isInstanceOf(MensagemNotFoundException.class).hasMessage("Mensagem não encontrada");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).save(any(Mensagem.class));
    }

    @Test
    void devePermitirRemoverMensagem() {
        fail("teste não implementado");
    }

    @Test
    void devePermitirListarMensagens() {
        fail("teste não implementado");
    }

    private Mensagem gerarMensagem() {
        return Mensagem.builder()
                .usuario("Jose")
                .conteudo("conteúdo da mensagem")
                .build();
    }

}
