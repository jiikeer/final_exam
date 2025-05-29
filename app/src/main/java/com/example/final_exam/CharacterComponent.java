package com.example.final_exam;

public class CharacterComponent {
    private String component; // 部件，如"日"
    private String targetCharacter; // 对应的目标汉字，如"明"
    private boolean isUsed; // 是否已被使用

    public CharacterComponent(String component, String targetCharacter) {
        this.component = component;
        this.targetCharacter = targetCharacter;
        this.isUsed = false;
    }

    // Getters and setters
    public String getComponent() { return component; }
    public String getTargetCharacter() { return targetCharacter; }
    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean used) { isUsed = used; }
}
