package codea.uni.desafio_fullstack.maintenance.domain.model.aggregates;

import codea.uni.desafio_fullstack.maintenance.domain.model.commands.CreateMaintenanceCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MaintenanceTest {

    @Test
    @DisplayName("Should create maintenance successfully with valid command")
    void shouldCreateMaintenanceSuccessfullyWithValidCommand() {
        UUID operatorId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 9, 6);
        CreateMaintenanceCommand command = new CreateMaintenanceCommand(
                "EQ-001",
                date,
                250.5f,
                operatorId,
                "Routine oil and filter change"
        );

        Maintenance maintenance = new Maintenance(command);

        assertEquals("EQ-001", maintenance.getMachineryCode());
        assertEquals(date, maintenance.getDate());
        assertEquals(250.5f, maintenance.getHourMeter());
        assertEquals(operatorId, maintenance.getOperatorId());
        assertEquals("Routine oil and filter change", maintenance.getObservation());
    }

    @Test
    @DisplayName("Should fail when machinery code is null or blank")
    void shouldFailWhenMachineryCodeIsNullOrBlank() {
        UUID operatorId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 9, 6);

        assertThrows(IllegalArgumentException.class, () ->
                new CreateMaintenanceCommand(null, date, 100.0f, operatorId, "Test"));

        assertThrows(IllegalArgumentException.class, () ->
                new CreateMaintenanceCommand("   ", date, 100.0f, operatorId, "Test"));
    }

    @Test
    @DisplayName("Should fail when date is null")
    void shouldFailWhenDateIsNull() {
        UUID operatorId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () ->
                new CreateMaintenanceCommand("EQ-001", null, 100.0f, operatorId, "Test"));
    }

    @Test
    @DisplayName("Should fail when hour meter is negative")
    void shouldFailWhenHourMeterIsNegative() {
        UUID operatorId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 9, 6);

        assertThrows(IllegalArgumentException.class, () ->
                new CreateMaintenanceCommand("EQ-001", date, -5.0f, operatorId, "Test"));
    }

    @Test
    @DisplayName("Should fail when operator ID is null")
    void shouldFailWhenOperatorIdIsNull() {
        LocalDate date = LocalDate.of(2026, 9, 6);

        assertThrows(IllegalArgumentException.class, () ->
                new CreateMaintenanceCommand("EQ-001", date, 100.0f, null, "Test"));
    }

    @Test
    @DisplayName("Should update maintenance details successfully")
    void shouldUpdateDetailsSuccessfully() {
        UUID operatorId = UUID.randomUUID();
        CreateMaintenanceCommand command = new CreateMaintenanceCommand(
                "EQ-001",
                LocalDate.of(2026, 9, 6),
                100.0f,
                operatorId,
                "Initial observation"
        );
        Maintenance maintenance = new Maintenance(command);

        UUID newOperatorId = UUID.randomUUID();
        maintenance.updateDetails(150.0f, newOperatorId, "Updated observation");

        assertEquals(150.0f, maintenance.getHourMeter());
        assertEquals(newOperatorId, maintenance.getOperatorId());
        assertEquals("Updated observation", maintenance.getObservation());
    }

    @Test
    @DisplayName("Should fail when updating details with negative hour meter")
    void shouldFailWhenUpdatingWithNegativeHourMeter() {
        UUID operatorId = UUID.randomUUID();
        CreateMaintenanceCommand command = new CreateMaintenanceCommand(
                "EQ-001",
                LocalDate.of(2026, 9, 6),
                100.0f,
                operatorId,
                "Initial observation"
        );
        Maintenance maintenance = new Maintenance(command);

        assertThrows(IllegalArgumentException.class, () ->
                maintenance.updateDetails(-1.0f, operatorId, "Valid"));
    }

    @Test
    @DisplayName("Should fail when updating details with null operator ID")
    void shouldFailWhenUpdatingWithNullOperatorId() {
        UUID operatorId = UUID.randomUUID();
        CreateMaintenanceCommand command = new CreateMaintenanceCommand(
                "EQ-001",
                LocalDate.of(2026, 9, 6),
                100.0f,
                operatorId,
                "Initial observation"
        );
        Maintenance maintenance = new Maintenance(command);

        assertThrows(IllegalArgumentException.class, () ->
                maintenance.updateDetails(120.0f, null, "Valid"));
    }
}
