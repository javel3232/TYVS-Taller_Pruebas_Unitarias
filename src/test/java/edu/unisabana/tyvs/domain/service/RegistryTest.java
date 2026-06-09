package edu.unisabana.tyvs.domain.service;

import edu.unisabana.tyvs.domain.model.Gender;
import edu.unisabana.tyvs.domain.model.Person;
import edu.unisabana.tyvs.domain.model.RegisterResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas unitarias para {@link Registry}.
 *
 * Diseño de pruebas:
 *  - Patrón AAA  (Arrange – Act – Assert)
 *  - Enfoque TDD (Red → Green → Refactor)
 *  - Escenarios BDD (Given – When – Then) documentados en cada test
 *  - Clases de equivalencia y valores límite cubiertos
 *
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║  Matriz de Clases de Equivalencia y Valores Límite                  ║
 * ╠══════════════╦══════════════════════════╦══════════════════════════╣
 * ║ Atributo     ║ Clase / Valor            ║ Resultado esperado       ║
 * ╠══════════════╬══════════════════════════╬══════════════════════════╣
 * ║ person       ║ null                     ║ INVALID                  ║
 * ║ id           ║ 0  (límite inferior)     ║ INVALID                  ║
 * ║ id           ║ -5 (clase negativa)      ║ INVALID                  ║
 * ║ id           ║ 1  (límite válido)       ║ continúa evaluación      ║
 * ║ alive        ║ false                    ║ DEAD                     ║
 * ║ alive        ║ true                     ║ continúa evaluación      ║
 * ║ age          ║ -1 (límite inferior inv) ║ INVALID_AGE              ║
 * ║ age          ║ 0  (límite borde bajo)   ║ UNDERAGE                 ║
 * ║ age          ║ 17 (límite menor edad)   ║ UNDERAGE                 ║
 * ║ age          ║ 18 (límite mayor edad)   ║ VALID                    ║
 * ║ age          ║ 30 (clase válida)        ║ VALID                    ║
 * ║ age          ║ 120 (límite superior)    ║ VALID                    ║
 * ║ age          ║ 121 (límite superior inv)║ INVALID_AGE              ║
 * ║ id duplicado ║ mismo id dos veces       ║ DUPLICATED               ║
 * ╚══════════════╩══════════════════════════╩══════════════════════════╝
 */
public class RegistryTest {

    private Registry registry;

    // ── Fixture ──────────────────────────────────────────────────────────────

    @Before
    public void setUp() {
        // Se crea una instancia fresca de Registry antes de cada prueba
        // para garantizar el aislamiento (sin estado compartido entre tests).
        registry = new Registry();
    }

    // ════════════════════════════════════════════════════════════════════════
    // BLOQUE 1: Validación de nulidad e id inválido
    // ════════════════════════════════════════════════════════════════════════

    /**
     * BDD:
     *   Given  la persona es null
     *   When   intento registrarla
     *   Then   el resultado debe ser INVALID
     */
    @Test
    public void shouldReturnInvalidWhenPersonIsNull() {
        // Arrange
        Person person = null;

        // Act
        RegisterResult result = registry.registerVoter(person);

        // Assert
        Assert.assertEquals(RegisterResult.INVALID, result);
    }

    /**
     * BDD:
     *   Given  la persona tiene id = 0 (límite inferior inválido)
     *   When   intento registrarla
     *   Then   el resultado debe ser INVALID
     */
    @Test
    public void shouldReturnInvalidWhenIdIsZero() {
        // Arrange
        Person person = new Person("Luis", 0, 25, Gender.MALE, true);

        // Act
        RegisterResult result = registry.registerVoter(person);

        // Assert
        Assert.assertEquals(RegisterResult.INVALID, result);
    }

    /**
     * BDD:
     *   Given  la persona tiene id = -5 (clase negativa)
     *   When   intento registrarla
     *   Then   el resultado debe ser INVALID
     */
    @Test
    public void shouldReturnInvalidWhenIdIsNegative() {
        // Arrange
        Person person = new Person("Maria", -5, 30, Gender.FEMALE, true);

        // Act
        RegisterResult result = registry.registerVoter(person);

        // Assert
        Assert.assertEquals(RegisterResult.INVALID, result);
    }

    // ════════════════════════════════════════════════════════════════════════
    // BLOQUE 2: Estado de vida
    // ════════════════════════════════════════════════════════════════════════

    /**
     * BDD:
     *   Given  la persona tiene 40 años, id válido, pero está muerta
     *   When   intento registrarla
     *   Then   el resultado debe ser DEAD
     */
    @Test
    public void shouldRejectDeadPerson() {
        // Arrange
        Person person = new Person("Carlos", 2, 40, Gender.MALE, false);

        // Act
        RegisterResult result = registry.registerVoter(person);

        // Assert
        Assert.assertEquals(RegisterResult.DEAD, result);
    }

    // ════════════════════════════════════════════════════════════════════════
    // BLOQUE 3: Validación de edad (INVALID_AGE)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * BDD:
     *   Given  la persona tiene edad = -1 (límite inferior inválido)
     *   When   intento registrarla
     *   Then   el resultado debe ser INVALID_AGE
     */
    @Test
    public void shouldReturnInvalidAgeWhenAgeIsNegative() {
        // Arrange
        Person person = new Person("Pedro", 3, -1, Gender.MALE, true);

        // Act
        RegisterResult result = registry.registerVoter(person);

        // Assert
        Assert.assertEquals(RegisterResult.INVALID_AGE, result);
    }

    /**
     * BDD:
     *   Given  la persona tiene edad = 121 (límite superior inválido)
     *   When   intento registrarla
     *   Then   el resultado debe ser INVALID_AGE
     */
    @Test
    public void shouldReturnInvalidAgeWhenAgeExceedsMaximum() {
        // Arrange
        Person person = new Person("Elena", 4, 121, Gender.FEMALE, true);

        // Act
        RegisterResult result = registry.registerVoter(person);

        // Assert
        Assert.assertEquals(RegisterResult.INVALID_AGE, result);
    }

    // ════════════════════════════════════════════════════════════════════════
    // BLOQUE 4: Menor de edad (UNDERAGE)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * BDD:
     *   Given  la persona tiene 17 años (límite inferior de mayoría de edad)
     *   When   intento registrarla
     *   Then   el resultado debe ser UNDERAGE
     */
    @Test
    public void shouldRejectUnderageAt17() {
        // Arrange
        Person person = new Person("Sofia", 5, 17, Gender.FEMALE, true);

        // Act
        RegisterResult result = registry.registerVoter(person);

        // Assert
        Assert.assertEquals(RegisterResult.UNDERAGE, result);
    }

    /**
     * BDD:
     *   Given  la persona tiene 0 años (límite borde bajo de la clase menor)
     *   When   intento registrarla
     *   Then   el resultado debe ser UNDERAGE
     */
    @Test
    public void shouldRejectUnderageAtZero() {
        // Arrange
        Person person = new Person("Bebe", 6, 0, Gender.UNIDENTIFIED, true);

        // Act
        RegisterResult result = registry.registerVoter(person);

        // Assert
        Assert.assertEquals(RegisterResult.UNDERAGE, result);
    }

    // ════════════════════════════════════════════════════════════════════════
    // BLOQUE 5: Registro válido (VALID)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * BDD:
     *   Given  la persona tiene 30 años, está viva y su id es único (caso típico)
     *   When   intento registrarla
     *   Then   el resultado debe ser VALID
     */
    @Test
    public void shouldRegisterValidPerson() {
        // Arrange
        Person person = new Person("Ana", 1, 30, Gender.FEMALE, true);

        // Act
        RegisterResult result = registry.registerVoter(person);

        // Assert
        Assert.assertEquals(RegisterResult.VALID, result);
    }

    /**
     * BDD:
     *   Given  la persona tiene exactamente 18 años (límite inferior de mayoría)
     *   When   intento registrarla
     *   Then   el resultado debe ser VALID
     */
    @Test
    public void shouldAcceptAdultAtExactly18() {
        // Arrange
        Person person = new Person("Juan", 7, 18, Gender.MALE, true);

        // Act
        RegisterResult result = registry.registerVoter(person);

        // Assert
        Assert.assertEquals(RegisterResult.VALID, result);
    }

    /**
     * BDD:
     *   Given  la persona tiene exactamente 120 años (límite superior válido)
     *   When   intento registrarla
     *   Then   el resultado debe ser VALID
     */
    @Test
    public void shouldAcceptPersonAtMaxAge120() {
        // Arrange
        Person person = new Person("Matilde", 8, 120, Gender.FEMALE, true);

        // Act
        RegisterResult result = registry.registerVoter(person);

        // Assert
        Assert.assertEquals(RegisterResult.VALID, result);
    }

    // ════════════════════════════════════════════════════════════════════════
    // BLOQUE 6: Duplicados
    // ════════════════════════════════════════════════════════════════════════

    /**
     * BDD:
     *   Given  una persona válida ya fue registrada con id = 777
     *   When   intento registrar otra persona con el mismo id = 777
     *   Then   el resultado de la segunda inscripción debe ser DUPLICATED
     */
    @Test
    public void shouldReturnDuplicatedWhenSameIdRegisteredTwice() {
        // Arrange
        Person firstPerson  = new Person("Jorge",  777, 25, Gender.MALE, true);
        Person secondPerson = new Person("Jorge2", 777, 28, Gender.MALE, true);
        registry.registerVoter(firstPerson); // primera inscripción exitosa

        // Act
        RegisterResult result = registry.registerVoter(secondPerson);

        // Assert
        Assert.assertEquals(RegisterResult.DUPLICATED, result);
    }

    /**
     * BDD:
     *   Given  tres personas con ids distintos y válidos
     *   When   las registro una a una
     *   Then   todas deben obtener VALID
     */
    @Test
    public void shouldRegisterMultipleUniqueVotersSuccessfully() {
        // Arrange
        Person p1 = new Person("Valentina", 10, 22, Gender.FEMALE, true);
        Person p2 = new Person("Ricardo",   11, 35, Gender.MALE,   true);
        Person p3 = new Person("Carmen",    12, 50, Gender.FEMALE, true);

        // Act
        RegisterResult r1 = registry.registerVoter(p1);
        RegisterResult r2 = registry.registerVoter(p2);
        RegisterResult r3 = registry.registerVoter(p3);

        // Assert
        Assert.assertEquals(RegisterResult.VALID, r1);
        Assert.assertEquals(RegisterResult.VALID, r2);
        Assert.assertEquals(RegisterResult.VALID, r3);
    }

    /**
     * BDD:
     *   Given  la misma instancia Registry con un registro existente (id=99)
     *   When   registro una persona diferente con id distinto (id=100)
     *   Then   el resultado debe ser VALID (no afecta el registro anterior)
     */
    @Test
    public void shouldNotConfuseDifferentIds() {
        // Arrange
        Person first  = new Person("Hugo",  99,  30, Gender.MALE, true);
        Person second = new Person("Paco", 100,  30, Gender.MALE, true);
        registry.registerVoter(first);

        // Act
        RegisterResult result = registry.registerVoter(second);

        // Assert
        Assert.assertEquals(RegisterResult.VALID, result);
    }
}
