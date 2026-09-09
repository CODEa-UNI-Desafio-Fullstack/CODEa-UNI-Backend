package codea.uni.desafio_fullstack.maintenance.application.internal.commandservices;

import codea.uni.desafio_fullstack.maintenance.application.internal.outboundservices.acl.ExternalMachineryService;
import codea.uni.desafio_fullstack.maintenance.application.internal.outboundservices.acl.ExternalOperatorService;
import codea.uni.desafio_fullstack.maintenance.domain.model.aggregates.Maintenance;
import codea.uni.desafio_fullstack.maintenance.domain.model.commands.CreateMaintenanceCommand;
import codea.uni.desafio_fullstack.maintenance.domain.model.commands.DeleteMaintenanceCommand;
import codea.uni.desafio_fullstack.maintenance.domain.model.commands.UpdateMaintenanceCommand;
import codea.uni.desafio_fullstack.maintenance.infrastructure.persistence.jpa.repositories.MaintenanceRepository;
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
class MaintenanceCommandServiceImplTest {

    @Mock
    private MaintenanceRepository maintenanceRepository;

    @Mock
    private ExternalMachineryService externalMachineryService;

    @Mock
    private ExternalOperatorService externalOperatorService;

    private MaintenanceCommandServiceImpl maintenanceCommandService;

    @BeforeEach
    void setUp() {
        maintenanceCommandService = new MaintenanceCommandServiceImpl(
                maintenanceRepository,
                externalMachineryService,
                externalOperatorService
        );
    }

    @Test
    @DisplayName("Should create maintenance successfully when machinery and operator exist")
    void shouldCreateMaintenanceSuccessfullyWhenMachineryAndOperatorExist() {
        UUID operatorId = UUID.randomUUID();
        CreateMaintenanceCommand command = new CreateMaintenanceCommand(
                "EQ-001",
                LocalDate.of(2026, 9, 6),
                300.0f,
                operatorId,
                "Oil change"
        );

        when(externalMachineryService.existsMachineryByCode("EQ-001")).thenReturn(true);
        when(externalOperatorService.existsOperatorById(operatorId)).thenReturn(true);
        when(maintenanceRepository.save(any(Maintenance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = maintenanceCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals("EQ-001", result.get().getMachineryCode());
        assertEquals(300.0f, result.get().getHourMeter());
        assertEquals(operatorId, result.get().getOperatorId());
        verify(maintenanceRepository, times(1)).save(any(Maintenance.class));
    }

    @Test
    @DisplayName("Should fail to create maintenance when machinery does not exist")
    void shouldFailToCreateMaintenanceWhenMachineryDoesNotExist() {
        UUID operatorId = UUID.randomUUID();
        CreateMaintenanceCommand command = new CreateMaintenanceCommand(
                "NON-EXISTENT",
                LocalDate.of(2026, 9, 6),
                300.0f,
                operatorId,
                "Oil change"
        );

        when(externalMachineryService.existsMachineryByCode("NON-EXISTENT")).thenReturn(false);

        var exception = assertThrows(IllegalArgumentException.class, () ->
                maintenanceCommandService.handle(command));
        assertTrue(exception.getMessage().contains("Machinery with code NON-EXISTENT does not exist"));
        verify(maintenanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail to create maintenance when operator does not exist")
    void shouldFailToCreateMaintenanceWhenOperatorDoesNotExist() {
        UUID operatorId = UUID.randomUUID();
        CreateMaintenanceCommand command = new CreateMaintenanceCommand(
                "EQ-001",
                LocalDate.of(2026, 9, 6),
                300.0f,
                operatorId,
                "Oil change"
        );

        when(externalMachineryService.existsMachineryByCode("EQ-001")).thenReturn(true);
        when(externalOperatorService.existsOperatorById(operatorId)).thenReturn(false);

        var exception = assertThrows(IllegalArgumentException.class, () ->
                maintenanceCommandService.handle(command));
        assertTrue(exception.getMessage().contains("Operator with id " + operatorId + " does not exist"));
        verify(maintenanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update maintenance successfully when maintenance and operator exist")
    void shouldUpdateMaintenanceSuccessfullyWhenMaintenanceAndOperatorExist() {
        UUID maintenanceId = UUID.randomUUID();
        UUID oldOperatorId = UUID.randomUUID();
        UUID newOperatorId = UUID.randomUUID();

        CreateMaintenanceCommand createCommand = new CreateMaintenanceCommand(
                "EQ-001",
                LocalDate.of(2026, 9, 6),
                200.0f,
                oldOperatorId,
                "Initial note"
        );
        Maintenance existingMaintenance = new Maintenance(createCommand);

        UpdateMaintenanceCommand updateCommand = new UpdateMaintenanceCommand(
                maintenanceId,
                250.0f,
                newOperatorId,
                "Updated note"
        );

        when(maintenanceRepository.findById(maintenanceId)).thenReturn(Optional.of(existingMaintenance));
        when(externalOperatorService.existsOperatorById(newOperatorId)).thenReturn(true);
        when(maintenanceRepository.save(any(Maintenance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = maintenanceCommandService.handle(updateCommand);

        assertTrue(result.isPresent());
        assertEquals(250.0f, result.get().getHourMeter());
        assertEquals(newOperatorId, result.get().getOperatorId());
        assertEquals("Updated note", result.get().getObservation());
        verify(maintenanceRepository, times(1)).save(existingMaintenance);
    }

    @Test
    @DisplayName("Should fail to update maintenance when maintenance does not exist")
    void shouldFailToUpdateMaintenanceWhenMaintenanceDoesNotExist() {
        UUID maintenanceId = UUID.randomUUID();
        UUID operatorId = UUID.randomUUID();

        UpdateMaintenanceCommand updateCommand = new UpdateMaintenanceCommand(
                maintenanceId,
                250.0f,
                operatorId,
                "Updated note"
        );

        when(maintenanceRepository.findById(maintenanceId)).thenReturn(Optional.empty());

        var exception = assertThrows(IllegalArgumentException.class, () ->
                maintenanceCommandService.handle(updateCommand));
        assertTrue(exception.getMessage().contains("Maintenance with id " + maintenanceId + " does not exist"));
        verify(maintenanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail to update maintenance when operator does not exist")
    void shouldFailToUpdateMaintenanceWhenOperatorDoesNotExist() {
        UUID maintenanceId = UUID.randomUUID();
        UUID oldOperatorId = UUID.randomUUID();
        UUID newOperatorId = UUID.randomUUID();

        CreateMaintenanceCommand createCommand = new CreateMaintenanceCommand(
                "EQ-001",
                LocalDate.of(2026, 9, 6),
                200.0f,
                oldOperatorId,
                "Initial note"
        );
        Maintenance existingMaintenance = new Maintenance(createCommand);

        UpdateMaintenanceCommand updateCommand = new UpdateMaintenanceCommand(
                maintenanceId,
                250.0f,
                newOperatorId,
                "Updated note"
        );

        when(maintenanceRepository.findById(maintenanceId)).thenReturn(Optional.of(existingMaintenance));
        when(externalOperatorService.existsOperatorById(newOperatorId)).thenReturn(false);

        var exception = assertThrows(IllegalArgumentException.class, () ->
                maintenanceCommandService.handle(updateCommand));
        assertTrue(exception.getMessage().contains("Operator with id " + newOperatorId + " does not exist"));
        verify(maintenanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete maintenance successfully")
    void shouldDeleteMaintenanceSuccessfully() {
        UUID maintenanceId = UUID.randomUUID();
        DeleteMaintenanceCommand command = new DeleteMaintenanceCommand(maintenanceId);

        when(maintenanceRepository.existsById(maintenanceId)).thenReturn(true);

        assertDoesNotThrow(() -> maintenanceCommandService.handle(command));
        verify(maintenanceRepository, times(1)).deleteById(maintenanceId);
    }

    @Test
    @DisplayName("Should fail to delete maintenance when maintenance does not exist")
    void shouldFailToDeleteMaintenanceWhenMaintenanceDoesNotExist() {
        UUID maintenanceId = UUID.randomUUID();
        DeleteMaintenanceCommand command = new DeleteMaintenanceCommand(maintenanceId);

        when(maintenanceRepository.existsById(maintenanceId)).thenReturn(false);

        var exception = assertThrows(IllegalArgumentException.class, () ->
                maintenanceCommandService.handle(command));
        assertTrue(exception.getMessage().contains("Maintenance with id " + maintenanceId + " does not exist"));
        verify(maintenanceRepository, never()).deleteById(any());
    }
}
