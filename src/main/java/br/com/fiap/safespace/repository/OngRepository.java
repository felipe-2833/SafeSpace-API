package br.com.fiap.safespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.fiap.safespace.model.Ong;

public interface OngRepository extends JpaRepository<Ong, Long>, JpaSpecificationExecutor<Ong>{
    
}
