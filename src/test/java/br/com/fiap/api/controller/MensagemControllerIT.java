package br.com.fiap.api.controller;

import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.repository.MensagemRepository;
import br.com.fiap.api.service.MensagemService;
import br.com.fiap.api.service.MensagemServiceImpl;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.transaction.Transactional;
import java.util.UUID;

import static br.com.fiap.api.util.MensagemHelper.gerarMensagem;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Transactional
public class MensagemControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private MensagemRepository repository;
    private MensagemService mensagemService;

    private final String PATH_JSON_SCHEMA_MENSAGEM = "schemas/mensagem.schema.json";
    private final String PATH_JSON_SCHEMA_MENSAGEM_PAGE = "schemas/mensagem-page.schema.json";
    private final String PATH_JSON_SCHEMA_ERROR_BAD_REQUEST = "schemas/error-bad-request.schema.json";

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        mensagemService = new MensagemServiceImpl(repository);
    }

    @Nested
    class RegistarMensagem {
        @Test
        void devePermitirRegistrarMensagem() {
            var mensagem = gerarMensagem();

            // @formatter:off
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mensagem)
//                .log().all()
            .when()
                .post("/mensagens")
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath(PATH_JSON_SCHEMA_MENSAGEM));
//                .log().all();
            // @formatter:on
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_PayloadXML() {
            var xmlPayload = "<mensagem><usuario>Ana</usuario><conteudo>Mensagem do Conteudo</conteudo></mensagem>";

            // @formatter:off
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(xmlPayload)
            .when()
                .post("/mensagens")
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(matchesJsonSchemaInClasspath(PATH_JSON_SCHEMA_ERROR_BAD_REQUEST))
                .body("$", hasKey("timestamp"))
                .body("$", hasKey("error"))
                .body("$", hasKey("path"))
                .body("$", hasKey("status"))
                .body("error", equalTo("Bad Request"))
                .body("path", equalTo("/mensagens"));
            // @formatter:on
        }
    }

    @Nested
    class BuscarMensagem {
        @Test
        void devePermitirBuscarMensagem() {
            var id = mensagemService.listarMensagens(Pageable.unpaged()).stream().findFirst().get().getId().toString();

            // @formatter:off
            when()
                .get("/mensagens/{id}", id)
            .then()
                .statusCode(HttpStatus.OK.value());
            // @formatter:on
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExiste() {
            var id = UUID.fromString("e4dba38f-31b4-45a4-8ed9-cc53aa97d8a3");

            // @formatter:off
            when()
                .get("/mensagens/{id}", id)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Mensagem não encontrada"));
            // @formatter:on
        }
    }

    @Nested
    class AlterarMensagem {
        @Test
        void devePermitirAlterarMensagem() {
            var id = mensagemService.listarMensagens(Pageable.unpaged()).stream().findFirst().get().getId().toString();
            var mensagem = Mensagem.builder()
                    .id(UUID.fromString(id))
                    .conteudo("Conteudo da mensagem")
                    .build();

            var mensagens = mensagemService.listarMensagens(Pageable.unpaged());

            // @formatter:off
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mensagem)
            .when()
                .put("/mensagens/{id}", id)
            .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .body(matchesJsonSchemaInClasspath(PATH_JSON_SCHEMA_MENSAGEM));
            // @formatter:on
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_PayloadXML() {
            var xmlPayload = "<mensagem><usuario>Ana</usuario><conteudo>Mensagem do Conteudo</conteudo></mensagem>";

            // @formatter:off
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(xmlPayload)
            .when()
                .put("/mensagens/{id}", UUID.randomUUID().toString())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(matchesJsonSchemaInClasspath(PATH_JSON_SCHEMA_ERROR_BAD_REQUEST));
            // @formatter:on
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
            // @formatter:off
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(gerarMensagem())
            .when()
                .put("/mensagens/{id}", UUID.randomUUID().toString())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Mensagem não encontrada"));
            // @formatter:on
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
            var id = mensagemService.listarMensagens(Pageable.unpaged()).stream().findFirst().get().getId().toString();
            var mensagem = Mensagem.builder()
                    .id(UUID.randomUUID())
                    .conteudo("Conteudo da mensagem")
                    .build();

            // @formatter:off
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mensagem)
            .when()
                .put("/mensagens/{id}", id)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Mensagem atualizada não apresenta o ID correto"));
            // @formatter:on
        }
    }

    @Nested
    class RemoverMensagem {
        @Test
        void devePermitirRemoverMensagem() {
            var id = mensagemService.listarMensagens(Pageable.unpaged()).stream().findFirst().get().getId().toString();

            // @formatter:off
            when()
                .delete("/mensagens/{id}", id)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("mensagem removida"));
            // @formatter:on
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
            var id = UUID.randomUUID();

            // @formatter:off
            when()
                .delete("/mensagens/{id}", id)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Mensagem não encontrada"));;
            // @formatter:on
        }
    }

    @Nested
    class ListarMensagens {
        @Test
        void devePermitirListarMensagens() {
            // @formatter:off
            given()
                .queryParam("page", "0")
                .queryParam("size", "10")
            .when()
                .get("/mensagens")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath(PATH_JSON_SCHEMA_MENSAGEM_PAGE));
            // @formatter:on
        }

        @Test
        void devePermitirListarMensagens_QuandoNaoInformadoPaginacao() {
            // @formatter:off
            when()
                .get("/mensagens")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath(PATH_JSON_SCHEMA_MENSAGEM_PAGE));
            // @formatter:on
        }
    }


}
