package br.com.fiap.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mensagem {

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private UUID id;

    @Column(nullable = false)
    @NotEmpty(message = "usuário não pode estar vazio")
    private String usuario;

    @Column(nullable = false)
    @NotEmpty(message = "o conteúdo não pode estar vazio")
    private String conteudo;

    @Builder.Default
    private Date dataCriacao = new Date();

    @Builder.Default
    private int gostei = 0;

}
