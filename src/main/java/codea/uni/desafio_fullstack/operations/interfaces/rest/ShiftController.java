package codea.uni.desafio_fullstack.operations.interfaces.rest;

import codea.uni.desafio_fullstack.operations.domain.model.commands.DeleteShiftCommand;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetAllShiftsQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetShiftByIdQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetShiftsByFilterQuery;
import codea.uni.desafio_fullstack.operations.domain.services.ShiftCommandService;
import codea.uni.desafio_fullstack.operations.domain.services.ShiftQueryService;
import codea.uni.desafio_fullstack.operations.interfaces.rest.resources.CreateShiftResource;
import codea.uni.desafio_fullstack.operations.interfaces.rest.resources.ShiftResource;
import codea.uni.desafio_fullstack.operations.interfaces.rest.resources.UpdateShiftResource;
import codea.uni.desafio_fullstack.operations.interfaces.rest.transform.CreateShiftCommandFromResourceAssembler;
import codea.uni.desafio_fullstack.operations.interfaces.rest.transform.ShiftResourceFromEntityAssembler;
import codea.uni.desafio_fullstack.operations.interfaces.rest.transform.UpdateShiftCommandFromResourceAssembler;
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
@RequestMapping(value = "api/v1/shifts")
@Tag(name = "Shifts", description = "Shift Management Endpoints")
public class ShiftController {

    private final ShiftCommandService shiftCommandService;
    private final ShiftQueryService shiftQueryService;

    public ShiftController(ShiftCommandService shiftCommandService, ShiftQueryService shiftQueryService) {
        this.shiftCommandService = shiftCommandService;
        this.shiftQueryService = shiftQueryService;
    }

    @Operation(summary = "Register a new Shift")
    @PostMapping
    public ResponseEntity<ShiftResource> registerShift(@RequestBody CreateShiftResource resource) {
        var command = CreateShiftCommandFromResourceAssembler.toCommandFromResource(resource);
        var shift = this.shiftCommandService.handle(command);
        if (shift == null || shift.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        var responseResource = ShiftResourceFromEntityAssembler.toResourceFromEntity(shift.get());
        return new ResponseEntity<>(responseResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all Shifts")
    @GetMapping
    public ResponseEntity<List<ShiftResource>> getAllShifts() {
        var query = new GetAllShiftsQuery();
        var shifts = this.shiftQueryService.handle(query);
        var resources = shifts.stream()
                .map(ShiftResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Get Shift by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ShiftResource> getShiftById(@PathVariable UUID id) {
        var query = new GetShiftByIdQuery(id);
        var shift = this.shiftQueryService.handle(query);
        if (shift == null || shift.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var resource = ShiftResourceFromEntityAssembler.toResourceFromEntity(shift.get());
        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Get Shifts filtered by date and/or shiftType (at least one parameter is required)")
    @GetMapping("/filter")
    public ResponseEntity<List<ShiftResource>> getShiftsByFilter(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Boolean shiftType) {
        if (date == null && shiftType == null) {
            return ResponseEntity.badRequest().build();
        }
        var query = new GetShiftsByFilterQuery(date, shiftType);
        var shifts = this.shiftQueryService.handle(query);
        var resources = shifts.stream()
                .map(ShiftResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Update Shift Details")
    @PutMapping("/{id}")
    public ResponseEntity<ShiftResource> updateShift(
            @PathVariable UUID id,
            @RequestBody UpdateShiftResource resource) {
        var command = UpdateShiftCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updated = this.shiftCommandService.handle(command);
        if (updated == null || updated.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        var responseResource = ShiftResourceFromEntityAssembler.toResourceFromEntity(updated.get());
        return ResponseEntity.ok(responseResource);
    }

    @Operation(summary = "Delete a Shift by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResource> deleteShift(@PathVariable UUID id) {
        var command = new DeleteShiftCommand(id);
        this.shiftCommandService.handle(command);
        return ResponseEntity.ok(new MessageResource("Shift with ID " + id + " deleted successfully"));
    }
}
