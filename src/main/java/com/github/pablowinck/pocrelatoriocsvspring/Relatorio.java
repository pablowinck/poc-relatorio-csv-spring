package com.github.pablowinck.pocrelatoriocsvspring;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Relatorio {

    @Id
    private Long id;

    private String nome;

    private LocalDate dataCadastro;

    private String situacao;

}
