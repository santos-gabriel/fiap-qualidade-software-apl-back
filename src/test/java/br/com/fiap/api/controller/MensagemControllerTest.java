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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static br.com.fiap.api.util.MensagemHelper.gerarMensagem;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MensagemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MensagemService mensagemService;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        MensagemController mensagemController = new MensagemController(mensagemService);
        mockMvc = MockMvcBuilders.standaloneSetup(mensagemController).addFilter((request, response, chain) -> {
            response.setCharacterEncoding("UTF-8");
            chain.doFilter(request, response);
        }).build();
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

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_PayloadXML() throws Exception {
            var xmlPayload = "<mensagem><usuario>Ana</usuario><conteudo>Mensagem do Conteudo</conteudo></mensagem>";

            mockMvc.perform(post("/mensagens").contentType(MediaType.APPLICATION_XML).content(xmlPayload)).andExpect(status().isUnsupportedMediaType());
            verify(mensagemService, never()).registrarMensagem(any(Mensagem.class));
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
        void deveGerarExcecao_QuandoAlterarMensagem_PayloadXML() throws Exception {
            var xmlPayload = "<mensagem><usuario>Ana</usuario><conteudo>Mensagem do Conteudo</conteudo></mensagem>";
            var id = UUID.randomUUID();

            mockMvc.perform(put("/mensagens/{id}", id).contentType(MediaType.APPLICATION_XML).content(xmlPayload)).andExpect(status().isUnsupportedMediaType());
            verify(mensagemService, never()).alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() throws Exception {
            var id = UUID.randomUUID();
            var mensagem = gerarMensagem();
            mensagem.setId(id);
            var conteudoDaExcecao = "Mensagem não encontrada";
            when(mensagemService.alterarMensagem(any(UUID.class), any(Mensagem.class))).thenThrow(new MensagemNotFoundException(conteudoDaExcecao));

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagem)))
//                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(conteudoDaExcecao));

            verify(mensagemService, times(1)).alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() throws Exception {
            var id = UUID.randomUUID();
            var mensagem = gerarMensagem();
            mensagem.setId(UUID.randomUUID());
            var conteudoDaExcecao = "Mensagem atualizada não apresenta o ID correto";
            when(mensagemService.alterarMensagem(id, mensagem)).thenThrow(new MensagemNotFoundException(conteudoDaExcecao));

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagem)))
//                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(conteudoDaExcecao));

            verify(mensagemService, times(1)).alterarMensagem(any(UUID.class), any(Mensagem.class));
        }
    }

    @Nested
    class RemoverMensagem {
        @Test
        void devePermitirRemoverMensagem() throws Exception {
            var id = UUID.randomUUID();
            when(mensagemService.removerMensagem(id)).thenReturn(true);

            mockMvc.perform(delete("/mensagens/{id}", id)).andExpect(status().isOk()).andExpect(content().string("mensagem removida"));
            verify(mensagemService, times(1)).removerMensagem(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() throws Exception {
            var id = UUID.randomUUID();
            var mensagemDaExcecao = "Mensagem não encontrada";
            when(mensagemService.removerMensagem(id)).thenThrow(new MensagemNotFoundException((mensagemDaExcecao)));

            mockMvc.perform(delete("/mensagens/{id}", id)).andExpect(status().isBadRequest()).andExpect(content().string(mensagemDaExcecao));
            verify(mensagemService, times(1)).removerMensagem(any(UUID.class));
        }
    }

    @Nested
    class ListarMensagens {
        @Test
        void devePermitirListarMensagens() throws Exception {
            var mensagens = new PageImpl<>(Collections.singletonList(gerarMensagem()));
            when(mensagemService.listarMensagens(any(Pageable.class))).thenReturn(mensagens);

            mockMvc.perform(get("/mensagens")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.content", not(empty())))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        void devePermitirListarMensagens_QuandoNaoInformadoPaginacao() throws Exception {
            var mensagens = new PageImpl<>(Collections.singletonList(gerarMensagem()));
            when(mensagemService.listarMensagens(any(Pageable.class))).thenReturn(mensagens);

            mockMvc.perform(get("/mensagens"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.content", not(empty())))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }
    }

    public static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

}
