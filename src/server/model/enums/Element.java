package server.model.enums;

public enum Element {
    FIRE("Fire", "#FF5722"),
    ICE("Ice", "#4FC3F7"),
    LIGHTNING("Lightning", "#FFEB3B"),
    WIND("Wind", "#4CAF50"),
    EARTH("Earth", "#8D6E63");

    private final String displayName;
    private final String color;

    Element(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() { return displayName; }
    public String getColor() { return color; }
}
