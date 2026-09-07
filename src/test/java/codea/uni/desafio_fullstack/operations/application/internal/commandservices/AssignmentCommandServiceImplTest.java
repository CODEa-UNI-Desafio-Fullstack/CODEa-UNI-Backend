package codea.uni.desafio_fullstack.operations.application.internal.commandservices;

import codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl.ExternalMachineryService;
import codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl.ExternalOperatorService;
import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Assignment;
import codea.uni.desafio_fullstack.operations.domain.model.commands.*;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.AssignmentRepository;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.ShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentCommandServiceImplTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private ExternalMachineryService externalMachineryService;

    @Mock
    private ExternalOperatorService externalOperatorService;

    private AssignmentCommandServiceImpl assignmentCommandService;

    @BeforeEach
    void setUp() {
        assignmentCommandService = new AssignmentCommandServiceImpl(
                assignmentRepository,
                shiftRepository,
                externalMachineryService,
                externalOperatorService
        );
    }

    @Test
    @DisplayName("Should create assignment successfully when all references exist")
    void shouldCreateAssignmentSuccessfullyWhenAllReferencesExist() {
        UUID operatorId = UUID.randomUUID();
        UUID shiftId = UUID.randomUUID();
        CreateAssignmentCommand command = new CreateAssignmentCommand(operatorId, "EQ-001", shiftId);

        when(externalOperatorService.existsOperatorById(operatorId)).thenReturn(true);
        when(externalMachineryService.existsMachineryByCode("EQ-001")).thenReturn(true);
        when(shiftRepository.existsById(shiftId)).thenReturn(true);
        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Assignment> result = assignmentCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals(operatorId, result.get().getOperatorId());
        assertEquals("EQ-001", result.get().getMachineryCode());
        assertEquals(shiftId, result.get().getShiftId());
        verify(assignmentRepository, times(1)).save(any(Assignment.class));
    }

    @Test
    @DisplayName("Should fail to create assignment when operator does not exist")
    void shouldFailToCreateWhenOperatorDoesNotExist() {
        UUID operatorId = UUID.randomUUID();
        UUID shiftId = UUID.randomUUID();
        CreateAssignmentCommand command = new CreateAssignmentCommand(operatorId, "EQ-001", shiftId);

        when(externalOperatorService.existsOperatorById(operatorId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> assignmentCommandService.handle(command));
        verify(assignmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail to create assignment when machinery does not exist")
    void shouldFailToCreateWhenMachineryDoesNotExist() {
        UUID operatorId = UUID.randomUUID();
        UUID shiftId = UUID.randomUUID();
        CreateAssignmentCommand command = new CreateAssignmentCommand(operatorId, "UNKNOWN", shiftId);

        when(externalOperatorService.existsOperatorById(operatorId)).thenReturn(true);
        when(externalMachineryService.existsMachineryByCode("UNKNOWN")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> assignmentCommandService.handle(command));
        verify(assignmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail to create assignment when shift does not exist")
    void shouldFailToCreateWhenShiftDoesNotExist() {
        UUID operatorId = UUID.randomUUID();
        UUID shiftId = UUID.randomUUID();
        CreateAssignmentCommand command = new CreateAssignmentCommand(operatorId, "EQ-001", shiftId);

        when(externalOperatorService.existsOperatorById(operatorId)).thenReturn(true);
        when(externalMachineryService.existsMachineryByCode("EQ-001")).thenReturn(true);
        when(shiftRepository.existsById(shiftId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> assignmentCommandService.handle(command));
        verify(assignmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update assignment successfully when all references exist")
    void shouldUpdateAssignmentSuccessfully() {
        UUID assignmentId = UUID.randomUUID();
        UUID operatorId = UUID.randomUUID();
        UUID shiftId = UUID.randomUUID();
        Assignment assignment = new Assignment(new CreateAssignmentCommand(operatorId, "EQ-001", shiftId));

        UUID newOperatorId = UUID.randomUUID();
        UUID newShiftId = UUID.randomUUID();
        UpdateAssignmentCommand command = new UpdateAssignmentCommand(assignmentId, newOperatorId, "EQ-002", newShiftId);

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(externalOperatorService.existsOperatorById(newOperatorId)).thenReturn(true);
        when(externalMachineryService.existsMachineryByCode("EQ-002")).thenReturn(true);
        when(shiftRepository.existsById(newShiftId)).thenReturn(true);
        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Assignment> result = assignmentCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals(newOperatorId, result.get().getOperatorId());
        assertEquals("EQ-002", result.get().getMachineryCode());
        assertEquals(newShiftId, result.get().getShiftId());
        verify(assignmentRepository, times(1)).save(assignment);
    }

    @Test
    @DisplayName("Should fail to update when assignment does not exist")
    void shouldFailToUpdateWhenAssignmentDoesNotExist() {
        UUID assignmentId = UUID.randomUUID();
        UpdateAssignmentCommand command = new UpdateAssignmentCommand(assignmentId, UUID.randomUUID(), "EQ-001", UUID.randomUUID());

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> assignmentCommandService.handle(command));
        verify(assignmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should start assignment successfully")
    void shouldStartAssignmentSuccessfully() {
        UUID assignmentId = UUID.randomUUID();
        Assignment assignment = new Assignment(new CreateAssignmentCommand(UUID.randomUUID(), "EQ-001", UUID.randomUUID()));
        LocalDateTime start = LocalDateTime.of(2026, 9, 7, 8, 0);

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StartAssignmentCommand command = new StartAssignmentCommand(assignmentId, start);
        Optional<Assignment> result = assignmentCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals(start, result.get().getTimeStart());
        verify(assignmentRepository, times(1)).save(assignment);
    }

    @Test
    @DisplayName("Should fail to start assignment when assignment does not exist")
    void shouldFailToStartWhenAssignmentDoesNotExist() {
        UUID assignmentId = UUID.randomUUID();
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.empty());

        StartAssignmentCommand command = new StartAssignmentCommand(assignmentId, LocalDateTime.now());

        assertThrows(IllegalArgumentException.class, () -> assignmentCommandService.handle(command));
        verify(assignmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should end assignment successfully and calculate worked hours")
    void shouldEndAssignmentSuccessfully() {
        UUID assignmentId = UUID.randomUUID();
        Assignment assignment = new Assignment(new CreateAssignmentCommand(UUID.randomUUID(), "EQ-001", UUID.randomUUID()));
        LocalDateTime start = LocalDateTime.of(2026, 9, 7, 8, 0);
        assignment.startShift(start);

        LocalDateTime end = LocalDateTime.of(2026, 9, 7, 18, 0);

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EndAssignmentCommand command = new EndAssignmentCommand(assignmentId, end);
        Optional<Assignment> result = assignmentCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals(end, result.get().getTimeEnd());
        assertEquals(10, result.get().getActualShiftTime());
        verify(assignmentRepository, times(1)).save(assignment);
    }

    @Test
    @DisplayName("Should fail to end assignment when assignment does not exist")
    void shouldFailToEndWhenAssignmentDoesNotExist() {
        UUID assignmentId = UUID.randomUUID();
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.empty());

        EndAssignmentCommand command = new EndAssignmentCommand(assignmentId, LocalDateTime.now());

        assertThrows(IllegalArgumentException.class, () -> assignmentCommandService.handle(command));
        verify(assignmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete assignment successfully")
    void shouldDeleteAssignmentSuccessfully() {
        UUID assignmentId = UUID.randomUUID();
        Assignment assignment = new Assignment(new CreateAssignmentCommand(UUID.randomUUID(), "EQ-001", UUID.randomUUID()));

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));

        DeleteAssignmentCommand command = new DeleteAssignmentCommand(assignmentId);
        assignmentCommandService.handle(command);

        verify(assignmentRepository, times(1)).delete(assignment);
    }

    @Test
    @DisplayName("Should fail to delete assignment when assignment does not exist")
    void shouldFailToDeleteWhenAssignmentDoesNotExist() {
        UUID assignmentId = UUID.randomUUID();
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.empty());

        DeleteAssignmentCommand command = new DeleteAssignmentCommand(assignmentId);

        assertThrows(IllegalArgumentException.class, () -> assignmentCommandService.handle(command));
        verify(assignmentRepository, never()).delete(any());
    }
}
