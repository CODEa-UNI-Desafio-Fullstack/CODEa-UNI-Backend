package codea.uni.desafio_fullstack.shared.infrastructure.seeder;

import codea.uni.desafio_fullstack.machinery.domain.model.aggregates.Machinery;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryTypeCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;
import codea.uni.desafio_fullstack.machinery.infrastructure.persistence.jpa.repositories.MachineryRepository;
import codea.uni.desafio_fullstack.machinery.infrastructure.persistence.jpa.repositories.MachineryTypeRepository;
import codea.uni.desafio_fullstack.maintenance.domain.model.aggregates.Maintenance;
import codea.uni.desafio_fullstack.maintenance.domain.model.commands.CreateMaintenanceCommand;
import codea.uni.desafio_fullstack.maintenance.infrastructure.persistence.jpa.repositories.MaintenanceRepository;
import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Assignment;
import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Shift;
import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateAssignmentCommand;
import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateShiftCommand;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.AssignmentRepository;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.ShiftRepository;
import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import codea.uni.desafio_fullstack.operators.domain.model.entities.MachineryCertification;
import codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories.MachineryCertificationRepository;
import codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories.OperatorRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio utilitario independiente de dominio para resetear la base de datos y
 * sembrar datos de prueba diseñados para validar casos borde del sistema:
 * 1. Equipo próximo a alcanzar mantenimiento (EXC-001, faltan 8h de 250h).
 * 2. Operador con certificación vencida (Pedro Ramírez, vencida hace 15 días).
 * 3. Turno cuyo cierre provoca el bloqueo automático de un equipo (PRF-101 con 95h de 100h en turno de 8h).
 * 4. Equipo bloqueado por mantenimiento (CAM-999 con 510h de 500h).
 * 5. Casos borde para combinaciones de múltiples rechazos simultáneos.
 */
@Service
public class TestDataSeederService {

    private final AssignmentRepository assignmentRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final MachineryCertificationRepository machineryCertificationRepository;
    private final ShiftRepository shiftRepository;
    private final OperatorRepository operatorRepository;
    private final MachineryRepository machineryRepository;
    private final MachineryTypeRepository machineryTypeRepository;
    private final EntityManager entityManager;

    public TestDataSeederService(
            AssignmentRepository assignmentRepository,
            MaintenanceRepository maintenanceRepository,
            MachineryCertificationRepository machineryCertificationRepository,
            ShiftRepository shiftRepository,
            OperatorRepository operatorRepository,
            MachineryRepository machineryRepository,
            MachineryTypeRepository machineryTypeRepository,
            EntityManager entityManager) {
        this.assignmentRepository = assignmentRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.machineryCertificationRepository = machineryCertificationRepository;
        this.shiftRepository = shiftRepository;
        this.operatorRepository = operatorRepository;
        this.machineryRepository = machineryRepository;
        this.machineryTypeRepository = machineryTypeRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public Map<String, Object> resetAndSeed() {
        // 1. Limpieza en orden estricto de integridad referencial con eliminación directa en BD (evita defer en cola Hibernate)
        assignmentRepository.deleteAllInBatch();
        maintenanceRepository.deleteAllInBatch();
        machineryCertificationRepository.deleteAllInBatch();
        shiftRepository.deleteAllInBatch();
        operatorRepository.deleteAllInBatch();
        machineryRepository.deleteAllInBatch();
        machineryTypeRepository.deleteAllInBatch();

        // Sincronizar y limpiar el contexto de persistencia antes de sembrar los nuevos datos
        entityManager.flush();
        entityManager.clear();

        // 2. Creación de Tipos de Maquinaria
        // Excavadora: 250h | Camión: 500h | Perforadora: 100h
        MachineryType excavatorType = machineryTypeRepository.save(
                new MachineryType(new CreateMachineryTypeCommand("Excavadora", 250)));
        MachineryType truckType = machineryTypeRepository.save(
                new MachineryType(new CreateMachineryTypeCommand("Camión de Acarreo", 500)));
        MachineryType drillType = machineryTypeRepository.save(
                new MachineryType(new CreateMachineryTypeCommand("Perforadora", 100)));

        // 3. Creación de Maquinarias de Prueba
        // a) EXC-001: Próxima a alcanzar mantenimiento (242h de 250h, le faltan 8h)
        Machinery exc001 = new Machinery(new CreateMachineryCommand("EXC-001", excavatorType.getId()), excavatorType);
        exc001.setHourMeter(242.0f);
        exc001.setState(true); // Activa
        machineryRepository.save(exc001);

        // b) PRF-101: 95h de 100h acumuladas. Cerrar su turno de 8h sumará 103h y la bloqueará automáticamente
        Machinery prf101 = new Machinery(new CreateMachineryCommand("PRF-101", drillType.getId()), drillType);
        prf101.setHourMeter(95.0f);
        prf101.setState(true); // Activa
        machineryRepository.save(prf101);

        // c) CAM-001: Operativa normal con 120h
        Machinery cam001 = new Machinery(new CreateMachineryCommand("CAM-001", truckType.getId()), truckType);
        cam001.setHourMeter(120.0f);
        cam001.setState(true);
        machineryRepository.save(cam001);

        // d) CAM-999: Bloqueada por mantenimiento (510h de 500h, state = false)
        Machinery cam999 = new Machinery(new CreateMachineryCommand("CAM-999", truckType.getId()), truckType);
        cam999.setHourMeter(510.0f);
        cam999.setState(false); // Bloqueada
        machineryRepository.save(cam999);

        // 4. Creación de Operadores
        // a) Carlos Mendoza: Certificado vigente para Excavadora, Camión y Perforadora
        Operator carlos = operatorRepository.save(new Operator("Carlos Mendoza"));
        // b) Pedro Ramírez: Certificación vencida para Excavadora
        Operator pedro = operatorRepository.save(new Operator("Pedro Ramírez"));
        // c) Ana Torres: Certificada vigente para Camión y Excavadora
        Operator ana = operatorRepository.save(new Operator("Ana Torres"));

        // 5. Certificaciones
        LocalDate today = LocalDate.now();
        // Carlos: vigentes por 1 año
        machineryCertificationRepository.save(new MachineryCertification(carlos, excavatorType.getId(), today.plusYears(1)));
        machineryCertificationRepository.save(new MachineryCertification(carlos, drillType.getId(), today.plusYears(1)));
        machineryCertificationRepository.save(new MachineryCertification(carlos, truckType.getId(), today.plusYears(1)));

        // Pedro: Certificación VENCIDA (hace 15 días) para Excavadora (y sin cert para otros)
        machineryCertificationRepository.save(new MachineryCertification(pedro, excavatorType.getId(), today.minusDays(15)));

        // Ana: Vigente para Excavadora y Camión
        machineryCertificationRepository.save(new MachineryCertification(ana, excavatorType.getId(), today.plusMonths(6)));
        machineryCertificationRepository.save(new MachineryCertification(ana, truckType.getId(), today.plusMonths(6)));

        // 6. Turnos
        // Turno 1: Turno de Hoy (Día = true, 8 horas)
        Shift shiftToday = shiftRepository.save(new Shift(new CreateShiftCommand(today, true, 8)));

        // Turno 2: Turno de Mañana (Noche = false, 12 horas)
        Shift shiftTomorrow = shiftRepository.save(new Shift(new CreateShiftCommand(today.plusDays(1), false, 12)));

        // 7. Asignaciones Iniciales
        // Asignación A: Carlos Mendoza opera PRF-101 en el turno de hoy (iniciado hace 8 horas)
        // Al ejecutar PATCH /api/v1/assignments/{id}/end con LocalDateTime.now(), se registran 8 horas reales,
        // el horómetro pasa a 95 + 8 = 103h y PRF-101 queda BLOQUEADA automáticamente.
        Assignment assignmentDrill = new Assignment(new CreateAssignmentCommand(carlos.getId(), "PRF-101", shiftToday.getId()));
        assignmentDrill.startShift(LocalDateTime.now().minusHours(8));
        assignmentDrill = assignmentRepository.save(assignmentDrill);

        // Asignación B: Ana Torres opera CAM-001 en el turno de hoy
        Assignment assignmentTruck = new Assignment(new CreateAssignmentCommand(ana.getId(), "CAM-001", shiftToday.getId()));
        assignmentTruck = assignmentRepository.save(assignmentTruck);

        // 8. Historial de Mantenimiento Previo
        // Registro de mantenimiento realizado hace 10 días en CAM-001
        maintenanceRepository.save(new Maintenance(new CreateMaintenanceCommand(
                "CAM-001",
                today.minusDays(10),
                500.0f,
                carlos.getId(),
                "Mantenimiento preventivo de ciclo completado exitosamente."
        )));

        // Estructura de respuesta descriptiva
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("message", "Base de datos reseteada y cargada con datos de prueba exitosamente.");
        summary.put("testScenariosAvailable", List.of(
                "1. Equipo próximo a mantenimiento: 'EXC-001' (242h / 250h, faltan 8h)",
                "2. Operador con certificación vencida: 'Pedro Ramírez' (" + pedro.getId() + ") - Vencida hace 15 días para Excavadora",
                "3. Turno cuyo cierre bloquea equipo: Asignación '" + assignmentDrill.getId() + "' de 'PRF-101' (95h) en turno de 8h. Cerrar turno disparará bloqueo a 103h",
                "4. Equipo ya bloqueado por mantenimiento: 'CAM-999' (510h / 500h)",
                "5. Múltiples errores simultáneos: Intentar asignar en turno de hoy a 'Pedro Ramírez' (certificación vencida) con 'CAM-999' (bloqueado) disparará múltiples razones",
                "6. Duplicados: 'Carlos Mendoza' y 'CAM-001' ya están asignados al turno de hoy (" + shiftToday.getId() + ")"
        ));

        summary.put("entities", Map.of(
                "machineryTypes", List.of("Excavadora (250h)", "Camión de Acarreo (500h)", "Perforadora (100h)"),
                "machinery", List.of("EXC-001 (Activa, 242h)", "PRF-101 (Activa, 95h)", "CAM-001 (Activa, 120h)", "CAM-999 (Bloqueada, 510h)"),
                "operators", List.of("Carlos Mendoza (Vigente)", "Pedro Ramírez (Vencida)", "Ana Torres (Vigente)"),
                "shifts", List.of("Turno Hoy: " + shiftToday.getId() + " (Día, 8h)", "Turno Mañana: " + shiftTomorrow.getId() + " (Noche, 12h)"),
                "activeAssignmentToCloseAndBlock", assignmentDrill.getId()
        ));

        return summary;
    }
}
