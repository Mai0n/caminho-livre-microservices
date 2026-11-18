CREATE TABLE IF NOT EXISTS tb_atendimento (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ocorrencia_id BIGINT NOT NULL,
    endereco_ocorrencia VARCHAR(255) NOT NULL,
    indice_prioridade DOUBLE NOT NULL,
    equipe_responsavel VARCHAR(100) NOT NULL,
    descricao_servico VARCHAR(255) NOT NULL,
    data_abertura DATE NOT NULL,
    data_prevista DATE NOT NULL,
    data_conclusao DATE NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT pk_tb_atendimento PRIMARY KEY (id)
);
