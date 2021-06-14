package presenter.business.layer.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ErrorDetailMessage {

    private final String code;
    private final String description;

}
