package br.com.fiap.safespace.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.com.fiap.safespace.controller.PsicologoController.PsicologoFilter;
import br.com.fiap.safespace.model.Psicologo;
import jakarta.persistence.criteria.Predicate;

public class PsicologoSpecification {
    public static Specification<Psicologo> withFilters(PsicologoFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.nome() != null && !filter.nome().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("nome")), "%" + filter.nome().toLowerCase() + "%"));
            }

            if (filter.endereco() != null && !filter.endereco().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("endereco")), "%" + filter.endereco().toLowerCase() + "%"));
            }


            if (filter.crp() != null && !filter.crp().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("crp")), "%" + filter.crp().toLowerCase() + "%"));
            }

            if (filter.area_atuacao() != null && !filter.area_atuacao().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("area_atuacao")), "%" + filter.area_atuacao().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
