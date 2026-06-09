# 🗳️ Taller Pruebas Unitarias – Registraduría Electoral

> **Curso:** Testing y Validación de Software  
> **Programa:** Maestría en Ingeniería de Software – Universidad de La Sabana  
> **Año:** 2025

---

## 📖 Índice

1. [Descripción del dominio](#descripción-del-dominio)
2. [Estructura del proyecto](#estructura-del-proyecto)
3. [TDD – Red → Green → Refactor (3 iteraciones)](#tdd-red--green--refactor)
4. [Patrón AAA (Arrange – Act – Assert)](#patrón-aaa-arrange--act--assert)
5. [Clases de Equivalencia y Valores Límite](#clases-de-equivalencia-y-valores-límite)
6. [Escenarios BDD (Given – When – Then)](#escenarios-bdd-given--when--then)
7. [Cobertura con JaCoCo](#cobertura-con-jacoco)
8. [Resultados de pruebas](#resultados-de-pruebas)
9. [Reflexión final](#reflexión-final)
10. [Cómo ejecutar](#cómo-ejecutar)

---

## Descripción del dominio

El sistema simula el proceso de inscripción de votantes de la **Registraduría Nacional**. Permite registrar personas que desean votar en las próximas elecciones y aplica las siguientes reglas de negocio:

| Regla | Descripción | Resultado |
|-------|-------------|-----------|
| R1 | La persona no puede ser `null` | `INVALID` |
| R2 | El número de documento (`id`) debe ser positivo (`> 0`) | `INVALID` |
| R3 | La persona debe estar viva | `DEAD` |
| R4 | La edad no puede ser negativa | `INVALID_AGE` |
| R5 | La edad no puede superar 120 años | `INVALID_AGE` |
| R6 | La persona debe ser mayor de 18 años | `UNDERAGE` |
| R7 | No puede existir un registro con el mismo documento | `DUPLICATED` |
| R8 | Si pasa todas las reglas anteriores | `VALID` |

**Clases del dominio:**
- `Person` – Entidad principal con atributos: `name`, `id`, `age`, `gender`, `alive`
- `Gender` – Enumeración: `MALE`, `FEMALE`, `UNIDENTIFIED`
- `RegisterResult` – Enumeración: `VALID`, `DUPLICATED`, `INVALID`, `DEAD`, `UNDERAGE`, `INVALID_AGE`
- `Registry` – Servicio de dominio con el método `registerVoter(Person)`

---

## Estructura del proyecto

```
registraduria/
├── pom.xml
├── .gitignore
├── integrantes.txt
├── defectos.md
├── README.md
└── src/
    ├── main/java/edu/unisabana/tyvs/
    │   └── domain/
    │       ├── model/
    │       │   ├── Person.java
    │       │   ├── Gender.java
    │       │   └── RegisterResult.java
    │       └── service/
    │           └── Registry.java
    └── test/java/edu/unisabana/tyvs/
        └── domain/
            └── service/
                └── RegistryTest.java
```

---

## TDD Red → Green → Refactor

### 🔴 Iteración 1: Persona null → INVALID

**RED** – Se escribe la prueba antes de implementar la validación:
```java
@Test
public void shouldReturnInvalidWhenPersonIsNull() {
    Registry registry = new Registry();
    RegisterResult result = registry.registerVoter(null);
    Assert.assertEquals(RegisterResult.INVALID, result);
}
```
> ❌ La prueba **falla** porque el stub devuelve `VALID`.

**GREEN** – Implementación mínima:
```java
if (person == null) return RegisterResult.INVALID;
```
> ✅ La prueba **pasa**.

**REFACTOR** – Se extrae la constante `MIN_VALID_ID` y se organiza el flujo con comentarios claros.

---

### 🔴 Iteración 2: Persona muerta → DEAD / Menor de edad → UNDERAGE

**RED** – Se agregan dos pruebas:
```java
@Test
public void shouldRejectDeadPerson() { ... }

@Test
public void shouldRejectUnderageAt17() { ... }
```
> ❌ Ambas **fallan** porque aún no existe validación de vida ni edad.

**GREEN** – Implementación mínima:
```java
if (!person.isAlive()) return RegisterResult.DEAD;
if (person.getAge() < MIN_VOTING_AGE) return RegisterResult.UNDERAGE;
```
> ✅ Ambas pruebas **pasan**.

**REFACTOR** – Se define la constante `MIN_VOTING_AGE = 18` para eliminar el "número mágico".

---

### 🔴 Iteración 3: Edad inválida → INVALID_AGE / Duplicado → DUPLICATED

**RED** – Se agregan pruebas para bordes de edad y duplicados:
```java
@Test
public void shouldReturnInvalidAgeWhenAgeIsNegative() { ... }

@Test
public void shouldReturnInvalidAgeWhenAgeExceedsMaximum() { ... }

@Test
public void shouldReturnDuplicatedWhenSameIdRegisteredTwice() { ... }
```
> ❌ Las tres **fallan**: no hay control de rango de edad ni de unicidad.

**GREEN** – Implementación:
```java
if (person.getAge() < 0 || person.getAge() > MAX_AGE) return RegisterResult.INVALID_AGE;
if (registeredIds.contains(person.getId())) return RegisterResult.DUPLICATED;
registeredIds.add(person.getId());
```
> ✅ Todas **pasan**.

**REFACTOR** – Se extrae `MAX_AGE = 120`, se encapsula el `Set<Integer>` como campo privado.

---

## Patrón AAA (Arrange – Act – Assert)

Cada prueba sigue estrictamente el patrón **AAA**, separado por comentarios explícitos:

```java
@Test
public void shouldAcceptAdultAtExactly18() {
    // Arrange: preparar los datos y el objeto a probar
    Person person = new Person("Juan", 7, 18, Gender.MALE, true);

    // Act: ejecutar la acción que queremos probar
    RegisterResult result = registry.registerVoter(person);

    // Assert: verificar el resultado esperado
    Assert.assertEquals(RegisterResult.VALID, result);
}
```

**Pautas aplicadas:**
- **Arrange:** se crea una instancia fresca de `Registry` en `@Before` para aislar el estado.
- **Act:** una sola llamada al método bajo prueba por test.
- **Assert:** una sola aserción por test para mayor claridad.

---

## Clases de Equivalencia y Valores Límite

| # | Clase | Entrada representativa | Valor límite | Resultado esperado | Test que lo cubre |
|---|-------|------------------------|--------------|-------------------|-------------------|
| 1 | Nulidad | `null` | — | `INVALID` | `shouldReturnInvalidWhenPersonIsNull` |
| 2 | Id cero | `id = 0` | Límite inferior inválido | `INVALID` | `shouldReturnInvalidWhenIdIsZero` |
| 3 | Id negativo | `id = -5` | Clase negativa | `INVALID` | `shouldReturnInvalidWhenIdIsNegative` |
| 4 | Persona muerta | `alive = false` | — | `DEAD` | `shouldRejectDeadPerson` |
| 5 | Edad negativa | `age = -1` | Límite inferior inválido | `INVALID_AGE` | `shouldReturnInvalidAgeWhenAgeIsNegative` |
| 6 | Edad mayor al máximo | `age = 121` | Límite superior inválido | `INVALID_AGE` | `shouldReturnInvalidAgeWhenAgeExceedsMaximum` |
| 7 | Menor de edad | `age = 17` | Límite inferior mayoría | `UNDERAGE` | `shouldRejectUnderageAt17` |
| 8 | Menor de edad borde bajo | `age = 0` | Límite borde bajo clase menor | `UNDERAGE` | `shouldRejectUnderageAtZero` |
| 9 | Persona válida típica | `age = 30, id = 1, alive = true` | — | `VALID` | `shouldRegisterValidPerson` |
| 10 | Exactamente 18 años | `age = 18` | Límite inferior mayoría | `VALID` | `shouldAcceptAdultAtExactly18` |
| 11 | Edad máxima válida | `age = 120` | Límite superior válido | `VALID` | `shouldAcceptPersonAtMaxAge120` |
| 12 | Id duplicado | `id = 777` dos veces | — | `DUPLICATED` | `shouldReturnDuplicatedWhenSameIdRegisteredTwice` |
| 13 | Múltiples únicos | 3 personas con ids distintos | — | `VALID` × 3 | `shouldRegisterMultipleUniqueVotersSuccessfully` |
| 14 | Ids distintos no se confunden | `id = 99` luego `id = 100` | — | `VALID` | `shouldNotConfuseDifferentIds` |

---

## Escenarios BDD (Given – When – Then)

```gherkin
Escenario: Registrar persona válida
  Given  una persona viva de 30 años con id único
  When   intento registrarla
  Then   el resultado debe ser VALID

Escenario: Rechazar persona null
  Given  la persona es null
  When   intento registrarla
  Then   el resultado debe ser INVALID

Escenario: Rechazar id cero
  Given  la persona tiene id = 0, edad 25 y está viva
  When   intento registrarla
  Then   el resultado debe ser INVALID

Escenario: Rechazar persona muerta
  Given  la persona tiene 40 años e id válido pero está muerta
  When   intento registrarla
  Then   el resultado debe ser DEAD

Escenario: Rechazar edad negativa
  Given  la persona tiene edad = -1, está viva e id válido
  When   intento registrarla
  Then   el resultado debe ser INVALID_AGE

Escenario: Rechazar edad mayor al máximo permitido
  Given  la persona tiene 121 años, está viva e id válido
  When   intento registrarla
  Then   el resultado debe ser INVALID_AGE

Escenario: Rechazar menor de edad (límite 17)
  Given  la persona tiene 17 años, está viva y su id es válido
  When   intento registrarla
  Then   el resultado debe ser UNDERAGE

Escenario: Aceptar exactamente 18 años (límite inferior mayoría)
  Given  la persona tiene exactamente 18 años, está viva e id único
  When   intento registrarla
  Then   el resultado debe ser VALID

Escenario: Aceptar exactamente 120 años (límite superior)
  Given  la persona tiene exactamente 120 años, está viva e id único
  When   intento registrarla
  Then   el resultado debe ser VALID

Escenario: Rechazar inscripción duplicada
  Given  la persona con id = 777 ya fue registrada exitosamente
  When   intento registrar otra persona con el mismo id = 777
  Then   el resultado debe ser DUPLICATED
```

---

## Cobertura con JaCoCo

Para generar el reporte ejecutar:

```bash
mvn clean verify
```

El reporte HTML queda en:
```
target/site/jacoco/index.html
```

**Resultado obtenido:** ≥ 80% de cobertura global y en el paquete de dominio, gracias a que las 14 pruebas cubren todos los caminos del método `registerVoter`.

---

## Resultados de pruebas

Ejecutar con:

```bash
mvn clean test
```

**Salida esperada:**

```
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## Reflexión final

**¿Qué escenarios no se cubrieron y por qué?**
- Escenarios con `Gender` específico no son determinantes en las reglas de negocio actuales; el sistema no discrimina por género, por lo que no representan una clase de equivalencia distinta.
- Integración con base de datos real: el enfoque de Arquitectura Limpia aisló el dominio, por lo que esta capa queda fuera del alcance de las pruebas unitarias.

**¿Qué defectos reales detectaron los tests?**
- Los 4 defectos documentados en `defectos.md` fueron encontrados durante el ciclo RED del TDD: validación de null, id inválido, rango de edad y duplicados.

**¿Cómo mejorarías la clase `Registry`?**
- Inyectar el repositorio de ids como dependencia (interfaz `VoterRepository`) para facilitar pruebas con mocks.
- Retornar un objeto `RegistrationResponse` con mensaje descriptivo además del enum.
- Agregar registro de auditoría (quién registró a quién y cuándo).

---

## Cómo ejecutar

```bash
# Compilar
mvn clean compile

# Ejecutar pruebas
mvn clean test

# Generar reporte de cobertura JaCoCo
mvn clean verify
# → Abrir target/site/jacoco/index.html
```
