package codea.uni.desafio_fullstack.operations.application.internal.queryservices;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Assignment;
import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateAssignmentCommand;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetAssignmentByIdQuery;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.AssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentQueryServiceImplTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    private AssignmentQueryServiceImpl assignmentQueryService;

    @BeforeEach
    void setUp() {
        assignmentQueryService = new AssignmentQueryServiceImpl(assignmentRepository);
    }

    @Test
    @DisplayName("Should get assignment by ID when exists")
    void shouldGetAssignmentByIdWhenExists() {
        UUID assignmentId = UUID.randomUUID();
        Assignment assignment = new Assignment(new CreateAssignmentCommand(UUID.randomUUID(), "EQ-001", UUID.randomUUID()));

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));

        Optional<Assignment> result = assignmentQueryService.handle(new GetAssignmentByIdQuery(assignmentId));

        assertTrue(result.isPresent());
        assertEquals("EQ-001", result.get().getMachineryCode());
        verify(assignmentRepository, times(1)).findById(assignmentId);
    }

    @Test
    @DisplayName("Should return empty when assignment does not exist")
    void shouldReturnEmptyWhenAssignmentDoesNotExist() {
        UUID assignmentId = UUID.randomUUID();
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.empty());

        Optional<Assignment> result = assignmentQueryService.handle(new GetAssignmentByIdQuery(assignmentId));

        assertFalse(result.isPresent());
        verify(assignmentRepository, times(1)).findById(assignmentId);
    }
}
