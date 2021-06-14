package presenter.business.layer.exchange.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presenter.business.layer.exchange.cache.ExchangeCurrencyCache;
import presenter.business.layer.exchange.cache.ExchangeEuroRateCurrencyCache;
import presenter.business.layer.exchange.cache.ExchangeQueryRateCurrencyCache;
import presenter.business.layer.exchange.gateway.ExchangeGateway;

@Configuration
public class ExchangeCurrencyCacheConfiguration {

    private final ExchangeGateway exchangeGateway;

    @Autowired
    public ExchangeCurrencyCacheConfiguration( ExchangeGateway exchangeGateway ) {
        this.exchangeGateway = exchangeGateway;
    }

    @Bean
    public ExchangeCurrencyCache createExchangeCurrencyCache() {
        return ExchangeCurrencyCache.of();
    }

    @Bean
    public ExchangeEuroRateCurrencyCache createExchangeRateCurrencyCache() {
        return ExchangeEuroRateCurrencyCache.of( exchangeGateway );
    }

    @Bean
    public ExchangeQueryRateCurrencyCache createExchangeQueryCurrencyCache() {
        return ExchangeQueryRateCurrencyCache.of( exchangeGateway );
    }
}
