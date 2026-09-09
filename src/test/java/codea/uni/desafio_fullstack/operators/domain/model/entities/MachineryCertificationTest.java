package codea.uni.desafio_fullstack.operators.domain.model.entities;

import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MachineryCertificationTest {

    private Operator operator;
    private UUID operatorId;

    @BeforeEach
    void setUp() {
        operatorId = UUID.randomUUID();
        operator = new Operator("Juan Perez");
        operator.setId(operatorId);
    }

    @Test
    @DisplayName("Should create certification successfully with valid parameters")
    void shouldCreateCertification_WithValidParameters() {
        var expiration = LocalDate.of(2027, 12, 31);
        var certification = new MachineryCertification(operator, 1, expiration);

        assertNotNull(certification.getId());
        assertEquals(operatorId, certification.getId().getOperatorId());
        assertEquals(1, certification.getId().getMachineryType());
        assertEquals(expiration, certification.getExpirationDate());
        assertEquals(operator, certification.getOperator());
    }

    @Test
    @DisplayName("Should validate expiration status correctly based on reference date")
    void shouldValidateExpirationStatus_Correctly() {
        var expiration = LocalDate.of(2026, 10, 15);
        var certification = new MachineryCertification(operator, 1, expiration);

        // Before expiration: valid
        assertTrue(certification.isValidOn(LocalDate.of(2026, 10, 14)));
        assertFalse(certification.isExpiredOn(LocalDate.of(2026, 10, 14)));

        // On expiration date: valid (expires after this day)
        assertTrue(certification.isValidOn(LocalDate.of(2026, 10, 15)));
        assertFalse(certification.isExpiredOn(LocalDate.of(2026, 10, 15)));

        // After expiration date: expired
        assertFalse(certification.isValidOn(LocalDate.of(2026, 10, 16)));
        assertTrue(certification.isExpiredOn(LocalDate.of(2026, 10, 16)));
    }

    @Test
    @DisplayName("Should update expiration date successfully")
    void shouldUpdateExpirationDate_Successfully() {
        var certification = new MachineryCertification(operator, 1, LocalDate.of(2026, 12, 31));
        var newExpiration = LocalDate.of(2028, 6, 30);

        certification.updateExpirationDate(newExpiration);

        assertEquals(newExpiration, certification.getExpirationDate());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid constructor arguments")
    void shouldThrowException_ForInvalidConstructorArguments() {
        var validDate = LocalDate.of(2027, 1, 1);

        assertThrows(IllegalArgumentException.class, () -> new MachineryCertification(null, 1, validDate));
        assertThrows(IllegalArgumentException.class, () -> new MachineryCertification(operator, null, validDate));
        assertThrows(IllegalArgumentException.class, () -> new MachineryCertification(operator, 0, validDate));
        assertThrows(IllegalArgumentException.class, () -> new MachineryCertification(operator, -5, validDate));
        assertThrows(IllegalArgumentException.class, () -> new MachineryCertification(operator, 1, null));
    }
}
