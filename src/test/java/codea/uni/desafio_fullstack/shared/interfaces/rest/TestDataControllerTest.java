package codea.uni.desafio_fullstack.shared.interfaces.rest;

import codea.uni.desafio_fullstack.shared.infrastructure.seeder.TestDataSeederService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestDataControllerTest {

    @Mock
    private TestDataSeederService testDataSeederService;

    @InjectMocks
    private TestDataController testDataController;

    @Test
    @DisplayName("POST /api/v1/test-data/reset should delegate to TestDataSeederService and return OK status")
    void resetAndSeedDatabase_ShouldReturnOk() {
        when(testDataSeederService.resetAndSeed()).thenReturn(Map.of("message", "Base de datos reseteada exitosamente"));

        var response = testDataController.resetAndSeedDatabase();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
        verify(testDataSeederService, times(1)).resetAndSeed();
    }
}
