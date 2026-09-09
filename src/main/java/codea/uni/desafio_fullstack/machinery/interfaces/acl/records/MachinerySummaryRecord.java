package codea.uni.desafio_fullstack.machinery.interfaces.acl.records;


//ACL Record providing an immutable summary of machinery details for external bounded contexts
//(operations context checking availability, maintenance projection calculations).

public record MachinerySummaryRecord(
        String code,
        Integer machineryTypeId,
        String machineryTypeName,
        float currentHourMeter,
        int maintenanceThreshold,
        boolean active,
        boolean blocked,
        float remainingHoursToMaintenance
) {}
