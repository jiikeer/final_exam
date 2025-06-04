package com.example.final_exam;

import java.io.Serializable;

public class IdiomModel implements Serializable {
    private int id;
    private String idiom;
    private String explanation;
    private String level;
    private String interferingParts;

    public IdiomModel(int id, String idiom, String explanation, String level, String interferingParts) {
        this.id = id;
        this.idiom = idiom;
        this.explanation = explanation;
        this.level = level;
        this.interferingParts = interferingParts;
    }

    public int getId() { return id; }
    public String getIdiom() { return idiom; }
    public String getExplanation() { return explanation; }
    public String getLevel() { return level; }
    public String getInterferingParts() { return interferingParts; } // 新增 getter
}