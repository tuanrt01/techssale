package com.techzen.techsale.controller.advice;

import com.techzen.techsale.exception.BadRequestException;
import com.techzen.techsale.exception.ConflictException;
import com.techzen.techsale.exception.ForbiddenException;
import com.techzen.techsale.exception.InternalServerErrorException;
import com.techzen.techsale.exception.NotFoundException;
import com.techzen.techsale.exception.UnauthorizedException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.Conflict;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

@ControllerAdvice
public class ExceptionHandlerController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class, BadRequest.class, MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<Object> handleBadRequestException(HttpServletRequest request, Exception ex) {
        Map<String, Object> body = buildBody(ex, request, 400);
        return ResponseEntity.badRequest().body(body);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({ForbiddenException.class, Forbidden.class})
    public ResponseEntity<Object> handleForbiddenException(HttpServletRequest request, Exception ex) {
        Map<String, Object> body = buildBody(ex, request, 403);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({InternalServerErrorException.class, InternalServerError.class})
    public ResponseEntity<Object> handleInternalServerErrorException(HttpServletRequest request, Exception ex) {
        Map<String, Object> body = buildBody(ex, request, 500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class, ChangeSetPersister.NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(HttpServletRequest request, Exception ex) {
        Map<String, Object> body = buildBody(ex, request, 404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({UnauthorizedException.class, Unauthorized.class})
    public ResponseEntity<Object> handleUnauthorizedException(HttpServletRequest request, Exception ex) {
        Map<String, Object> body = buildBody(ex, request, 401);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({Conflict.class, ConflictException.class})
    public ResponseEntity<Object> handleConflicException(HttpServletRequest request, Exception ex) {
        Map<String, Object> body = buildBody(ex, request, 409);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    private <T extends Exception> Map<String, Object> buildBody(T ex, HttpServletRequest request, int status) {
        Map<String, Object> body = new LinkedHashMap<>();
        List<String> msg;
        if (BindException.class.isAssignableFrom(ex.getClass())) {
            msg = ((BindingResult) ex).getFieldErrors()
                .stream()
                .map(x -> x.getField() + ": " + x.getDefaultMessage())
                .collect(Collectors.toList());
        } else {
            msg = Collections.singletonList(ex.getMessage());
        }

        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status);
        body.put("error", ex.getClass().getSimpleName());
        body.put("message", msg);
        body.put("path", request.getRequestURI());

        return body;
    }
}


