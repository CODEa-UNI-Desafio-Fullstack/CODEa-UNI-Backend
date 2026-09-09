package codea.uni.desafio_fullstack.operators.domain.model.aggregates;

import codea.uni.desafio_fullstack.operators.domain.model.commands.CreateOperatorCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperatorTest {

    @Test
    @DisplayName("Should instantiate operator with valid name")
    void shouldCreateOperator_WithValidName() {
        var command = new CreateOperatorCommand("Carlos Mendoza");
        var operator = new Operator(command);

        assertEquals("Carlos Mendoza", operator.getName());
    }

    @Test
    @DisplayName("Should trim whitespace in operator name")
    void shouldTrimWhitespace_InOperatorName() {
        var command = new CreateOperatorCommand("   Ana Morales   ");
        var operator = new Operator(command);

        assertEquals("Ana Morales", operator.getName());
    }

    @Test
    @DisplayName("Should update operator name successfully")
    void shouldUpdateName_Successfully() {
        var operator = new Operator("Carlos Mendoza");
        operator.updateName("Carlos Mendoza Jr.");

        assertEquals("Carlos Mendoza Jr.", operator.getName());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when name is null or blank on creation")
    void shouldThrowException_WhenNameIsNullOrEmptyOnCreation() {
        assertThrows(IllegalArgumentException.class, () -> new Operator((String) null));
        assertThrows(IllegalArgumentException.class, () -> new Operator("   "));
        assertThrows(IllegalArgumentException.class, () -> new CreateOperatorCommand(null));
        assertThrows(IllegalArgumentException.class, () -> new CreateOperatorCommand(""));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when name exceeds 50 characters")
    void shouldThrowException_WhenNameExceeds50Characters() {
        var longName = "A".repeat(51);
        assertThrows(IllegalArgumentException.class, () -> new Operator(longName));

        var operator = new Operator("Valid Name");
        assertThrows(IllegalArgumentException.class, () -> operator.updateName(longName));
    }
}
