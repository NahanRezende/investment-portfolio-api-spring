package com.investments.portfolio.config;

import com.investments.portfolio.model.dto.InvestmentRequestDTO;
import com.investments.portfolio.model.entity.Investment;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);
        
        // Configurações específicas para mapeamento
        configureInvestmentMappings(modelMapper);
        
        return modelMapper;
    }
    
    private void configureInvestmentMappings(ModelMapper modelMapper) {
        // Mapeamento de InvestmentRequestDTO para Investment
        modelMapper.typeMap(InvestmentRequestDTO.class, Investment.class)
                .addMappings(mapper -> {
                    mapper.skip(Investment::setId);
                    mapper.skip(Investment::setCurrentPrice);
                    mapper.skip(Investment::setCreatedAt);
                    mapper.skip(Investment::setUpdatedAt);
                });
    }
}
