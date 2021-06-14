package presenter.business.layer.exchange.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presenter.business.layer.exchange.common.proporties.ExchangeAvailableCurrencyValidation;

@Configuration
public class ExchangeAvailableCurrencyValidationConfiguration {

    @Bean
    @ConfigurationProperties ( "exchange.currency.validation" )
    public ExchangeAvailableCurrencyValidation getAvailableCurrencySymbol() {
        return new ExchangeAvailableCurrencyValidation();
    }
}
