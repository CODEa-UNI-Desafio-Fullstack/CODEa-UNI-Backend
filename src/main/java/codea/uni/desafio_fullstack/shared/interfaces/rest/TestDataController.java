package codea.uni.desafio_fullstack.shared.interfaces.rest;

import codea.uni.desafio_fullstack.shared.infrastructure.seeder.TestDataSeederService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "api/v1/test-data")
@Tag(name = "Test Data Seeder", description = "Utilitario para resetear y sembrar datos de prueba para casos de borde")
public class TestDataController {

    private final TestDataSeederService testDataSeederService;

    public TestDataController(TestDataSeederService testDataSeederService) {
        this.testDataSeederService = testDataSeederService;
    }

    @Operation(summary = "Resetear y precargar la base de datos con datos de prueba de casos borde")
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetAndSeedDatabase() {
        var summary = this.testDataSeederService.resetAndSeed();
        return ResponseEntity.ok(summary);
    }
}
