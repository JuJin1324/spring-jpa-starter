package practice.jpastarter.exceptions;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 2021/07/27
 * Copyright (C) 2021, Centum Factorial all rights reserved.
 */
public class NotEnoughStockException extends RuntimeException {
    public NotEnoughStockException() {
    }

    public NotEnoughStockException(String message) {
        super(message);
    }

    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }
}
