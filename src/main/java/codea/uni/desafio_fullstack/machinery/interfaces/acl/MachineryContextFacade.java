package codea.uni.desafio_fullstack.machinery.interfaces.acl;

import codea.uni.desafio_fullstack.machinery.interfaces.acl.records.MachinerySummaryRecord;
import codea.uni.desafio_fullstack.machinery.interfaces.acl.records.MachineryWorkedHoursResult;

import java.util.List;
import java.util.Optional;

/**
 * Anti-Corruption Layer (ACL) Facade for the Machinery Bounded Context.
 * Enables external contexts (operations, maintenance, operators) to interact with
 * machinery functionality without coupling to internal domain models or repositories.
 */
public interface MachineryContextFacade {

    // Checks if a machinery exists by its unique code
    boolean existsByCode(String machineryCode);

    // Checks if a machinery is in ACTIVE state (not blocked, available for operation).
    boolean isMachineryActive(String machineryCode);

    // Retrieves the MachineryType ID of a machinery (used by operations context to validate operator certifications).
    Optional<Integer> getMachineryTypeId(String machineryCode);


    // Records worked hours during a shift closure, accumulating into the horometer
    // and auto-blocking the machinery if the maintenance threshold is reached or exceeded.
    MachineryWorkedHoursResult recordWorkedHours(String machineryCode, float workedHours);


    // Retrieves the current hour meter of a machinery (used when registering maintenance).
    Optional<Float> getMachineryHourMeter(String machineryCode);


    // Resets the hour meter to 0 and unblocks the machinery following completed maintenance (Policy P3).
    void resetMachineryAfterMaintenance(String machineryCode);


    // Retrieves an immutable summary of a machinery by code.
    Optional<MachinerySummaryRecord> getMachinerySummary(String machineryCode);


    // Checks if a machinery type exists by its ID (used by operators context to validate certification issuance).
    boolean existsMachineryTypeById(Integer machineryTypeId);

    // Retrieves all machineries formatted as summaries for maintenance projection calculations (e.g. 7-day forecast).
    List<MachinerySummaryRecord> getAllMachineriesForProjection();
}
