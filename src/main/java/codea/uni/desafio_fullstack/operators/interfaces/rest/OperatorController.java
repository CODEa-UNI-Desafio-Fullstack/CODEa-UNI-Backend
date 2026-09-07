package codea.uni.desafio_fullstack.operators.interfaces.rest;

import codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorByIdQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorsByFilterQuery;
import codea.uni.desafio_fullstack.operators.domain.services.OperatorCommandService;
import codea.uni.desafio_fullstack.operators.domain.services.OperatorQueryService;
import codea.uni.desafio_fullstack.operators.interfaces.rest.resources.CreateOperatorResource;
import codea.uni.desafio_fullstack.operators.interfaces.rest.resources.OperatorResource;
import codea.uni.desafio_fullstack.operators.interfaces.rest.resources.UpdateOperatorNameResource;
import codea.uni.desafio_fullstack.operators.interfaces.rest.transform.CreateOperatorCommandFromResourceAssembler;
import codea.uni.desafio_fullstack.operators.interfaces.rest.transform.OperatorResourceFromEntityAssembler;
import codea.uni.desafio_fullstack.operators.interfaces.rest.transform.UpdateOperatorNameCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/operators")
@Tag(name = "Operators", description = "Operators Management Endpoints")
public class OperatorController {

    private final OperatorCommandService operatorCommandService;
    private final OperatorQueryService operatorQueryService;

    public OperatorController(OperatorCommandService operatorCommandService, OperatorQueryService operatorQueryService) {
        this.operatorCommandService = operatorCommandService;
        this.operatorQueryService = operatorQueryService;
    }

    @Operation(summary = "Register a new Operator")
    @PostMapping
    public ResponseEntity<OperatorResource> registerOperator(@RequestBody CreateOperatorResource resource) {
        var command = CreateOperatorCommandFromResourceAssembler.toCommandFromResource(resource);
        var operator = this.operatorCommandService.handle(command);
        if (operator == null || operator.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        var responseResource = OperatorResourceFromEntityAssembler.toResourceFromEntity(operator.get());
        return new ResponseEntity<>(responseResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all Operators with optional filters")
    @GetMapping
    public ResponseEntity<List<OperatorResource>> getAllOperators(
            @RequestParam(required = false) Integer machineryTypeId,
            @RequestParam(required = false) String name) {
        var query = new GetOperatorsByFilterQuery(machineryTypeId, name);
        var operators = this.operatorQueryService.handle(query);
        var resources = operators.stream()
                .map(OperatorResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Get an Operator by ID")
    @GetMapping("/{id}")
    public ResponseEntity<OperatorResource> getOperatorById(@PathVariable UUID id) {
        var query = new GetOperatorByIdQuery(id);
        var operator = this.operatorQueryService.handle(query);
        if (operator == null || operator.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var resource = OperatorResourceFromEntityAssembler.toResourceFromEntity(operator.get());
        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Update an Operator's Name")
    @PutMapping("/{id}")
    public ResponseEntity<OperatorResource> updateOperatorName(
            @PathVariable UUID id,
            @RequestBody UpdateOperatorNameResource resource) {
        var command = UpdateOperatorNameCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updated = this.operatorCommandService.handle(command);
        if (updated == null || updated.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        var responseResource = OperatorResourceFromEntityAssembler.toResourceFromEntity(updated.get());
        return ResponseEntity.ok(responseResource);
    }
}
