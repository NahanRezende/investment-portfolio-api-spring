package com.investments.portfolio.model.dto;

import com.investments.portfolio.model.enums.AssetType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryDTO {

    private BigDecimal totalInvested;
    private Map<AssetType, BigDecimal> totalByType;
    private Integer assetCount;

}