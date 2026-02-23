-- Inicializacao do banco de dados
-- Criar tabela de investimentos
CREATE TABLE IF NOT EXISTS investments (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    symbol VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    quantity NUMERIC(15,4) NOT NULL,
    purchase_price NUMERIC(15,2) NOT NULL,
    current_price NUMERIC(15,2),
    purchase_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_quantity_positive CHECK (quantity > 0),
    CONSTRAINT chk_purchase_price_positive CHECK (purchase_price > 0),
    CONSTRAINT chk_current_price_non_negative CHECK (current_price >= 0 OR current_price IS NULL)
);

-- Indices para performance
CREATE INDEX IF NOT EXISTS idx_investments_type ON investments(type);
CREATE INDEX IF NOT EXISTS idx_investments_symbol ON investments(symbol);
CREATE INDEX IF NOT EXISTS idx_investments_purchase_date ON investments(purchase_date);

-- Dados iniciais para demonstracao (apenas se a tabela estiver vazia)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM investments LIMIT 1) THEN
        INSERT INTO investments (type, symbol, name, quantity, purchase_price, current_price, purchase_date) VALUES
            ('ACAO', 'PETR4', 'Petrobras PN', 100, 28.50, 30.50, '2024-01-15'),
            ('ACAO', 'VALE3', 'Vale ON', 50, 65.80, 68.90, '2024-02-20'),
            ('ACAO', 'ITUB4', 'Itau Unibanco PN', 200, 30.15, 32.15, '2024-03-10'),
            ('CRIPTO', 'BTC', 'Bitcoin', 0.5, 200000.00, 250000.00, '2023-12-10'),
            ('CRIPTO', 'ETH', 'Ethereum', 2.0, 14000.00, 16000.00, '2024-03-05'),
            ('FUNDO', 'BOVA11', 'iShares Ibovespa', 20, 100.50, 105.30, '2024-04-12'),
            ('FUNDO', 'IVVB11', 'iShares S&P 500', 15, 240.80, 245.80, '2024-05-01'),
            ('RENDA_FIXA', 'CDB', 'CDB Banco XP', 10, 1000.00, 1025.00, '2024-05-18');
    END IF;
END $$;

-- Compatibilidade com bases antigas (enums legados)
UPDATE investments SET type = 'ACAO' WHERE type = 'STOCK';
UPDATE investments SET type = 'CRIPTO' WHERE type = 'CRYPTO';
UPDATE investments SET type = 'FUNDO' WHERE type = 'FUND';
UPDATE investments SET type = 'RENDA_FIXA' WHERE type = 'FIXED_INCOME';

-- Funcao para atualizar o timestamp de atualizacao
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger para atualizar automaticamente o updated_at
DROP TRIGGER IF EXISTS update_investments_updated_at ON investments;
CREATE TRIGGER update_investments_updated_at
    BEFORE UPDATE ON investments
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
