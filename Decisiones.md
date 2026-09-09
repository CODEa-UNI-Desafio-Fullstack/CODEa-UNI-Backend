## Decisiones
### Bloqueo de equipo con turnos futuros
- **Resolucion:**
  - Al superar el umbral de horas, la maquinaria pasa al estado bloqueado (`state = false`), impidiendo el despacho de nuevas asignaciones (`EQUIPMENT_BLOCKED`).
  - Las asignaciones futuras programadas quedan expuestas para su cancelacion o reasignacion via endpoint `DELETE /api/v1/assignments/{id}`.
  - El endpoint de proyecciones a 7 dias (`/api/v1/assignments/projections`) anticipa qué equipos alcanzarán el umbral en base a sus turnos programados.
- **Justificacion:**
     Decidi mantener las asignaciones futuras del equipo para que el usuario pueda cancelarlas o reasignarlas , ya que si se eliminaran automaticamente, el usuario no tendria forma de saber a que turnos estaba asignado el equipo por lo que se tendria primero que buscar que turnos quedaron libres para volver a crearlos.

---

### Asignacion forzada
- **Resolucion:**
  - No se permiten bypasses silenciosos ni excepciones no controladas.
  - Todas las reglas de negocio (`OPERATOR_SHIFT_DUPLICATE`, `MACHINERY_SHIFT_DUPLICATE`, `EQUIPMENT_BLOCKED`, `NO_CERTIFICATION`) se validan de forma estricta y acumulativa.
  - Si una o más reglas se incumplen, se rechaza la operacion con `AssignmentValidationException` (HTTP 422 `ASSIGNMENT_REJECTED`) devolviendo la lista completa de infracciones.
- **Justificacion:**
    Decidi que no se permitieran excepciones, ya que creo que los errores deben ser controlados y reportados al usuario para que pueda tomar accion. Si se permitieran excepciones a las reglas, el usuario podria cometer errores sin darse cuenta.
---

### Siguiente ciclo de mantenimiento
- **Resolucion:**
  - El reinicio del ciclo ocurre únicamente al registrar un mantenimiento (`POST /api/v1/maintenances`).
  - Mediante `Machinery.resetAfterMaintenance()`, el horometro (`hourMeter`) se reinicia exactamente a `0.0` y el equipo se desbloquea automáticamente (`state = true`), sin importar cuántas horas excedio el umbral antes del servicio técnico.
- **Justificacion:**
    Por simplicidad decidi resetear el horometro a 0 horas cuando se realiza un mantenimiento, ya que el objetivo es que el equipo vuelva a tener horas de funcionamiento como si fuera nuevo. Aunque se podria implementar un sistema más complejo que almacene el historial de mantenimientos y horas acumuladas para tener un control más preciso del mantenimiento del equipo.
---

### Horas planificadas vs. reales
- **Resolucion:**
  - Se mantienen separados los conceptos en el modelo:
    - `Shift.duration`: Horas planificadas de la jornada.
    - `Assignment.timeStart` y `Assignment.timeEnd`: Timestamps reales del turno (con soporte de cruce de medianoche en turnos nocturnos).
    - `Assignment.actualShiftTime`: Horas reales calculadas al cerrar turno (`PATCH /api/v1/assignments/{id}/end`).
  - El horometro de la maquinaria se incrementa con las horas reales ejecutadas (`actualShiftTime`), preservando las horas planificadas intactas para auditoria.
- **Justificacion:**
    Decidi mantener separados los conceptos de horas planificadas y horas reales, ya que creo que es importante tener un control preciso de las horas que se han ejecutado en comparacion con las horas que se habian planificado. Aunque se podria implementar un sistema más simple que almacene solo las horas reales, creo que mantener ambos conceptos separados proporciona un mejor control.
---

### Certificacion que vence durante un turno
- **Resolucion:**
  - En `OperatorContextFacadeImpl`, se valida que la fecha de vencimiento de la certificacion sea estrictamente posterior a la fecha del turno (`expirationDate.isAfter(shiftDate)`).
  - Si la certificacion vence el mismo dia o antes de la fecha del turno asignado, se considera no vigente y se deniega la asignacion con el error `NO_CERTIFICATION`.
- **Justificacion:**
    Decidi mitigar esto haciendo que no sea posible asignar un turno a un operador si su certificacion vence en la fecha del turno, si bien se podria implementar un sistema calcule la hora de vencimiento y lo valide con la fecha de inicio del turno, es mejor evitar tener un tiempo limite pequeño con el que el operador puede trabajar.
---

### Concurrencia
- **Resolucion:**
  - **Base de datos (Persistencia):** Restricciones de unicidad compuestas en la tabla `assignments`:
    - `uk_assignment_shift_machinery` (`shift_id`, `machinery_code`).
    - `uk_assignment_shift_operator` (`shift_id`, `operator_id`).
  - **Capa de aplicacion:** Validacion previa en servicio, bloqueo pesimista en maquinaria (`@Lock(LockModeType.PESSIMISTIC_WRITE)` en `findByCodeWithLock`) y manejo transaccional para que, ante peticiones simultáneas, solo una prospere y la segunda sea rechazada por violacion de integridad sin corromper datos.
  - **Justificacion:**
    Para manejar la concurrencia, decidi usar restricciones de unicidad compuestas en la tabla `assignments`, bloqueo pesimista en maquinaria y manejo transaccional, de esta manera, ante peticiones simultáneas, solo una pasará y la segunda será rechazada sin corromper datos.
---

## Lo que se dejo de lado
Se dejo de lado el sistema de autenticacion por cuestiones de simplicidad y tiempo. Si bien he trabajado con sistemas auth como JWT o la implementacion de un tercero como OAuth, no tengo la experiencia para hacer uno de 0 robusto y seguro en un tiempo considerable sin comprometer el resto del proyecto. Y dado que esto es solo una demostracion de concepto, considero que no era necesario.

## Si tuviera más tiempo
De haberle puesto mas tiempo, creo que se podria haber implementado un sistema de autenticaacion, ademaàs de otros sistemas mas complejos como el calculo de horas reales de mantenimiento o un sistema de notificaciones por correo para alertar a los operadores de que sus certificaciones estan por vencer.

## Uso de la IA
Utilice el IDE agentico de AntiGravity para agilizar la implementacion de codigo repetitivo y simple. Por ejemplo, ya que la arquitectura que use esta basada en dominios, la logica CRUD basica es muy similar en todos los dominios, por lo que luego de yo implementar la logica CRUD para un dominio, le pedi a la IA que implementara la logica para los demas dominios, basandose en el ejemplo que le di, utilizando los records para las queries y commands, los recursos, el mapeo, etc. De esta manera me pude enfocar en implementar la logica de negocio que era mas compleja y unica de cada dominio. De ahi en adelante se utilizo para resolver dudas teoricas, autogeneracion de snippets (los q viene con el IDE) y la generacion de tests para los metodos utilizando mockito de la misma forma en la que se hizo con los CRUDs.