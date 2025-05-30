package br.com.fiap.safespace.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.fiap.safespace.model.Voluntario;
import br.com.fiap.safespace.repository.VoluntarioRepository;
import br.com.fiap.safespace.specification.VoluntarioSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/voluntarios")
@Slf4j
public class VoluntarioController {

    public record VoluntarioFilter(String nome, String endereco, String disponibilidade, String area_atuacao) {
    }

    @Autowired
    private VoluntarioRepository repository;

    @GetMapping
    @Cacheable("voluntarios")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Listagem realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Falha na validação dos filtros ou parâmetros"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Listar voluntarios", tags = "voluntarios", summary = "Lista de voluntarios")
    public Page<Voluntario> index(@ParameterObject @ModelAttribute VoluntarioFilter filter,
        @ParameterObject @PageableDefault(size = 5, sort = "nome", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Buscando voluntarios");
        var specification = VoluntarioSpecification.withFilters(filter);
        return repository.findAll(specification, pageable);
    }

    @PostMapping
    @CacheEvict(value = "voluntarios", allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(responses = {
            @ApiResponse(responseCode = "400", description = "Falha na validação")
    }, description = "Cadastrar voluntario", tags = "voluntarios", summary = "Cadastrar voluntario")
    public Voluntario create(@RequestBody @Valid Voluntario voluntario) {
        log.info("Cadastrando voluntario " + voluntario.getNome());
        return repository.save(voluntario);
    }

    @GetMapping("{id_voluntario}")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Registro encontrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Listar voluntario pelo id", tags = "voluntarios", summary = "Listar voluntario pelo id")
    public Voluntario get(@PathVariable Long id_voluntario) {
        log.info("Buscando voluntario " + id_voluntario);
        return getVoluntario(id_voluntario);
    }

    @DeleteMapping("{id_voluntario}")
    @Operation(responses = {
        @ApiResponse(responseCode = "204", description = "Registro removido com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Deletar voluntario pelo id", tags = "voluntarios", summary = "Deletar voluntario")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id_voluntario) {
        log.info("Apagando voluntario " + id_voluntario);
        repository.delete(getVoluntario(id_voluntario));
    }

    @PutMapping("{id_voluntario}")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Falha na validação dos dados"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Update voluntario pelo id", tags = "voluntarios", summary = "Update voluntario pelo id")
    public Voluntario update(@PathVariable Long id_voluntario, @RequestBody @Valid Voluntario voluntario) {
        log.info("Atualizando voluntario " + id_voluntario + " " + voluntario);
        getVoluntario(id_voluntario);
        voluntario.setId_user(id_voluntario);
        return repository.save(voluntario);
    }

    private Voluntario getVoluntario(Long id_voluntario) {
        return repository.findById(id_voluntario)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "voluntario não encontrado"));
    }
}
