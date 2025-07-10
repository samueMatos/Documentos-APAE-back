package br.apae.ged.domain.models;

import br.apae.ged.domain.models.enums.TipoArquivo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity(name = "tb_documentos")
@Table(indexes = {
        @Index(name = "titulo_idx", columnList = "titulo"),
        @Index(name = "aluno_idx", columnList = "aluno_id")
})
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String conteudo;
    private String tipoConteudo;
    private LocalDateTime dataUpload;
    private LocalDateTime dataDownload;
    private LocalDateTime dataUpdate;
    private LocalDate dataDocumento;
    @Column(name = "is_ativo", nullable = false)
    private boolean isAtivo = true;

    @ManyToOne
    @JoinColumn(name = "document_type_id")
    private TipoDocumento tipoDocumento;

    @ManyToOne
    @JoinColumn(name = "aluno_id", referencedColumnName = "id")
    private Alunos aluno;

    @ManyToOne
    @JoinColumn(name = "downloaded_by", referencedColumnName = "id")
    private User downloadedBy;

    @ManyToOne
    @JoinColumn(name = "uploaded_by", referencedColumnName = "id")
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", referencedColumnName = "id")
    private User updatedBy;


    private Boolean isLast;

    public Document(){
        this.dataUpload = LocalDateTime.now();
        this.isLast = true;
    }
}