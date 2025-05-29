package br.com.fiap.safespace.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.com.fiap.safespace.controller.UserController.UserFilter;
import br.com.fiap.safespace.model.User;
import jakarta.persistence.criteria.Predicate;

public class UserSpecification {
    public static Specification<User> withFilters(UserFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.nome() != null && !filter.nome().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("nome")), "%" + filter.nome().toLowerCase() + "%"));
            }

            if (filter.endereco() != null && !filter.endereco().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("endereco")), "%" + filter.endereco().toLowerCase() + "%"));
            }

            if (filter.role() != null) {
                predicates.add(cb.equal(root.get("role"), filter.role()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
