package codea.uni.desafio_fullstack.machinery.interfaces.acl.records;


//ACL Record containing the outcome of recording worked hours on a machine during shift closure.

public record MachineryWorkedHoursResult(
        String code,
        float updatedHourMeter,
        boolean blocked,
        boolean blockedByThisOperation
) {}
