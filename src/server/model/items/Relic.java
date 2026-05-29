package server.model.items;

import server.model.abstracts.InventoryItem;
import server.model.enums.Rarity;

public class Relic extends InventoryItem {
    private String origin;
    private int ageInYears;
    private double mysticalPower;

    public Relic(String name, Rarity rarity, int level, String origin, int ageInYears, double mysticalPower) {
        super(name, rarity, level);
        this.origin = origin;
        this.ageInYears = ageInYears;
        this.mysticalPower = mysticalPower;
    }

    @Override
    public String getItemType() { return "Relic"; }

    public double getTotalPower() {
        return mysticalPower * (1 + level * 0.1) * ageInYears;
    }

    @Override
    public String describeYourself() {
        return "[Relic] " + name + " | " + rarity + " | Origin: " + origin +
            " | Age: " + ageInYears + " years | Power: " + String.format("%.1f", getTotalPower());
    }

    public String getOrigin() { return origin; }
    public int getAgeInYears() { return ageInYears; }
    public double getMysticalPower() { return mysticalPower; }
}
