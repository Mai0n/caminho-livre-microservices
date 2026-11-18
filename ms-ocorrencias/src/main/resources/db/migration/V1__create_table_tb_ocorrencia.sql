CREATE TABLE IF NOT EXISTS tb_ocorrencia (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tipo_barreira VARCHAR(30) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    logradouro VARCHAR(150) NOT NULL,
    bairro VARCHAR(100) NOT NULL,
    cep VARCHAR(9) NOT NULL,
    gravidade INT NOT NULL,
    fator_entorno INT NOT NULL,
    confirmacoes INT NOT NULL DEFAULT 0,
    indice_prioridade DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL,
    data_registro DATE NOT NULL,
    CONSTRAINT pk_tb_ocorrencia PRIMARY KEY (id)
);
