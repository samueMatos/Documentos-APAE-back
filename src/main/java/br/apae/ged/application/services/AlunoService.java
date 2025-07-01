package br.apae.ged.application.services;

import br.apae.ged.application.dto.aluno.AlunoByIdResponse;
import br.apae.ged.application.dto.aluno.AlunoRequestDTO;
import br.apae.ged.application.dto.aluno.AlunoResponseDTO;
import br.apae.ged.application.exceptions.NotFoundException;
import br.apae.ged.domain.models.Alunos;
import br.apae.ged.domain.models.Endereco;
import br.apae.ged.domain.repositories.AlunoRepository;
import br.apae.ged.domain.repositories.CidadeRepository;
import br.apae.ged.domain.repositories.DocumentRepository;
import br.apae.ged.domain.repositories.EnderecoRepository;
import br.apae.ged.domain.repositories.specifications.AlunoSpecification;
import br.apae.ged.application.strategy.NewAlunoValidationStrategy;
import br.apae.ged.domain.utils.AuthenticationUtil;
import br.apae.ged.domain.valueObjects.CPF;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final EnderecoRepository enderecoRepository;
    private final List<NewAlunoValidationStrategy> alunoValidationStrategies;
    private final CidadeRepository cidadeRepository;
    private final DocumentRepository documentRepository;

    public Alunos create(AlunoRequestDTO request) {
        var cidade = cidadeRepository.findByIbge(request.ibge())
                .orElseThrow(() -> new NotFoundException("Cidade não encontrada"));

        Alunos aluno = Alunos.paraEntidade(request);
        aluno.setCreatedBy(AuthenticationUtil.retriveAuthenticatedUser());
        aluno.setIsAtivo(true);
        alunoRepository.save(aluno);

        Endereco endereco = Endereco.paraEntidade(request, cidade);
        endereco.setAluno(aluno);
        enderecoRepository.save(endereco);

        return aluno;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        DataFormatter formatter;
        formatter = new DataFormatter();
        return formatter.formatCellValue(cell);
    }

    public List<Alunos> importarAlunos(InputStream arquivo) throws Exception {
        List<Alunos> alunos = new ArrayList<>();
        List<Endereco> enderecos = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(arquivo)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                if (row == null || getCellValueAsString(row.getCell(0)).isBlank()) {
                    continue;
                }
                Alunos aluno = new Alunos();
                aluno.setNome(getCellValueAsString(row.getCell(0)));
                aluno.setDataNascimento(row.getCell(1).getLocalDateTimeCellValue().toLocalDate());
                aluno.setSexo(getCellValueAsString(row.getCell(2)));
                aluno.setCpf(new CPF(getCellValueAsString(row.getCell(3))));
                aluno.setTelefone(getCellValueAsString(row.getCell(4)));
                aluno.setDataEntrada(row.getCell(5).getLocalDateTimeCellValue().toLocalDate());
                aluno.setObservacoes(getCellValueAsString(row.getCell(6)));
                aluno.setIsAtivo(true);
                aluno.setCreatedBy(AuthenticationUtil.retriveAuthenticatedUser());
                aluno.setCreatedAt(LocalDateTime.now());

                String cep = getCellValueAsString(row.getCell(7)).trim();
                String ufEstado = getCellValueAsString(row.getCell(8)).trim();
                String nomeCidade = getCellValueAsString(row.getCell(9)).trim();
                String bairro = getCellValueAsString(row.getCell(10)).trim();
                String rua = getCellValueAsString(row.getCell(11)).trim();
                String numero = getCellValueAsString(row.getCell(12)).trim();
                String complemento = getCellValueAsString(row.getCell(13)).trim();

                var cidade = cidadeRepository.findByNomeIgnoreCaseAndEstadoUfIgnoreCase(nomeCidade, ufEstado)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Cidade ou Estado inválido na linha " + (row.getRowNum() + 1) +
                                        ". Valor recebido: Cidade='" + nomeCidade + "', UF='" + ufEstado + "'. Verifique a ortografia."
                        ));
                Endereco endereco = new Endereco();
                endereco.setCep(cep);
                endereco.setRua(rua);
                try {
                    if (!numero.isBlank()) {
                        endereco.setNumero(Integer.parseInt(numero));
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("O 'Número' do endereço na linha " + (row.getRowNum() + 1) + " não é um número válido.");
                }
                endereco.setComplemento(complemento);
                endereco.setBairro(bairro);
                endereco.setCidade(cidade);
                endereco.setAluno(aluno);

                alunos.add(aluno);
                enderecos.add(endereco);
            }
        }
        List<Alunos> alunosSalvos = alunoRepository.saveAll(alunos);
        enderecoRepository.saveAll(enderecos);

        return alunosSalvos;
    }

    public Page<AlunoResponseDTO> findAll(String nome, Pageable pageable) {
        var spec = Specification
                .where(AlunoSpecification.isAtivo())
                .and(AlunoSpecification.byNome(nome));

        return alunoRepository.findAll(spec, pageable)
                .map(AlunoResponseDTO::fromEntity);
    }

    public AlunoByIdResponse byID(Long id) {
         var aluno = alunoRepository.findById(id)
                 .orElseThrow(() -> new NotFoundException("Aluno não encontrado"));
         var endereco = enderecoRepository.findByAluno(aluno);
         
         return AlunoByIdResponse.daEntidade(aluno, endereco);
    }

    public void changeStatusAluno(Long id) {
        Alunos byId = alunoRepository.findById(id).orElseThrow(() -> new NotFoundException("Aluno não encontrado"));
        byId.setUpdatedAt(LocalDateTime.now());

        if (!byId.getIsAtivo()) {
            byId.setIsAtivo(true);
            alunoRepository.save(byId);
            return;
        }

        byId.setIsAtivo(false);
        alunoRepository.save(byId);
    }


    public Alunos update(Long id, AlunoRequestDTO atualizacao) {
        Alunos byID = alunoRepository.findById(id).orElseThrow(() -> new NotFoundException("Aluno não encontrado"));
        var cidade = cidadeRepository.findByIbge(atualizacao.ibge())
                .orElseThrow(() -> new NotFoundException("Cidade não encontrada"));

        byID.atualizarDados(atualizacao, AuthenticationUtil.retriveAuthenticatedUser());
        Endereco endereco = enderecoRepository.findByAluno(byID);
        endereco.atualizarDados(atualizacao, cidade);

        enderecoRepository.save(endereco);
        return alunoRepository.save(byID);
    }
}
