package br.com.fiap.safespace.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder 
@NoArgsConstructor
@AllArgsConstructor
public class Filiado extends Voluntario{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_filiado;

    @ManyToOne
    @JsonIgnore
    private Ong ong;

    @PrePersist
    public void setRoleAsFiliado() {
        this.setRole(UserRole.FILIADO);
    }
}
