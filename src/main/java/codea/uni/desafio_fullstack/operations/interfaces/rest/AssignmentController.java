package codea.uni.desafio_fullstack.operations.interfaces.rest;

import codea.uni.desafio_fullstack.operations.domain.model.commands.DeleteAssignmentCommand;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetAssignmentByIdQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetAssignmentsByFilterQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetMachineryMaintenanceProjectionsQuery;
import codea.uni.desafio_fullstack.operations.domain.services.AssignmentCommandService;
import codea.uni.desafio_fullstack.operations.domain.services.AssignmentQueryService;
import codea.uni.desafio_fullstack.operations.interfaces.rest.resources.*;
import codea.uni.desafio_fullstack.operations.interfaces.rest.transform.*;
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
@RequestMapping(value = "api/v1/assignments")
@Tag(name = "Assignments", description = "Assignment Management Endpoints")
public class AssignmentController {

    private final AssignmentCommandService assignmentCommandService;
    private final AssignmentQueryService assignmentQueryService;

    public AssignmentController(AssignmentCommandService assignmentCommandService,
                                AssignmentQueryService assignmentQueryService) {
        this.assignmentCommandService = assignmentCommandService;
        this.assignmentQueryService = assignmentQueryService;
    }

    @Operation(summary = "Get 7-day maintenance projections for active machineries that will be blocked")
    @GetMapping("/projections")
    public ResponseEntity<List<MachineryMaintenanceProjectionResource>> getMachineryMaintenanceProjections(
            @RequestParam(required = false) String machineryCode,
            @RequestParam(required = false) String machineryType) {
        var query = new GetMachineryMaintenanceProjectionsQuery(machineryCode, machineryType);
        var projections = this.assignmentQueryService.handle(query);
        var resources = projections.stream()
                .map(MachineryMaintenanceProjectionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Get all assignments with complete details and optional filters")
    @GetMapping
    public ResponseEntity<List<AssignmentDetailResource>> getAllAssignments(
            @RequestParam(required = false) String operatorName,
            @RequestParam(required = false) String machineryType,
            @RequestParam(required = false) String machineryCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Boolean shiftType) {
        var query = new GetAssignmentsByFilterQuery(operatorName, machineryType, machineryCode, startDate, endDate, shiftType);
        var assignments = this.assignmentQueryService.handle(query);
        var resources = assignments.stream()
                .map(AssignmentDetailResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Register a new Assignment")
    @PostMapping
    public ResponseEntity<AssignmentResource> registerAssignment(@RequestBody CreateAssignmentResource resource) {
        var command = CreateAssignmentCommandFromResourceAssembler.toCommandFromResource(resource);
        var assignment = this.assignmentCommandService.handle(command);
        if (assignment == null || assignment.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        var responseResource = AssignmentResourceFromEntityAssembler.toResourceFromEntity(assignment.get());
        return new ResponseEntity<>(responseResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Get Assignment by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResource> getAssignmentById(@PathVariable UUID id) {
        var query = new GetAssignmentByIdQuery(id);
        var assignment = this.assignmentQueryService.handle(query);
        if (assignment == null || assignment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var resource = AssignmentResourceFromEntityAssembler.toResourceFromEntity(assignment.get());
        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Update Assignment Details (operator, machinery, shift)")
    @PutMapping("/{id}")
    public ResponseEntity<AssignmentResource> updateAssignment(
            @PathVariable UUID id,
            @RequestBody UpdateAssignmentResource resource) {
        var command = UpdateAssignmentCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updated = this.assignmentCommandService.handle(command);
        if (updated == null || updated.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        var responseResource = AssignmentResourceFromEntityAssembler.toResourceFromEntity(updated.get());
        return ResponseEntity.ok(responseResource);
    }

    @Operation(summary = "Start Shift for Assignment")
    @PatchMapping("/{id}/start")
    public ResponseEntity<AssignmentResource> startAssignment(
            @PathVariable UUID id,
            @RequestBody(required = false) StartAssignmentResource resource) {
        var command = StartAssignmentCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updated = this.assignmentCommandService.handle(command);
        if (updated == null || updated.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        var responseResource = AssignmentResourceFromEntityAssembler.toResourceFromEntity(updated.get());
        return ResponseEntity.ok(responseResource);
    }

    @Operation(summary = "End Shift for Assignment and compute actual worked hours")
    @PatchMapping("/{id}/end")
    public ResponseEntity<AssignmentResource> endAssignment(
            @PathVariable UUID id,
            @RequestBody(required = false) EndAssignmentResource resource) {
        var command = EndAssignmentCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updated = this.assignmentCommandService.handle(command);
        if (updated == null || updated.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        var responseResource = AssignmentResourceFromEntityAssembler.toResourceFromEntity(updated.get());
        return ResponseEntity.ok(responseResource);
    }

    @Operation(summary = "Delete an Assignment by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResource> deleteAssignment(@PathVariable UUID id) {
        var command = new DeleteAssignmentCommand(id);
        this.assignmentCommandService.handle(command);
        return ResponseEntity.ok(new MessageResource("Assignment with ID " + id + " deleted successfully"));
    }
}
