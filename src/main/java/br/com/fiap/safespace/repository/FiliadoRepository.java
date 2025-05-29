package br.com.fiap.safespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.fiap.safespace.model.Filiado;

public interface FiliadoRepository extends JpaRepository<Filiado, Long>, JpaSpecificationExecutor<Filiado>{
    
}
