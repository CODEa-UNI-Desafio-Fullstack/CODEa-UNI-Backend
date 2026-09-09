package codea.uni.desafio_fullstack.operations.infrastructure.acl;

import codea.uni.desafio_fullstack.machinery.interfaces.acl.MachineryContextFacade;
import codea.uni.desafio_fullstack.machinery.interfaces.acl.records.MachinerySummaryRecord;
import codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl.ExternalMachineryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public boolean isMachineryActive(String machineryCode) {
        return this.machineryContextFacade.isMachineryActive(machineryCode);
    }

    @Override
    public Integer getMachineryTypeId(String machineryCode) {
        return this.machineryContextFacade.getMachineryTypeId(machineryCode).orElse(null);
    }

    @Override
    public Object recordWorkedHours(String machineryCode, float workedHours) {
        return this.machineryContextFacade.recordWorkedHours(machineryCode, workedHours);
    }

    @Override
    public List<MachinerySummaryRecord> getAllMachineriesForProjection() {
        return this.machineryContextFacade.getAllMachineriesForProjection();
    }

    @Override
    public Optional<MachinerySummaryRecord> getMachinerySummary(String machineryCode) {
        return this.machineryContextFacade.getMachinerySummary(machineryCode);
    }
}
