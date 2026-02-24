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

-- Dados para demonstracao
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM investments LIMIT 1) THEN
        INSERT INTO investments (type, symbol, name, quantity, purchase_price, current_price, purchase_date) VALUES
            ('ACAO', 'BBAS3', 'Banco do Brasil ON', 80, 51.20, 54.90, '2024-06-14'),
            ('ACAO', 'WEGE3', 'WEG ON', 35, 37.60, 39.10, '2024-07-03'),
            ('ACAO', 'ABEV3', 'Ambev ON', 300, 13.45, 14.10, '2024-08-22'),
            ('CRIPTO', 'BTC', 'Bitcoin', 0.18, 285000.00, 312000.00, '2024-09-10'),
            ('CRIPTO', 'SOL', 'Solana', 12.0, 520.00, 610.00, '2024-10-05'),
            ('FUNDO', 'SMAL11', 'iShares Small Cap', 25, 118.40, 121.70, '2024-11-18'),
            ('FUNDO', 'HASH11', 'Hashdex Nasdaq Crypto', 10, 62.30, 59.80, '2024-12-02'),
            ('RENDA_FIXA', 'LCI', 'LCI Banco Inter', 6, 5000.00, 5225.00, '2025-01-20');
    END IF;
END $$;

-- Função de atualização do timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger que atualiza updated_at
DROP TRIGGER IF EXISTS update_investments_updated_at ON investments;
CREATE TRIGGER update_investments_updated_at
    BEFORE UPDATE ON investments
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();