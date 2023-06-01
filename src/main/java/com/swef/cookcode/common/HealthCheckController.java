package com.swef.cookcode.common;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse> checkHealthStatus() {
        ApiResponse response = ApiResponse.builder()
                                        .message("healthy")
                                        .status(HttpStatus.OK.value())
                                        .build();
        return ResponseEntity.ok(response);
    }

}
