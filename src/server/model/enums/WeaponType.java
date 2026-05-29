package server.model.enums;

public enum WeaponType {
    SWORD("Sword", 100),
    BOW("Bow", 90),
    STAFF("Staff", 80),
    CLAYMORE("Claymore", 120),
    POLEARM("Polearm", 110);

    private final String displayName;
    private final int baseDamage;

    WeaponType(String displayName, int baseDamage) {
        this.displayName = displayName;
        this.baseDamage = baseDamage;
    }

    public String getDisplayName() { return displayName; }
    public int getBaseDamage() { return baseDamage; }
}
