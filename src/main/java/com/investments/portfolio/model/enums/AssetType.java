package com.investments.portfolio.model.enums;

public enum AssetType {

    ACAO("Acao"),
    CRIPTO("Criptomoeda"),
    FUNDO("Fundo de Investimento"),
    RENDA_FIXA("Renda Fixa"),
    OUTRO("Outro");

    private final String descricao;

    AssetType(String descricao) {
        this.descricao = descricao;
    }
}