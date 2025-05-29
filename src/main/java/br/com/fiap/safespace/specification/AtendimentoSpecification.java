package br.com.fiap.safespace.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.com.fiap.safespace.controller.AtendimentoController.AtendimentoFilter;
import br.com.fiap.safespace.model.Atendimento;
import jakarta.persistence.criteria.Predicate;

public class AtendimentoSpecification {
    public static Specification<Atendimento> withFilters(AtendimentoFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.relatorio() != null && !filter.relatorio().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("relatorio")), "%" + filter.relatorio().toLowerCase() + "%"));
            }
            
            if (filter.startDate() != null && filter.endDate() != null) {
                predicates.add(
                        cb.between(root.get("data_cadastro"), filter.startDate(), filter.endDate()));
            }

            if (filter.startDate() != null && filter.endDate() == null) {
                predicates.add(cb.equal(root.get("data_cadastro"), filter.startDate()));
            }

            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }

            if (filter.atendimentoType() != null) {
                predicates.add(cb.equal(root.get("atendimentoType"), filter.atendimentoType()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
