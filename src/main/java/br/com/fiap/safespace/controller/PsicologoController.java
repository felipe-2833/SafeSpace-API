package br.com.fiap.safespace.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import br.com.fiap.safespace.model.Psicologo;
import br.com.fiap.safespace.repository.PsicologoRepository;
import br.com.fiap.safespace.specification.PsicologoSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/psicologos")
@Slf4j
public class PsicologoController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public record PsicologoFilter(String nome, String endereco, String crp, String area_atuacao) {
    }

    @Autowired
    private PsicologoRepository repository;

    @GetMapping
    @Cacheable("psicologos")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Listagem realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Falha na validação dos filtros ou parâmetros"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Listar psicologos", tags = "psicologos", summary = "Lista de psicologos")
    public Page<Psicologo> index(@ParameterObject @ModelAttribute PsicologoFilter filter,
        @ParameterObject @PageableDefault(size = 5, sort = "nome", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Buscando psicologos");
        var specification = PsicologoSpecification.withFilters(filter);
        return repository.findAll(specification, pageable);
    }

    @PostMapping
    @CacheEvict(value = "psicologos", allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(responses = {
            @ApiResponse(responseCode = "400", description = "Falha na validação"),
            @ApiResponse(responseCode = "403", description = "Permissão negada")
    }, description = "Cadastrar psicologo", tags = "psicologos", summary = "Cadastrar psicologo")
    public Psicologo create(@RequestBody @Valid Psicologo psicologo) {
        log.info("Cadastrando psicologo " + psicologo.getNome());
        psicologo.setPassword(passwordEncoder.encode(psicologo.getPassword()));
        return repository.save(psicologo);
    }

    @GetMapping("{id_psicologo}")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Registro encontrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Listar psicologo pelo id", tags = "psicologos", summary = "Listar psicologo pelo id")
    public Psicologo get(@PathVariable Long id_psicologo) {
        log.info("Buscando psicologo " + id_psicologo);
        return getPsicologo(id_psicologo);
    }

    @DeleteMapping("{id_psicologo}")
    @Operation(responses = {
        @ApiResponse(responseCode = "204", description = "Registro removido com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Deletar psicologo pelo id", tags = "psicologos", summary = "Deletar psicologo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id_psicologo) {
        log.info("Apagando psicologo " + id_psicologo);
        repository.delete(getPsicologo(id_psicologo));
    }

    @PutMapping("{id_psicologo}")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Falha na validação dos dados"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Update psicologo pelo id", tags = "psicologos", summary = "Update psicologo pelo id")
    public Psicologo update(@PathVariable Long id_psicologo, @RequestBody @Valid Psicologo psicologo) {
        log.info("Atualizando psicologo " + id_psicologo + " " + psicologo);
        getPsicologo(id_psicologo);
        psicologo.setId_user(id_psicologo);
        return repository.save(psicologo);
    }

    private Psicologo getPsicologo(Long id_psicologo) {
        return repository.findById(id_psicologo)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "psicologo não encontrado"));
    }
}
