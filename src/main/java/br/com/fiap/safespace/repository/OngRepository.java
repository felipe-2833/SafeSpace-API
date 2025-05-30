package br.com.fiap.safespace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.fiap.safespace.model.Ong;
import br.com.fiap.safespace.model.User;

public interface OngRepository extends JpaRepository<Ong, Long>, JpaSpecificationExecutor<Ong>{

    Page<Ong> findByUser(User user, Specification<Ong> specification, Pageable pageable);
    
}
