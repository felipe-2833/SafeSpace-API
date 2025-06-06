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

import br.com.fiap.safespace.model.Atendimento;
import br.com.fiap.safespace.model.Statustype;
import br.com.fiap.safespace.model.User;
import br.com.fiap.safespace.model.UserRole;
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
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Listar atendimentos", tags = "atendimentos", summary = "Lista de atendimentos")
    public Page<Atendimento> index(@AuthenticationPrincipal User principalUser, @ParameterObject @ModelAttribute AtendimentoFilter filter,
        @ParameterObject @PageableDefault(size = 5, sort = "user.nome", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Buscando atendimentos");
        var filterSpecification = AtendimentoSpecification.withFilters(filter);
        var userSpecification = AtendimentoSpecification.byPrincipal(principalUser);
        var combinedSpecification = filterSpecification.and(userSpecification);
        return repository.findAll(combinedSpecification, pageable);
    }

    @PostMapping
    @CacheEvict(value = "atendimentos", allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(responses = {
            @ApiResponse(responseCode = "400", description = "Falha na validação"),
            @ApiResponse(responseCode = "403", description = "Permissão negada")
    }, description = "Cadastrar atendimento", tags = "atendimentos", summary = "Cadastrar atendimento")
    public Atendimento create(@RequestBody @Valid Atendimento atendimento, @AuthenticationPrincipal User user) {
        log.info("Cadastrando atendimento do" + atendimento.getUser().getNome());
        atendimento.setUser(user);
        return repository.save(atendimento);
    }

    @GetMapping("{id_atendimento}")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Registro encontrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Listar atendimento pelo id", tags = "atendimentos", summary = "Listar atendimento pelo id")
    public Atendimento get(@PathVariable Long id_atendimento, @AuthenticationPrincipal User user) {
        log.info("Buscando atendimento " + id_atendimento);
        checkPermission(id_atendimento, user);
        return getAtendimento(id_atendimento);
    }

    @DeleteMapping("{id_atendimento}")
    @Operation(responses = {
        @ApiResponse(responseCode = "204", description = "Registro removido com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Deletar atendimento pelo id", tags = "atendimentos", summary = "Deletar atendimento")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id_atendimento, @AuthenticationPrincipal User user) {
        log.info("Apagando atendimento " + id_atendimento);
        checkPermission(id_atendimento, user);
        repository.delete(getAtendimento(id_atendimento));
    }

    @PutMapping("{id_atendimento}")
    @Operation(responses = {
        @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Falha na validação dos dados"),
        @ApiResponse(responseCode = "403", description = "Permissão negada"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    },description = "Update atendimento pelo id", tags = "atendimentos", summary = "Update atendimento pelo id")
    public Atendimento update(@PathVariable long id_atendimento, @RequestBody @Valid Atendimento atendimento, @AuthenticationPrincipal User user) {
        log.info("Atualizando atendimento " + id_atendimento + " " + atendimento);
        checkPermission(id_atendimento, user);
        atendimento.setId_atendimento(id_atendimento);
        atendimento.setUser(user);
        return repository.save(atendimento);
    }

    private void checkPermission(Long id, User principalUser) {
        var atendimentoOld = getAtendimento(id);

        // Debug para ver os IDs
        System.out.println("Principal User ID: " + principalUser.getId_user() + " Email: " + principalUser.getEmail());
        if (atendimentoOld.getUser() != null) {
            System.out.println("Atendimento Old User ID: " + atendimentoOld.getUser().getId_user() + " Email: " + atendimentoOld.getUser().getEmail());
        }
        if (atendimentoOld.getPsicologo() != null) {
            System.out.println("Atendimento Old Psicologo ID: " + atendimentoOld.getPsicologo().getId_user() + " Email: " + atendimentoOld.getPsicologo().getEmail());
        }

        boolean isUser = atendimentoOld.getUser() != null &&
                atendimentoOld.getUser().getId_user().equals(principalUser.getId_user());

        boolean isPsicologo = atendimentoOld.getPsicologo() != null &&
                atendimentoOld.getPsicologo().getId_user().equals(principalUser.getId_user());

        if (!(isUser || isPsicologo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este atendimento.");
        }
    }

    private Atendimento getAtendimento(Long id_atendimento) {
        return repository.findById(id_atendimento)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "atendimento não encontrada"));
    }
}
