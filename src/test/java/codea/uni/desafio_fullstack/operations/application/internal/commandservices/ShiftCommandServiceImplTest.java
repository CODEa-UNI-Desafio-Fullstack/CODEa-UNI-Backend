package codea.uni.desafio_fullstack.operations.application.internal.commandservices;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Shift;
import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateShiftCommand;
import codea.uni.desafio_fullstack.operations.domain.model.commands.DeleteShiftCommand;
import codea.uni.desafio_fullstack.operations.domain.model.commands.UpdateShiftCommand;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.ShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftCommandServiceImplTest {

    @Mock
    private ShiftRepository shiftRepository;

    private ShiftCommandServiceImpl shiftCommandService;

    @BeforeEach
    void setUp() {
        shiftCommandService = new ShiftCommandServiceImpl(shiftRepository);
    }

    @Test
    @DisplayName("Should create shift successfully")
    void shouldCreateShiftSuccessfully() {
        CreateShiftCommand command = new CreateShiftCommand(LocalDate.of(2026, 9, 7), true, 8);
        when(shiftRepository.save(any(Shift.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Shift> result = shiftCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals(LocalDate.of(2026, 9, 7), result.get().getDate());
        assertTrue(result.get().getShiftType());
        assertEquals(8, result.get().getDuration());
        verify(shiftRepository, times(1)).save(any(Shift.class));
    }

    @Test
    @DisplayName("Should update shift successfully")
    void shouldUpdateShiftSuccessfully() {
        UUID shiftId = UUID.randomUUID();
        Shift existingShift = new Shift(new CreateShiftCommand(LocalDate.of(2026, 9, 7), true, 8));

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(existingShift));
        when(shiftRepository.save(any(Shift.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateShiftCommand command = new UpdateShiftCommand(shiftId, LocalDate.of(2026, 9, 8), false, 12);
        Optional<Shift> result = shiftCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals(LocalDate.of(2026, 9, 8), result.get().getDate());
        assertFalse(result.get().getShiftType());
        assertEquals(12, result.get().getDuration());
        verify(shiftRepository, times(1)).save(existingShift);
    }

    @Test
    @DisplayName("Should fail to update when shift does not exist")
    void shouldFailToUpdateWhenShiftDoesNotExist() {
        UUID shiftId = UUID.randomUUID();
        when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());

        UpdateShiftCommand command = new UpdateShiftCommand(shiftId, LocalDate.of(2026, 9, 8), false, 12);

        assertThrows(IllegalArgumentException.class, () -> shiftCommandService.handle(command));
        verify(shiftRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete shift successfully")
    void shouldDeleteShiftSuccessfully() {
        UUID shiftId = UUID.randomUUID();
        Shift existingShift = new Shift(new CreateShiftCommand(LocalDate.of(2026, 9, 7), true, 8));

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(existingShift));

        DeleteShiftCommand command = new DeleteShiftCommand(shiftId);
        shiftCommandService.handle(command);

        verify(shiftRepository, times(1)).delete(existingShift);
    }

    @Test
    @DisplayName("Should fail to delete when shift does not exist")
    void shouldFailToDeleteWhenShiftDoesNotExist() {
        UUID shiftId = UUID.randomUUID();
        when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());

        DeleteShiftCommand command = new DeleteShiftCommand(shiftId);

        assertThrows(IllegalArgumentException.class, () -> shiftCommandService.handle(command));
        verify(shiftRepository, never()).delete(any());
    }
}
