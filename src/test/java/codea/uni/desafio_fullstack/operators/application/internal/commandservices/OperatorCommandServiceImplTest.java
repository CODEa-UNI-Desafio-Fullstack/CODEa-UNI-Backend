package codea.uni.desafio_fullstack.operators.application.internal.commandservices;

import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import codea.uni.desafio_fullstack.operators.domain.model.commands.CreateOperatorCommand;
import codea.uni.desafio_fullstack.operators.domain.model.commands.UpdateOperatorNameCommand;
import codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories.OperatorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperatorCommandServiceImplTest {

    @Mock
    private OperatorRepository operatorRepository;

    @InjectMocks
    private OperatorCommandServiceImpl operatorCommandService;

    @Test
    @DisplayName("handle(CreateOperatorCommand) should persist and return new operator")
    void handle_CreateOperator_ShouldPersistAndReturnOperator() {
        when(operatorRepository.save(any(Operator.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var command = new CreateOperatorCommand("Roberto Gomez");
        var result = operatorCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals("Roberto Gomez", result.get().getName());
        verify(operatorRepository).save(any(Operator.class));
    }

    @Test
    @DisplayName("handle(UpdateOperatorNameCommand) should update name and persist when operator exists")
    void handle_UpdateOperatorName_ShouldUpdateAndPersist() {
        var id = UUID.randomUUID();
        var existingOperator = new Operator("Roberto Gomez");
        existingOperator.setId(id);

        when(operatorRepository.findById(id)).thenReturn(Optional.of(existingOperator));
        when(operatorRepository.save(any(Operator.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var command = new UpdateOperatorNameCommand(id, "Roberto Gomez Bolaños");
        var result = operatorCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals("Roberto Gomez Bolaños", result.get().getName());
        verify(operatorRepository).findById(id);
        verify(operatorRepository).save(existingOperator);
    }

    @Test
    @DisplayName("handle(UpdateOperatorNameCommand) should throw IllegalArgumentException when operator not found")
    void handle_UpdateOperatorName_WhenNotFound_ThrowsException() {
        var id = UUID.randomUUID();
        when(operatorRepository.findById(id)).thenReturn(Optional.empty());

        var command = new UpdateOperatorNameCommand(id, "Nuevo Nombre");

        assertThrows(IllegalArgumentException.class, () -> operatorCommandService.handle(command));
        verify(operatorRepository).findById(id);
        verify(operatorRepository, never()).save(any());
    }
}
