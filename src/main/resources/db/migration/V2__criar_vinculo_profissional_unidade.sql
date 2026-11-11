CREATE TABLE profissional_unidade (
    profissional_id UUID NOT NULL REFERENCES profissional_voluntaria(id) ON DELETE CASCADE,
    unidade_id UUID NOT NULL REFERENCES unidade_apoio(id) ON DELETE CASCADE,
    PRIMARY KEY (profissional_id, unidade_id)
);

CREATE INDEX idx_profissional_unidade_unidade ON profissional_unidade(unidade_id);
