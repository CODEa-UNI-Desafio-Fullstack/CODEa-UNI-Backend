package codea.uni.desafio_fullstack.operators.interfaces.acl;

import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import codea.uni.desafio_fullstack.operators.domain.model.entities.MachineryCertification;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetAllOperatorsQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetCertificationByOperatorIdAndMachineryTypeIdQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorByIdQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorsByMachineryTypeCertificationQuery;
import codea.uni.desafio_fullstack.operators.domain.services.MachineryCertificationQueryService;
import codea.uni.desafio_fullstack.operators.domain.services.OperatorQueryService;
import codea.uni.desafio_fullstack.operators.interfaces.acl.records.OperatorCertificationSummaryRecord;
import codea.uni.desafio_fullstack.operators.interfaces.acl.records.OperatorSummaryRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OperatorContextFacadeImpl implements OperatorContextFacade {

    private final OperatorQueryService operatorQueryService;
    private final MachineryCertificationQueryService machineryCertificationQueryService;

    public OperatorContextFacadeImpl(OperatorQueryService operatorQueryService,
                                     MachineryCertificationQueryService machineryCertificationQueryService) {
        this.operatorQueryService = operatorQueryService;
        this.machineryCertificationQueryService = machineryCertificationQueryService;
    }

    @Override
    public boolean existsById(UUID operatorId) {
        if (operatorId == null) {
            return false;
        }
        return this.operatorQueryService.handle(new GetOperatorByIdQuery(operatorId)).isPresent();
    }

    @Override
    public Optional<OperatorSummaryRecord> getOperatorSummary(UUID operatorId) {
        if (operatorId == null) {
            return Optional.empty();
        }
        return this.operatorQueryService.handle(new GetOperatorByIdQuery(operatorId))
                .map(this::toSummaryRecord);
    }

    @Override
    public boolean isOperatorCertifiedForMachineryType(UUID operatorId, Integer machineryTypeId, LocalDate shiftDate) {
        if (operatorId == null || machineryTypeId == null || shiftDate == null) {
            return false;
        }
        var query = new GetCertificationByOperatorIdAndMachineryTypeIdQuery(operatorId, machineryTypeId);
        var certOptional = this.machineryCertificationQueryService.handle(query);

        if (certOptional.isEmpty()) {
            return false;
        }

        var certification = certOptional.get();
        // If it expires on or before the shift date, it is considered invalid for this shift.
        // The certification must expire strictly AFTER the shift date to be valid.
        return certification.getExpirationDate() != null && certification.getExpirationDate().isAfter(shiftDate);
    }

    @Override
    public Optional<OperatorCertificationSummaryRecord> getCertificationSummary(UUID operatorId, Integer machineryTypeId) {
        if (operatorId == null || machineryTypeId == null) {
            return Optional.empty();
        }
        var query = new GetCertificationByOperatorIdAndMachineryTypeIdQuery(operatorId, machineryTypeId);
        return this.machineryCertificationQueryService.handle(query)
                .map(this::toCertificationSummaryRecord);
    }

    @Override
    public List<UUID> getCertifiedOperatorIdsForMachineryType(Integer machineryTypeId, LocalDate shiftDate) {
        if (machineryTypeId == null || shiftDate == null) {
            return List.of();
        }
        var query = new GetOperatorsByMachineryTypeCertificationQuery(machineryTypeId);
        var operators = this.operatorQueryService.handle(query);

        return operators.stream()
                .map(Operator::getId)
                .filter(opId -> isOperatorCertifiedForMachineryType(opId, machineryTypeId, shiftDate))
                .toList();
    }

    @Override
    public List<OperatorSummaryRecord> getAllOperators() {
        return this.operatorQueryService.handle(new GetAllOperatorsQuery())
                .stream()
                .map(this::toSummaryRecord)
                .toList();
    }

    private OperatorSummaryRecord toSummaryRecord(Operator operator) {
        return new OperatorSummaryRecord(operator.getId(), operator.getName());
    }

    private OperatorCertificationSummaryRecord toCertificationSummaryRecord(MachineryCertification certification) {
        boolean isValidToday = certification.isValidOn(LocalDate.now());
        return new OperatorCertificationSummaryRecord(
                certification.getId().getOperatorId(),
                certification.getId().getMachineryType(),
                certification.getExpirationDate(),
                isValidToday
        );
    }
}
