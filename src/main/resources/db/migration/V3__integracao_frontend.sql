ALTER TABLE tipo_servico ADD COLUMN codigo VARCHAR(50);

INSERT INTO tipo_servico (nome, descricao, codigo) VALUES
 ('Atendimento psicológico', 'Escuta e acompanhamento psicológico.', 'PSICOLOGICO'),
 ('Atendimento jurídico', 'Orientação jurídica e acompanhamento de direitos.', 'JURIDICO'),
 ('Assistência social', 'Acesso a benefícios, programas de renda e rede de proteção.', 'ASSISTENCIA_SOCIAL'),
 ('Saúde da mulher', 'Consultas, exames e orientação em saúde.', 'SAUDE_MULHER'),
 ('Apoio à vítima de violência doméstica', 'Acolhimento e orientação a vítimas de violência.', 'VIOLENCIA_DOMESTICA'),
 ('Orientação profissional', 'Currículo, entrevistas e retorno ao mercado de trabalho.', 'ORIENTACAO_PROFISSIONAL'),
 ('Apoio financeiro', 'Orientação sobre benefícios e apoio financeiro.', 'APOIO_FINANCEIRO'),
 ('Delegacia especializada', 'Registro de ocorrência e medidas protetivas.', 'DELEGACIA'),
 ('Outros', 'Outros tipos de apoio.', 'OUTROS')
ON CONFLICT (nome) DO UPDATE SET codigo = EXCLUDED.codigo;

UPDATE tipo_servico SET codigo = upper(regexp_replace(nome, '[^A-Za-z0-9]+', '_', 'g')) WHERE codigo IS NULL;

ALTER TABLE tipo_servico ALTER COLUMN codigo SET NOT NULL;
ALTER TABLE tipo_servico ADD CONSTRAINT uk_tipo_servico_codigo UNIQUE (codigo);

ALTER TABLE profissional_voluntaria
    ADD COLUMN area VARCHAR(30),
    ADD COLUMN profissao VARCHAR(100),
    ADD COLUMN bairro VARCHAR(100),
    ADD COLUMN endereco_atendimento VARCHAR(255),
    ADD COLUMN modalidades VARCHAR(100),
    ADD COLUMN disponibilidade VARCHAR(255);

ALTER TABLE unidade_apoio
    ADD COLUMN categoria VARCHAR(30),
    ADD COLUMN horario_funcionamento VARCHAR(100),
    ADD COLUMN modalidades VARCHAR(100),
    ADD COLUMN latitude DECIMAL(10,8),
    ADD COLUMN longitude DECIMAL(11,8),
    ADD CONSTRAINT ck_unidade_lat CHECK (latitude IS NULL OR latitude BETWEEN -90 AND 90),
    ADD CONSTRAINT ck_unidade_lon CHECK (longitude IS NULL OR longitude BETWEEN -180 AND 180);

CREATE INDEX idx_profissional_verificado ON profissional_voluntaria(verificado_em);
