package com.investments.portfolio.model.enums;

public enum AssetType {
    ACAO("Acao"),
    CRIPTO("Criptomoeda"),
    FUNDO("Fundo de Investimento"),
    RENDA_FIXA("Renda Fixa"),
    OUTRO("Outro");

    private final String description;

    AssetType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
