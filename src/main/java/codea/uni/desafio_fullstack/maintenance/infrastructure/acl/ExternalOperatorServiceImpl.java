package codea.uni.desafio_fullstack.maintenance.infrastructure.acl;

import codea.uni.desafio_fullstack.maintenance.application.internal.outboundservices.acl.ExternalOperatorService;
import codea.uni.desafio_fullstack.operators.interfaces.acl.OperatorContextFacade;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("maintenanceExternalOperatorService")
public class ExternalOperatorServiceImpl implements ExternalOperatorService {

    private final OperatorContextFacade operatorContextFacade;

    public ExternalOperatorServiceImpl(OperatorContextFacade operatorContextFacade) {
        this.operatorContextFacade = operatorContextFacade;
    }

    @Override
    public boolean existsOperatorById(UUID operatorId) {
        return this.operatorContextFacade.existsById(operatorId);
    }
}
