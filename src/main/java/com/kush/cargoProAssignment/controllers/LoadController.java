package com.kush.cargoProAssignment.controllers;

import com.kush.cargoProAssignment.dto.LoadDTO;
import com.kush.cargoProAssignment.model.enums.LoadStatus;
import com.kush.cargoProAssignment.service.LoadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/load")
@Tag(name = "Load Management", description = "APIs for managing loads")
@RequiredArgsConstructor
public class LoadController {

    private final LoadService loadService;

    @PostMapping
    @Operation(summary = "Create a new load")
    public ResponseEntity<LoadDTO> createLoad(@Valid @RequestBody LoadDTO loadDTO) {
        LoadDTO createdLoad = loadService.createLoad(loadDTO);
        return new ResponseEntity<>(createdLoad, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get loads with filters and pagination")
    public ResponseEntity<Page<LoadDTO>> getLoads(
            @RequestParam(required = false) String shipperId,
            @RequestParam(required = false) String truckType,
            @RequestParam(required = false) LoadStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<LoadDTO> loads = loadService.getLoads(shipperId, truckType, status, page, size);
        return ResponseEntity.ok(loads);
    }

    @GetMapping("/{loadId}")
    @Operation(summary = "Get load by ID")
    public ResponseEntity<LoadDTO> getLoadById(@PathVariable UUID loadId) {
        LoadDTO load = loadService.getLoadById(loadId);
        return ResponseEntity.ok(load);
    }

    @PutMapping("/{loadId}")
    @Operation(summary = "Update load details")
    public ResponseEntity<LoadDTO> updateLoad(@PathVariable UUID loadId, @Valid @RequestBody LoadDTO loadDTO) {
        LoadDTO updatedLoad = loadService.updateLoad(loadId, loadDTO);
        return ResponseEntity.ok(updatedLoad);
    }

    @DeleteMapping("/{loadId}")
    @Operation(summary = "Delete a load")
    public ResponseEntity<Void> deleteLoad(@PathVariable UUID loadId) {
        loadService.deleteLoad(loadId);
        return ResponseEntity.noContent().build();
    }
}