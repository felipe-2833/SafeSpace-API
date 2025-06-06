package br.com.fiap.safespace.specification;

import java.util.ArrayList;
import java.util.List;

import br.com.fiap.safespace.model.User;
import org.springframework.data.jpa.domain.Specification;

import br.com.fiap.safespace.controller.PedidoController.PedidoFilter;
import br.com.fiap.safespace.model.Pedido;
import jakarta.persistence.criteria.Predicate;

public class PedidoSpecification {
    public static Specification<Pedido> withFilters(PedidoFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.descricao() != null && !filter.descricao().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("descricao")), "%" + filter.descricao().toLowerCase() + "%"));
            }
            
            if (filter.startDate() != null && filter.endDate() != null) {
                predicates.add(
                        cb.between(root.get("dataSolicitacao"), filter.startDate(), filter.endDate()));
            }

            if (filter.startDate() != null && filter.endDate() == null) {
                predicates.add(cb.equal(root.get("dataSolicitacao"), filter.startDate()));
            }

            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }

            if (filter.pedidoType() != null) {
                predicates.add(cb.equal(root.get("pedidoType"), filter.pedidoType()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Pedido> byUser(User user) {
        return (root, query, cb) -> cb.equal(root.get("user"), user);
    }
}
