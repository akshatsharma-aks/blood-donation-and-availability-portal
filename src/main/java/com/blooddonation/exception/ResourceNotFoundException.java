package com.blooddonation.exception;



import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;



@ResponseStatus(HttpStatus.NOT_FOUND)

public class ResourceNotFoundException extends RuntimeException {



    private final String resourceName;

    private final String fieldName;

    private final Object fieldValue;



    /**

     * Full constructor — produces a descriptive message.

     * Example: "DonorProfile not found with id : 42"

     */

    public ResourceNotFoundException(

            String resourceName, String fieldName, Object fieldValue) {

        super(String.format("%s not found with %s : '%s'",

                resourceName, fieldName, fieldValue));

        this.resourceName = resourceName;

        this.fieldName    = fieldName;

        this.fieldValue   = fieldValue;

    }



    /**

     * Simple constructor — use when message is already descriptive.

     * Example: new ResourceNotFoundException("Donor profile not found")

     */

    public ResourceNotFoundException(String message) {

        super(message);

        this.resourceName = null;

        this.fieldName    = null;

        this.fieldValue   = null;

    }



    public String getResourceName() { return resourceName; }

    public String getFieldName()    { return fieldName; }

    public Object getFieldValue()   { return fieldValue; }

}

