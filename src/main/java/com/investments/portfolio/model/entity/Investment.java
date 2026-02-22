package com.investments.portfolio.model.entity;

import com.investments.portfolio.model.enums.AssetType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "investments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Investment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType type;
    
    @Column(nullable = false, length = 20)
    private String symbol;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;
    
    @Column(name = "purchase_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal purchasePrice;
    
    @Column(name = "current_price", precision = 15, scale = 2)
    private BigDecimal currentPrice;
    
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Transient
    public BigDecimal getCurrentValue() {
        if (currentPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return currentPrice.multiply(quantity);
    }
    
    @Transient
    public BigDecimal getProfitLoss() {
        if (purchasePrice == null || quantity == null || currentPrice == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalPurchaseValue = purchasePrice.multiply(quantity);
        BigDecimal totalCurrentValue = currentPrice.multiply(quantity);
        return totalCurrentValue.subtract(totalPurchaseValue);
    }
    
    @Transient
    public BigDecimal getProfitLossPercentage() {
        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal profitLoss = getProfitLoss();
        BigDecimal totalPurchaseValue = purchasePrice.multiply(quantity);
        
        if (totalPurchaseValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return profitLoss.divide(totalPurchaseValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
