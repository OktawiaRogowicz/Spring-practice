package com.example.accessingdatamysql.entity;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class HttpResponse {

    private Date timeStamp;
    private int httpStatusCode;
    private HttpStatus httpsStatus;
    private String reason;
    private String message;

}
