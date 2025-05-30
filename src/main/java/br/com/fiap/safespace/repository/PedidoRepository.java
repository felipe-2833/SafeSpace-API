package br.com.fiap.safespace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.fiap.safespace.model.Pedido;
import br.com.fiap.safespace.model.User;

public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido>{

    Page<Pedido> findByuser(User user, Specification<Pedido> specification, Pageable pageable);
    
}
