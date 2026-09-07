package codea.uni.desafio_fullstack.maintenance.interfaces.rest;

import codea.uni.desafio_fullstack.maintenance.domain.model.commands.DeleteMaintenanceCommand;
import codea.uni.desafio_fullstack.maintenance.domain.model.queries.*;
import codea.uni.desafio_fullstack.maintenance.domain.services.MaintenanceCommandService;
import codea.uni.desafio_fullstack.maintenance.domain.services.MaintenanceQueryService;
import codea.uni.desafio_fullstack.maintenance.interfaces.rest.resources.CreateMaintenanceResource;
import codea.uni.desafio_fullstack.maintenance.interfaces.rest.resources.MaintenanceResource;
import codea.uni.desafio_fullstack.maintenance.interfaces.rest.resources.UpdateMaintenanceResource;
import codea.uni.desafio_fullstack.maintenance.interfaces.rest.transform.CreateMaintenanceCommandFromResourceAssembler;
import codea.uni.desafio_fullstack.maintenance.interfaces.rest.transform.MaintenanceResourceFromEntityAssembler;
import codea.uni.desafio_fullstack.maintenance.interfaces.rest.transform.UpdateMaintenanceCommandFromResourceAssembler;
import codea.uni.desafio_fullstack.shared.interfaces.rest.resources.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/maintenances")
@Tag(name = "Maintenances", description = "Maintenance Management Endpoints")
public class MaintenanceController {

    private final MaintenanceCommandService maintenanceCommandService;
    private final MaintenanceQueryService maintenanceQueryService;

    public MaintenanceController(MaintenanceCommandService maintenanceCommandService,
                                 MaintenanceQueryService maintenanceQueryService) {
        this.maintenanceCommandService = maintenanceCommandService;
        this.maintenanceQueryService = maintenanceQueryService;
    }

    @Operation(summary = "Register a new Maintenance")
    @PostMapping
    public ResponseEntity<MaintenanceResource> registerMaintenance(@RequestBody CreateMaintenanceResource resource) {
        var command = CreateMaintenanceCommandFromResourceAssembler.toCommandFromResource(resource);
        var maintenance = this.maintenanceCommandService.handle(command);
        if (maintenance == null || maintenance.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        var responseResource = MaintenanceResourceFromEntityAssembler.toResourceFromEntity(maintenance.get());
        return new ResponseEntity<>(responseResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all Maintenances with optional filters")
    @GetMapping
    public ResponseEntity<List<MaintenanceResource>> getAllMaintenances(
            @RequestParam(required = false) UUID operatorId,
            @RequestParam(required = false) String machineryCode,
            @RequestParam(required = false) Integer machineryTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        var query = new GetMaintenancesByFilterQuery(operatorId, machineryCode, machineryTypeId, startDate, endDate);
        var maintenances = this.maintenanceQueryService.handle(query);
        var resources = maintenances.stream()
                .map(MaintenanceResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Get a Maintenance by ID")
    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceResource> getMaintenanceById(@PathVariable UUID id) {
        var query = new GetMaintenanceByIdQuery(id);
        var maintenance = this.maintenanceQueryService.handle(query);
        if (maintenance == null || maintenance.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var resource = MaintenanceResourceFromEntityAssembler.toResourceFromEntity(maintenance.get());
        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Update Maintenance Details")
    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceResource> updateMaintenance(
            @PathVariable UUID id,
            @RequestBody UpdateMaintenanceResource resource) {
        var command = UpdateMaintenanceCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updated = this.maintenanceCommandService.handle(command);
        if (updated == null || updated.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        var responseResource = MaintenanceResourceFromEntityAssembler.toResourceFromEntity(updated.get());
        return ResponseEntity.ok(responseResource);
    }

    @Operation(summary = "Delete a Maintenance by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResource> deleteMaintenance(@PathVariable UUID id) {
        var command = new DeleteMaintenanceCommand(id);
        this.maintenanceCommandService.handle(command);
        return ResponseEntity.ok(new MessageResource("Maintenance with ID " + id + " deleted successfully"));
    }
}
