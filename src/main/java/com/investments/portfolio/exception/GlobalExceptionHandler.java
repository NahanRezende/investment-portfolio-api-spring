package com.investments.portfolio.exception;

import com.investments.portfolio.model.enums.AssetType;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(
            EntityNotFoundException ex, HttpServletRequest request) {
        return buildProblem(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                ex.getMessage(),
                "INVESTMENT_NOT_FOUND",
                "investment-not-found",
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail problem = buildProblem(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                "Validation failed for request body.",
                "VALIDATION_ERROR",
                "validation-error",
                request
        );

        List<FieldErrorDTO> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new FieldErrorDTO(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()))
                .toList();

        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        if ("type".equals(ex.getName()) && AssetType.class.equals(ex.getRequiredType())) {
            String acceptedValues = Arrays.stream(AssetType.values())
                    .map(Enum::name)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            return buildProblem(
                    HttpStatus.BAD_REQUEST,
                    "Invalid Asset Type",
                    "Invalid value for 'type'. Accepted values: " + acceptedValues + ".",
                    "INVALID_ASSET_TYPE",
                    "invalid-asset-type",
                    request
            );
        }

        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Invalid Request Parameter",
                "Invalid value for parameter '" + ex.getName() + "'.",
                "VALIDATION_ERROR",
                "invalid-request-parameter",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(
            Exception ex, HttpServletRequest request) {
        return buildProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred.",
                "INTERNAL_ERROR",
                "internal-error",
                request
        );
    }

    private ProblemDetail buildProblem(
            HttpStatus status,
            String title,
            String detail,
            String errorCode,
            String typeSuffix,
            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/problems/")
                .path(typeSuffix)
                .toUriString()));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("errorCode", errorCode);
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }

    private record FieldErrorDTO(String field, String error) {
    }
}
