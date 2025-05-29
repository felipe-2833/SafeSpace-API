package br.com.fiap.safespace.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.com.fiap.safespace.controller.VoluntarioController;
import br.com.fiap.safespace.model.Voluntario;
import jakarta.persistence.criteria.Predicate;

public class VoluntarioSpecification {
     public static Specification<Voluntario> withFilters(VoluntarioController.VoluntarioFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.nome() != null && !filter.nome().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("nome")), "%" + filter.nome().toLowerCase() + "%"));
            }

            if (filter.endereco() != null && !filter.endereco().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("endereco")), "%" + filter.endereco().toLowerCase() + "%"));
            }

            if (filter.disponibilidade() != null && !filter.disponibilidade().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("disponibilidade")), "%" + filter.disponibilidade().toLowerCase() + "%"));
            }

            if (filter.area_atuacao() != null && !filter.area_atuacao().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("area_atuacao")), "%" + filter.area_atuacao().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
