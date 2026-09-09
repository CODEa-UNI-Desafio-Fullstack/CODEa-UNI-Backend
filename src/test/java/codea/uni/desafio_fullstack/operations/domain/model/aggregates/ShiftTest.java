package codea.uni.desafio_fullstack.operations.domain.model.aggregates;

import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateShiftCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ShiftTest {

    @Test
    @DisplayName("Should create shift successfully with valid command")
    void shouldCreateShiftSuccessfullyWithValidCommand() {
        LocalDate date = LocalDate.of(2026, 9, 7);
        CreateShiftCommand command = new CreateShiftCommand(date, true, 8);

        Shift shift = new Shift(command);

        assertEquals(date, shift.getDate());
        assertTrue(shift.getShiftType());
        assertEquals(8, shift.getDuration());
    }

    @Test
    @DisplayName("Should update shift successfully")
    void shouldUpdateShiftSuccessfully() {
        Shift shift = new Shift(new CreateShiftCommand(LocalDate.of(2026, 9, 7), true, 8));

        LocalDate newDate = LocalDate.of(2026, 9, 8);
        shift.update(newDate, false, 12);

        assertEquals(newDate, shift.getDate());
        assertFalse(shift.getShiftType());
        assertEquals(12, shift.getDuration());
    }

    @Test
    @DisplayName("Should fail when shift date is null")
    void shouldFailWhenDateIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateShiftCommand(null, true, 8));

        Shift shift = new Shift(new CreateShiftCommand(LocalDate.of(2026, 9, 7), true, 8));
        assertThrows(IllegalArgumentException.class, () ->
                shift.update(null, true, 8));
    }

    @Test
    @DisplayName("Should fail when shift type is null")
    void shouldFailWhenShiftTypeIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateShiftCommand(LocalDate.of(2026, 9, 7), null, 8));

        Shift shift = new Shift(new CreateShiftCommand(LocalDate.of(2026, 9, 7), true, 8));
        assertThrows(IllegalArgumentException.class, () ->
                shift.update(LocalDate.of(2026, 9, 7), null, 8));
    }

    @Test
    @DisplayName("Should fail when duration is null or zero or negative")
    void shouldFailWhenDurationIsZeroOrNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateShiftCommand(LocalDate.of(2026, 9, 7), true, null));

        assertThrows(IllegalArgumentException.class, () ->
                new CreateShiftCommand(LocalDate.of(2026, 9, 7), true, 0));

        assertThrows(IllegalArgumentException.class, () ->
                new CreateShiftCommand(LocalDate.of(2026, 9, 7), true, -5));

        Shift shift = new Shift(new CreateShiftCommand(LocalDate.of(2026, 9, 7), true, 8));
        assertThrows(IllegalArgumentException.class, () ->
                shift.update(LocalDate.of(2026, 9, 7), true, 0));
        assertThrows(IllegalArgumentException.class, () ->
                shift.update(LocalDate.of(2026, 9, 7), true, -1));
    }
}
