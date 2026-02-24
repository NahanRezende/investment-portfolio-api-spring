package com.investments.portfolio.model.dto;

import com.investments.portfolio.model.enums.AssetType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentResponseDTO {

    private Long id;
    private AssetType type;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
    private LocalDate purchaseDate;

}