package codea.uni.desafio_fullstack.machinery.interfaces.rest;

import codea.uni.desafio_fullstack.machinery.domain.model.aggregates.Machinery;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.DeleteMachineryCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetAllMachineryByMachineryTypeIdQuery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetAllMachineryByStateQuery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetAllMachineryQuery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetMachineryByCodeQuery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetMachineryByFilterQuery;
import codea.uni.desafio_fullstack.machinery.domain.services.MachineryCommandService;
import codea.uni.desafio_fullstack.machinery.domain.services.MachineryQueryService;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.resources.CreateMachineryResource;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.resources.MachineryResource;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.resources.UpdateMachineryMachineryTypeResource;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.transform.CreateMachineryCommandFromResourceAssembler;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.transform.MachineryResourceFromEntityAssembler;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.transform.UpdateMachineryMachineryTypeCommandFromResourceAssembler;
import codea.uni.desafio_fullstack.shared.interfaces.rest.resources.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value= "api/v1/Machinery")
@Tag(name = "Machinery", description = "Machinery Management Endpoints")
public class MachineryController {
    private final MachineryCommandService machineryCommandService;
    private final MachineryQueryService machineryQueryService;

    public MachineryController(MachineryCommandService machineryCommandService, MachineryQueryService machineryQueryService) {
        this.machineryCommandService = machineryCommandService;
        this.machineryQueryService = machineryQueryService;
    }

    @Operation(summary = "Register a Machinery")
    @PostMapping
    public ResponseEntity<MachineryResource> registerMachinery(@RequestBody CreateMachineryResource resource) {
        var createMachineryCommand = CreateMachineryCommandFromResourceAssembler.toCommandFromResource(resource);

        var machinery = this.machineryCommandService.handle(createMachineryCommand);
        if (machinery == null || machinery.isEmpty()){
            return ResponseEntity.internalServerError().build();
        }

        var createMachineryFromResource = MachineryResourceFromEntityAssembler.toResourceFromEntity(machinery.get());

        return new ResponseEntity<>(createMachineryFromResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Get All Machineries with optional filters")
    @GetMapping
    public ResponseEntity<List<MachineryResource>> getAllMachinery(
            @RequestParam(required = false) Boolean state,
            @RequestParam(required = false) Integer machineryTypeId,
            @RequestParam(required = false) String code) {
        var query = new GetMachineryByFilterQuery(state, machineryTypeId, code);
        var machineries = this.machineryQueryService.handle(query);
        var machineryResources = machineries.stream()
                .map(MachineryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(machineryResources);
    }

    @Operation(summary = "Get Machinery by Code")
    @GetMapping("/{code}")
    public ResponseEntity<MachineryResource> getMachineryByCode(@PathVariable String code) {
        var getMachineryByCodeQuery = new GetMachineryByCodeQuery(code);
        var machinery = this.machineryQueryService.handle(getMachineryByCodeQuery);
        if (machinery == null || machinery.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var machineryResource = MachineryResourceFromEntityAssembler.toResourceFromEntity(machinery.get());
        return ResponseEntity.ok(machineryResource);
    }

    @Operation(summary = "Update Machinery Type of a Machinery")
    @PutMapping("/{code}/machinery-type")
    public ResponseEntity<MachineryResource> updateMachineryMachineryType(
            @PathVariable String code,
            @RequestBody UpdateMachineryMachineryTypeResource resource) {
        var updateMachineryMachineryTypeCommand = UpdateMachineryMachineryTypeCommandFromResourceAssembler.toCommandFromResource(resource);
        var machinery = this.machineryCommandService.handle(updateMachineryMachineryTypeCommand);
        if (machinery == null || machinery.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        var machineryResource = MachineryResourceFromEntityAssembler.toResourceFromEntity(machinery.get());
        return ResponseEntity.ok(machineryResource);
    }

    @Operation(summary = "Delete a Machinery")
    @DeleteMapping("/{code}")
    public ResponseEntity<MessageResource> deleteMachinery(@PathVariable String code) {
        var deleteMachineryCommand = new DeleteMachineryCommand(code);
        this.machineryCommandService.handle(deleteMachineryCommand);
        return ResponseEntity.ok(new MessageResource("Machinery with code " + code + " deleted successfully"));
    }
}
