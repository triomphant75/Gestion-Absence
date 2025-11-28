package com.university.attendance.exception;

/**
 * Exception levée quand un utilisateur n'a pas les droits nécessaires
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
