package codea.uni.desafio_fullstack.maintenance.infrastructure.acl;

import codea.uni.desafio_fullstack.machinery.interfaces.acl.MachineryContextFacade;
import codea.uni.desafio_fullstack.maintenance.application.internal.outboundservices.acl.ExternalMachineryService;
import org.springframework.stereotype.Service;

import codea.uni.desafio_fullstack.machinery.interfaces.acl.records.MachinerySummaryRecord;

import java.util.List;

@Service("maintenanceExternalMachineryService")
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
    public void resetMachineryAfterMaintenance(String machineryCode) {
        this.machineryContextFacade.resetMachineryAfterMaintenance(machineryCode);
    }

    @Override
    public List<MachinerySummaryRecord> getAllMachineries() {
        return this.machineryContextFacade.getAllMachineriesForProjection();
    }
}
