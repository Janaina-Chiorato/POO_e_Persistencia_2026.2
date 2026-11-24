-- ==========================================
-- TECHLAB INVENTORY & SERVICE DESK
-- Banco de Dados Relacional - SQLite
-- ==========================================

PRAGMA foreign_keys = ON;

BEGIN TRANSACTION;

-- ==========================================
-- TABELA DE ATIVOS
-- ==========================================

CREATE TABLE IF NOT EXISTS ativos (

    id INTEGER PRIMARY KEY CHECK (id > 0),

    codigo_patrimonio TEXT NOT NULL UNIQUE
        CHECK (length(trim(codigo_patrimonio)) > 0),

    nome TEXT NOT NULL
        CHECK (length(trim(nome)) > 0),

    modelo TEXT NOT NULL
        CHECK (length(trim(modelo)) > 0),

    data_aquisicao TEXT NOT NULL,

    status TEXT NOT NULL
        CHECK (length(trim(status)) > 0),

    valor REAL NOT NULL
        CHECK (valor >= 0),

    tipo TEXT NOT NULL
        CHECK (
            tipo IN (
                'SERVIDOR',
                'ESTACAO_TRABALHO',
                'DISPOSITIVO_REDE'
            )
        ),

    -- Atributos especificos de servidor

    ip_estatico TEXT,

    sistema_operacional TEXT,

    -- Compartilhado por servidor e estacao

    memoria_ram INTEGER
        CHECK (memoria_ram > 0),

    -- Atributos especificos de estacao

    processador TEXT,

    departamento TEXT,

    -- Atributos especificos de dispositivo

    tipo_dispositivo TEXT,

    endereco_ip TEXT,

    quantidade_portas INTEGER
        CHECK (quantidade_portas > 0),

    -- Validacao dos atributos por subclasse

    CHECK (

        (
            tipo = 'SERVIDOR'

            AND ip_estatico IS NOT NULL
            AND sistema_operacional IS NOT NULL
            AND memoria_ram IS NOT NULL

            AND processador IS NULL
            AND departamento IS NULL
            AND tipo_dispositivo IS NULL
            AND endereco_ip IS NULL
            AND quantidade_portas IS NULL
        )

        OR

        (
            tipo = 'ESTACAO_TRABALHO'

            AND processador IS NOT NULL
            AND memoria_ram IS NOT NULL
            AND departamento IS NOT NULL

            AND ip_estatico IS NULL
            AND sistema_operacional IS NULL
            AND tipo_dispositivo IS NULL
            AND endereco_ip IS NULL
            AND quantidade_portas IS NULL
        )

        OR

        (
            tipo = 'DISPOSITIVO_REDE'

            AND tipo_dispositivo IS NOT NULL
            AND endereco_ip IS NOT NULL
            AND quantidade_portas IS NOT NULL

            AND ip_estatico IS NULL
            AND sistema_operacional IS NULL
            AND memoria_ram IS NULL
            AND processador IS NULL
            AND departamento IS NULL
        )
    )
);

-- ==========================================
-- TABELA DE CHAMADOS
-- ==========================================

CREATE TABLE IF NOT EXISTS chamados (

    id INTEGER PRIMARY KEY CHECK (id > 0),

    titulo TEXT NOT NULL
        CHECK (length(trim(titulo)) > 0),

    descricao TEXT NOT NULL
        CHECK (length(trim(descricao)) > 0),

    prioridade TEXT NOT NULL
        CHECK (
            prioridade IN (
                'BAIXA',
                'MEDIA',
                'ALTA',
                'CRITICA'
            )
        ),

    data_abertura TEXT NOT NULL,

    status TEXT NOT NULL
        DEFAULT 'ABERTO'
        CHECK (
            status IN (
                'ABERTO',
                'EM_ATENDIMENTO',
                'RESOLVIDO',
                'FECHADO'
            )
        ),

    ativo_id INTEGER NOT NULL,

    FOREIGN KEY (ativo_id)
        REFERENCES ativos(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
);

-- ==========================================
-- INDICES PARA OTIMIZACAO DAS CONSULTAS
-- ==========================================

CREATE INDEX IF NOT EXISTS idx_ativos_tipo
    ON ativos(tipo);

CREATE INDEX IF NOT EXISTS idx_ativos_status
    ON ativos(status);

CREATE INDEX IF NOT EXISTS idx_ativos_nome
    ON ativos(nome);

CREATE INDEX IF NOT EXISTS idx_chamados_ativo
    ON chamados(ativo_id);

CREATE INDEX IF NOT EXISTS idx_chamados_status
    ON chamados(status);

CREATE INDEX IF NOT EXISTS idx_chamados_prioridade
    ON chamados(prioridade);

CREATE INDEX IF NOT EXISTS idx_chamados_data
    ON chamados(data_abertura);

COMMIT;