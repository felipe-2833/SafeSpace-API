package br.com.fiap.safespace.controller;

import java.time.LocalDateTime;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.fiap.safespace.model.Atendimento;
import br.com.fiap.safespace.model.Statustype;
import br.com.fiap.safespace.model.AtendimentoType;
import br.com.fiap.safespace.repository.AtendimentoRepository;
import br.com.fiap.safespace.specification.AtendimentoSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/atendimentos")
@Slf4j
public class AtendimentoController {

    public record AtendimentoFilter(LocalDateTime startDate, LocalDateTime endDate, String relatorio, Statustype status, AtendimentoType atendimentoType) {
    }

    @Autowired
    private AtendimentoRepository repository;

    @GetMapping
    @Cacheable("atendimentos")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Listagem realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Falha na validação dos filtros ou parâmetros"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Listar atendimentos", tags = "atendimentos", summary = "Lista de atendimentos")
    public Page<Atendimento> index(AtendimentoFilter filter,
        @ParameterObject @PageableDefault(sort = "user.nome", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Buscando atendimentos");
        var specification = AtendimentoSpecification.withFilters(filter);
        return repository.findAll(specification, pageable);
    }

    @PostMapping
    @CacheEvict(value = "atendimentos", allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(responses = {
            @ApiResponse(responseCode = "400", description = "Falha na validação")
    }, description = "Cadastrar atendimento", tags = "atendimentos", summary = "Cadastrar atendimento")
    public Atendimento create(@RequestBody @Valid Atendimento atendimento) {
        log.info("Cadastrando atendimento do" + atendimento.getUser().getNome());
        return repository.save(atendimento);
    }

    @GetMapping("{id_atendimento}")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Registro encontrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Listar atendimento pelo id", tags = "atendimentos", summary = "Listar atendimento pelo id")
    public Atendimento get(@PathVariable Long id_atendimento) {
        log.info("Buscando atendimento " + id_atendimento);
        return getAtendimento(id_atendimento);
    }

    @DeleteMapping("{id_atendimento}")
    @Operation(responses = {
        @ApiResponse(responseCode = "204", description = "Registro removido com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Deletar atendimento pelo id", tags = "atendimentos", summary = "Deletar atendimento")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id_atendimento) {
        log.info("Apagando atendimento " + id_atendimento);
        repository.delete(getAtendimento(id_atendimento));
    }

    @PutMapping("{id_atendimento}")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Falha na validação dos dados"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Update atendimento pelo id", tags = "atendimentos", summary = "Update atendimento pelo id")
    public Atendimento update(@PathVariable long id_atendimento, @RequestBody @Valid Atendimento atendimento) {
        log.info("Atualizando atendimento " + id_atendimento + " " + atendimento);
        getAtendimento(id_atendimento);
        atendimento.setId_atendimento(id_atendimento);
        return repository.save(atendimento);
    }

    private Atendimento getAtendimento(Long id_atendimento) {
        return repository.findById(id_atendimento)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "atendimento não encontrada"));
    }
}
