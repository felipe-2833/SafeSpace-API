package br.com.fiap.safespace.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Atendimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_atendimento;

    @PastOrPresent(message = "não pode ser no futuro")
    @JsonIgnore
    private LocalDateTime dataHora;

    @NotNull(message = "campo obrigatório")
    @Enumerated(EnumType.STRING)
    private Statustype status;

    @NotBlank(message = "campo obrigatório")
    private String realtorio;

    @NotNull(message = "campo obrigatório")
    @Enumerated(EnumType.STRING)
    private TipoAtendimento tipoAtendimento;

    @NotNull(message = "campo obrigatório")
    @ManyToOne
    private User user;

    @NotNull(message = "campo obrigatório")
    @ManyToOne
    private Psicologo psicologo;


}
