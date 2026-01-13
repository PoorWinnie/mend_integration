package com.example.back.dto;

public class FeatureTagSimpleDTO {

    private Integer tagId;
    private String tagName;

    public FeatureTagSimpleDTO() {
    }

    public FeatureTagSimpleDTO(Integer tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
