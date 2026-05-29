package server.model.enums;

public enum Rarity {
    COMMON(0.943, 1, "#888888"),
    RARE(0.051, 10, "#4FC3F7"),
    LEGENDARY(0.006, 1, "#FFD700");

    private final double dropRate;
    private final int minLevel;
    private final String color;

    Rarity(double dropRate, int minLevel, String color) {
        this.dropRate = dropRate;
        this.minLevel = minLevel;
        this.color = color;
    }

    public double getDropRate() { return dropRate; }
    public int getMinLevel() { return minLevel; }
    public String getColor() { return color; }
}
