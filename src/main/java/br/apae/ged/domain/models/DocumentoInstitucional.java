package br.apae.ged.domain.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity(name = "tb_documento_institucional")
public class DocumentoInstitucional extends EntityID {

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String conteudo;

    private String tipoConteudo;

    @Builder.Default
    private LocalDateTime dataUpload = LocalDateTime.now();

    @Column(name = "is_ativo", nullable = false)
    @Builder.Default
    private boolean isAtivo = true;

    @ManyToOne
    @JoinColumn(name = "uploaded_by", referencedColumnName = "id")
    private User uploadedBy;
}