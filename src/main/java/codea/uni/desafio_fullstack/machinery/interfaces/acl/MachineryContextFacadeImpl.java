package codea.uni.desafio_fullstack.machinery.interfaces.acl;

import codea.uni.desafio_fullstack.machinery.domain.model.aggregates.Machinery;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.RecordMachineryWorkedHoursCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.ResetMachineryMaintenanceCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetAllMachineryQuery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetMachineryByCodeQuery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetMachineryTypeByIdQuery;
import codea.uni.desafio_fullstack.machinery.domain.services.MachineryCommandService;
import codea.uni.desafio_fullstack.machinery.domain.services.MachineryQueryService;
import codea.uni.desafio_fullstack.machinery.domain.services.MachineryTypeQueryService;
import codea.uni.desafio_fullstack.machinery.interfaces.acl.records.MachinerySummaryRecord;
import codea.uni.desafio_fullstack.machinery.interfaces.acl.records.MachineryWorkedHoursResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MachineryContextFacadeImpl implements MachineryContextFacade {

    private final MachineryCommandService machineryCommandService;
    private final MachineryQueryService machineryQueryService;
    private final MachineryTypeQueryService machineryTypeQueryService;

    public MachineryContextFacadeImpl(MachineryCommandService machineryCommandService,
                                      MachineryQueryService machineryQueryService,
                                      MachineryTypeQueryService machineryTypeQueryService) {
        this.machineryCommandService = machineryCommandService;
        this.machineryQueryService = machineryQueryService;
        this.machineryTypeQueryService = machineryTypeQueryService;
    }

    @Override
    public boolean existsByCode(String machineryCode) {
        if (machineryCode == null || machineryCode.isBlank()) {
            return false;
        }
        return this.machineryQueryService.handle(new GetMachineryByCodeQuery(machineryCode)).isPresent();
    }

    @Override
    public boolean isMachineryActive(String machineryCode) {
        if (machineryCode == null || machineryCode.isBlank()) {
            return false;
        }
        return this.machineryQueryService.handle(new GetMachineryByCodeQuery(machineryCode))
                .map(Machinery::isActive)
                .orElse(false);
    }

    @Override
    public Optional<Integer> getMachineryTypeId(String machineryCode) {
        if (machineryCode == null || machineryCode.isBlank()) {
            return Optional.empty();
        }
        return this.machineryQueryService.handle(new GetMachineryByCodeQuery(machineryCode))
                .map(m -> m.getMachineryType() != null ? m.getMachineryType().getId() : null);
    }

    @Override
    public MachineryWorkedHoursResult recordWorkedHours(String machineryCode, float workedHours) {
        boolean wasBlockedBefore = !isMachineryActive(machineryCode);

        var command = new RecordMachineryWorkedHoursCommand(machineryCode, workedHours);
        var machinery = this.machineryCommandService.handle(command)
                .orElseThrow(() -> new IllegalArgumentException("Machinery not found with code: " + machineryCode));

        boolean isBlockedNow = machinery.isBlocked();
        boolean blockedByThisOperation = !wasBlockedBefore && isBlockedNow;

        return new MachineryWorkedHoursResult(
                machinery.getCode(),
                machinery.getHourMeter(),
                isBlockedNow,
                blockedByThisOperation
        );
    }

    @Override
    public Optional<Float> getMachineryHourMeter(String machineryCode) {
        if (machineryCode == null || machineryCode.isBlank()) {
            return Optional.empty();
        }
        return this.machineryQueryService.handle(new GetMachineryByCodeQuery(machineryCode))
                .map(Machinery::getHourMeter);
    }

    @Override
    public void resetMachineryAfterMaintenance(String machineryCode) {
        var command = new ResetMachineryMaintenanceCommand(machineryCode);
        this.machineryCommandService.handle(command);
    }

    @Override
    public Optional<MachinerySummaryRecord> getMachinerySummary(String machineryCode) {
        if (machineryCode == null || machineryCode.isBlank()) {
            return Optional.empty();
        }
        return this.machineryQueryService.handle(new GetMachineryByCodeQuery(machineryCode))
                .map(this::toSummaryRecord);
    }

    @Override
    public boolean existsMachineryTypeById(Integer machineryTypeId) {
        if (machineryTypeId == null) {
            return false;
        }
        return this.machineryTypeQueryService.handle(new GetMachineryTypeByIdQuery(machineryTypeId)).isPresent();
    }

    @Override
    public List<MachinerySummaryRecord> getAllMachineriesForProjection() {
        return this.machineryQueryService.handle(new GetAllMachineryQuery())
                .stream()
                .map(this::toSummaryRecord)
                .toList();
    }

    private MachinerySummaryRecord toSummaryRecord(Machinery machinery) {
        var type = machinery.getMachineryType();
        Integer typeId = type != null ? type.getId() : null;
        String typeName = type != null ? type.getName() : "UNKNOWN";
        int threshold = type != null ? type.getMaintenanceTime() : 0;

        return new MachinerySummaryRecord(
                machinery.getCode(),
                typeId,
                typeName,
                machinery.getHourMeter(),
                threshold,
                machinery.isActive(),
                machinery.isBlocked(),
                machinery.getRemainingHoursToMaintenance()
        );
    }
}
