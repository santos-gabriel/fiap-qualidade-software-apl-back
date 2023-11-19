package br.com.fiap.api.controller;


import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.service.MensagemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static br.com.fiap.api.util.MensagemHelper.gerarMensagem;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MensagemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MensagemService mensagemService;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        MensagemController mensagemController = new MensagemController(mensagemService);
        mockMvc = MockMvcBuilders.standaloneSetup(mensagemController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class RegistarMensagem {
        @Test
        void devePermitirRegistrarMensagem() throws Exception {
            // Arrange
            var mensagem = gerarMensagem();
            when(mensagemService.registrarMensagem(any(Mensagem.class))).thenAnswer(i -> i.getArgument(0));

            // Act & Assert
            mockMvc.perform(
                    post("/mensagens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagem))
            ).andExpect(status().isCreated());

            verify(mensagemService, times(1)).registrarMensagem(any(Mensagem.class));
        }
    }

    @Nested
    class BuscarMensagem {
        @Test
        void devePermitirBuscarMensagem() throws Exception {
            // Arrange
            var mensagem = gerarMensagem();
            var id = UUID.fromString("2147d814-cc0c-4235-8c2f-bfb5fe7083ea");
            when(mensagemService.buscarMensagem(any(UUID.class))).thenReturn(mensagem);

            // Act & Assert
            mockMvc.perform(get("/mensagens/{id}", id)).andExpect(status().isOk());

            verify(mensagemService, times(1)).buscarMensagem(any(UUID.class));

        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExiste() throws Exception {
            var id = UUID.fromString("c0740540-489f-4f0e-a135-e2cd5b8ce177");
            when(mensagemService.buscarMensagem(id)).thenThrow(MensagemNotFoundException.class);

            mockMvc.perform(get("/mensagens/{id}", id)).andExpect(status().isBadRequest());

            verify(mensagemService, times(1)).buscarMensagem(id);


        }
    }

    @Nested
    class AlterarMensagem {
        @Test
        void devePermitirAlterarMensagem() throws Exception {
            var id = UUID.randomUUID();
            var mensagem = gerarMensagem();
            mensagem.setId(id);
            when(mensagemService.alterarMensagem(any(UUID.class), any(Mensagem.class))).thenAnswer(i -> i.getArgument(1));

            mockMvc.perform(put("/mensagens/{id}", id).contentType(MediaType.APPLICATION_JSON).content(asJsonString(mensagem))).andExpect(status().isAccepted());

            verify(mensagemService, times(1)).alterarMensagem(id, mensagem);

        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
            fail("teste não implementado");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
            fail("teste não implementado");
        }
    }

    @Nested
    class RemoverMensagem {
        @Test
        void devePermitirRemoverMensagem() {
            fail("teste não implementado");
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
            fail("teste não implementado");
        }
    }

    @Nested
    class ListarMensagens {
        @Test
        void devePermitirListarMensagens() {
            fail("teste não implementado");
        }
    }

    public static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

}
