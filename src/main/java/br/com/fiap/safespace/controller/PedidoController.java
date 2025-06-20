package br.com.fiap.safespace.controller;

import java.time.LocalDate;

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

import br.com.fiap.safespace.model.Pedido;
import br.com.fiap.safespace.model.Statustype;
import br.com.fiap.safespace.model.User;
import br.com.fiap.safespace.model.PedidoType;
import br.com.fiap.safespace.repository.PedidoRepository;
import br.com.fiap.safespace.specification.PedidoSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/pedidos")
@Slf4j
public class PedidoController {

    public record PedidoFilter(String descricao, LocalDate startDate, LocalDate endDate, Statustype status, PedidoType pedidoType) {
    }

    @Autowired
    private PedidoRepository repository;

    @GetMapping
    @Cacheable("Pedidos")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Listagem realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Falha na validação dos filtros ou parâmetros"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Listar pedidos", tags = "pedidos", summary = "Lista de pedidos")
    public Page<Pedido> index(@AuthenticationPrincipal User user, @ParameterObject @ModelAttribute PedidoFilter filter,
        @ParameterObject @PageableDefault(size = 5, sort = "user.nome", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Buscando pedidos");
        var filterSpecification = PedidoSpecification.withFilters(filter);
        var userSpecification = PedidoSpecification.byUser(user);
        var combinedSpecification = filterSpecification.and(userSpecification);
        return repository.findAll(combinedSpecification, pageable);
    }

    @PostMapping
    @CacheEvict(value = "pedidos", allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(responses = {
            @ApiResponse(responseCode = "400", description = "Falha na validação"),
            @ApiResponse(responseCode = "403", description = "Permissão negada")
    }, description = "Cadastrar pedido", tags = "pedidos", summary = "Cadastrar pedido")
    public Pedido create(@RequestBody @Valid Pedido pedido, @AuthenticationPrincipal User user) {
        log.info("Cadastrando pedido do" + pedido.getUser().getNome());
        pedido.setUser(user);
        return repository.save(pedido);
    }

    @GetMapping("{id_pedido}")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Registro encontrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Listar pedido pelo id", tags = "pedidos", summary = "Listar pedido pelo id")
    public Pedido get(@PathVariable Long id_pedido, @AuthenticationPrincipal User user) {
        log.info("Buscando pedido " + id_pedido);
        checkPermission(id_pedido, user);
        return getPedido(id_pedido);
    }

    @DeleteMapping("{id_pedido}")
    @Operation(responses = {
        @ApiResponse(responseCode = "204", description = "Registro removido com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Deletar pedido pelo id", tags = "pedidos", summary = "Deletar pedido")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id_pedido, @AuthenticationPrincipal User user) {
        log.info("Apagando pedido " + id_pedido);
        checkPermission(id_pedido, user);
        repository.delete(getPedido(id_pedido));
    }

    @PutMapping("{id_pedido}")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Falha na validação dos dados"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Update pedido pelo id", tags = "pedidos", summary = "Update pedido pelo id")
    public Pedido update(@PathVariable Long id_pedido, @RequestBody @Valid Pedido pedido, @AuthenticationPrincipal User user) {
        log.info("Atualizando pedido " + id_pedido + " " + pedido);
        checkPermission(id_pedido, user);
        pedido.setId_pedido(id_pedido);
        pedido.setUser(user);
        return repository.save(pedido);
    }

    private void checkPermission(Long id, User user) {
        var pedidoOld = getPedido(id);
        if(!pedidoOld.getUser().equals(user)) 
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    private Pedido getPedido(Long id_pedido) {
        return repository.findById(id_pedido)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "pedido não encontrado"));
    }
}
