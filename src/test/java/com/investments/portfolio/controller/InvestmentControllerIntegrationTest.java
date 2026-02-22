package com.investments.portfolio.controller;

import com.investments.portfolio.exception.GlobalExceptionHandler;
import com.investments.portfolio.model.dto.InvestmentResponseDTO;
import com.investments.portfolio.model.dto.SummaryDTO;
import com.investments.portfolio.model.enums.AssetType;
import com.investments.portfolio.repository.InvestmentRepository;
import com.investments.portfolio.service.InvestmentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvestmentController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class InvestmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvestmentService investmentService;

    @MockBean
    private InvestmentRepository investmentRepository;

    @Test
    void createInvestment_ShouldReturnMinimalResponseWithoutExtraFields() throws Exception {
        given(investmentService.createInvestment(any()))
                .willReturn(sampleResponse(1L, AssetType.ACAO, "BBAS3"));

        mockMvc.perform(post("/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "ACAO",
                                  "symbol": "BBAS3",
                                  "quantity": 100,
                                  "purchasePrice": 19.68,
                                  "purchaseDate": "2025-07-31"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("ACAO"))
                .andExpect(jsonPath("$.symbol").value("BBAS3"))
                .andExpect(jsonPath("$.quantity").value(100))
                .andExpect(jsonPath("$.purchasePrice").value(19.68))
                .andExpect(jsonPath("$.purchaseDate").value("2025-07-31"))
                .andExpect(jsonPath("$.name").doesNotExist())
                .andExpect(jsonPath("$.currentPrice").doesNotExist())
                .andExpect(jsonPath("$.currentValue").doesNotExist())
                .andExpect(jsonPath("$.profitLoss").doesNotExist())
                .andExpect(jsonPath("$.profitLossPercentage").doesNotExist())
                .andExpect(jsonPath("$.createdAt").doesNotExist())
                .andExpect(jsonPath("$.updatedAt").doesNotExist());
    }

    @Test
    void getAllInvestments_ShouldReturnMinimalItemsWithoutExtraFields() throws Exception {
        given(investmentService.getAllInvestments())
                .willReturn(List.of(sampleResponse(1L, AssetType.ACAO, "PETR4")));

        mockMvc.perform(get("/investments"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("ACAO"))
                .andExpect(jsonPath("$[0].symbol").value("PETR4"))
                .andExpect(jsonPath("$[0].name").doesNotExist())
                .andExpect(jsonPath("$[0].currentPrice").doesNotExist())
                .andExpect(jsonPath("$[0].currentValue").doesNotExist());
    }

    @Test
    void getInvestmentsByType_WithPtBrEnum_ShouldFilterUsingCripto() throws Exception {
        given(investmentService.getInvestmentsByType(eq(AssetType.CRIPTO)))
                .willReturn(List.of(sampleResponse(2L, AssetType.CRIPTO, "BTC")));

        mockMvc.perform(get("/investments").param("type", "CRIPTO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("CRIPTO"))
                .andExpect(jsonPath("$[0].symbol").value("BTC"))
                .andExpect(jsonPath("$[0].name").doesNotExist());
    }

    @Test
    void getInvestmentById_ShouldReturnMinimalResponseWithoutExtraFields() throws Exception {
        given(investmentService.getInvestmentById(eq(1L)))
                .willReturn(sampleResponse(1L, AssetType.ACAO, "PETR4"));

        mockMvc.perform(get("/investments/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("ACAO"))
                .andExpect(jsonPath("$.symbol").value("PETR4"))
                .andExpect(jsonPath("$.name").doesNotExist())
                .andExpect(jsonPath("$.currentPrice").doesNotExist());
    }

    @Test
    void updateInvestment_ShouldReturnMinimalResponseWithoutExtraFields() throws Exception {
        given(investmentService.updateInvestment(eq(1L), any()))
                .willReturn(sampleResponse(1L, AssetType.ACAO, "BBAS3"));

        mockMvc.perform(put("/investments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "ACAO",
                                  "symbol": "BBAS3",
                                  "quantity": 120,
                                  "purchasePrice": 20.15,
                                  "purchaseDate": "2025-07-31"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("ACAO"))
                .andExpect(jsonPath("$.symbol").value("BBAS3"))
                .andExpect(jsonPath("$.name").doesNotExist())
                .andExpect(jsonPath("$.currentValue").doesNotExist());
    }

    @Test
    void deleteInvestment_ShouldReturnNoContent() throws Exception {
        doNothing().when(investmentService).deleteInvestment(1L);

        mockMvc.perform(delete("/investments/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void getSummary_ShouldReturnExactlyEnunciadoFields() throws Exception {
        SummaryDTO summary = SummaryDTO.builder()
                .totalInvested(new BigDecimal("15000.00"))
                .totalByType(Map.of(
                        AssetType.ACAO, new BigDecimal("8000.00"),
                        AssetType.CRIPTO, new BigDecimal("1000.00"),
                        AssetType.FUNDO, new BigDecimal("6000.00")
                ))
                .assetCount(5)
                .build();

        given(investmentService.getSummary()).willReturn(summary);

        mockMvc.perform(get("/investments/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalInvested").value(15000.00))
                .andExpect(jsonPath("$.assetCount").value(5))
                .andExpect(jsonPath("$.totalByType.ACAO").value(8000.00))
                .andExpect(jsonPath("$.totalByType.CRIPTO").value(1000.00))
                .andExpect(jsonPath("$.totalByType.FUNDO").value(6000.00))
                .andExpect(jsonPath("$.currentTotalValue").doesNotExist())
                .andExpect(jsonPath("$.totalProfitLoss").doesNotExist())
                .andExpect(jsonPath("$.totalProfitLossPercentage").doesNotExist())
                .andExpect(jsonPath("$.currentValueByType").doesNotExist())
                .andExpect(jsonPath("$.*", hasSize(3)));
    }

    @Test
    void getInvestmentById_WhenInvestmentDoesNotExist_ShouldReturn404WithProblemDetail() throws Exception {
        given(investmentService.getInvestmentById(eq(999L)))
                .willThrow(new EntityNotFoundException("Investimento com ID 999 nao encontrado"));

        mockMvc.perform(get("/investments/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errorCode").value("INVESTMENT_NOT_FOUND"));
    }

    @Test
    void getInvestmentsByType_WhenTypeIsInvalid_ShouldReturn400WithInvalidAssetTypeErrorCode() throws Exception {
        mockMvc.perform(get("/investments").param("type", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("INVALID_ASSET_TYPE"));
    }

    @Test
    void createInvestment_WhenPayloadIsInvalid_ShouldReturn400WithValidationErrorCode() throws Exception {
        mockMvc.perform(post("/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    private InvestmentResponseDTO sampleResponse(Long id, AssetType type, String symbol) {
        return InvestmentResponseDTO.builder()
                .id(id)
                .type(type)
                .symbol(symbol)
                .quantity(new BigDecimal("100"))
                .purchasePrice(new BigDecimal("19.68"))
                .purchaseDate(LocalDate.of(2025, 7, 31))
                .build();
    }
}
