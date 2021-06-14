package presenter.business.layer.exchange.common.proporties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeApiEndpoint {
    private String host;
    private String patch;
}
