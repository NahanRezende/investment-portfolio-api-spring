package com.investments.portfolio.controller;

import com.investments.portfolio.model.dto.InvestmentRequestDTO;
import com.investments.portfolio.model.dto.InvestmentResponseDTO;
import com.investments.portfolio.model.dto.SummaryDTO;
import com.investments.portfolio.model.enums.AssetType;
import com.investments.portfolio.service.InvestmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/investments")
@RequiredArgsConstructor
@Tag(name = "Investments", description = "API para gerenciamento de carteira de investimentos")
public class InvestmentController {

    private final InvestmentService investmentService;

    @PostMapping
    @Operation(summary = "Cadastrar novo ativo na carteira")
    public ResponseEntity<InvestmentResponseDTO> createInvestment(
            @Valid @RequestBody InvestmentRequestDTO requestDTO) {
        InvestmentResponseDTO response = investmentService.createInvestment(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os ativos da carteira")
    public ResponseEntity<List<InvestmentResponseDTO>> getAllInvestments(
            @RequestParam(required = false) AssetType type) {

        List<InvestmentResponseDTO> investments;
        if (type != null) {
            investments = investmentService.getInvestmentsByType(type);
        } else {
            investments = investmentService.getAllInvestments();
        }

        return ResponseEntity.ok(investments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ativo por ID")
    public ResponseEntity<InvestmentResponseDTO> getInvestmentById(@PathVariable Long id) {
        InvestmentResponseDTO investment = investmentService.getInvestmentById(id);
        return ResponseEntity.ok(investment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um ativo")
    public ResponseEntity<InvestmentResponseDTO> updateInvestment(
            @PathVariable Long id,
            @Valid @RequestBody InvestmentRequestDTO requestDTO) {

        InvestmentResponseDTO updatedInvestment = investmentService.updateInvestment(id, requestDTO);
        return ResponseEntity.ok(updatedInvestment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover um ativo da carteira")
    public ResponseEntity<Void> deleteInvestment(@PathVariable Long id) {
        investmentService.deleteInvestment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    @Operation(summary = "Obter resumo da carteira")
    public ResponseEntity<SummaryDTO> getSummary() {
        SummaryDTO summary = investmentService.getSummary();
        return ResponseEntity.ok(summary);
    }
}
