package com.blooddonation.exception;



import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;



@ResponseStatus(HttpStatus.BAD_REQUEST)

public class BusinessException extends RuntimeException {



    private final String errorCode;



    /**

     * Constructor with message only.

     * Use for straightforward rule violations.

     */

    public BusinessException(String message) {

        super(message);

        this.errorCode = "BUSINESS_ERROR";

    }



    /**

     * Constructor with message + machine-readable error code.

     * Allows the frontend to show localised error text.

     *

     * Common codes:

     *   EMAIL_ALREADY_EXISTS

     *   DONOR_NOT_ELIGIBLE   (donated < 90 days ago)

     *   HOSPITAL_NOT_VERIFIED

     *   INSUFFICIENT_BLOOD_UNITS

     *   VERIFICATION_EXPIRED

     */

    public BusinessException(String message, String errorCode) {

        super(message);

        this.errorCode = errorCode;

    }



    public String getErrorCode() { return errorCode; }

}