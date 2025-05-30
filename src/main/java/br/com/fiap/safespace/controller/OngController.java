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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import br.com.fiap.safespace.model.Ong;
import br.com.fiap.safespace.model.User;
import br.com.fiap.safespace.repository.OngRepository;
import br.com.fiap.safespace.specification.OngSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ongs")
@Slf4j
public class OngController {

    public record OngFilter(String nome, String telefone, String endereco) {
    }

    @Autowired
    private OngRepository repository;

    @GetMapping
    @Cacheable("ongs")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Listagem realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Falha na validação dos filtros ou parâmetros"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Listar ongs", tags = "ongs", summary = "Lista de ongs")
    public Page<Ong> index(@AuthenticationPrincipal User user, @ParameterObject @ModelAttribute OngFilter filter,
        @ParameterObject @PageableDefault(size = 5, sort = "nome", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Buscando ongs");
        var specification = OngSpecification.withFilters(filter);
        return repository.findByUser(user, specification, pageable);
    }

    @PostMapping
    @CacheEvict(value = "ongs", allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(responses = {
            @ApiResponse(responseCode = "400", description = "Falha na validação"),
            @ApiResponse(responseCode = "403", description = "Permissão negada")
    }, description = "Cadastrar ong", tags = "ongs", summary = "Cadastrar ong")
    public Ong create(@RequestBody @Valid Ong ong, @AuthenticationPrincipal User user) {
        log.info("Cadastrando ong do" + ong.getNome());
        ong.setUser(user);
        return repository.save(ong);
    }

    @GetMapping("{id_ong}")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Registro encontrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Listar ong pelo id", tags = "ongs", summary = "Listar ong pelo id")
    public Ong get(@PathVariable Long id_ong, @AuthenticationPrincipal User user) {
        log.info("Buscando ong " + id_ong);
        checkPermission(id_ong, user);
        return getOng(id_ong);
    }

    @DeleteMapping("{id_ong}")
    @Operation(responses = {
        @ApiResponse(responseCode = "204", description = "Registro removido com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Deletar ong pelo id", tags = "ongs", summary = "Deletar ong")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id_ong, @AuthenticationPrincipal User user) {
        log.info("Apagando ong " + id_ong);
        checkPermission(id_ong, user);
        repository.delete(getOng(id_ong));
    }

    @PutMapping("{id_ong}")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Falha na validação dos dados"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Update ong pelo id", tags = "ongs", summary = "Update ong pelo id")
    public Ong update(@PathVariable Long id_ong, @RequestBody @Valid Ong ong, @AuthenticationPrincipal User user) {
        log.info("Atualizando ong " + id_ong + " " + ong);
        checkPermission(id_ong, user);
        ong.setId_ong(id_ong);
        ong.setUser(user);
        return repository.save(ong);
    }

    private void checkPermission(Long id, User user) {
        var ongOld = getOng(id);
        if(!ongOld.getUser().equals(user)) 
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    private Ong getOng(Long id_ong) {
        return repository.findById(id_ong)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "ong não encontrada"));
    }
}
