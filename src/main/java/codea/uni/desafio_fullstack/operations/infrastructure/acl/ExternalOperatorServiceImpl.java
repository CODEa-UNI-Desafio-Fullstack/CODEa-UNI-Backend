package codea.uni.desafio_fullstack.operations.infrastructure.acl;

import codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl.ExternalOperatorService;
import codea.uni.desafio_fullstack.operators.interfaces.acl.OperatorContextFacade;
import codea.uni.desafio_fullstack.operators.interfaces.acl.records.OperatorSummaryRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("operationsExternalOperatorService")
public class ExternalOperatorServiceImpl implements ExternalOperatorService {

    private final OperatorContextFacade operatorContextFacade;

    public ExternalOperatorServiceImpl(OperatorContextFacade operatorContextFacade) {
        this.operatorContextFacade = operatorContextFacade;
    }

    @Override
    public boolean existsOperatorById(UUID operatorId) {
        return this.operatorContextFacade.existsById(operatorId);
    }

    @Override
    public boolean isOperatorCertifiedForMachineryType(UUID operatorId, Integer machineryTypeId, LocalDate shiftDate) {
        return this.operatorContextFacade.isOperatorCertifiedForMachineryType(operatorId, machineryTypeId, shiftDate);
    }

    @Override
    public List<OperatorSummaryRecord> getAllOperators() {
        return this.operatorContextFacade.getAllOperators();
    }

    @Override
    public Optional<OperatorSummaryRecord> getOperatorSummary(UUID operatorId) {
        return this.operatorContextFacade.getOperatorSummary(operatorId);
    }
}
