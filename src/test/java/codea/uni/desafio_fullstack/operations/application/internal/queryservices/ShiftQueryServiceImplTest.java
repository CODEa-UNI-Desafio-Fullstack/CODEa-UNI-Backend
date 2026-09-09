package codea.uni.desafio_fullstack.operations.application.internal.queryservices;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Shift;
import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateShiftCommand;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetAllShiftsQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetShiftByIdQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetShiftsByFilterQuery;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.ShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftQueryServiceImplTest {

    @Mock
    private ShiftRepository shiftRepository;

    private ShiftQueryServiceImpl shiftQueryService;

    @BeforeEach
    void setUp() {
        shiftQueryService = new ShiftQueryServiceImpl(shiftRepository);
    }

    @Test
    @DisplayName("Should get all shifts")
    void shouldGetAllShifts() {
        Shift shift1 = new Shift(new CreateShiftCommand(LocalDate.of(2026, 9, 7), true, 8));
        Shift shift2 = new Shift(new CreateShiftCommand(LocalDate.of(2026, 9, 8), false, 12));

        when(shiftRepository.findAll()).thenReturn(List.of(shift1, shift2));

        List<Shift> results = shiftQueryService.handle(new GetAllShiftsQuery());

        assertEquals(2, results.size());
        verify(shiftRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get shift by id")
    void shouldGetShiftById() {
        UUID shiftId = UUID.randomUUID();
        Shift shift = new Shift(new CreateShiftCommand(LocalDate.of(2026, 9, 7), true, 8));

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));

        Optional<Shift> result = shiftQueryService.handle(new GetShiftByIdQuery(shiftId));

        assertTrue(result.isPresent());
        assertEquals(shift.getDate(), result.get().getDate());
        verify(shiftRepository, times(1)).findById(shiftId);
    }

    @Test
    @DisplayName("Should get shifts filtered by date only")
    void shouldGetShiftsFilteredByDateOnly() {
        LocalDate date = LocalDate.of(2026, 9, 7);
        Shift shift = new Shift(new CreateShiftCommand(date, true, 8));

        when(shiftRepository.findAllByDate(date)).thenReturn(List.of(shift));

        List<Shift> results = shiftQueryService.handle(new GetShiftsByFilterQuery(date, null));

        assertEquals(1, results.size());
        assertEquals(date, results.get(0).getDate());
        verify(shiftRepository, times(1)).findAllByDate(date);
    }

    @Test
    @DisplayName("Should get shifts filtered by shift type only")
    void shouldGetShiftsFilteredByShiftTypeOnly() {
        Shift shift = new Shift(new CreateShiftCommand(LocalDate.of(2026, 9, 7), true, 8));

        when(shiftRepository.findAllByShiftType(true)).thenReturn(List.of(shift));

        List<Shift> results = shiftQueryService.handle(new GetShiftsByFilterQuery(null, true));

        assertEquals(1, results.size());
        assertTrue(results.get(0).getShiftType());
        verify(shiftRepository, times(1)).findAllByShiftType(true);
    }

    @Test
    @DisplayName("Should get shifts filtered by both date and shift type")
    void shouldGetShiftsFilteredByBothDateAndShiftType() {
        LocalDate date = LocalDate.of(2026, 9, 7);
        Shift shift = new Shift(new CreateShiftCommand(date, true, 8));

        when(shiftRepository.findAllByDateAndShiftType(date, true)).thenReturn(List.of(shift));

        List<Shift> results = shiftQueryService.handle(new GetShiftsByFilterQuery(date, true));

        assertEquals(1, results.size());
        assertEquals(date, results.get(0).getDate());
        assertTrue(results.get(0).getShiftType());
        verify(shiftRepository, times(1)).findAllByDateAndShiftType(date, true);
    }

    @Test
    @DisplayName("Should get all shifts when both filter parameters are null")
    void shouldGetAllShiftsWhenBothFilterParametersAreNull() {
        Shift shift1 = new Shift(new CreateShiftCommand(LocalDate.of(2026, 9, 7), true, 8));
        when(shiftRepository.findAll()).thenReturn(List.of(shift1));

        List<Shift> results = shiftQueryService.handle(new GetShiftsByFilterQuery(null, null));

        assertEquals(1, results.size());
        verify(shiftRepository, times(1)).findAll();
    }
}
