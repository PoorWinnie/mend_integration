package com.example.back.dto;

public class PetPolicyDTO {
    private Integer petPolicyId;
    private String petPolicyName;

    public PetPolicyDTO(Integer petPolicyId, String petPolicyName) {
        this.petPolicyId = petPolicyId;
        this.petPolicyName = petPolicyName;
    }

    public Integer getPetPolicyId() {
        return petPolicyId;
    }

    public String getPetPolicyName() {
        return petPolicyName;
    }
}

