package br.com.fiap.safespace.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class Psicologo extends User{

    @NotBlank(message = "campo obrigatório")
    @Size(min = 6)
    private String crp;

    @NotBlank(message = "campo obrigatório")
    private String area_atuacao;

    @PrePersist
    public void setRoleAsPsicologo() {
        this.setRole(UserRole.PSICOLOGO);
    }

}
