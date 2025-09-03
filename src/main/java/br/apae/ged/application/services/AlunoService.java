package br.apae.ged.application.services;

import br.apae.ged.application.dto.aluno.AlunoByIdResponse;
import br.apae.ged.application.dto.aluno.AlunoRequestDTO;
import br.apae.ged.application.dto.aluno.AlunoResponseDTO;
import br.apae.ged.application.exceptions.BusinessException;
import br.apae.ged.application.exceptions.NotFoundException;
import br.apae.ged.domain.models.Alunos;
import br.apae.ged.domain.models.Cidade;
import br.apae.ged.domain.models.Endereco;
import br.apae.ged.domain.models.User;
import br.apae.ged.domain.repositories.AlunoRepository;
import br.apae.ged.domain.repositories.CidadeRepository;
import br.apae.ged.domain.repositories.EnderecoRepository;
import br.apae.ged.domain.repositories.specifications.AlunoSpecification;
import br.apae.ged.domain.utils.AuthenticationUtil;
import br.apae.ged.domain.valueObjects.CPF;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final EnderecoRepository enderecoRepository;
    private final CidadeRepository cidadeRepository;



    public Alunos create(AlunoRequestDTO request) {
        CPF cpf = new CPF(request.cpf());
        Optional<Alunos> alunoExistenteOpt = alunoRepository.findByCpf(cpf);

        if (alunoExistenteOpt.isPresent()) {

            Alunos alunoExistente = alunoExistenteOpt.get();

            if (alunoExistente.getIsAtivo()) {

                throw new BusinessException("Já existe um aluno ativo cadastrado com este CPF.");
            } else {

                var cidade = cidadeRepository.findByIbge(request.ibge())
                        .orElseThrow(() -> new NotFoundException("Cidade não encontrada para atualização do aluno."));


                alunoExistente.atualizarDados(request, AuthenticationUtil.retriveAuthenticatedUser());


                Endereco endereco = enderecoRepository.findByAluno(alunoExistente);
                if(endereco == null) {

                    endereco = Endereco.paraEntidade(request, cidade);
                    alunoExistente.setEndereco(endereco);
                    endereco.setAluno(alunoExistente);
                } else {
                    endereco.atualizarDados(request, cidade);
                }


                alunoExistente.setIsAtivo(true);

                return alunoRepository.save(alunoExistente);
            }

        } else {

            var cidade = cidadeRepository.findByIbge(request.ibge())
                    .orElseThrow(() -> new NotFoundException("Cidade não encontrada para novo aluno."));

            Alunos alunoNovo = Alunos.paraEntidade(request, AuthenticationUtil.retriveAuthenticatedUser());
            Endereco enderecoNovo = Endereco.paraEntidade(request, cidade);

            alunoNovo.setEndereco(enderecoNovo);
            enderecoNovo.setAluno(alunoNovo);

            alunoNovo.setCreatedBy(AuthenticationUtil.retriveAuthenticatedUser());
            alunoNovo.setIsAtivo(true);
            alunoNovo.setCreatedAt(LocalDateTime.now());

            return alunoRepository.save(alunoNovo);
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return "";
        }
        return new DataFormatter().formatCellValue(cell);
    }

    private LocalDate getLocalDateValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        if (cell.getCellType() == CellType.STRING) {
            try {
                return LocalDate.parse(cell.getStringCellValue());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public List<Alunos> importarAlunos(InputStream arquivo) throws Exception {
        List<Alunos> alunosProcessados = new ArrayList<>();
        User usuarioLogado = AuthenticationUtil.retriveAuthenticatedUser();

        try (Workbook workbook = WorkbookFactory.create(arquivo)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String nome = getCellValueAsString(row.getCell(0));
                if (nome.isBlank()) continue;


                LocalDate dataNascimento = getLocalDateValue(row.getCell(1));
                String sexo = getCellValueAsString(row.getCell(2));
                CPF cpf = new CPF(getCellValueAsString(row.getCell(3)));
                String telefone = getCellValueAsString(row.getCell(4));
                LocalDate dataEntrada = getLocalDateValue(row.getCell(5));
                String observacoes = getCellValueAsString(row.getCell(6));
                String cep = getCellValueAsString(row.getCell(7)).trim();
                String ufEstado = getCellValueAsString(row.getCell(8)).trim();
                String nomeCidade = getCellValueAsString(row.getCell(9)).trim();
                String bairro = getCellValueAsString(row.getCell(10)).trim();
                String rua = getCellValueAsString(row.getCell(11)).trim();
                String numeroStr = getCellValueAsString(row.getCell(12)).trim();
                String complemento = getCellValueAsString(row.getCell(13)).trim();
                String matricula = getCellValueAsString(row.getCell(14));


                if (cpf.getCpf().isBlank() || nome.isBlank()) {
                    throw new IllegalArgumentException("Nome e CPF são obrigatórios. Verifique a linha " + (row.getRowNum() + 1));
                }

                Cidade cidade = cidadeRepository.findByNomeIgnoreCaseAndEstadoUfIgnoreCase(nomeCidade, ufEstado)
                        .orElseThrow(() -> new IllegalArgumentException("Cidade/UF inválido na linha " + (row.getRowNum() + 1)));


                Optional<Alunos> alunoExistenteOpt = alunoRepository.findByCpf(cpf);

                Alunos alunoParaSalvar;
                Endereco enderecoParaSalvar;

                if (alunoExistenteOpt.isPresent()) {
                    alunoParaSalvar = alunoExistenteOpt.get();
                    enderecoParaSalvar = enderecoRepository.findByAluno(alunoParaSalvar);
                    alunoParaSalvar.setUpdatedBy(usuarioLogado);
                    alunoParaSalvar.setUpdatedAt(LocalDateTime.now());
                    alunoParaSalvar.setIsAtivo(true);
                } else {
                    alunoParaSalvar = new Alunos();
                    enderecoParaSalvar = new Endereco();
                    alunoParaSalvar.setCreatedBy(usuarioLogado);
                    alunoParaSalvar.setCreatedAt(LocalDateTime.now());
                    alunoParaSalvar.setIsAtivo(true);
                    alunoParaSalvar.setCpf(cpf);

                }

                alunoParaSalvar.setNome(nome);
                alunoParaSalvar.setDataNascimento(dataNascimento);
                alunoParaSalvar.setSexo(sexo);
                alunoParaSalvar.setTelefone(telefone);
                alunoParaSalvar.setDataEntrada(dataEntrada);
                alunoParaSalvar.setObservacoes(observacoes);
                alunoParaSalvar.setMatricula(matricula);

                enderecoParaSalvar.setCep(cep);
                enderecoParaSalvar.setRua(rua);
                enderecoParaSalvar.setBairro(bairro);
                enderecoParaSalvar.setComplemento(complemento);
                enderecoParaSalvar.setCidade(cidade);
                if (!numeroStr.isBlank()) {
                    enderecoParaSalvar.setNumero(Integer.parseInt(numeroStr));
                }

                alunoParaSalvar.setEndereco(enderecoParaSalvar);
                enderecoParaSalvar.setAluno(alunoParaSalvar);

                alunosProcessados.add(alunoRepository.save(alunoParaSalvar));
            }
        }
        return alunosProcessados;
    }



    public Page<AlunoResponseDTO> findAll(String termoBusca, Pageable pageable) {

        Specification<Alunos> spec = Specification.where(AlunoSpecification.isAtivo());


        if (termoBusca != null && !termoBusca.isBlank()) {


            Specification<Alunos> buscaCombinada = Specification.anyOf(
                    AlunoSpecification.byNome(termoBusca),
                    AlunoSpecification.byCpf(termoBusca),
                    AlunoSpecification.byMatricula(termoBusca)
            );

            spec = spec.and(buscaCombinada);
        }

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
