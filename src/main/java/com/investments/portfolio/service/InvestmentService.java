package com.investments.portfolio.service;

import com.investments.portfolio.model.dto.InvestmentRequestDTO;
import com.investments.portfolio.model.dto.InvestmentResponseDTO;
import com.investments.portfolio.model.dto.SummaryDTO;
import com.investments.portfolio.model.enums.AssetType;

import java.math.BigDecimal;
import java.util.List;

public interface InvestmentService {
    
    InvestmentResponseDTO createInvestment(InvestmentRequestDTO requestDTO);
    
    List<InvestmentResponseDTO> getAllInvestments();
    
    List<InvestmentResponseDTO> getInvestmentsByType(AssetType type);
    
    InvestmentResponseDTO getInvestmentById(Long id);
    
    InvestmentResponseDTO updateInvestment(Long id, InvestmentRequestDTO requestDTO);
    
    void deleteInvestment(Long id);
    
    SummaryDTO getSummary();
    
    InvestmentResponseDTO updateMarketPrice(Long id, BigDecimal currentPrice);
    
    List<InvestmentResponseDTO> searchInvestments(String symbol, String name);
}
