package com.vpe.finalstore.seeder.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeedDatabaseRequest {
    @Schema(description = "Whether to clean the database before seeding", defaultValue = "true", example = "true")
    private boolean cleanStart = true;

    @Schema(description = "Number of users to create", defaultValue = "100", example = "100")
    private int userCount = 100;

    @Schema(description = "Number of products to create", defaultValue = "50", example = "50")
    private int productCount = 50;

    @Schema(description = "Number of orders to create", defaultValue = "200", example = "200")
    private int orderCount = 200;
}

