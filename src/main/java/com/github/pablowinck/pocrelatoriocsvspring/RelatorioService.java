package com.github.pablowinck.pocrelatoriocsvspring;

import com.opencsv.CSVWriter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

import javax.sql.DataSource;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class RelatorioService {

    private final S3Client s3Client;

    private final DataSource dataSource;

    @Value("${s3.bucket.name:teste-poc-relatorio-pablo}")
    private String bucketName;

    @Value("${s3.reports.path:reports/}")
    private String reportsPath;

    @Value("${report.delay.milliseconds:100}")
    private long delayMilliseconds;

    @Value("${report.pagination.size:1000}")
    private int paginationSize;

    public RelatorioService(S3Client s3Client, DataSource dataSource) {
        this.s3Client = s3Client;
        this.dataSource = dataSource;
    }

    @SuppressWarnings("BusyWait")
    public void gerarRelatorio(String key, String sqlQuery) throws Exception {
        log.info("Gerando relatório com chave {} e query {}", key, sqlQuery);
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setFetchSize(paginationSize);
                log.info("Executando query {}", sqlQuery);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // Usando S3OutputStream para streamar dados para o S3
                    try (S3OutputStream s3OutputStream = new S3OutputStream(s3Client, bucketName, reportsPath + key)) {
                        Writer writer = new OutputStreamWriter(s3OutputStream);
                        CSVWriter csvWriter = new CSVWriter(writer);
                        // Escreve os cabeçalhos
                        csvWriter.writeNext(getHeaders(resultSet));
                        // Escreve os dados linha por linha
                        List<String[]> rows = new ArrayList<>();
                        while (resultSet.next()) {
                            rows.add(getRowData(resultSet));
                            if (rows.size() >= paginationSize) {
                                log.info("Escrevendo {} linhas no S3", rows.size());
                                csvWriter.writeAll(rows);
                                rows.clear();
                                log.info("Adicionando delay de {}ms", delayMilliseconds);
                                Thread.sleep(delayMilliseconds);  // Adiciona delay configurável entre linhas
                            }
                        }
                        if (!rows.isEmpty()) {
                            log.info("Escrevendo {} linhas no S3", rows.size());
                            csvWriter.writeAll(rows);
                        }
                        csvWriter.flush();
                    }
                }
            }
        }
    }

    private String[] getHeaders(ResultSet resultSet) throws Exception {
        int columnCount = resultSet.getMetaData().getColumnCount();
        String[] headers = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            headers[i - 1] = resultSet.getMetaData().getColumnName(i);
        }
        return headers;
    }

    private String[] getRowData(ResultSet resultSet) throws Exception {
        int columnCount = resultSet.getMetaData().getColumnCount();
        String[] rowData = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            rowData[i - 1] = resultSet.getString(i);
        }
        return rowData;
    }

}
