package codea.uni.desafio_fullstack.shared.infrastructure.seeder;

import codea.uni.desafio_fullstack.machinery.domain.model.aggregates.Machinery;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryTypeCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;
import codea.uni.desafio_fullstack.machinery.infrastructure.persistence.jpa.repositories.MachineryRepository;
import codea.uni.desafio_fullstack.machinery.infrastructure.persistence.jpa.repositories.MachineryTypeRepository;
import codea.uni.desafio_fullstack.maintenance.domain.model.aggregates.Maintenance;
import codea.uni.desafio_fullstack.maintenance.infrastructure.persistence.jpa.repositories.MaintenanceRepository;
import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Assignment;
import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Shift;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.AssignmentRepository;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.ShiftRepository;
import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import codea.uni.desafio_fullstack.operators.domain.model.entities.MachineryCertification;
import codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories.MachineryCertificationRepository;
import codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories.OperatorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestDataSeederServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;
    @Mock
    private MaintenanceRepository maintenanceRepository;
    @Mock
    private MachineryCertificationRepository machineryCertificationRepository;
    @Mock
    private ShiftRepository shiftRepository;
    @Mock
    private OperatorRepository operatorRepository;
    @Mock
    private MachineryRepository machineryRepository;
    @Mock
    private MachineryTypeRepository machineryTypeRepository;
    @Mock
    private jakarta.persistence.EntityManager entityManager;

    private TestDataSeederService testDataSeederService;

    @BeforeEach
    void setUp() {
        testDataSeederService = new TestDataSeederService(
                assignmentRepository,
                maintenanceRepository,
                machineryCertificationRepository,
                shiftRepository,
                operatorRepository,
                machineryRepository,
                machineryTypeRepository,
                entityManager
        );
    }

    @Test
    @DisplayName("resetAndSeed should delete all existing records in order and seed new test dataset")
    void resetAndSeed_ShouldCleanAndSeed() {
        // Setup mock answers to simulate ID generation
        when(machineryTypeRepository.save(any(MachineryType.class))).thenAnswer(inv -> {
            MachineryType mt = inv.getArgument(0);
            mt.setId(1);
            return mt;
        });

        when(operatorRepository.save(any(Operator.class))).thenAnswer(inv -> {
            Operator op = inv.getArgument(0);
            op.setId(UUID.randomUUID());
            return op;
        });

        when(shiftRepository.save(any(Shift.class))).thenAnswer(inv -> {
            Shift s = inv.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(inv -> {
            Assignment a = inv.getArgument(0);
            if (a.getId() == null) {
                a.setId(UUID.randomUUID());
            }
            return a;
        });

        when(machineryRepository.save(any(Machinery.class))).thenAnswer(inv -> inv.getArgument(0));
        when(machineryCertificationRepository.save(any(MachineryCertification.class))).thenAnswer(inv -> inv.getArgument(0));
        when(maintenanceRepository.save(any(Maintenance.class))).thenAnswer(inv -> inv.getArgument(0));

        var summary = testDataSeederService.resetAndSeed();

        // 1. Verify deletion order with batch deletion and persistence context sync
        verify(assignmentRepository, times(1)).deleteAllInBatch();
        verify(maintenanceRepository, times(1)).deleteAllInBatch();
        verify(machineryCertificationRepository, times(1)).deleteAllInBatch();
        verify(shiftRepository, times(1)).deleteAllInBatch();
        verify(operatorRepository, times(1)).deleteAllInBatch();
        verify(machineryRepository, times(1)).deleteAllInBatch();
        verify(machineryTypeRepository, times(1)).deleteAllInBatch();
        verify(entityManager, times(1)).flush();
        verify(entityManager, times(1)).clear();

        // 2. Verify creation of entities
        verify(machineryTypeRepository, atLeast(3)).save(any(MachineryType.class));
        verify(machineryRepository, atLeast(4)).save(any(Machinery.class));
        verify(operatorRepository, atLeast(3)).save(any(Operator.class));
        verify(machineryCertificationRepository, atLeast(4)).save(any(MachineryCertification.class));
        verify(shiftRepository, atLeast(2)).save(any(Shift.class));
        verify(assignmentRepository, atLeast(2)).save(any(Assignment.class));
        verify(maintenanceRepository, atLeast(1)).save(any(Maintenance.class));

        // 3. Verify returned payload
        assertNotNull(summary);
        assertTrue(summary.containsKey("message"));
        assertTrue(summary.containsKey("testScenariosAvailable"));
        assertTrue(summary.containsKey("entities"));
    }
}
