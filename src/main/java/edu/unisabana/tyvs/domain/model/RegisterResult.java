package edu.unisabana.tyvs.domain.model;

/**
 * Resultado posible del registro de un votante en la registraduría.
 *
 * VALID       - La persona fue registrada exitosamente.
 * DUPLICATED  - Ya existe un registro con el mismo número de documento.
 * INVALID     - La persona o sus datos son nulos/inválidos (id <= 0, persona null).
 * DEAD        - La persona no está viva; no puede votar.
 * UNDERAGE    - La persona es menor de 18 años.
 * INVALID_AGE - La edad está fuera del rango permitido (< 0 o > 120).
 */
public enum RegisterResult {
    VALID,
    DUPLICATED,
    INVALID,
    DEAD,
    UNDERAGE,
    INVALID_AGE
}
