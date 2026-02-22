package com.investments.portfolio.repository;

import com.investments.portfolio.model.entity.Investment;
import com.investments.portfolio.model.enums.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    
    List<Investment> findByType(AssetType type);
    
    List<Investment> findBySymbolContainingIgnoreCase(String symbol);
    
    List<Investment> findByNameContainingIgnoreCase(String name);
    
}
