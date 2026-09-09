package codea.uni.desafio_fullstack.machinery.domain.model.aggregates;

import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryTypeCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MachineryTest {

    private MachineryType excavatorType;
    private Machinery machinery;

    @BeforeEach
    void setUp() {
        // Intervalo de mantenimiento de 250 horas
        var typeCommand = new CreateMachineryTypeCommand("Excavadora", 250);
        excavatorType = new MachineryType(typeCommand);

        var machineryCommand = new CreateMachineryCommand("EXC-001", 1);
        machinery = new Machinery(machineryCommand, excavatorType);
    }

    @Test
    @DisplayName("Initial machinery state should be active with 0.0 hour meter")
    void shouldInitializeWithActiveStateAndZeroHours() {
        assertEquals("EXC-001", machinery.getCode());
        assertEquals(0.0f, machinery.getHourMeter());
        assertTrue(machinery.isActive());
        assertFalse(machinery.isBlocked());
        assertEquals(250.0f, machinery.getRemainingHoursToMaintenance());
    }

    @Test
    @DisplayName("Recording worked hours below threshold should accumulate hours and keep machinery active")
    void recordWorkedHours_BelowThreshold_ShouldKeepActive() {
        // Cierre de turno: se registran 20 horas trabajadas
        boolean isBlocked = machinery.recordWorkedHours(20.0f);

        assertEquals(20.0f, machinery.getHourMeter());
        assertFalse(isBlocked);
        assertTrue(machinery.isActive());
        assertFalse(machinery.isBlocked());
        assertEquals(230.0f, machinery.getRemainingHoursToMaintenance());
    }

    @Test
    @DisplayName("Recording worked hours reaching exactly threshold should automatically block machinery")
    void recordWorkedHours_ReachingExactThreshold_ShouldAutoBlock() {
        // Cierre de turno: horómetro alcanza exactamente las 250 horas
        boolean isBlocked = machinery.recordWorkedHours(250.0f);

        assertEquals(250.0f, machinery.getHourMeter());
        assertTrue(isBlocked);
        assertFalse(machinery.isActive());
        assertTrue(machinery.isBlocked());
        assertEquals(0.0f, machinery.getRemainingHoursToMaintenance());
    }

    @Test
    @DisplayName("Recording worked hours exceeding threshold should automatically block machinery")
    void recordWorkedHours_ExceedingThreshold_ShouldAutoBlock() {
        // Horómetro inicial en 240, se agregan 20 horas -> nuevo horómetro 260
        machinery.recordWorkedHours(240.0f);
        assertTrue(machinery.isActive());

        boolean isBlocked = machinery.recordWorkedHours(20.0f);

        assertEquals(260.0f, machinery.getHourMeter());
        assertTrue(isBlocked);
        assertTrue(machinery.isBlocked());
        assertFalse(machinery.isActive());
        assertEquals(0.0f, machinery.getRemainingHoursToMaintenance());
    }

    @Test
    @DisplayName("Reset after maintenance should set hour meter to 0 and unblock machinery (Policy P3)")
    void resetAfterMaintenance_ShouldResetHourMeterToZeroAndUnblock() {
        // Maquinaria que llegó a 270 horas y quedó bloqueada
        machinery.recordWorkedHours(270.0f);
        assertTrue(machinery.isBlocked());

        // Se registra el mantenimiento
        machinery.resetAfterMaintenance();

        assertEquals(0.0f, machinery.getHourMeter());
        assertTrue(machinery.isActive());
        assertFalse(machinery.isBlocked());
        assertEquals(250.0f, machinery.getRemainingHoursToMaintenance());
    }

    @Test
    @DisplayName("willExceedMaintenance should accurately project whether additional hours trigger threshold")
    void willExceedMaintenance_ShouldCorrectlyPredict() {
        machinery.recordWorkedHours(230.0f);

        assertFalse(machinery.willExceedMaintenance(10.0f)); // 240 < 250
        assertTrue(machinery.willExceedMaintenance(20.0f));  // 250 == 250
        assertTrue(machinery.willExceedMaintenance(25.0f));  // 255 > 250
    }

    @Test
    @DisplayName("Recording negative or zero worked hours should throw IllegalArgumentException")
    void recordWorkedHours_InvalidHours_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> machinery.recordWorkedHours(0.0f));
        assertThrows(IllegalArgumentException.class, () -> machinery.recordWorkedHours(-5.0f));
    }
}
