package codea.uni.desafio_fullstack.maintenance.infrastructure.acl;

import codea.uni.desafio_fullstack.machinery.interfaces.acl.MachineryContextFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalMachineryServiceImplTest {

    @Mock
    private MachineryContextFacade machineryContextFacade;

    private ExternalMachineryServiceImpl externalMachineryService;

    @BeforeEach
    void setUp() {
        externalMachineryService = new ExternalMachineryServiceImpl(machineryContextFacade);
    }

    @Test
    @DisplayName("Should return true when machinery exists in Machinery context")
    void shouldReturnTrueWhenMachineryExists() {
        when(machineryContextFacade.existsByCode("EQ-001")).thenReturn(true);

        boolean exists = externalMachineryService.existsMachineryByCode("EQ-001");

        assertTrue(exists);
        verify(machineryContextFacade, times(1)).existsByCode("EQ-001");
    }

    @Test
    @DisplayName("Should return false when machinery does not exist in Machinery context")
    void shouldReturnFalseWhenMachineryDoesNotExist() {
        when(machineryContextFacade.existsByCode("UNKNOWN")).thenReturn(false);

        boolean exists = externalMachineryService.existsMachineryByCode("UNKNOWN");

        assertFalse(exists);
        verify(machineryContextFacade, times(1)).existsByCode("UNKNOWN");
    }

    @Test
    @DisplayName("Should return all machineries from MachineryContextFacade")
    void shouldReturnAllMachineries() {
        var summary = new codea.uni.desafio_fullstack.machinery.interfaces.acl.records.MachinerySummaryRecord(
                "CAM-001", 1, "Camión de Acarreo", 100.0f, 500, true, false, 400.0f
        );
        when(machineryContextFacade.getAllMachineriesForProjection()).thenReturn(java.util.List.of(summary));

        var result = externalMachineryService.getAllMachineries();

        org.junit.jupiter.api.Assertions.assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertEquals(1, result.size());
        org.junit.jupiter.api.Assertions.assertEquals("CAM-001", result.get(0).code());
        verify(machineryContextFacade, times(1)).getAllMachineriesForProjection();
    }
}
