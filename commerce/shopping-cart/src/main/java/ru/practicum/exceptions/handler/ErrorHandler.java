package ru.practicum.exceptions.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exceptions.NoProductsInShoppingCartException;
import ru.practicum.exceptions.NotAuthorizedUserException;
import ru.practicum.exceptions.NotFoundShoppingCartException;
import ru.practicum.feign_client.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.practicum.feign_client.exception.ProductNotFoundInWarehouseException;
import ru.practicum.feign_client.exception.WarehouseServerUnavailable;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception e) {
        log.info("получили ошибку", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleNotAuthorizedUser(final NotAuthorizedUserException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({NotFoundShoppingCartException.class, ProductNotFoundInWarehouseException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExceptions(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({NoProductsInShoppingCartException.class,
            ProductInShoppingCartLowQuantityInWarehouseException.class,
            MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestExceptions(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleWarehouseServerUnavailable(final WarehouseServerUnavailable e) {
        return new ErrorResponse(e.getMessage());
    }
}