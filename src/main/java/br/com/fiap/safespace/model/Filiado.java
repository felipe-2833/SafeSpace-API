package br.com.fiap.safespace.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "campo obrigatório")
    @ManyToOne
    private Ong ong;

    @PrePersist
    public void setRoleAsFiliado() {
        this.setRole(UserRole.FILIADO);
    }
}
