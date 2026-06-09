# Registro de Defectos – Taller Pruebas Unitarias Registraduría

> Archivo de gestión de defectos encontrados durante el desarrollo TDD.

---

## Defecto 01

| Campo            | Detalle                                                                                  |
|------------------|------------------------------------------------------------------------------------------|
| **ID**           | DEF-01                                                                                   |
| **Caso**         | Persona con edad negativa (`age = -1`)                                                   |
| **Esperado**     | `INVALID_AGE`                                                                            |
| **Obtenido**     | `VALID` (implementación inicial sin validación de rango de edad)                         |
| **Causa**        | La implementación stub de `Registry.registerVoter()` retornaba `VALID` sin validaciones |
| **Ciclo TDD**    | RED – la prueba `shouldReturnInvalidAgeWhenAgeIsNegative()` falló correctamente          |
| **Corrección**   | Se agregó: `if (person.getAge() < 0 || person.getAge() > MAX_AGE) return INVALID_AGE;`  |
| **Estado**       | ✅ Cerrado                                                                               |

---

## Defecto 02

| Campo            | Detalle                                                                                   |
|------------------|-------------------------------------------------------------------------------------------|
| **ID**           | DEF-02                                                                                    |
| **Caso**         | Persona con id = 0                                                                        |
| **Esperado**     | `INVALID`                                                                                 |
| **Obtenido**     | `VALID` (implementación inicial sin validación de id)                                     |
| **Causa**        | No existía regla para rechazar ids menores o iguales a 0                                  |
| **Ciclo TDD**    | RED – la prueba `shouldReturnInvalidWhenIdIsZero()` falló correctamente                   |
| **Corrección**   | Se agregó: `if (person.getId() < MIN_VALID_ID) return RegisterResult.INVALID;`            |
| **Estado**       | ✅ Cerrado                                                                                |

---

## Defecto 03

| Campo            | Detalle                                                                                   |
|------------------|-------------------------------------------------------------------------------------------|
| **ID**           | DEF-03                                                                                    |
| **Caso**         | Persona menor de 18 años (`age = 17`)                                                     |
| **Esperado**     | `UNDERAGE`                                                                                |
| **Obtenido**     | `VALID` (implementación inicial sin validación de edad mínima para votar)                 |
| **Causa**        | Faltaba regla de negocio: edad mínima para votar es 18 años                               |
| **Ciclo TDD**    | RED – la prueba `shouldRejectUnderageAt17()` falló correctamente                          |
| **Corrección**   | Se agregó: `if (person.getAge() < MIN_VOTING_AGE) return RegisterResult.UNDERAGE;`        |
| **Estado**       | ✅ Cerrado                                                                                |

---

## Defecto 04

| Campo            | Detalle                                                                                   |
|------------------|-------------------------------------------------------------------------------------------|
| **ID**           | DEF-04                                                                                    |
| **Caso**         | Segunda inscripción con el mismo id (id = 777)                                            |
| **Esperado**     | `DUPLICATED`                                                                              |
| **Obtenido**     | `VALID` (sin control de unicidad)                                                         |
| **Causa**        | La implementación inicial no llevaba registro de ids ya inscritos                         |
| **Ciclo TDD**    | RED – la prueba `shouldReturnDuplicatedWhenSameIdRegisteredTwice()` falló correctamente   |
| **Corrección**   | Se introdujo `Set<Integer> registeredIds` y la validación de duplicado antes de registrar |
| **Estado**       | ✅ Cerrado                                                                                |

---

> **Nota:** Todos los defectos fueron detectados por las pruebas unitarias **antes** de escribir el código de producción, siguiendo el ciclo TDD: RED → GREEN → REFACTOR.
