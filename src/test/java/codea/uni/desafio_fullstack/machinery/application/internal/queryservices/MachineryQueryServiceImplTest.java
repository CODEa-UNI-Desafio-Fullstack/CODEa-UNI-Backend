package codea.uni.desafio_fullstack.machinery.application.internal.queryservices;

import codea.uni.desafio_fullstack.machinery.domain.model.aggregates.Machinery;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryTypeCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.*;
import codea.uni.desafio_fullstack.machinery.infrastructure.persistence.jpa.repositories.MachineryRepository;
import codea.uni.desafio_fullstack.machinery.infrastructure.persistence.jpa.repositories.MachineryTypeRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MachineryQueryServiceImplTest {

    @Mock
    private MachineryRepository machineryRepository;

    @Mock
    private MachineryTypeRepository machineryTypeRepository;

    @InjectMocks
    private MachineryQueryServiceImpl machineryQueryService;

    private MachineryType type1;
    private MachineryType type2;
    private Machinery m1;
    private Machinery m2;
    private Machinery m3;

    @BeforeEach
    void setUp() {
        type1 = new MachineryType(new CreateMachineryTypeCommand("Camión de Acarreo", 500));
        type1.setId(1);

        type2 = new MachineryType(new CreateMachineryTypeCommand("Excavadora", 600));
        type2.setId(2);

        m1 = new Machinery(new CreateMachineryCommand("CAM-001", 1), type1);
        m1.setState(true); // Active

        m2 = new Machinery(new CreateMachineryCommand("EXC-001", 2), type2);
        m2.setState(false); // Blocked

        m3 = new Machinery(new CreateMachineryCommand("CAM-002", 1), type1);
        m3.setState(false); // Blocked
    }

    @Test
    @DisplayName("Should return all machineries when no filters provided")
    void shouldReturnAll_WhenNoFiltersProvided() {
        when(machineryRepository.findAll()).thenReturn(List.of(m1, m2, m3));

        var query = new GetMachineryByFilterQuery(null, null, null);
        var result = machineryQueryService.handle(query);

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Should filter machineries by state active")
    void shouldFilter_ByStateActive() {
        when(machineryRepository.findAll()).thenReturn(List.of(m1, m2, m3));

        var query = new GetMachineryByFilterQuery(Boolean.TRUE, null, null);
        var result = machineryQueryService.handle(query);

        assertEquals(1, result.size());
        assertEquals("CAM-001", result.get(0).getCode());
        assertTrue(result.get(0).isActive());
    }

    @Test
    @DisplayName("Should filter machineries by state blocked")
    void shouldFilter_ByStateBlocked() {
        when(machineryRepository.findAll()).thenReturn(List.of(m1, m2, m3));

        var query = new GetMachineryByFilterQuery(Boolean.FALSE, null, null);
        var result = machineryQueryService.handle(query);

        assertEquals(2, result.size());
        assertFalse(result.get(0).isActive());
        assertFalse(result.get(1).isActive());
    }

    @Test
    @DisplayName("Should filter machineries by machineryTypeId")
    void shouldFilter_ByMachineryTypeId() {
        when(machineryRepository.findAll()).thenReturn(List.of(m1, m2, m3));

        var query = new GetMachineryByFilterQuery(null, 2, null);
        var result = machineryQueryService.handle(query);

        assertEquals(1, result.size());
        assertEquals("EXC-001", result.get(0).getCode());
    }

    @Test
    @DisplayName("Should filter machineries by code substring")
    void shouldFilter_ByCodeSubstring() {
        when(machineryRepository.findAll()).thenReturn(List.of(m1, m2, m3));

        var query = new GetMachineryByFilterQuery(null, null, "exc");
        var result = machineryQueryService.handle(query);

        assertEquals(1, result.size());
        assertEquals("EXC-001", result.get(0).getCode());
    }

    @Test
    @DisplayName("Should filter machineries combining state and machineryTypeId")
    void shouldFilter_CombinedCriteria() {
        when(machineryRepository.findAll()).thenReturn(List.of(m1, m2, m3));

        // Filter: blocked + type 1 (Camión)
        var query = new GetMachineryByFilterQuery(Boolean.FALSE, 1, "cam");
        var result = machineryQueryService.handle(query);

        assertEquals(1, result.size());
        assertEquals("CAM-002", result.get(0).getCode());
    }

    @Test
    @DisplayName("Should get machinery by code")
    void shouldGetMachineryByCode_WhenExists() {
        when(machineryRepository.findMachineryByCode("CAM-001")).thenReturn(Optional.of(m1));

        var result = machineryQueryService.handle(new GetMachineryByCodeQuery("CAM-001"));

        assertTrue(result.isPresent());
        assertEquals("CAM-001", result.get().getCode());
    }

    @Test
    @DisplayName("Should return empty when machinery by code does not exist")
    void shouldReturnEmpty_WhenMachineryNotFound() {
        when(machineryRepository.findMachineryByCode("UNKNOWN")).thenReturn(Optional.empty());

        var result = machineryQueryService.handle(new GetMachineryByCodeQuery("UNKNOWN"));

        assertTrue(result.isEmpty());
    }
}
