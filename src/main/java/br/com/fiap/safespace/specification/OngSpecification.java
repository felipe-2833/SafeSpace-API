package br.com.fiap.safespace.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.com.fiap.safespace.controller.OngController.OngFilter;
import br.com.fiap.safespace.model.Ong;
import jakarta.persistence.criteria.Predicate;

public class OngSpecification {
    public static Specification<Ong> withFilters(OngFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.nome() != null && !filter.nome().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("nome")), "%" + filter.nome().toLowerCase() + "%"));
            }
            
            if (filter.telefone() != null && !filter.telefone().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("telefone")), "%" + filter.telefone().toLowerCase() + "%"));
            }

            if (filter.endereco() != null && !filter.endereco().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("endereco")), "%" + filter.endereco().toLowerCase() + "%"));
            }
            

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
