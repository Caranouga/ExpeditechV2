package fr.caranouga.expeditech.common.capabilities.tech;

public interface ITechLevel {
    int getTechLevel();
    int getTechXp();
    void setTechLevel(int techLevel);
    void setTechXp(int techXp);
    void addTechXp(int techXp);
    void addTechLevel(int techLevel);
    void set(ITechLevel oldTechLevel);
    int getTechXpToNextLevel();
    int getTotalXpToNextLevel();
}
