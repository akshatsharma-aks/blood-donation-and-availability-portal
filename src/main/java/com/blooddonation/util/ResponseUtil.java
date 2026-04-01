package com.blooddonation.util;



import com.blooddonation.dto.response.ApiResponse;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;



/**

 * Utility class for building ResponseEntity<ApiResponse<T>> objects.

 *

 * Usage in a controller:

 *

 *   return ResponseUtil.ok("Donors found", donorList);

 *   return ResponseUtil.created("Profile created", profile);

 *   return ResponseUtil.notFound("Donor profile not found");

 *   return ResponseUtil.badRequest("Email already in use");

 */

public final class ResponseUtil {



    private ResponseUtil() { /* utility class — no instantiation */ }



    // ── 2xx Success ─────────────────────────────────────────────



    /** 200 OK with data payload. */

    public static <T> ResponseEntity<ApiResponse<T>> ok(

            String message, T data) {

        return ResponseEntity.ok(ApiResponse.ok(message, data));

    }



    /** 200 OK with no data (e.g. delete confirmation). */

    public static <T> ResponseEntity<ApiResponse<T>> ok(

            String message) {

        return ResponseEntity.ok(ApiResponse.ok(message, null));

    }



    /** 201 Created — use after saving a new entity. */

    public static <T> ResponseEntity<ApiResponse<T>> created(

            String message, T data) {

        return ResponseEntity

                .status(HttpStatus.CREATED)

                .body(ApiResponse.ok(message, data));

    }



    // ── 4xx Client Errors ────────────────────────────────────────



    /** 400 Bad Request — validation or business rule failure. */

    public static <T> ResponseEntity<ApiResponse<T>> badRequest(

            String message) {

        return ResponseEntity

                .status(HttpStatus.BAD_REQUEST)

                .body(ApiResponse.error(message));

    }



    /** 401 Unauthorized — missing or invalid credentials. */

    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(

            String message) {

        return ResponseEntity

                .status(HttpStatus.UNAUTHORIZED)

                .body(ApiResponse.error(message));

    }



    /** 403 Forbidden — authenticated but insufficient permissions. */

    public static <T> ResponseEntity<ApiResponse<T>> forbidden(

            String message) {

        return ResponseEntity

                .status(HttpStatus.FORBIDDEN)

                .body(ApiResponse.error(message));

    }



    /** 404 Not Found — entity does not exist. */

    public static <T> ResponseEntity<ApiResponse<T>> notFound(

            String message) {

        return ResponseEntity

                .status(HttpStatus.NOT_FOUND)

                .body(ApiResponse.error(message));

    }



    // ── 5xx Server Errors ────────────────────────────────────────



    /** 500 Internal Server Error — unexpected failure. */

    public static <T> ResponseEntity<ApiResponse<T>> serverError(

            String message) {

        return ResponseEntity

                .status(HttpStatus.INTERNAL_SERVER_ERROR)

                .body(ApiResponse.error(message));

    }

}

