package com.blooddonation.exception;



import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;



@ResponseStatus(HttpStatus.UNAUTHORIZED)

public class UnauthorizedException extends RuntimeException {



    /**

     * Thrown when the JWT is missing, invalid, or the

     * authenticated user does not have permission to perform

     * the requested action.

     *

     * Example usages:

     *   throw new UnauthorizedException("Invalid or expired token");

     *   throw new UnauthorizedException(

     *       "You do not have permission to update this profile");

     */

    public UnauthorizedException(String message) {

        super(message);

    }

}