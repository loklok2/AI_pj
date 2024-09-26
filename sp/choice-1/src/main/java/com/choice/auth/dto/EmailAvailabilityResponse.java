package com.choice.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailAvailabilityResponse {
    private boolean available;
}