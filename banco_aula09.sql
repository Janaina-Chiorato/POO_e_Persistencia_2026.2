
PRAGMA foreign_keys = ON;

-- Tabela de ativos
CREATE TABLE IF NOT EXISTS ativos (
    id INTEGER PRIMARY KEY,
    nome TEXT NOT NULL,
    patrimonio TEXT NOT NULL UNIQUE,
    valor REAL NOT NULL
);

-- Tabela de chamados de suporte
CREATE TABLE IF NOT EXISTS chamados (
    id INTEGER PRIMARY KEY,
    descricao TEXT NOT NULL,
    status TEXT NOT NULL,
    ativo_id INTEGER NOT NULL,
    FOREIGN KEY (ativo_id) REFERENCES ativos(id)
);
