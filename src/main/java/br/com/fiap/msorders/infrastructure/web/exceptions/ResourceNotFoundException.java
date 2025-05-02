package br.com.fiap.msorders.infrastructure.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception {

    private static final long serialVersionUID = 1321234567L;

    public ResourceNotFoundException(String message){
        super(message);
    }
}
