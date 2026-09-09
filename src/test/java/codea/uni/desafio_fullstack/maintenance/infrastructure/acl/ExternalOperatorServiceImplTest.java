package codea.uni.desafio_fullstack.maintenance.infrastructure.acl;

import codea.uni.desafio_fullstack.operators.interfaces.acl.OperatorContextFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalOperatorServiceImplTest {

    @Mock
    private OperatorContextFacade operatorContextFacade;

    private ExternalOperatorServiceImpl externalOperatorService;

    @BeforeEach
    void setUp() {
        externalOperatorService = new ExternalOperatorServiceImpl(operatorContextFacade);
    }

    @Test
    @DisplayName("Should return true when operator exists in Operator context")
    void shouldReturnTrueWhenOperatorExists() {
        UUID operatorId = UUID.randomUUID();
        when(operatorContextFacade.existsById(operatorId)).thenReturn(true);

        boolean exists = externalOperatorService.existsOperatorById(operatorId);

        assertTrue(exists);
        verify(operatorContextFacade, times(1)).existsById(operatorId);
    }

    @Test
    @DisplayName("Should return false when operator does not exist in Operator context")
    void shouldReturnFalseWhenOperatorDoesNotExist() {
        UUID operatorId = UUID.randomUUID();
        when(operatorContextFacade.existsById(operatorId)).thenReturn(false);

        boolean exists = externalOperatorService.existsOperatorById(operatorId);

        assertFalse(exists);
        verify(operatorContextFacade, times(1)).existsById(operatorId);
    }
}
