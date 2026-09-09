package codea.uni.desafio_fullstack.operators.application.internal.commandservices;

import codea.uni.desafio_fullstack.operators.application.internal.outboundservices.acl.ExternalMachineryService;
import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import codea.uni.desafio_fullstack.operators.domain.model.commands.CreateMachineryCertificationCommand;
import codea.uni.desafio_fullstack.operators.domain.model.commands.DeleteMachineryCertificationCommand;
import codea.uni.desafio_fullstack.operators.domain.model.commands.UpdateMachineryCertificationCommand;
import codea.uni.desafio_fullstack.operators.domain.model.entities.MachineryCertification;
import codea.uni.desafio_fullstack.operators.domain.model.valueobjects.MachineryCertificationId;
import codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories.MachineryCertificationRepository;
import codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories.OperatorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MachineryCertificationCommandServiceImplTest {

    @Mock
    private OperatorRepository operatorRepository;

    @Mock
    private MachineryCertificationRepository machineryCertificationRepository;

    @Mock
    private ExternalMachineryService externalMachineryService;

    @InjectMocks
    private MachineryCertificationCommandServiceImpl commandService;

    private Operator operator;
    private UUID operatorId;

    @BeforeEach
    void setUp() {
        operatorId = UUID.randomUUID();
        operator = new Operator("Pedro Gomez");
        operator.setId(operatorId);
    }

    @Test
    @DisplayName("handle(CreateMachineryCertificationCommand) should create and persist certification when valid")
    void handle_CreateCertification_WhenValid_PersistsAndReturns() {
        var command = new CreateMachineryCertificationCommand(operatorId, 2, LocalDate.of(2027, 5, 20));

        when(operatorRepository.findById(operatorId)).thenReturn(Optional.of(operator));
        when(externalMachineryService.existsMachineryTypeById(2)).thenReturn(true);
        when(machineryCertificationRepository.existsById(new MachineryCertificationId(operatorId, 2))).thenReturn(false);
        when(machineryCertificationRepository.save(any(MachineryCertification.class))).thenAnswer(i -> i.getArgument(0));

        var result = commandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals(operatorId, result.get().getId().getOperatorId());
        assertEquals(2, result.get().getId().getMachineryType());
        verify(operatorRepository).findById(operatorId);
        verify(externalMachineryService).existsMachineryTypeById(2);
        verify(machineryCertificationRepository).save(any(MachineryCertification.class));
    }

    @Test
    @DisplayName("handle(CreateMachineryCertificationCommand) should throw when operator does not exist")
    void handle_CreateCertification_WhenOperatorNotFound_Throws() {
        var command = new CreateMachineryCertificationCommand(operatorId, 2, LocalDate.of(2027, 5, 20));

        when(operatorRepository.findById(operatorId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> commandService.handle(command));
        verify(machineryCertificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("handle(CreateMachineryCertificationCommand) should throw when machinery type does not exist in ACL")
    void handle_CreateCertification_WhenMachineryTypeNotFound_Throws() {
        var command = new CreateMachineryCertificationCommand(operatorId, 99, LocalDate.of(2027, 5, 20));

        when(operatorRepository.findById(operatorId)).thenReturn(Optional.of(operator));
        when(externalMachineryService.existsMachineryTypeById(99)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> commandService.handle(command));
        verify(machineryCertificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("handle(CreateMachineryCertificationCommand) should throw when certification already exists")
    void handle_CreateCertification_WhenAlreadyExists_Throws() {
        var command = new CreateMachineryCertificationCommand(operatorId, 2, LocalDate.of(2027, 5, 20));

        when(operatorRepository.findById(operatorId)).thenReturn(Optional.of(operator));
        when(externalMachineryService.existsMachineryTypeById(2)).thenReturn(true);
        when(machineryCertificationRepository.existsById(new MachineryCertificationId(operatorId, 2))).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> commandService.handle(command));
        verify(machineryCertificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("handle(UpdateMachineryCertificationCommand) should update expiration date when exists")
    void handle_UpdateCertification_WhenExists_UpdatesDate() {
        var command = new UpdateMachineryCertificationCommand(operatorId, 2, LocalDate.of(2028, 12, 31));
        var existing = new MachineryCertification(operator, 2, LocalDate.of(2026, 12, 31));

        when(operatorRepository.existsById(operatorId)).thenReturn(true);
        when(externalMachineryService.existsMachineryTypeById(2)).thenReturn(true);
        when(machineryCertificationRepository.findById(new MachineryCertificationId(operatorId, 2)))
                .thenReturn(Optional.of(existing));
        when(machineryCertificationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = commandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals(LocalDate.of(2028, 12, 31), result.get().getExpirationDate());
        verify(machineryCertificationRepository).save(existing);
    }

    @Test
    @DisplayName("handle(DeleteMachineryCertificationCommand) should delete certification when exists")
    void handle_DeleteCertification_WhenExists_Deletes() {
        var certId = new MachineryCertificationId(operatorId, 2);
        when(machineryCertificationRepository.existsById(certId)).thenReturn(true);

        var command = new DeleteMachineryCertificationCommand(operatorId, 2);
        commandService.handle(command);

        verify(machineryCertificationRepository).deleteById(certId);
    }
}
