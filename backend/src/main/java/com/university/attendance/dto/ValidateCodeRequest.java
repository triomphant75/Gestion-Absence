package com.university.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO pour valider le code dynamique de présence
 */
public class ValidateCodeRequest {

    @NotNull(message = "L'ID de la séance est obligatoire")
    private Long seanceId;

    @NotBlank(message = "Le code est obligatoire")
    private String code;

    // Constructeurs
    public ValidateCodeRequest() {
    }

    public ValidateCodeRequest(Long seanceId, String code) {
        this.seanceId = seanceId;
        this.code = code;
    }

    // Getters et Setters
    public Long getSeanceId() {
        return seanceId;
    }

    public void setSeanceId(Long seanceId) {
        this.seanceId = seanceId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
