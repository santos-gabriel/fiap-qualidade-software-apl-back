package br.com.fiap.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Generated;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mensagem {

    @Id
    private UUID id;

    @Column(nullable = false)
    @NotEmpty(message = "usuário não pode estar vazio")
    private String usuario;

    @Column(nullable = false)
    @NotEmpty(message = "o conteúdo não pode estar vazio")
    private String conteudo;

    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Builder.Default
    private int gostei = 0;

}
