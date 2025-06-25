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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public Page<AlunoResponseDTO> findAll(String cpf, String nome, Pageable pageable) {
        var spec = Specification
                .where(AlunoSpecification.isAtivo())
                .and(AlunoSpecification.byNome(nome));

        return alunoRepository.findAll(spec, pageable)
                .map(AlunoResponseDTO::daEntidade);
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
