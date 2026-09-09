package codea.uni.desafio_fullstack.operations.interfaces.rest;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Shift;
import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateShiftCommand;
import codea.uni.desafio_fullstack.operations.domain.model.commands.DeleteShiftCommand;
import codea.uni.desafio_fullstack.operations.domain.model.commands.UpdateShiftCommand;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetShiftByIdQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetShiftsByFilterQuery;
import codea.uni.desafio_fullstack.operations.domain.services.ShiftCommandService;
import codea.uni.desafio_fullstack.operations.domain.services.ShiftQueryService;
import codea.uni.desafio_fullstack.operations.interfaces.rest.resources.CreateShiftResource;
import codea.uni.desafio_fullstack.operations.interfaces.rest.resources.UpdateShiftResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftControllerTest {

    @Mock
    private ShiftCommandService shiftCommandService;

    @Mock
    private ShiftQueryService shiftQueryService;

    @InjectMocks
    private ShiftController shiftController;

    private Shift shift;
    private UUID shiftId;
    private LocalDate shiftDate;

    @BeforeEach
    void setUp() {
        shiftId = UUID.randomUUID();
        shiftDate = LocalDate.of(2026, 9, 7);
        shift = new Shift(new CreateShiftCommand(shiftDate, true, 8));
        shift.setId(shiftId);
    }

    @Test
    @DisplayName("getAllShifts should return list of resources with optional filters")
    void getAllShifts_ShouldReturnFilteredList() {
        when(shiftQueryService.handle(any(GetShiftsByFilterQuery.class)))
                .thenReturn(List.of(shift));

        var response = shiftController.getAllShifts(shiftDate, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(shiftDate, response.getBody().get(0).date());
        assertEquals("Dia", response.getBody().get(0).shiftType());
    }

    @Test
    @DisplayName("getShiftById should return 200 when found")
    void getShiftById_WhenFound_ShouldReturnOk() {
        when(shiftQueryService.handle(any(GetShiftByIdQuery.class)))
                .thenReturn(Optional.of(shift));

        var response = shiftController.getShiftById(shiftId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(shiftId, response.getBody().id());
        assertEquals(shiftDate, response.getBody().date());
    }

    @Test
    @DisplayName("getShiftById should return 404 when not found")
    void getShiftById_WhenNotFound_ShouldReturnNotFound() {
        when(shiftQueryService.handle(any(GetShiftByIdQuery.class)))
                .thenReturn(Optional.empty());

        var response = shiftController.getShiftById(UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("registerShift should return 201 when successful")
    void registerShift_WhenSuccessful_ShouldReturnCreated() {
        when(shiftCommandService.handle(any(CreateShiftCommand.class)))
                .thenReturn(Optional.of(shift));

        var resource = new CreateShiftResource(shiftDate, true, 8);
        var response = shiftController.registerShift(resource);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(shiftDate, response.getBody().date());
    }

    @Test
    @DisplayName("updateShift should return 200 when successful")
    void updateShift_WhenSuccessful_ShouldReturnOk() {
        when(shiftCommandService.handle(any(UpdateShiftCommand.class)))
                .thenReturn(Optional.of(shift));

        var resource = new UpdateShiftResource(shiftDate, false, 10);
        var response = shiftController.updateShift(shiftId, resource);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("deleteShift should return 200 when successful")
    void deleteShift_WhenSuccessful_ShouldReturnOk() {
        doNothing().when(shiftCommandService).handle(any(DeleteShiftCommand.class));

        var response = shiftController.deleteShift(shiftId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().message().contains("deleted successfully"));
        verify(shiftCommandService).handle(any(DeleteShiftCommand.class));
    }
}
