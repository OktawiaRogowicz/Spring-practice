package com.example.accessingdatamysql.entity;

import lombok.*;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class HttpResponse {

    private int httpStatusCode;
    private HttpStatus httpsStatus;
    private String reason;
    private String message;
}
