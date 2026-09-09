package codea.uni.desafio_fullstack.machinery.interfaces.rest;

import codea.uni.desafio_fullstack.machinery.domain.model.commands.DeleteMachineryTypeCommmand;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetAllMachineryTypesQuery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetMachineryTypeByIdQuery;
import codea.uni.desafio_fullstack.machinery.domain.services.MachineryTypeCommandService;
import codea.uni.desafio_fullstack.machinery.domain.services.MachineryTypeQueryService;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.resources.CreateMachineryTypeResource;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.resources.MachineryTypeResource;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.resources.UpdateMachineryTypeResource;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.transform.CreateMachineryTypeCommandFromResourceAssembler;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.transform.MachineryTypeResourceFromEntityAssembler;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.transform.UpdateMachineryTypeCommandFromResourceAssembler;
import codea.uni.desafio_fullstack.shared.interfaces.rest.resources.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/machinery-types")
@Tag(name = "Machinery Types", description = "Machinery Types Management Endpoints")
public class MachineryTypeController {
    private final MachineryTypeCommandService machineryTypeCommandService;
    private final MachineryTypeQueryService machineryTypeQueryService;

    public MachineryTypeController(MachineryTypeCommandService machineryTypeCommandService, MachineryTypeQueryService machineryTypeQueryService) {
        this.machineryTypeCommandService = machineryTypeCommandService;
        this.machineryTypeQueryService = machineryTypeQueryService;
    }

    @Operation(summary = "Register a Machinery Type")
    @PostMapping
    public ResponseEntity<MachineryTypeResource> registerMachineryType(@RequestBody CreateMachineryTypeResource resource) {
        var command = CreateMachineryTypeCommandFromResourceAssembler.toCommandFromResource(resource);
        var machineryType = this.machineryTypeCommandService.handle(command);
        if (machineryType == null || machineryType.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        var responseResource = MachineryTypeResourceFromEntityAssembler.toResourceFromEntity(machineryType.get());
        return new ResponseEntity<>(responseResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Get All Machinery Types")
    @GetMapping
    public ResponseEntity<List<MachineryTypeResource>> getAllMachineryTypes() {
        var query = new GetAllMachineryTypesQuery();
        var types = this.machineryTypeQueryService.handle(query);
        var resources = types.stream()
                .map(MachineryTypeResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Get Machinery Type by ID")
    @GetMapping("/{id}")
    public ResponseEntity<MachineryTypeResource> getMachineryTypeById(@PathVariable Integer id) {
        var query = new GetMachineryTypeByIdQuery(id);
        var machineryType = this.machineryTypeQueryService.handle(query);
        if (machineryType == null || machineryType.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var resource = MachineryTypeResourceFromEntityAssembler.toResourceFromEntity(machineryType.get());
        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Update a Machinery Type")
    @PutMapping("/{id}")
    public ResponseEntity<MachineryTypeResource> updateMachineryType(
            @PathVariable Integer id,
            @RequestBody UpdateMachineryTypeResource resource) {
        var command = UpdateMachineryTypeCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updated = this.machineryTypeCommandService.handle(command);
        if (updated == null || updated.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        var responseResource = MachineryTypeResourceFromEntityAssembler.toResourceFromEntity(updated.get());
        return ResponseEntity.ok(responseResource);
    }

    @Operation(summary = "Delete a Machinery Type")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResource> deleteMachineryType(@PathVariable Integer id) {
        var command = new DeleteMachineryTypeCommmand(id);
        this.machineryTypeCommandService.handle(command);
        return ResponseEntity.ok(new MessageResource("Machinery Type with id " + id + " deleted successfully"));
    }
}
