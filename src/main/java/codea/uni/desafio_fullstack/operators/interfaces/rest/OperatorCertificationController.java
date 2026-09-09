package codea.uni.desafio_fullstack.operators.interfaces.rest;

import codea.uni.desafio_fullstack.operators.domain.model.commands.DeleteMachineryCertificationCommand;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetAllCertificationsByOperatorIdQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetCertificationByOperatorIdAndMachineryTypeIdQuery;
import codea.uni.desafio_fullstack.operators.domain.services.MachineryCertificationCommandService;
import codea.uni.desafio_fullstack.operators.domain.services.MachineryCertificationQueryService;
import codea.uni.desafio_fullstack.operators.interfaces.rest.resources.CreateMachineryCertificationResource;
import codea.uni.desafio_fullstack.operators.interfaces.rest.resources.MachineryCertificationResource;
import codea.uni.desafio_fullstack.operators.interfaces.rest.resources.UpdateMachineryCertificationResource;
import codea.uni.desafio_fullstack.operators.interfaces.rest.transform.CreateMachineryCertificationCommandFromResourceAssembler;
import codea.uni.desafio_fullstack.operators.interfaces.rest.transform.MachineryCertificationResourceFromEntityAssembler;
import codea.uni.desafio_fullstack.operators.interfaces.rest.transform.UpdateMachineryCertificationCommandFromResourceAssembler;
import codea.uni.desafio_fullstack.shared.interfaces.rest.resources.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/operators/{operatorId}/certifications")
@Tag(name = "Operator Certifications", description = "Operator Certifications Management Endpoints")
public class OperatorCertificationController {

    private final MachineryCertificationCommandService machineryCertificationCommandService;
    private final MachineryCertificationQueryService machineryCertificationQueryService;

    public OperatorCertificationController(MachineryCertificationCommandService machineryCertificationCommandService,
                                           MachineryCertificationQueryService machineryCertificationQueryService) {
        this.machineryCertificationCommandService = machineryCertificationCommandService;
        this.machineryCertificationQueryService = machineryCertificationQueryService;
    }

    @Operation(summary = "Register a certification for an Operator")
    @PostMapping
    public ResponseEntity<MachineryCertificationResource> registerCertification(
            @PathVariable UUID operatorId,
            @RequestBody CreateMachineryCertificationResource resource) {
        var command = CreateMachineryCertificationCommandFromResourceAssembler.toCommandFromResource(operatorId, resource);
        var certification = this.machineryCertificationCommandService.handle(command);
        if (certification == null || certification.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        var responseResource = MachineryCertificationResourceFromEntityAssembler.toResourceFromEntity(certification.get());
        return new ResponseEntity<>(responseResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all certifications of an Operator")
    @GetMapping
    public ResponseEntity<List<MachineryCertificationResource>> getAllCertifications(@PathVariable UUID operatorId) {
        var query = new GetAllCertificationsByOperatorIdQuery(operatorId);
        var certifications = this.machineryCertificationQueryService.handle(query);
        var resources = certifications.stream()
                .map(MachineryCertificationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Get a certification of an Operator by Machinery Type")
    @GetMapping("/{machineryTypeId}")
    public ResponseEntity<MachineryCertificationResource> getCertification(
            @PathVariable UUID operatorId,
            @PathVariable Integer machineryTypeId) {
        var query = new GetCertificationByOperatorIdAndMachineryTypeIdQuery(operatorId, machineryTypeId);
        var certification = this.machineryCertificationQueryService.handle(query);
        if (certification == null || certification.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var resource = MachineryCertificationResourceFromEntityAssembler.toResourceFromEntity(certification.get());
        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Update a certification for an Operator")
    @PutMapping("/{machineryTypeId}")
    public ResponseEntity<MachineryCertificationResource> updateCertification(
            @PathVariable UUID operatorId,
            @PathVariable Integer machineryTypeId,
            @RequestBody UpdateMachineryCertificationResource resource) {
        var command = UpdateMachineryCertificationCommandFromResourceAssembler.toCommandFromResource(operatorId, machineryTypeId, resource);
        var updated = this.machineryCertificationCommandService.handle(command);
        if (updated == null || updated.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        var responseResource = MachineryCertificationResourceFromEntityAssembler.toResourceFromEntity(updated.get());
        return ResponseEntity.ok(responseResource);
    }

    @Operation(summary = "Delete a certification of an Operator")
    @DeleteMapping("/{machineryTypeId}")
    public ResponseEntity<MessageResource> deleteCertification(
            @PathVariable UUID operatorId,
            @PathVariable Integer machineryTypeId) {
        var command = new DeleteMachineryCertificationCommand(operatorId, machineryTypeId);
        this.machineryCertificationCommandService.handle(command);
        return ResponseEntity.ok(new MessageResource("Certification for operator " + operatorId
                + " and machinery type " + machineryTypeId + " deleted successfully"));
    }
}
