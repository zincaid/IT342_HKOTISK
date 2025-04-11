package com.CSIT321.Hkotisk.Response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServerResponse {
    private String status;
    private String message;
    private String authToken;
    private String role;
}
