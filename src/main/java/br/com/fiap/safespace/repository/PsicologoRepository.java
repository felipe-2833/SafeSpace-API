package br.com.fiap.safespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.fiap.safespace.model.Psicologo;

public interface PsicologoRepository extends JpaRepository<Psicologo, Long>, JpaSpecificationExecutor<Psicologo>{
    
}
