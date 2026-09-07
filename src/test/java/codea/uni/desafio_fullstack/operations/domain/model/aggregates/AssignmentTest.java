package codea.uni.desafio_fullstack.operations.domain.model.aggregates;

import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateAssignmentCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AssignmentTest {

    @Test
    @DisplayName("Should create assignment successfully with valid command")
    void shouldCreateAssignmentSuccessfullyWithValidCommand() {
        UUID operatorId = UUID.randomUUID();
        UUID shiftId = UUID.randomUUID();
        CreateAssignmentCommand command = new CreateAssignmentCommand(operatorId, "EQ-001", shiftId);

        Assignment assignment = new Assignment(command);

        assertEquals(operatorId, assignment.getOperatorId());
        assertEquals("EQ-001", assignment.getMachineryCode());
        assertEquals(shiftId, assignment.getShiftId());
        assertNull(assignment.getTimeStart());
        assertNull(assignment.getTimeEnd());
        assertNull(assignment.getActualShiftTime());
    }

    @Test
    @DisplayName("Should update assignment details successfully")
    void shouldUpdateAssignmentDetailsSuccessfully() {
        UUID operatorId = UUID.randomUUID();
        UUID shiftId = UUID.randomUUID();
        Assignment assignment = new Assignment(new CreateAssignmentCommand(operatorId, "EQ-001", shiftId));

        UUID newOperatorId = UUID.randomUUID();
        UUID newShiftId = UUID.randomUUID();
        assignment.updateDetails(newOperatorId, "EQ-002", newShiftId);

        assertEquals(newOperatorId, assignment.getOperatorId());
        assertEquals("EQ-002", assignment.getMachineryCode());
        assertEquals(newShiftId, assignment.getShiftId());
    }

    @Test
    @DisplayName("Should fail when operator id is null")
    void shouldFailWhenOperatorIdIsNull() {
        UUID shiftId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () ->
                new CreateAssignmentCommand(null, "EQ-001", shiftId));

        Assignment assignment = new Assignment(new CreateAssignmentCommand(UUID.randomUUID(), "EQ-001", shiftId));
        assertThrows(IllegalArgumentException.class, () ->
                assignment.updateDetails(null, "EQ-001", shiftId));
    }

    @Test
    @DisplayName("Should fail when machinery code is null or blank")
    void shouldFailWhenMachineryCodeIsNullOrBlank() {
        UUID operatorId = UUID.randomUUID();
        UUID shiftId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () ->
                new CreateAssignmentCommand(operatorId, null, shiftId));

        assertThrows(IllegalArgumentException.class, () ->
                new CreateAssignmentCommand(operatorId, "   ", shiftId));

        Assignment assignment = new Assignment(new CreateAssignmentCommand(operatorId, "EQ-001", shiftId));
        assertThrows(IllegalArgumentException.class, () ->
                assignment.updateDetails(operatorId, null, shiftId));
        assertThrows(IllegalArgumentException.class, () ->
                assignment.updateDetails(operatorId, "   ", shiftId));
    }

    @Test
    @DisplayName("Should fail when shift id is null")
    void shouldFailWhenShiftIdIsNull() {
        UUID operatorId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () ->
                new CreateAssignmentCommand(operatorId, "EQ-001", null));

        Assignment assignment = new Assignment(new CreateAssignmentCommand(operatorId, "EQ-001", UUID.randomUUID()));
        assertThrows(IllegalArgumentException.class, () ->
                assignment.updateDetails(operatorId, "EQ-001", null));
    }

    @Test
    @DisplayName("Should start shift successfully with explicit time")
    void shouldStartShiftSuccessfullyWithExplicitTime() {
        Assignment assignment = new Assignment(new CreateAssignmentCommand(UUID.randomUUID(), "EQ-001", UUID.randomUUID()));
        LocalDateTime start = LocalDateTime.of(2026, 9, 7, 8, 0);

        assignment.startShift(start);

        assertEquals(start, assignment.getTimeStart());
    }

    @Test
    @DisplayName("Should start shift successfully with default time when null is provided")
    void shouldStartShiftSuccessfullyWithDefaultTime() {
        Assignment assignment = new Assignment(new CreateAssignmentCommand(UUID.randomUUID(), "EQ-001", UUID.randomUUID()));

        assignment.startShift(null);

        assertNotNull(assignment.getTimeStart());
    }

    @Test
    @DisplayName("Should end shift successfully and calculate actual shift time")
    void shouldEndShiftSuccessfullyAndCalculateActualShiftTime() {
        Assignment assignment = new Assignment(new CreateAssignmentCommand(UUID.randomUUID(), "EQ-001", UUID.randomUUID()));
        LocalDateTime start = LocalDateTime.of(2026, 9, 7, 8, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 7, 16, 0);

        assignment.startShift(start);
        assignment.endShift(end);

        assertEquals(start, assignment.getTimeStart());
        assertEquals(end, assignment.getTimeEnd());
        assertEquals(8, assignment.getActualShiftTime());
    }

    @Test
    @DisplayName("Should fail to end shift when not started")
    void shouldFailToEndShiftWhenNotStarted() {
        Assignment assignment = new Assignment(new CreateAssignmentCommand(UUID.randomUUID(), "EQ-001", UUID.randomUUID()));
        LocalDateTime end = LocalDateTime.of(2026, 9, 7, 16, 0);

        assertThrows(IllegalStateException.class, () -> assignment.endShift(end));
    }

    @Test
    @DisplayName("Should fail to end shift when end time is before start time")
    void shouldFailToEndShiftWhenEndTimeIsBeforeStartTime() {
        Assignment assignment = new Assignment(new CreateAssignmentCommand(UUID.randomUUID(), "EQ-001", UUID.randomUUID()));
        LocalDateTime start = LocalDateTime.of(2026, 9, 7, 16, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 7, 8, 0);

        assignment.startShift(start);
        assertThrows(IllegalArgumentException.class, () -> assignment.endShift(end));
    }
}
