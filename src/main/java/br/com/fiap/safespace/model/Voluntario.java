package br.com.fiap.safespace.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
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
public class Voluntario extends User{

    @NotBlank(message = "campo obrigatório")
    private String disponibilidade;

    @NotBlank(message = "campo obrigatório")
    private String area_atuacao;

    @PrePersist
    public void setRoleAsVoluntario() {
        this.setRole(UserRole.VOLUNTARIO);
    }

}
