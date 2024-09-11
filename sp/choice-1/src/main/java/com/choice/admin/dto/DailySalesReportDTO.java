package com.choice.admin.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailySalesReportDTO {
    private LocalDate date;
    private Long totalSales;
    private Long orderCount;
}
