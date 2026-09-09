package codea.uni.desafio_fullstack.machinery.application.internal.commandservices;

import codea.uni.desafio_fullstack.machinery.domain.model.aggregates.Machinery;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryTypeCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.RecordMachineryWorkedHoursCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.ResetMachineryMaintenanceCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;
import codea.uni.desafio_fullstack.machinery.infrastructure.persistence.jpa.repositories.MachineryRepository;
import codea.uni.desafio_fullstack.machinery.infrastructure.persistence.jpa.repositories.MachineryTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MachineryCommandServiceImplTest {

    @Mock
    private MachineryRepository machineryRepository;

    @Mock
    private MachineryTypeRepository machineryTypeRepository;

    @InjectMocks
    private MachineryCommandServiceImpl machineryCommandService;

    private Machinery machinery;
    private MachineryType machineryType;

    @BeforeEach
    void setUp() {
        var typeCmd = new CreateMachineryTypeCommand("Perforadora", 100);
        machineryType = new MachineryType(typeCmd);

        var machCmd = new CreateMachineryCommand("PRF-101", 1);
        machinery = new Machinery(machCmd, machineryType);
    }

    @Test
    @DisplayName("handle(RecordMachineryWorkedHoursCommand) should increment hours, persist and block if threshold reached")
    void handle_RecordWorkedHours_ShouldBlockWhenReachingThreshold() {
        when(machineryRepository.findByCodeWithLock("PRF-101")).thenReturn(Optional.of(machinery));
        when(machineryRepository.save(any(Machinery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var command = new RecordMachineryWorkedHoursCommand("PRF-101", 105.0f);
        var result = machineryCommandService.handle(command);

        assertTrue(result.isPresent());
        var updated = result.get();
        assertEquals(105.0f, updated.getHourMeter());
        assertTrue(updated.isBlocked());
        assertFalse(updated.isActive());

        verify(machineryRepository).findByCodeWithLock("PRF-101");
        verify(machineryRepository).save(machinery);
    }

    @Test
    @DisplayName("handle(ResetMachineryMaintenanceCommand) should reset hour meter to 0 and activate equipment")
    void handle_ResetMachineryMaintenance_ShouldResetAndUnblock() {
        // Maquinaria inicialmente bloqueada
        machinery.recordWorkedHours(120.0f);
        assertTrue(machinery.isBlocked());

        when(machineryRepository.findById("PRF-101")).thenReturn(Optional.of(machinery));
        when(machineryRepository.save(any(Machinery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var command = new ResetMachineryMaintenanceCommand("PRF-101");
        var result = machineryCommandService.handle(command);

        assertTrue(result.isPresent());
        var updated = result.get();
        assertEquals(0.0f, updated.getHourMeter());
        assertTrue(updated.isActive());
        assertFalse(updated.isBlocked());

        verify(machineryRepository).findById("PRF-101");
        verify(machineryRepository).save(machinery);
    }
}
