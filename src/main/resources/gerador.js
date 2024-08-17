const fs = require('fs');
const path = require('path');

// Caminho para o arquivo schema.sql
const filePathImport = path.join(__dirname, 'schema.sql');
const filePathData = path.join(__dirname, 'data.sql');

// Função para gerar um nome aleatório
function gerarNomeAleatorio() {
    const nomes = ['João', 'Maria', 'Pedro', 'Ana', 'Lucas', 'Julia', 'Mateus', 'Carla', 'Gabriel', 'Fernanda'];
    return nomes[Math.floor(Math.random() * nomes.length)];
}

// Função para gerar uma data aleatória
function gerarDataAleatoria() {
    const start = new Date(2000, 0, 1);
    const end = new Date(2023, 7, 1);
    const date = new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()));
    return date.toISOString().split('T')[0];
}

// Função para gerar um status aleatório
function gerarStatusAleatorio() {
    const status = ['ATIVO', 'INATIVO', 'PENDENTE', 'CANCELADO'];
    return status[Math.floor(Math.random() * status.length)];
}

function gerarDDL() {
    const stream = fs.createWriteStream(filePathImport);

    stream.write('CREATE TABLE Relatorio (\n');
    stream.write('id INT PRIMARY KEY AUTO_INCREMENT,\n');
    stream.write('nome VARCHAR(255),\n');
    stream.write('dataCadastro DATE,\n');
    stream.write('situacao VARCHAR(255)\n');
    stream.write(');\n\n');

    stream.end();
    console.log('Arquivo schema.sql gerado com sucesso!');
}

// Função para criar o arquivo SQL com 1 milhão de registros
function gerarRegistros() {
    gerarDDL();
    const stream = fs.createWriteStream(filePathData);
    const tamanho = 1_000_000;
    // Cabeçalho do script SQL
    stream.write('INSERT INTO Relatorio (id, nome, dataCadastro, situacao) VALUES\n');

    for (let i = 1; i <= tamanho; i++) {
        const nome = gerarNomeAleatorio();
        const data = gerarDataAleatoria();
        const status = gerarStatusAleatorio();

        // Adiciona uma linha ao arquivo SQL
        stream.write(`(${i}, '${nome}', '${data}', '${status}')`);

        if (i < tamanho) {
            stream.write(',\n');
        } else {
            stream.write(';\n');
        }
    }

    stream.end();
    console.log('Arquivo data.sql gerado com sucesso!');
}

gerarRegistros();
