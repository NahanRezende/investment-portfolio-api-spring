package com.investments.portfolio.config;

import com.investments.portfolio.model.entity.Investment;
import com.investments.portfolio.model.enums.AssetType;
import com.investments.portfolio.repository.InvestmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final InvestmentRepository investmentRepository;

    @Override
    public void run(String... args) {
        if (investmentRepository.count() == 0) {
            log.info("Inicializando dados de exemplo...");

            createInvestment(AssetType.ACAO, "PETR4", "Petrobras PN",
                    BigDecimal.valueOf(100), BigDecimal.valueOf(28.50), BigDecimal.valueOf(30.50),
                    LocalDate.of(2024, 1, 15));

            createInvestment(AssetType.ACAO, "VALE3", "Vale ON",
                    BigDecimal.valueOf(50), BigDecimal.valueOf(65.80), BigDecimal.valueOf(68.90),
                    LocalDate.of(2024, 2, 20));

            createInvestment(AssetType.ACAO, "ITUB4", "Itau Unibanco PN",
                    BigDecimal.valueOf(200), BigDecimal.valueOf(30.15), BigDecimal.valueOf(32.15),
                    LocalDate.of(2024, 3, 10));

            createInvestment(AssetType.CRIPTO, "BTC", "Bitcoin",
                    BigDecimal.valueOf(0.5), BigDecimal.valueOf(200000.00), BigDecimal.valueOf(250000.00),
                    LocalDate.of(2023, 12, 10));

            createInvestment(AssetType.CRIPTO, "ETH", "Ethereum",
                    BigDecimal.valueOf(2.0), BigDecimal.valueOf(14000.00), BigDecimal.valueOf(16000.00),
                    LocalDate.of(2024, 3, 5));

            createInvestment(AssetType.FUNDO, "BOVA11", "iShares Ibovespa",
                    BigDecimal.valueOf(20), BigDecimal.valueOf(100.50), BigDecimal.valueOf(105.30),
                    LocalDate.of(2024, 4, 12));

            createInvestment(AssetType.FUNDO, "IVVB11", "iShares S&P 500",
                    BigDecimal.valueOf(15), BigDecimal.valueOf(240.80), BigDecimal.valueOf(245.80),
                    LocalDate.of(2024, 5, 1));

            createInvestment(AssetType.RENDA_FIXA, "CDB", "CDB Banco XP",
                    BigDecimal.valueOf(10), BigDecimal.valueOf(1000.00), BigDecimal.valueOf(1025.00),
                    LocalDate.of(2024, 5, 18));

            log.info("Dados de exemplo inicializados com sucesso!");
        } else {
            log.info("Banco de dados ja possui dados. Pulando inicializacao.");
        }
    }

    private void createInvestment(AssetType type, String symbol, String name,
                                  BigDecimal quantity, BigDecimal purchasePrice,
                                  BigDecimal currentPrice, LocalDate purchaseDate) {
        Investment investment = Investment.builder()
                .type(type)
                .symbol(symbol)
                .name(name)
                .quantity(quantity)
                .purchasePrice(purchasePrice)
                .currentPrice(currentPrice)
                .purchaseDate(purchaseDate)
                .build();

        investmentRepository.save(investment);
        log.info("Investimento criado: {} - {}", symbol, name);
    }
}
