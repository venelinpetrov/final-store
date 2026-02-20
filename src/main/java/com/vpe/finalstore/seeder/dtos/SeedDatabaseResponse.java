package com.vpe.finalstore.seeder.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeedDatabaseResponse {
    private boolean success;
    private String message;
    private long durationMs;
    private Integer usersCreated;
    private Integer productsCreated;
    private Integer ordersCreated;
    private boolean cleanStart;
}

