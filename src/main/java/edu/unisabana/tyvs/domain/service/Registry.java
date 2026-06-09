package edu.unisabana.tyvs.domain.service;

import edu.unisabana.tyvs.domain.model.Person;
import edu.unisabana.tyvs.domain.model.RegisterResult;

import java.util.HashSet;
import java.util.Set;

/**
 * Servicio de dominio: caso de uso "Registrar Votante".
 *
 * Reglas de negocio (orden de evaluación):
 *  1. La persona no puede ser null            → INVALID
 *  2. El id debe ser positivo (> 0)           → INVALID
 *  3. La persona debe estar viva              → DEAD
 *  4. La edad no puede ser negativa           → INVALID_AGE
 *  5. La edad no puede superar MAX_AGE        → INVALID_AGE
 *  6. La persona debe ser mayor de edad       → UNDERAGE
 *  7. No puede existir un registro duplicado  → DUPLICATED
 *  8. Todo correcto                           → VALID
 */
public class Registry {

    // ── Constantes de dominio ────────────────────────────────────────────────
    static final int MIN_VOTING_AGE = 18;
    static final int MAX_AGE        = 120;
    static final int MIN_VALID_ID   = 1;

    // ── Estado interno ───────────────────────────────────────────────────────
    private final Set<Integer> registeredIds = new HashSet<>();

    // ── Caso de uso principal ────────────────────────────────────────────────

    /**
     * Registra a una persona como votante, aplicando todas las reglas de negocio.
     *
     * @param person Persona a registrar (puede ser null).
     * @return {@link RegisterResult} con el resultado de la operación.
     */
    public RegisterResult registerVoter(Person person) {

        // Regla 1: null → INVALID
        if (person == null) {
            return RegisterResult.INVALID;
        }

        // Regla 2: id inválido → INVALID
        if (person.getId() < MIN_VALID_ID) {
            return RegisterResult.INVALID;
        }

        // Regla 3: persona muerta → DEAD
        if (!person.isAlive()) {
            return RegisterResult.DEAD;
        }

        // Regla 4 y 5: edad fuera de rango → INVALID_AGE
        if (person.getAge() < 0 || person.getAge() > MAX_AGE) {
            return RegisterResult.INVALID_AGE;
        }

        // Regla 6: menor de edad → UNDERAGE
        if (person.getAge() < MIN_VOTING_AGE) {
            return RegisterResult.UNDERAGE;
        }

        // Regla 7: duplicado → DUPLICATED
        if (registeredIds.contains(person.getId())) {
            return RegisterResult.DUPLICATED;
        }

        // Regla 8: registro exitoso
        registeredIds.add(person.getId());
        return RegisterResult.VALID;
    }
}
