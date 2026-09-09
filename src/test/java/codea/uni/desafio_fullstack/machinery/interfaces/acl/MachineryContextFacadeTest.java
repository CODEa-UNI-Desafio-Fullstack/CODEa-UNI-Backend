package codea.uni.desafio_fullstack.machinery.interfaces.acl;

import codea.uni.desafio_fullstack.machinery.domain.model.aggregates.Machinery;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryTypeCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.RecordMachineryWorkedHoursCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.ResetMachineryMaintenanceCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetAllMachineryQuery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetMachineryByCodeQuery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetMachineryTypeByIdQuery;
import codea.uni.desafio_fullstack.machinery.domain.services.MachineryCommandService;
import codea.uni.desafio_fullstack.machinery.domain.services.MachineryQueryService;
import codea.uni.desafio_fullstack.machinery.domain.services.MachineryTypeQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MachineryContextFacadeTest {

    @Mock
    private MachineryCommandService machineryCommandService;

    @Mock
    private MachineryQueryService machineryQueryService;

    @Mock
    private MachineryTypeQueryService machineryTypeQueryService;

    @InjectMocks
    private MachineryContextFacadeImpl machineryContextFacade;

    private Machinery machinery;
    private MachineryType machineryType;

    @BeforeEach
    void setUp() {
        var typeCmd = new CreateMachineryTypeCommand("Camión de Acarreo", 500);
        machineryType = new MachineryType(typeCmd);
        machineryType.setId(10);

        var machCmd = new CreateMachineryCommand("CAM-001", 10);
        machinery = new Machinery(machCmd, machineryType);
    }

    @Test
    @DisplayName("isMachineryActive should return true when machinery exists and is active")
    void isMachineryActive_WhenActive_ReturnsTrue() {
        when(machineryQueryService.handle(any(GetMachineryByCodeQuery.class))).thenReturn(Optional.of(machinery));

        assertTrue(machineryContextFacade.isMachineryActive("CAM-001"));
        assertFalse(!machineryContextFacade.isMachineryActive("CAM-001"));
    }

    @Test
    @DisplayName("isMachineryBlocked should return true when machinery has reached maintenance threshold")
    void isMachineryBlocked_WhenBlocked_ReturnsTrue() {
        machinery.recordWorkedHours(500.0f);
        when(machineryQueryService.handle(any(GetMachineryByCodeQuery.class))).thenReturn(Optional.of(machinery));

        assertTrue(!machineryContextFacade.isMachineryActive("CAM-001"));
        assertFalse(machineryContextFacade.isMachineryActive("CAM-001"));
    }

    @Test
    @DisplayName("getMachineryTypeId should return the type id for operator certification validation")
    void getMachineryTypeId_ReturnsCorrectId() {
        when(machineryQueryService.handle(any(GetMachineryByCodeQuery.class))).thenReturn(Optional.of(machinery));

        var typeId = machineryContextFacade.getMachineryTypeId("CAM-001");

        assertTrue(typeId.isPresent());
        assertEquals(10, typeId.get());
    }

    @Test
    @DisplayName("recordWorkedHours should return MachineryWorkedHoursResult with auto-block indication")
    void recordWorkedHours_ShouldReturnWorkedHoursResult() {
        var activeMachinery = new Machinery(new CreateMachineryCommand("CAM-001", 10), machineryType);

        var blockedMachinery = new Machinery(new CreateMachineryCommand("CAM-001", 10), machineryType);
        blockedMachinery.recordWorkedHours(505.0f);

        when(machineryQueryService.handle(any(GetMachineryByCodeQuery.class))).thenReturn(Optional.of(activeMachinery));
        when(machineryCommandService.handle(any(RecordMachineryWorkedHoursCommand.class)))
                .thenReturn(Optional.of(blockedMachinery));

        var result = machineryContextFacade.recordWorkedHours("CAM-001", 505.0f);

        assertEquals("CAM-001", result.code());
        assertEquals(505.0f, result.updatedHourMeter());
        assertTrue(result.blocked());
        assertTrue(result.blockedByThisOperation());
    }


    @Test
    @DisplayName("resetMachineryAfterMaintenance should delegate to command service")
    void resetMachineryAfterMaintenance_ShouldDelegate() {
        machineryContextFacade.resetMachineryAfterMaintenance("CAM-001");

        verify(machineryCommandService).handle(any(ResetMachineryMaintenanceCommand.class));
    }

    @Test
    @DisplayName("getAllMachineriesForProjection should return immutable summary records for 7-day projection")
    void getAllMachineriesForProjection_ReturnsSummaryRecords() {
        machinery.recordWorkedHours(400.0f);
        when(machineryQueryService.handle(any(GetAllMachineryQuery.class))).thenReturn(List.of(machinery));

        var summaries = machineryContextFacade.getAllMachineriesForProjection();

        assertNotNull(summaries);
        assertEquals(1, summaries.size());
        var summary = summaries.get(0);
        assertEquals("CAM-001", summary.code());
        assertEquals(10, summary.machineryTypeId());
        assertEquals("Camión de Acarreo", summary.machineryTypeName());
        assertEquals(400.0f, summary.currentHourMeter());
        assertEquals(500, summary.maintenanceThreshold());
        assertEquals(100.0f, summary.remainingHoursToMaintenance());
        assertTrue(summary.active());
        assertFalse(summary.blocked());
    }

    @Test
    @DisplayName("existsMachineryTypeById should return true when machinery type exists")
    void existsMachineryTypeById_WhenExists_ReturnsTrue() {
        when(machineryTypeQueryService.handle(any(GetMachineryTypeByIdQuery.class))).thenReturn(Optional.of(machineryType));

        assertTrue(machineryContextFacade.existsMachineryTypeById(10));
        verify(machineryTypeQueryService).handle(any(GetMachineryTypeByIdQuery.class));
    }

    @Test
    @DisplayName("existsMachineryTypeById should return false when id is null or not found")
    void existsMachineryTypeById_WhenNotFoundOrNull_ReturnsFalse() {
        assertFalse(machineryContextFacade.existsMachineryTypeById(null));

        when(machineryTypeQueryService.handle(any(GetMachineryTypeByIdQuery.class))).thenReturn(Optional.empty());
        assertFalse(machineryContextFacade.existsMachineryTypeById(999));
    }
}
