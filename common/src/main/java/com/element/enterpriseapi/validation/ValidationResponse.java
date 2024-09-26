package com.element.enterpriseapi.validation;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ValidationResponse {
    private String batchId;
    private Integer failedCount;
    private Integer receivedCount;
    private Integer skippedCount;
    private Integer status;
    private Integer successCount;
    private List<ValidationRecord> records;
}

