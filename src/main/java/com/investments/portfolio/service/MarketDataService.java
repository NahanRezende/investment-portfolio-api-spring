package com.investments.portfolio.service;

import com.investments.portfolio.model.enums.AssetType;

import java.math.BigDecimal;

public interface MarketDataService {
    
    BigDecimal getCurrentPrice(String symbol, AssetType assetType);
    
    void updateAllMarketPrices();
}
