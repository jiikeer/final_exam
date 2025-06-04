package com.example.final_exam;

public class CharacterComponent {
    private String component;
    private String targetCharacter;
    private boolean isUsed;

    public CharacterComponent(String component, String targetCharacter) {
        this.component = component;
        this.targetCharacter = targetCharacter;
        this.isUsed = false;
    }

    public String getComponent() { return component; }
    public String getTargetCharacter() { return targetCharacter; }
    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean used) { isUsed = used; }
}