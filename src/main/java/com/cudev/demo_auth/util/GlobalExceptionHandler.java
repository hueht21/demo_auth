package com.cudev.demo_auth.util;

import com.cudev.demo_auth.model.ReponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Xử lý lỗi thiếu request param (@RequestParam)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ReponseObject> handleMissingParam(MissingServletRequestParameterException ex) {
        String paramName = ex.getParameterName();
        String message = "Thiếu tham số bắt buộc: '" + paramName + "'";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ReponseObject(false, message, null));
    }

    // Xử lý lỗi RuntimeException chung
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ReponseObject> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ReponseObject(false, "Lỗi hệ thống: " + ex.getMessage(), null));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ReponseObject> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String message = "Phương thức " + ex.getMethod() + " không được hỗ trợ cho endpoint này. Hãy dùng: " + ex.getSupportedHttpMethods();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ReponseObject(false, message, null));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ReponseObject> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ReponseObject(false, "Dung lượng file vượt quá giới hạn cho phép (tối đa 5MB)", null));
    }
}
