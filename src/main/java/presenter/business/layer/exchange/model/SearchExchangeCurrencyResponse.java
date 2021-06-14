package presenter.business.layer.exchange.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SearchExchangeCurrencyResponse {
    @JsonProperty ( "source" )
    private String source;
    @JsonProperty ( "target" )
    private String target;
    @JsonProperty ( "resultExchange" )
    private BigDecimal resultExchange;

    public SearchExchangeCurrencyResponse( String source, String target, BigDecimal resultExchange ) {
        this.source = source;
        this.target = target;
        this.resultExchange = resultExchange;
    }

    public SearchExchangeCurrencyResponse() {
    }
}
