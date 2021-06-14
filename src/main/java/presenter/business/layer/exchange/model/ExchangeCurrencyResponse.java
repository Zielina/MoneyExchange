package presenter.business.layer.exchange.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Builder
@Data
public class ExchangeCurrencyResponse {
    private Boolean success;
    private Integer timestamp;
    private String base;
    private String date;
    private Map<String, BigDecimal> rates = new HashMap<>();

    public ExchangeCurrencyResponse() {
    }

    public ExchangeCurrencyResponse( Boolean success, Integer timestamp, String base, String date,
            Map<String, BigDecimal> rates ) {
        this.success = success;
        this.timestamp = timestamp;
        this.base = base;
        this.date = date;
        this.rates = rates;
    }
}
