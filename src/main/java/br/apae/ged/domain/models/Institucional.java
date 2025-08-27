package br.apae.ged.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Getter @Setter @Table(name = "tb_documentos_institucionais")
public class Institucional extends EntityID {

    private String titulo;
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


}
