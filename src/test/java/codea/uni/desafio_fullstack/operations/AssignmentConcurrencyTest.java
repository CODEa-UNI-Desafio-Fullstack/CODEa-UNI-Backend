package codea.uni.desafio_fullstack.operations;

import codea.uni.desafio_fullstack.operations.application.internal.commandservices.AssignmentCommandServiceImpl;
import codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl.ExternalMachineryService;
import codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl.ExternalOperatorService;
import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Assignment;
import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateAssignmentCommand;
import codea.uni.desafio_fullstack.operations.domain.model.exceptions.AssignmentValidationException;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.AssignmentRepository;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.ShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas de concurrencia para asignaciones de turnos.
 * Cumple con el requisito 13 de arquitectura y negocio (P6):
 * "Dos supervisores pueden intentar asignar simultáneamente el mismo equipo al mismo turno.
 * El sistema debe garantizar que solo una asignación pueda confirmarse.
 * Una estrategia como SELECT disponibilidad -> INSERT asignación sin protección contra
 * concurrencia NO es suficiente.
 * Se debe demostrar que la condición de carrera no permite dos asignaciones válidas simultáneas."
 */
@ExtendWith(MockitoExtension.class)
class AssignmentConcurrencyTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private ExternalMachineryService externalMachineryService;

    @Mock
    private ExternalOperatorService externalOperatorService;

    private AssignmentCommandServiceImpl assignmentCommandService;

    // Almacenamiento concurrente thread-safe para simular las restricciones únicas de base de datos:
    // uk_assignment_shift_machinery: (shift_id, machinery_code)
    // uk_assignment_shift_operator:  (shift_id, operator_id)
    private Set<String> shiftMachineryUniqueStore;
    private Set<String> shiftOperatorUniqueStore;
    private List<Assignment> savedAssignments;

    @BeforeEach
    void setUp() {
        assignmentCommandService = new AssignmentCommandServiceImpl(
                assignmentRepository,
                shiftRepository,
                externalMachineryService,
                externalOperatorService
        );

        shiftMachineryUniqueStore = Collections.synchronizedSet(new HashSet<>());
        shiftOperatorUniqueStore = Collections.synchronizedSet(new HashSet<>());
        savedAssignments = Collections.synchronizedList(new ArrayList<>());
    }

    @Test
    @DisplayName("Concurrencia: Dos supervisores intentan asignar simultáneamente la misma maquinaria al mismo turno -> Solo uno debe confirmarse")
    void concurrentAssignmentForSameMachineryInSameShift_ShouldAllowOnlyOneAndRejectSecond() throws Exception {
        UUID shiftId = UUID.randomUUID();
        String machineryCode = "EXC-001";
        UUID operator1Id = UUID.randomUUID();
        UUID operator2Id = UUID.randomUUID();

        // Configuración de existencia y validaciones para ambos operadores y la maquinaria
        when(shiftRepository.existsById(shiftId)).thenReturn(true);
        when(externalMachineryService.existsMachineryByCode(machineryCode)).thenReturn(true);
        when(externalMachineryService.isMachineryActive(machineryCode)).thenReturn(true);
        when(externalMachineryService.getMachineryTypeId(machineryCode)).thenReturn(1);

        when(externalOperatorService.existsOperatorById(operator1Id)).thenReturn(true);
        when(externalOperatorService.existsOperatorById(operator2Id)).thenReturn(true);
        when(externalOperatorService.isOperatorCertifiedForMachineryType(eq(operator1Id), eq(1), any(LocalDate.class))).thenReturn(true);
        when(externalOperatorService.isOperatorCertifiedForMachineryType(eq(operator2Id), eq(1), any(LocalDate.class))).thenReturn(true);

        // Simulamos condición de carrera: en el momento del SELECT ambos leen que no existe asignación previa
        when(assignmentRepository.existsByShiftIdAndMachineryCode(shiftId, machineryCode))
                .thenAnswer(inv -> shiftMachineryUniqueStore.contains(shiftId + "_" + machineryCode));
        when(assignmentRepository.existsByShiftIdAndOperatorId(eq(shiftId), any(UUID.class)))
                .thenReturn(false);

        // A nivel de persistencia (INSERT / save), se simula la restricción única de BD uk_assignment_shift_machinery
        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(invocation -> {
            Assignment assignment = invocation.getArgument(0);
            String key = assignment.getShiftId() + "_" + assignment.getMachineryCode();

            synchronized (shiftMachineryUniqueStore) {
                if (shiftMachineryUniqueStore.contains(key)) {
                    // Violación de restricción única en BD (Duplicate entry for key 'uk_assignment_shift_machinery')
                    throw new DataIntegrityViolationException("Duplicate entry '" + key + "' for key 'uk_assignment_shift_machinery'");
                }
                shiftMachineryUniqueStore.add(key);
                assignment.setId(UUID.randomUUID());
                savedAssignments.add(assignment);
                return assignment;
            }
        });

        // Lanzamos dos hilos concurrentes simulando a los dos supervisores compitiendo
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger collisionCount = new AtomicInteger(0);

        Callable<Void> taskSupervisor1 = () -> {
            readyLatch.countDown();
            startLatch.await(); // Esperar disparo simultáneo
            try {
                var result = assignmentCommandService.handle(new CreateAssignmentCommand(operator1Id, machineryCode, shiftId));
                if (result.isPresent()) {
                    successCount.incrementAndGet();
                }
            } catch (DataIntegrityViolationException | AssignmentValidationException e) {
                collisionCount.incrementAndGet();
            }
            return null;
        };

        Callable<Void> taskSupervisor2 = () -> {
            readyLatch.countDown();
            startLatch.await(); // Esperar disparo simultáneo
            try {
                var result = assignmentCommandService.handle(new CreateAssignmentCommand(operator2Id, machineryCode, shiftId));
                if (result.isPresent()) {
                    successCount.incrementAndGet();
                }
            } catch (DataIntegrityViolationException | AssignmentValidationException e) {
                collisionCount.incrementAndGet();
            }
            return null;
        };

        Future<Void> future1 = executorService.submit(taskSupervisor1);
        Future<Void> future2 = executorService.submit(taskSupervisor2);

        // Disparar ambos hilos al mismo tiempo
        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown();

        future1.get(5, TimeUnit.SECONDS);
        future2.get(5, TimeUnit.SECONDS);
        executorService.shutdown();

        // Aserciones de negocio y persistencia
        // 1. Exactamente una asignación tuvo éxito
        assertEquals(1, successCount.get(), "Exactamente una asignación debe confirmarse");
        // 2. Exactamente una asignación fue rechazada por la colisión de carrera
        assertEquals(1, collisionCount.get(), "La asignación concurrente competidora debe ser rechazada");
        // 3. En la persistencia solo existe 1 registro, nunca 2
        assertEquals(1, savedAssignments.size(), "Solo debe existir un registro persistido en la base de datos");
        assertEquals(machineryCode, savedAssignments.get(0).getMachineryCode());
        assertEquals(shiftId, savedAssignments.get(0).getShiftId());
    }

    @Test
    @DisplayName("Concurrencia: Dos supervisores intentan asignar simultáneamente el mismo operador al mismo turno -> Solo uno debe confirmarse")
    void concurrentAssignmentForSameOperatorInSameShift_ShouldAllowOnlyOneAndRejectSecond() throws Exception {
        UUID shiftId = UUID.randomUUID();
        UUID operatorId = UUID.randomUUID();
        String machinery1Code = "EXC-001";
        String machinery2Code = "CAM-002";

        // Configuración de existencia y validaciones
        when(shiftRepository.existsById(shiftId)).thenReturn(true);
        when(externalOperatorService.existsOperatorById(operatorId)).thenReturn(true);

        when(externalMachineryService.existsMachineryByCode(machinery1Code)).thenReturn(true);
        when(externalMachineryService.existsMachineryByCode(machinery2Code)).thenReturn(true);
        when(externalMachineryService.isMachineryActive(machinery1Code)).thenReturn(true);
        when(externalMachineryService.isMachineryActive(machinery2Code)).thenReturn(true);
        when(externalMachineryService.getMachineryTypeId(machinery1Code)).thenReturn(1);
        when(externalMachineryService.getMachineryTypeId(machinery2Code)).thenReturn(2);

        when(externalOperatorService.isOperatorCertifiedForMachineryType(eq(operatorId), anyInt(), any(LocalDate.class))).thenReturn(true);

        when(assignmentRepository.existsByShiftIdAndMachineryCode(eq(shiftId), anyString())).thenReturn(false);
        when(assignmentRepository.existsByShiftIdAndOperatorId(shiftId, operatorId))
                .thenAnswer(inv -> shiftOperatorUniqueStore.contains(shiftId + "_" + operatorId));

        // A nivel de persistencia (INSERT / save), se simula la restricción única de BD uk_assignment_shift_operator
        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(invocation -> {
            Assignment assignment = invocation.getArgument(0);
            String key = assignment.getShiftId() + "_" + assignment.getOperatorId();

            synchronized (shiftOperatorUniqueStore) {
                if (shiftOperatorUniqueStore.contains(key)) {
                    throw new DataIntegrityViolationException("Duplicate entry '" + key + "' for key 'uk_assignment_shift_operator'");
                }
                shiftOperatorUniqueStore.add(key);
                assignment.setId(UUID.randomUUID());
                savedAssignments.add(assignment);
                return assignment;
            }
        });

        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger collisionCount = new AtomicInteger(0);

        Callable<Void> taskSupervisor1 = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                var result = assignmentCommandService.handle(new CreateAssignmentCommand(operatorId, machinery1Code, shiftId));
                if (result.isPresent()) {
                    successCount.incrementAndGet();
                }
            } catch (DataIntegrityViolationException | AssignmentValidationException e) {
                collisionCount.incrementAndGet();
            }
            return null;
        };

        Callable<Void> taskSupervisor2 = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                var result = assignmentCommandService.handle(new CreateAssignmentCommand(operatorId, machinery2Code, shiftId));
                if (result.isPresent()) {
                    successCount.incrementAndGet();
                }
            } catch (DataIntegrityViolationException | AssignmentValidationException e) {
                collisionCount.incrementAndGet();
            }
            return null;
        };

        Future<Void> future1 = executorService.submit(taskSupervisor1);
        Future<Void> future2 = executorService.submit(taskSupervisor2);

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown();

        future1.get(5, TimeUnit.SECONDS);
        future2.get(5, TimeUnit.SECONDS);
        executorService.shutdown();

        assertEquals(1, successCount.get(), "Exactamente una asignación debe confirmarse para el operador");
        assertEquals(1, collisionCount.get(), "El segundo intento concurrente debe ser rechazado");
        assertEquals(1, savedAssignments.size(), "Solo un registro debe quedar persistido");
        assertEquals(operatorId, savedAssignments.get(0).getOperatorId());
    }
}
