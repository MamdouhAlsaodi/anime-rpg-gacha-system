package api.protocol;

public enum CommandCode {
    SUMMON_SINGLE(160),
    SUMMON_TEN(1600),
    VIEW_INVENTORY(0),
    VIEW_PLAYER(0),
    LEVEL_UP(0),
    ENHANCE_ITEM(0),
    HELP(0),
    EXIT(0);

    private final int gemCost;

    CommandCode(int gemCost) {
        this.gemCost = gemCost;
    }

    public int getGemCost() { return gemCost; }
}
