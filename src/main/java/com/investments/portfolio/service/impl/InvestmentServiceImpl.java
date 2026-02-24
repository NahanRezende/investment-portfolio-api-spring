package com.investments.portfolio.service.impl;

import com.investments.portfolio.model.dto.InvestmentRequestDTO;
import com.investments.portfolio.model.dto.InvestmentResponseDTO;
import com.investments.portfolio.model.dto.SummaryDTO;
import com.investments.portfolio.model.entity.Investment;
import com.investments.portfolio.model.enums.AssetType;
import com.investments.portfolio.repository.InvestmentRepository;
import com.investments.portfolio.service.InvestmentService;
import com.investments.portfolio.service.MarketDataService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final MarketDataService marketDataService;

    @Override
    public InvestmentResponseDTO createInvestment(InvestmentRequestDTO requestDTO) {
        String symbol = normalizeSymbol(requestDTO.getSymbol());

        Investment investment = Investment.builder()
                .type(requestDTO.getType())
                .symbol(symbol)
                .name(symbol)
                .quantity(requestDTO.getQuantity())
                .purchasePrice(requestDTO.getPurchasePrice())
                .purchaseDate(requestDTO.getPurchaseDate())
                .build();

        BigDecimal marketPrice = resolveMarketPriceOrFallback(symbol, requestDTO.getType(), requestDTO.getPurchasePrice());
        investment.setCurrentPrice(marketPrice);

        Investment saved = investmentRepository.save(investment);
        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentResponseDTO> getAllInvestments() {
        return investmentRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentResponseDTO> getInvestmentsByType(AssetType type) {
        return investmentRepository.findByType(type)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InvestmentResponseDTO getInvestmentById(Long id) {
        return mapToResponseDTO(findInvestmentById(id));
    }

    @Override
    public InvestmentResponseDTO updateInvestment(Long id, InvestmentRequestDTO requestDTO) {
        Investment investment = findInvestmentById(id);
        String symbol = normalizeSymbol(requestDTO.getSymbol());

        investment.setType(requestDTO.getType());
        investment.setSymbol(symbol);
        investment.setName(symbol);
        investment.setQuantity(requestDTO.getQuantity());
        investment.setPurchasePrice(requestDTO.getPurchasePrice());
        investment.setPurchaseDate(requestDTO.getPurchaseDate());

        if (investment.getCurrentPrice() == null) {
            BigDecimal marketPrice = resolveMarketPriceOrFallback(symbol, requestDTO.getType(), requestDTO.getPurchasePrice());
            investment.setCurrentPrice(marketPrice);
        }

        Investment updated = investmentRepository.save(investment);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteInvestment(Long id) {
        if (!investmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Investimento não encontrado com ID: " + id);
        }
        investmentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public SummaryDTO getSummary() {
        List<Investment> investments = investmentRepository.findAll();
        int assetCount = investments.size();

        BigDecimal totalInvested = investments.stream()
                .map(this::investedValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<AssetType, BigDecimal> totalByType = investments.stream()
                .filter(i -> i.getType() != null)
                .collect(Collectors.groupingBy(
                        Investment::getType,
                        () -> new EnumMap<>(AssetType.class),
                        Collectors.mapping(this::investedValue, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        for (AssetType type : AssetType.values()) {
            totalByType.putIfAbsent(type, BigDecimal.ZERO);
        }

        return SummaryDTO.builder()
                .totalInvested(totalInvested)
                .totalByType(totalByType)
                .assetCount(assetCount)
                .build();
    }

    @Override
    public InvestmentResponseDTO updateMarketPrice(Long id, BigDecimal currentPrice) {
        Investment investment = findInvestmentById(id);
        investment.setCurrentPrice(currentPrice);
        Investment updated = investmentRepository.save(investment);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentResponseDTO> searchInvestments(String symbol, String name) {
        List<Investment> investments;

        if (symbol != null && !symbol.isBlank()) {
            investments = investmentRepository.findBySymbolContainingIgnoreCase(symbol.trim());
        } else if (name != null && !name.isBlank()) {
            investments = investmentRepository.findByNameContainingIgnoreCase(name.trim());
        } else {
            investments = investmentRepository.findAll();
        }

        return investments.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private Investment findInvestmentById(Long id) {
        return investmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Investimento não encontrado com ID: " + id));
    }

    private InvestmentResponseDTO mapToResponseDTO(Investment investment) {
        return InvestmentResponseDTO.builder()
                .id(investment.getId())
                .type(investment.getType())
                .symbol(investment.getSymbol())
                .quantity(investment.getQuantity())
                .purchasePrice(investment.getPurchasePrice())
                .purchaseDate(investment.getPurchaseDate())
                .build();
    }

    private String normalizeSymbol(String raw) {
        return raw == null ? "" : raw.toUpperCase().trim();
    }

    private BigDecimal resolveMarketPriceOrFallback(String symbol, AssetType type, BigDecimal fallback) {
        try {
            return marketDataService.getCurrentPrice(symbol, type);
        } catch (Exception e) {
            return fallback;
        }
    }

    private BigDecimal investedValue(Investment investment) {
        return investment.getPurchasePrice().multiply(investment.getQuantity());
    }
}