package presenter.business.layer.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import presenter.business.layer.exchange.exception.HostEndpointException;
import presenter.business.layer.exchange.exception.ValidationException;
import presenter.business.layer.exchange.model.ErrorDetailMessage;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ControllerAdvice
@Slf4j
public class ErrorHandlerControllerAdvice {

    private static final String SERVICE_NOT_AVAILABLE = "Service is currently unavailable, please try again later.";

    @ExceptionHandler ( Exception.class )
    public ResponseEntity handleGenericException( Exception exception ) {
        log.error( "ERROR ", exception );
        return createOutputInternalServerError();
    }

    @ExceptionHandler ( HostEndpointException.class )
    public ResponseEntity handleHttpServerErrorException( HttpServerErrorException exception ) {
        log.error( "ERROR ", exception );
        return createOutputInternalServerError();
    }

    @ExceptionHandler ( ValidationException.class )
    public ResponseEntity handleUnknownHttpStatusCodeException( ValidationException exception ) {
        log.warn( "VALIDATION ERROR ", exception.getResult() );
        return createOutputUnprocessedEntity( exception.getResult() );
    }

    @ExceptionHandler ( HttpClientErrorException.class )
    public ResponseEntity handleHttpClientErrorException( HttpClientErrorException exception ) {
        log.error( "ERROR ", exception );
        return createOutputInternalServerError();
    }

    private ResponseEntity createOutputInternalServerError() {
        return ResponseEntity
                .status( INTERNAL_SERVER_ERROR )
                .body( ErrorDetailMessage.builder()
                        .code( "500" )
                        .description( SERVICE_NOT_AVAILABLE )
                        .build() );
    }

    private ResponseEntity createOutputUnprocessedEntity( String message ) {
        return ResponseEntity
                .status( UNPROCESSABLE_ENTITY )
                .body( ErrorDetailMessage.builder()
                        .code( "-1" )
                        .description( message )
                        .build() );
    }
}
