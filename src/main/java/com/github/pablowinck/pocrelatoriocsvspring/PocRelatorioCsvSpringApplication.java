package com.github.pablowinck.pocrelatoriocsvspring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PocRelatorioCsvSpringApplication implements CommandLineRunner {

    @Autowired
    private RelatorioService relatorioService;

    public static void main(String[] args) {
        SpringApplication.run(PocRelatorioCsvSpringApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        var key = "relatorio-" + java.util.UUID.randomUUID() + ".csv";
        relatorioService.gerarRelatorio(key, "SELECT * FROM relatorio");
    }

}
