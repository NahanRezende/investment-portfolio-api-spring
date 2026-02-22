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
import java.util.Arrays;
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
        log.info("Criando novo investimento: {}", requestDTO.getSymbol());

        String normalizedSymbol = requestDTO.getSymbol().toUpperCase().trim();

        Investment investment = Investment.builder()
                .type(requestDTO.getType())
                .symbol(normalizedSymbol)
                // Campo interno mantido na entidade, mas nao exposto/recebido pelo contrato atual
                .name(normalizedSymbol)
                .quantity(requestDTO.getQuantity())
                .purchasePrice(requestDTO.getPurchasePrice())
                .purchaseDate(requestDTO.getPurchaseDate())
                .build();

        try {
            BigDecimal currentPrice = marketDataService.getCurrentPrice(
                    investment.getSymbol(),
                    investment.getType()
            );
            investment.setCurrentPrice(currentPrice);
        } catch (Exception e) {
            log.warn("Erro ao buscar preco de mercado para {}: {}. Usando preco de compra como fallback.",
                    investment.getSymbol(), e.getMessage());
            investment.setCurrentPrice(requestDTO.getPurchasePrice());
        }

        Investment savedInvestment = investmentRepository.save(investment);
        log.info("Investimento criado com ID: {}", savedInvestment.getId());

        return mapToResponseDTO(savedInvestment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentResponseDTO> getAllInvestments() {
        log.info("Buscando todos os investimentos");
        return investmentRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentResponseDTO> getInvestmentsByType(AssetType type) {
        log.info("Buscando investimentos por tipo: {}", type);
        return investmentRepository.findByType(type)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InvestmentResponseDTO getInvestmentById(Long id) {
        log.info("Buscando investimento por ID: {}", id);
        return mapToResponseDTO(findInvestmentById(id));
    }

    @Override
    public InvestmentResponseDTO updateInvestment(Long id, InvestmentRequestDTO requestDTO) {
        log.info("Atualizando investimento ID: {}", id);

        Investment investment = findInvestmentById(id);
        String normalizedSymbol = requestDTO.getSymbol().toUpperCase().trim();

        investment.setType(requestDTO.getType());
        investment.setSymbol(normalizedSymbol);
        investment.setName(normalizedSymbol);
        investment.setQuantity(requestDTO.getQuantity());
        investment.setPurchasePrice(requestDTO.getPurchasePrice());
        investment.setPurchaseDate(requestDTO.getPurchaseDate());

        if (investment.getCurrentPrice() == null) {
            BigDecimal currentPrice = marketDataService.getCurrentPrice(
                    investment.getSymbol(),
                    investment.getType()
            );
            investment.setCurrentPrice(currentPrice);
        }

        Investment updatedInvestment = investmentRepository.save(investment);
        log.info("Investimento ID: {} atualizado", id);

        return mapToResponseDTO(updatedInvestment);
    }

    @Override
    public void deleteInvestment(Long id) {
        log.info("Deletando investimento ID: {}", id);

        if (!investmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Investimento não encontrado com ID: " + id);
        }

        investmentRepository.deleteById(id);
        log.info("Investimento ID: {} deletado", id);
    }

    @Override
    @Transactional(readOnly = true)
    public SummaryDTO getSummary() {
        log.info("Gerando resumo da carteira");

        List<Investment> investments = investmentRepository.findAll();
        int assetCount = investments.size();

        BigDecimal totalInvested = investments.stream()
                .map(i -> i.getPurchasePrice().multiply(i.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<AssetType, BigDecimal> totalByType = Arrays.stream(AssetType.values())
                .collect(Collectors.toMap(
                        type -> type,
                        type -> investments.stream()
                                .filter(i -> i.getType() == type)
                                .map(i -> i.getPurchasePrice().multiply(i.getQuantity()))
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ));

        return SummaryDTO.builder()
                .totalInvested(totalInvested)
                .totalByType(totalByType)
                .assetCount(assetCount)
                .build();
    }

    @Override
    public InvestmentResponseDTO updateMarketPrice(Long id, BigDecimal currentPrice) {
        log.info("Atualizando preco de mercado para investimento ID: {}", id);

        Investment investment = findInvestmentById(id);
        investment.setCurrentPrice(currentPrice);

        Investment updatedInvestment = investmentRepository.save(investment);
        log.info("Preco de mercado atualizado para investimento ID: {}", id);

        return mapToResponseDTO(updatedInvestment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentResponseDTO> searchInvestments(String symbol, String name) {
        log.info("Buscando investimentos com simbolo: {}, nome: {}", symbol, name);

        List<Investment> investments;

        if (symbol != null && !symbol.isBlank()) {
            investments = investmentRepository.findBySymbolContainingIgnoreCase(symbol);
        } else if (name != null && !name.isBlank()) {
            investments = investmentRepository.findByNameContainingIgnoreCase(name);
        } else {
            investments = investmentRepository.findAll();
        }

        return investments.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private Investment findInvestmentById(Long id) {
        return investmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Investimento nao encontrado com ID: {}", id);
                    return new EntityNotFoundException("Investimento não encontrado com ID: " + id);
                });
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
}
