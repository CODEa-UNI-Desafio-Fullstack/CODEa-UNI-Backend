package codea.uni.desafio_fullstack.machinery.interfaces.rest;

import codea.uni.desafio_fullstack.machinery.domain.model.aggregates.Machinery;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryTypeCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetMachineryByCodeQuery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetMachineryByFilterQuery;
import codea.uni.desafio_fullstack.machinery.domain.services.MachineryCommandService;
import codea.uni.desafio_fullstack.machinery.domain.services.MachineryQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MachineryControllerTest {

    @Mock
    private MachineryCommandService machineryCommandService;

    @Mock
    private MachineryQueryService machineryQueryService;

    @InjectMocks
    private MachineryController machineryController;

    private Machinery machinery;

    @BeforeEach
    void setUp() {
        var type = new MachineryType(new CreateMachineryTypeCommand("Camión de Acarreo", 500));
        type.setId(1);
        machinery = new Machinery(new CreateMachineryCommand("CAM-001", 1), type);
    }

    @Test
    @DisplayName("getAllMachinery should return list of resources with filters")
    void getAllMachinery_ShouldReturnFilteredList() {
        when(machineryQueryService.handle(any(GetMachineryByFilterQuery.class)))
                .thenReturn(List.of(machinery));

        var response = machineryController.getAllMachinery(Boolean.TRUE, 1, "CAM");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("CAM-001", response.getBody().get(0).code());
        assertEquals("ACTIVE", response.getBody().get(0).state());
    }

    @Test
    @DisplayName("getMachineryByCode should return 200 when found")
    void getMachineryByCode_WhenFound_ShouldReturnOk() {
        when(machineryQueryService.handle(any(GetMachineryByCodeQuery.class)))
                .thenReturn(Optional.of(machinery));

        var response = machineryController.getMachineryByCode("CAM-001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CAM-001", response.getBody().code());
    }

    @Test
    @DisplayName("getMachineryByCode should return 404 when not found")
    void getMachineryByCode_WhenNotFound_ShouldReturnNotFound() {
        when(machineryQueryService.handle(any(GetMachineryByCodeQuery.class)))
                .thenReturn(Optional.empty());

        var response = machineryController.getMachineryByCode("UNKNOWN");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
