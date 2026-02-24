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
import java.util.Locale;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulationMarketDataService implements MarketDataService {

    private final InvestmentRepository investmentRepository;

    private final Random random = new Random();

    @Value("${app.market-data.simulation.price-variation-percentage:10.0}")
    private double priceVariationPercentage;

    private static final Map<String, BigDecimal> STOCK_BASE = Map.of(
            "PETR4", BigDecimal.valueOf(30.50),
            "VALE3", BigDecimal.valueOf(68.90),
            "ITUB4", BigDecimal.valueOf(32.15),
            "BBAS3", BigDecimal.valueOf(56.80),
            "BBDC4", BigDecimal.valueOf(17.45)
    );

    private static final Map<String, BigDecimal> CRYPTO_BASE = Map.of(
            "BTC", BigDecimal.valueOf(250000.00),
            "ETH", BigDecimal.valueOf(16000.00),
            "ADA", BigDecimal.valueOf(2.50),
            "SOL", BigDecimal.valueOf(350.00),
            "XRP", BigDecimal.valueOf(3.20)
    );

    private static final Map<String, BigDecimal> FUND_BASE = Map.of(
            "BOVA11", BigDecimal.valueOf(105.30),
            "IVVB11", BigDecimal.valueOf(245.80),
            "HGLG11", BigDecimal.valueOf(178.90),
            "SMAL11", BigDecimal.valueOf(121.70),
            "HASH11", BigDecimal.valueOf(59.80)
    );

    private static final Map<String, BigDecimal> FIXED_INCOME_BASE = Map.of(
            "CDB", BigDecimal.valueOf(1000.00),
            "LCI", BigDecimal.valueOf(1000.00),
            "LCA", BigDecimal.valueOf(1000.00),
            "TESOURO", BigDecimal.valueOf(1000.00)
    );

    @Override
    public BigDecimal getCurrentPrice(String symbol, AssetType assetType) {
        String key = normalizeSymbol(symbol);
        BigDecimal base = basePrice(assetType, key);
        return applyVariation(base, assetType);
    }

    @Override
    @Scheduled(fixedRateString = "${app.market-data.simulation.update-rate-ms:60000}")
    public void updateAllMarketPrices() {
        List<Investment> investments = investmentRepository.findAll();
        if (investments.isEmpty()) {
            return;
        }

        for (Investment inv : investments) {
            BigDecimal newPrice = getCurrentPrice(inv.getSymbol(), inv.getType());
            inv.setCurrentPrice(newPrice);
        }

        investmentRepository.saveAll(investments);
    }

    private BigDecimal basePrice(AssetType type, String symbol) {
        return switch (type) {
            case ACAO -> STOCK_BASE.getOrDefault(symbol, BigDecimal.valueOf(50.00));
            case CRIPTO -> CRYPTO_BASE.getOrDefault(symbol, BigDecimal.valueOf(100.00));
            case FUNDO -> FUND_BASE.getOrDefault(symbol, BigDecimal.valueOf(100.00));
            case RENDA_FIXA -> FIXED_INCOME_BASE.getOrDefault(symbol, BigDecimal.valueOf(1000.00));
            case OUTRO -> BigDecimal.valueOf(100.00);
        };
    }

    private BigDecimal applyVariation(BigDecimal basePrice, AssetType assetType) {
        double range = variationRange(assetType);
        double delta = (random.nextDouble() * 2.0 - 1.0) * range;
        double multiplier = 1.0 + (delta / 100.0);

        return basePrice.multiply(BigDecimal.valueOf(multiplier))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private double variationRange(AssetType assetType) {
        return switch (assetType) {
            case CRIPTO -> priceVariationPercentage * 2.0;
            case ACAO -> priceVariationPercentage * 1.5;
            default -> priceVariationPercentage;
        };
    }

    private String normalizeSymbol(String raw) {
        return raw == null ? "" : raw.trim().toUpperCase(Locale.ROOT);
    }
}