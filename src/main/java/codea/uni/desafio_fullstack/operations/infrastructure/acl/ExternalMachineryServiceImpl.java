package codea.uni.desafio_fullstack.operations.infrastructure.acl;

import codea.uni.desafio_fullstack.machinery.interfaces.acl.MachineryContextFacade;
import codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl.ExternalMachineryService;
import org.springframework.stereotype.Service;

@Service("operationsExternalMachineryService")
public class ExternalMachineryServiceImpl implements ExternalMachineryService {

    private final MachineryContextFacade machineryContextFacade;

    public ExternalMachineryServiceImpl(MachineryContextFacade machineryContextFacade) {
        this.machineryContextFacade = machineryContextFacade;
    }

    @Override
    public boolean existsMachineryByCode(String machineryCode) {
        return this.machineryContextFacade.existsByCode(machineryCode);
    }
}
