package com.investments.portfolio.model.dto;

import com.investments.portfolio.model.enums.AssetType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentRequestDTO {

    @NotNull(message = "Tipo do ativo e obrigatorio")
    private AssetType type;

    @NotBlank(message = "Simbolo e obrigatorio")
    @Size(max = 20, message = "Simbolo deve ter no maximo 20 caracteres")
    private String symbol;

    @NotNull(message = "Quantidade e obrigatoria")
    @DecimalMin(value = "0.0001", message = "Quantidade deve ser maior que zero")
    private BigDecimal quantity;

    @NotNull(message = "Preco de compra e obrigatorio")
    @DecimalMin(value = "0.01", message = "Preco de compra deve ser maior que zero")
    private BigDecimal purchasePrice;

    @NotNull(message = "Data de compra e obrigatoria")
    @PastOrPresent(message = "Data de compra deve ser no passado ou presente")
    private LocalDate purchaseDate;
}
