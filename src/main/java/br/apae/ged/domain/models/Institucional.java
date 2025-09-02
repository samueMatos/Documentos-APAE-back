package br.apae.ged.domain.models;

import br.apae.ged.application.dto.documentoIstitucional.UploadInstitucionalRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Getter @Setter @Table(name = "tb_documentos_institucionais")
public class Institucional extends EntityID {

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
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;
    @ManyToOne
    @JoinColumn
    private User createdBy;

    public Institucional() {}

    public Institucional (UploadInstitucionalRequest entrada, String base64, String tConteudo, TipoDocumento tipoDoc, User user){
        if (entrada.nome() == null || entrada.nome().isBlank()) {
            String tipo = tipoDoc != null ? tipoDoc.getNome() : "Documento";
            String data = LocalDate.now().toString();
            this.titulo = tipo + "_" + data;
        } else {
            this.titulo = entrada.nome();
        }
        this.conteudo = base64;
        this.tipoConteudo = tConteudo;
        this.isAtivo = true;
        this.dataUpload = LocalDateTime.now();
        this.dataDocumento = entrada.dataCriacao();
        this.tipoDocumento = tipoDoc;
        this.uploadedBy = user;
        this.createdBy = user;
    }

}
