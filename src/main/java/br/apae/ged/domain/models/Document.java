package br.apae.ged.domain.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // Necessário para o JPA
@Builder
@Entity(name = "tb_documentos")
@Table(indexes = {
        @Index(name = "titulo_idx", columnList = "titulo"),
        @Index(name = "pessoa_idx", columnList = "pessoa_id")
})
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String conteudo;

    private String tipoConteudo;

    @Builder.Default // Define o valor padrão para o builder
    private LocalDateTime dataUpload = LocalDateTime.now();

    private LocalDateTime dataDownload;
    private LocalDateTime dataUpdate;
    private LocalDate dataDocumento;

    @Column(name = "is_ativo", nullable = false)
    @Builder.Default // Define o valor padrão para o builder
    private boolean isAtivo = true;

    @ManyToOne
    @JoinColumn(name = "pessoa_id", referencedColumnName = "id")
    private Pessoa pessoa;

    @ManyToOne
    @JoinColumn(name = "document_type_id")
    private TipoDocumento tipoDocumento;

    @ManyToOne
    @JoinColumn(name = "downloaded_by", referencedColumnName = "id")
    private User downloadedBy;

    @ManyToOne
    @JoinColumn(name = "uploaded_by", referencedColumnName = "id")
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", referencedColumnName = "id")
    private User updatedBy;

    @Builder.Default // Define o valor padrão para o builder
    private Boolean isLast = true;

}