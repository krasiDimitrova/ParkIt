package uni.fmi.parkit.server.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.warn("ConstraintViolationException: ", ex);

        List<String> errors = extractErrorMessages(ex);
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), String.join(", ", errors), errors);

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    private List<String> extractErrorMessages(ConstraintViolationException ex) {
        List<String> errorMessages = new ArrayList<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            Iterator<Path.Node> propertyPathIterator = violation.getPropertyPath().iterator();
            StringJoiner propertyPathMessage = new StringJoiner(" ");

            while (propertyPathIterator.hasNext()) {
                Path.Node node = propertyPathIterator.next();
                ElementKind elementKind = node.getKind();

                // Cases when only the parameter is validated - it is primitive type
                // or there are no constraint violations on the object which is parameter
                if (elementKind == ElementKind.PARAMETER && !propertyPathIterator.hasNext()) {
                    propertyPathMessage.add(node.getName());
                }

                if (elementKind == ElementKind.PROPERTY) {
                    propertyPathMessage.add(node.getName());
                }

                if (elementKind == ElementKind.CONTAINER_ELEMENT) {
                    propertyPathMessage.add("elements");
                }
            }

            String errorMessage = String.format("%s %s", propertyPathMessage, violation.getMessage());

            // We want to add the error to the result only if a property is found and
            // the error message does not exist yet
            if (propertyPathMessage.length() > 0 && !errorMessages.contains(errorMessage)) {
                errorMessages.add(errorMessage);
            }
        }

        return errorMessages.stream().sorted().collect(Collectors.toList());
    }

    @ExceptionHandler({EntityNotFoundException.class, EmptyResultDataAccessException.class})
    public ResponseEntity<ApiError> handleDbNotFoundError(Exception ex) {
        logger.warn(ex.getClass().getSimpleName(), ex);

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("IllegalArgumentException: ", ex);

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ApiError> handleDbEntityExistsError(EntityExistsException ex) {
        logger.warn(ex.getClass().getSimpleName(), ex);

        ApiError apiError = new ApiError(HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }
}
