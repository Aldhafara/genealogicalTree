package com.aldhafara.genealogicalTree.exceptions;

import com.aldhafara.genealogicalTree.configuration.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PersonNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlePersonNotFound(PersonNotFoundException ex,
                                       HttpServletRequest request,
                                       Model model) {
        log.warn("Person not found. uri={}, message={}", request.getRequestURI(), ex.getMessage());
        addRequestId(model, request);
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex,
                                         HttpServletRequest request,
                                         Model model) {
        log.error("Unhandled exception. uri={}", request.getRequestURI(), ex);
        addRequestId(model, request);
        return "error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFound(NoResourceFoundException ex,
                                        HttpServletRequest request,
                                        Model model) {
        if (isTechnical404(request)) {
            log.debug("Ignored technical 404. uri={}", request.getRequestURI());
            addRequestId(model, request);
            return "error";
        }

        log.warn("Resource not found. uri={}, resource={}",
                request.getRequestURI(), ex.getResourcePath());
        addRequestId(model, request);
        return "error";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                   HttpServletRequest request,
                                                   Model model) {
        log.warn("Invalid request parameter. uri={}, parameter={}, value={}",
                request.getRequestURI(), ex.getName(), ex.getValue());
        addRequestId(model, request);
        return "error";
    }

    private void addRequestId(Model model, HttpServletRequest request) {
        model.addAttribute(
                "requestId",
                request.getAttribute(CorrelationIdFilter.CORRELATION_ID_REQUEST_ATTRIBUTE)
        );
    }

    private boolean isTechnical404(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && uri.startsWith("/.well-known/");
    }
}
