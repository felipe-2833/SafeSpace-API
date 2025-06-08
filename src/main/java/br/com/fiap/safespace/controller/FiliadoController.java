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

import br.com.fiap.safespace.controller.VoluntarioController.VoluntarioFilter;
import br.com.fiap.safespace.model.Filiado;
import br.com.fiap.safespace.repository.FiliadoRepository;
import br.com.fiap.safespace.specification.FiliadoSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/filiados")
@Slf4j
public class FiliadoController {

    @Autowired
    private FiliadoRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @Cacheable("filiados")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Listagem realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Falha na validação dos filtros ou parâmetros"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Listar filiados", tags = "filiados", summary = "Lista de filiados")
    public Page<Filiado> index(@ParameterObject @ModelAttribute VoluntarioFilter filter,
        @ParameterObject @PageableDefault(size = 5, sort = "nome", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Buscando filiados");
        var specification = FiliadoSpecification.withFilters(filter);
        return repository.findAll(specification, pageable);
    }

    @PostMapping
    @CacheEvict(value = "filiados", allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(responses = {
            @ApiResponse(responseCode = "400", description = "Falha na validação"),
            @ApiResponse(responseCode = "403", description = "Permissão negada")
    }, description = "Cadastrar filiado", tags = "filiados", summary = "Cadastrar filiado")
    public Filiado create(@RequestBody @Valid Filiado filiado) {
        log.info("Cadastrando filiado do" + filiado.getNome());
        filiado.setPassword(passwordEncoder.encode(filiado.getPassword()));
        return repository.save(filiado);
    }

    @GetMapping("{id_filiado}")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Registro encontrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Listar filiado pelo id", tags = "filiados", summary = "Listar filiado pelo id")
    public Filiado get(@PathVariable Long id_filiado) {
        log.info("Buscando filiado " + id_filiado);
        return getFiliado(id_filiado);
    }

    @DeleteMapping("{id_filiado}")
    @Operation(responses = {
        @ApiResponse(responseCode = "204", description = "Registro removido com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Deletar filiado pelo id", tags = "filiados", summary = "Deletar filiado")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id_filiado) {
        log.info("Apagando filiado " + id_filiado);
        repository.delete(getFiliado(id_filiado));
    }

    @PutMapping("{id_filiado}")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Falha na validação dos dados"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Update filiado pelo id", tags = "filiados", summary = "Update filiado pelo id")
    public Filiado update(@PathVariable long id_filiado, @RequestBody @Valid Filiado filiado) {
        log.info("Atualizando filiado " + id_filiado + " " + filiado);
        getFiliado(id_filiado);
        filiado.setId_user(id_filiado);
        return repository.save(filiado);
    }

    private Filiado getFiliado(Long id_filiado) {
        return repository.findById(id_filiado)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "filiado não encontrada"));
    }
}
