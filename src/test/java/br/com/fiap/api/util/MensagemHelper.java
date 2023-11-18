package br.com.fiap.api.util;

import br.com.fiap.api.model.Mensagem;

public abstract class MensagemHelper {

    public static Mensagem gerarMensagem() {
        return Mensagem.builder()
                .usuario("Jose")
                .conteudo("conteúdo da mensagem")
                .build();
    }

}
