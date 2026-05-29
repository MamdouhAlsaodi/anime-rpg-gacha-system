package server.model.player;

import server.exception.InsufficientGemsException;

public class Player {
    private String name;
    private int gems;
    private int pityCounter;
    private int softPityCounter;
    private int totalPulls;

    public Player(String name, int gems) {
        this.name = name;
        this.gems = gems;
        this.pityCounter = 0;
        this.softPityCounter = 0;
        this.totalPulls = 0;
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

    public void setName(String name) { this.name = name; }
    public void setGems(int gems) { this.gems = gems; }
    public void setTotalPulls(int totalPulls) { this.totalPulls = totalPulls; }

    @Override
    public String toString() {
        return "Player: " + name + " | Gems: " + gems + " | Pity: " + pityCounter + " | Soft Pity: " + softPityCounter + " | Total Pulls: " + totalPulls;
    }
}
