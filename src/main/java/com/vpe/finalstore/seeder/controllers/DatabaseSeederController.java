package com.vpe.finalstore.seeder.controllers;

import com.vpe.finalstore.seeder.dtos.SeedDatabaseRequest;
import com.vpe.finalstore.seeder.dtos.SeedDatabaseResponse;
import com.vpe.finalstore.seeder.services.DatabaseSeederService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/seeder")
@RequiredArgsConstructor
@Tag(name = "Database Seeder", description = "Endpoints for seeding the database with test data")
public class DatabaseSeederController {

    private final DatabaseSeederService seederService;

    @PostMapping("/db-seed")
    @Operation(
        summary = "Seed the database",
        description = "Seeds the database with test data. Can optionally clean existing data first."
    )
    public ResponseEntity<SeedDatabaseResponse> seedDatabase(
        @RequestBody(required = false) SeedDatabaseRequest request
    ) {
        log.info("Received database seed request: {}", request);

        // Use defaults if request is null
        if (request == null) {
            request = new SeedDatabaseRequest();
        }

        long startTime = System.currentTimeMillis();

        try {
            seederService.runAllSeeds(
                request.isCleanStart(),
                request.getUserCount(),
                request.getProductCount(),
                request.getOrderCount()
            );

            long duration = System.currentTimeMillis() - startTime;

            var response = SeedDatabaseResponse.builder()
                .success(true)
                .message("Database seeded successfully")
                .durationMs(duration)
                .usersCreated(request.getUserCount())
                .productsCreated(request.getProductCount())
                .ordersCreated(request.getOrderCount())
                .cleanStart(request.isCleanStart())
                .build();

            log.info("Database seeding completed in {}ms", duration);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error seeding database", e);

            long duration = System.currentTimeMillis() - startTime;

            SeedDatabaseResponse response = SeedDatabaseResponse.builder()
                .success(false)
                .message("Error seeding database: " + e.getMessage())
                .durationMs(duration)
                .build();

            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/clean")
    @Operation(
        summary = "Clean the database",
        description = "Removes all data from the database"
    )
    public ResponseEntity<SeedDatabaseResponse> cleanDatabase() {
        log.info("Received database clean request");

        long startTime = System.currentTimeMillis();

        try {
            seederService.cleanDatabase();

            long duration = System.currentTimeMillis() - startTime;

            SeedDatabaseResponse response = SeedDatabaseResponse.builder()
                .success(true)
                .message("Database cleaned successfully")
                .durationMs(duration)
                .cleanStart(true)
                .build();

            log.info("Database cleaning completed in {}ms", duration);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error cleaning database", e);

            long duration = System.currentTimeMillis() - startTime;

            SeedDatabaseResponse response = SeedDatabaseResponse.builder()
                .success(false)
                .message("Error cleaning database: " + e.getMessage())
                .durationMs(duration)
                .build();

            return ResponseEntity.internalServerError().body(response);
        }
    }
}

