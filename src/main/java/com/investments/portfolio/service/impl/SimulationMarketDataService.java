package com.investments.portfolio.service.impl;

import com.investments.portfolio.model.entity.Investment;
import com.investments.portfolio.model.enums.AssetType;
import com.investments.portfolio.repository.InvestmentRepository;
import com.investments.portfolio.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulationMarketDataService implements MarketDataService {

    private final InvestmentRepository investmentRepository;
    private final Random random = new Random();

    @Value("${app.market-data.simulation.price-variation-percentage:10.0}")
    private double priceVariationPercentage;

    @Override
    public BigDecimal getCurrentPrice(String symbol, AssetType assetType) {
        BigDecimal basePrice = getBasePriceBySymbol(symbol, assetType);
        return simulatePriceVariation(basePrice, assetType);
    }

    @Override
    @Scheduled(fixedRate = 60000)
    public void updateAllMarketPrices() {
        log.info("Atualizando precos de mercado simulados...");
        List<Investment> investments = investmentRepository.findAll();

        for (Investment investment : investments) {
            BigDecimal newPrice = getCurrentPrice(investment.getSymbol(), investment.getType());
            investment.setCurrentPrice(newPrice);
        }

        investmentRepository.saveAll(investments);
        log.info("Precos atualizados para {} investimentos", investments.size());
    }

    private BigDecimal getBasePriceBySymbol(String symbol, AssetType assetType) {
        return switch (assetType) {
            case ACAO -> BigDecimal.valueOf(getStockBasePrice(symbol));
            case CRIPTO -> BigDecimal.valueOf(getCryptoBasePrice(symbol));
            case FUNDO -> BigDecimal.valueOf(getFundBasePrice(symbol));
            case RENDA_FIXA -> BigDecimal.valueOf(getFixedIncomeBasePrice(symbol));
            case OUTRO -> BigDecimal.valueOf(100.0);
        };
    }

    private BigDecimal simulatePriceVariation(BigDecimal basePrice, AssetType assetType) {
        double variationRange = getVariationRange(assetType);
        double variation = (random.nextDouble() * 2 - 1) * variationRange;
        double multiplier = 1 + (variation / 100);

        return basePrice.multiply(BigDecimal.valueOf(multiplier))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private double getVariationRange(AssetType assetType) {
        return switch (assetType) {
            case CRIPTO -> priceVariationPercentage * 2;
            case ACAO -> priceVariationPercentage * 1.5;
            default -> priceVariationPercentage;
        };
    }

    private double getStockBasePrice(String symbol) {
        return switch (symbol.toUpperCase()) {
            case "PETR4" -> 30.50;
            case "VALE3" -> 68.90;
            case "ITUB4" -> 32.15;
            case "BBAS3" -> 56.80;
            case "BBDC4" -> 17.45;
            default -> 50.0;
        };
    }

    private double getCryptoBasePrice(String symbol) {
        return switch (symbol.toUpperCase()) {
            case "BTC" -> 250000.00;
            case "ETH" -> 16000.00;
            case "ADA" -> 2.50;
            case "SOL" -> 350.00;
            case "XRP" -> 3.20;
            default -> 100.0;
        };
    }

    private double getFundBasePrice(String symbol) {
        return switch (symbol.toUpperCase()) {
            case "BOVA11" -> 105.30;
            case "IVVB11" -> 245.80;
            case "HGLG11" -> 178.90;
            default -> 100.0;
        };
    }

    private double getFixedIncomeBasePrice(String symbol) {
        return switch (symbol.toUpperCase()) {
            case "CDB" -> 1000.00;
            case "LCI" -> 1000.00;
            case "LCA" -> 1000.00;
            case "TESOURO" -> 1000.00;
            default -> 1000.0;
        };
    }
}
