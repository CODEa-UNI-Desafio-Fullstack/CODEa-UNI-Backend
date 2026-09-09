package codea.uni.desafio_fullstack.operators.infrastructure.acl;

import codea.uni.desafio_fullstack.machinery.interfaces.acl.MachineryContextFacade;
import codea.uni.desafio_fullstack.operators.application.internal.outboundservices.acl.ExternalMachineryService;
import org.springframework.stereotype.Service;

@Service
public class ExternalMachineryServiceImpl implements ExternalMachineryService {

    private final MachineryContextFacade machineryContextFacade;

    public ExternalMachineryServiceImpl(MachineryContextFacade machineryContextFacade) {
        this.machineryContextFacade = machineryContextFacade;
    }

    @Override
    public boolean existsMachineryTypeById(Integer machineryTypeId) {
        return this.machineryContextFacade.existsMachineryTypeById(machineryTypeId);
    }
}
