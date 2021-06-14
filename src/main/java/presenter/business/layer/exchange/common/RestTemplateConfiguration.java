package presenter.business.layer.exchange.common;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import presenter.business.layer.exchange.cache.ExchangeCurrencyCache;
import presenter.business.layer.exchange.common.proportiest.ExchangeApiEndpoint;
import presenter.business.layer.exchange.common.proportiest.ExchangeAvailableCurrencyValidation;
import presenter.business.layer.exchange.common.proportiest.ExternalServiceCredentials;
import presenter.business.layer.exchange.gateway.ExchangeGateway;

import java.util.Map;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    @Qualifier ( "OKRestTemplate" )
    public RestTemplate createOKCustomRestTemplate( ClientHttpRequestFactory clientHttpRequestFactory, RestTemplateBuilder restTemplateBuilder ) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory( clientHttpRequestFactory );
        return restTemplate;
    }

    @Bean
    @ConfigurationProperties ( "exchange.credentials" )
    public ExternalServiceCredentials getExternalServiceCredentials() {
        return new ExternalServiceCredentials();
    }

    @Bean
    @ConfigurationProperties ( "exchange.endpoint" )
    public ExchangeApiEndpoint getEndpoint() {
        return new ExchangeApiEndpoint();
    }

}
