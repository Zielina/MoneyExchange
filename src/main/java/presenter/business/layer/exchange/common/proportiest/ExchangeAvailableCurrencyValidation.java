package presenter.business.layer.exchange.common.proportiest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeAvailableCurrencyValidation {
    private Map<String,String>  availableCurrency = new HashMap<>();
}
