CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE endereco (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(), cep VARCHAR(20), logradouro VARCHAR(255), numero VARCHAR(50), complemento VARCHAR(100),
 bairro VARCHAR(100), cidade VARCHAR(100), uf VARCHAR(2), latitude DECIMAL(10,8), longitude DECIMAL(11,8),
 CONSTRAINT ck_endereco_uf CHECK (uf IS NULL OR char_length(uf)=2),
 CONSTRAINT ck_endereco_lat CHECK (latitude IS NULL OR latitude BETWEEN -90 AND 90),
 CONSTRAINT ck_endereco_lon CHECK (longitude IS NULL OR longitude BETWEEN -180 AND 180));
CREATE TABLE usuaria (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(), nome VARCHAR(255) NOT NULL DEFAULT '', email VARCHAR(255) NOT NULL,
 senha_hash VARCHAR(255) NOT NULL, cidade_uf VARCHAR(100), role VARCHAR(20) NOT NULL DEFAULT 'USER', endereco_id UUID REFERENCES endereco(id) ON DELETE SET NULL,
 criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, CONSTRAINT uk_usuaria_email UNIQUE(email),
 CONSTRAINT ck_usuaria_role CHECK(role IN ('USER','VOLUNTARIA','ADMIN')));
CREATE TABLE unidade_apoio (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(), nome VARCHAR(255) NOT NULL, descricao TEXT, cidade VARCHAR(100) NOT NULL, bairro VARCHAR(100),
 endereco VARCHAR(255), telefone VARCHAR(40), site VARCHAR(500), ativa BOOLEAN NOT NULL DEFAULT TRUE, status VARCHAR(50) NOT NULL DEFAULT 'PENDENTE',
 endereco_id UUID REFERENCES endereco(id), verificado_em TIMESTAMPTZ, criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_unidade_status CHECK(status IN ('PENDENTE','APROVADA','INATIVA')));
CREATE TABLE tipo_servico (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), nome VARCHAR(100) NOT NULL, descricao TEXT, CONSTRAINT uk_tipo_servico_nome UNIQUE(nome));
CREATE TABLE unidade_servico (unidade_id UUID NOT NULL REFERENCES unidade_apoio(id) ON DELETE CASCADE,
 tipo_servico_id UUID NOT NULL REFERENCES tipo_servico(id) ON DELETE RESTRICT, PRIMARY KEY(unidade_id,tipo_servico_id));
CREATE TABLE profissional_voluntaria (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(), usuaria_id UUID NOT NULL UNIQUE REFERENCES usuaria(id) ON DELETE CASCADE,
 tipo_servico_id UUID NOT NULL REFERENCES tipo_servico(id) ON DELETE RESTRICT, endereco_id UUID REFERENCES endereco(id) ON DELETE SET NULL,
 registo_profissional VARCHAR(100), registro_profissional VARCHAR(100), telefone_contacto VARCHAR(30), biografia TEXT, cidade_uf VARCHAR(100),
 ativa BOOLEAN NOT NULL DEFAULT TRUE, status VARCHAR(50) NOT NULL DEFAULT 'EM_ANALISE', verificado_em TIMESTAMPTZ,
 criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, CONSTRAINT ck_profissional_status CHECK(status IN ('EM_ANALISE','APROVADA','INATIVA')));
CREATE TABLE profissional_servico (profissional_id UUID NOT NULL REFERENCES profissional_voluntaria(id) ON DELETE CASCADE,
 tipo_servico_id UUID NOT NULL REFERENCES tipo_servico(id) ON DELETE RESTRICT, PRIMARY KEY(profissional_id,tipo_servico_id));
CREATE TABLE meu_apoio (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), usuaria_id UUID NOT NULL REFERENCES usuaria(id) ON DELETE CASCADE,
 unidade_id UUID REFERENCES unidade_apoio(id) ON DELETE CASCADE, profissional_id UUID REFERENCES profissional_voluntaria(id) ON DELETE CASCADE,
 criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, CONSTRAINT ck_meu_apoio_alvo CHECK((unidade_id IS NOT NULL AND profissional_id IS NULL) OR (unidade_id IS NULL AND profissional_id IS NOT NULL)));
CREATE UNIQUE INDEX uk_meu_apoio_usuaria_unidade ON meu_apoio(usuaria_id,unidade_id) WHERE unidade_id IS NOT NULL;
CREATE UNIQUE INDEX uk_meu_apoio_usuaria_profissional ON meu_apoio(usuaria_id,profissional_id) WHERE profissional_id IS NOT NULL;
CREATE INDEX idx_endereco_cidade_bairro ON endereco(cidade,bairro); CREATE INDEX idx_unidade_cidade ON unidade_apoio(cidade);
CREATE INDEX idx_unidade_bairro ON unidade_apoio(bairro); CREATE INDEX idx_profissional_tipo ON profissional_voluntaria(tipo_servico_id);
CREATE INDEX idx_meu_apoio_usuaria ON meu_apoio(usuaria_id);
