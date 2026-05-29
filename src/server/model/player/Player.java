package server.model.player;

import server.exception.InsufficientGemsException;

public class Player {
    private String name;
    private int gems;
    private int pityCounter;
    private int softPityCounter;
    private int totalPulls;
    private int commonCurrency; // C: common upgrade material
    private int rareCurrency;   // R: rare upgrade material

    public Player(String name, int gems) {
        this.name = name;
        this.gems = gems;
        this.pityCounter = 0;
        this.softPityCounter = 0;
        this.totalPulls = 0;
        this.commonCurrency = 500;
        this.rareCurrency = 120;
    }

    public void spendGems(int amount) throws InsufficientGemsException {
        if (gems < amount) {
            throw new InsufficientGemsException(amount, gems);
        }
        gems -= amount;
        totalPulls++;
    }

    public void addGems(int amount) {
        gems += amount;
    }

    public void addCommonCurrency(int amount) { commonCurrency += Math.max(0, amount); }
    public void addRareCurrency(int amount) { rareCurrency += Math.max(0, amount); }

    public boolean spendCommonCurrency(int amount) {
        if (amount < 0 || commonCurrency < amount) return false;
        commonCurrency -= amount;
        return true;
    }

    public boolean spendRareCurrency(int amount) {
        if (amount < 0 || rareCurrency < amount) return false;
        rareCurrency -= amount;
        return true;
    }

    public boolean spendUpgradeCurrencies(int commonCost, int rareCost) {
        if (commonCurrency < commonCost || rareCurrency < rareCost) return false;
        commonCurrency -= commonCost;
        rareCurrency -= rareCost;
        return true;
    }

    public void incrementPity() {
        pityCounter++;
        softPityCounter++;
    }

    public void resetHardPity() { pityCounter = 0; }
    public void resetSoftPity() { softPityCounter = 0; }

    public String getName() { return name; }
    public int getGems() { return gems; }
    public int getPityCounter() { return pityCounter; }
    public int getSoftPityCounter() { return softPityCounter; }
    public int getTotalPulls() { return totalPulls; }
    public int getCommonCurrency() { return commonCurrency; }
    public int getRareCurrency() { return rareCurrency; }

    public void setName(String name) { this.name = name; }
    public void setGems(int gems) { this.gems = gems; }
    public void setTotalPulls(int totalPulls) { this.totalPulls = totalPulls; }
    public void setCommonCurrency(int commonCurrency) { this.commonCurrency = Math.max(0, commonCurrency); }
    public void setRareCurrency(int rareCurrency) { this.rareCurrency = Math.max(0, rareCurrency); }

    @Override
    public String toString() {
        return "Player: " + name + " | Gems: " + gems + " | C: " + commonCurrency + " | R: " + rareCurrency +
            " | Pity: " + pityCounter + " | Soft Pity: " + softPityCounter + " | Total Pulls: " + totalPulls;
    }
}
