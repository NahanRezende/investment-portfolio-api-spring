package com.investments.portfolio.model.dto;

import com.investments.portfolio.model.enums.AssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryDTO {

    private BigDecimal totalInvested;
    private Map<AssetType, BigDecimal> totalByType;
    private Integer assetCount;
}
