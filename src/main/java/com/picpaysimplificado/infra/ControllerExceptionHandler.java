package com.picpaysimplificado.infra;

import com.picpaysimplificado.dtos.ExceptionDTO;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        if (exception.getCause() instanceof ConstraintViolationException) {
            return threatDuplicateEntry(exception);
        } else {
            return threatNotPermissionEntry(exception);
        }
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity handleEntityNotFound(EntityNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity threatDuplicateEntry(DataIntegrityViolationException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO("Usuário já cadastrado", HttpStatus.BAD_REQUEST);
        return ResponseEntity.badRequest().body(exceptionDTO);
    }

    private ResponseEntity threatNotPermissionEntry(DataIntegrityViolationException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO("Usuário do tipo Logista não está autorizado a realizar transação", HttpStatus.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleGeneralException(Exception exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.internalServerError().body(exceptionDTO);
    }

}
