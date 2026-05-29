package server.service;

import server.model.player.Player;
import server.model.player.Inventory;
import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;
import server.model.enums.Rarity;
import java.util.*;

public class ResourceSystem {
    private Player player;
    private int loginStreak;
    private int totalDaysPlayed;
    private int missionsCompleted;
    private int achievementsUnlocked;
    private Map<String, Mission> dailyMissions;
    private Map<String, Boolean> achievements;
    private List<String> rewardHistory;
    private long lastDailyClaim;
    private int todayPulls;
    private int todayLegendaryPulls;
    private int todayRarePulls;
    private int todayCommonPulls;
    private int todayConstellations;
    private int todayLevelUps;
    private int todayEnhances;
    private int todayInventoryChecks;
    private boolean todayUsedReferral;

    public ResourceSystem(Player player) {
        this.player = player;
        this.loginStreak = 0;
        this.totalDaysPlayed = 0;
        this.missionsCompleted = 0;
        this.achievementsUnlocked = 0;
        this.dailyMissions = new LinkedHashMap<>();
        this.achievements = new LinkedHashMap<>();
        this.rewardHistory = new ArrayList<>();
        this.lastDailyClaim = 0;
        this.todayPulls = 0;
        this.todayLegendaryPulls = 0;
        this.todayRarePulls = 0;
        this.todayCommonPulls = 0;
        this.todayConstellations = 0;
        this.todayLevelUps = 0;
        this.todayEnhances = 0;
        this.todayInventoryChecks = 0;
        this.todayUsedReferral = false;
        initDailyMissions();
        initAchievements();
    }

    private void initDailyMissions() {
        dailyMissions.put("first_login",    new Mission("Daily Login",          "Login once per day",                           100,  Mission.Type.LOGIN));
        dailyMissions.put("summon_1",       new Mission("First Summon",         "Perform at least 1 summon",                    60,   Mission.Type.SUMMON,     1));
        dailyMissions.put("summon_10",      new Mission("Multi Summon",         "Perform a 10x summon",                         80,   Mission.Type.MULTI_SUMMON));
        dailyMissions.put("get_rare",       new Mission("Lucky Pull",           "Get a Rare or better item",                    100,  Mission.Type.GET_RARE));
        dailyMissions.put("get_legendary",  new Mission("Jackpot!",             "Get a Legendary character or item",            300,  Mission.Type.GET_LEGENDARY));
        dailyMissions.put("constellation",  new Mission("Constellation Master", "Trigger a constellation upgrade",              150,  Mission.Type.CONSTELLATION));
        dailyMissions.put("level_up",       new Mission("Power Up",             "Level up any character",                       120,  Mission.Type.LEVEL_UP));
        dailyMissions.put("enhance",        new Mission("Blacksmith",           "Enhance any weapon or artifact",               100,  Mission.Type.ENHANCE));
        dailyMissions.put("pulls_5",        new Mission("Gacha Addict",         "Perform 5 total summons today",                80,   Mission.Type.TOTAL_PULLS, 5));
        dailyMissions.put("pulls_20",       new Mission("Gacha Fever",          "Perform 20 total summons today",               200,  Mission.Type.TOTAL_PULLS, 20));
        dailyMissions.put("check_inv",      new Mission("Collector",            "Check your inventory",                         50,   Mission.Type.CHECK_INV));
        dailyMissions.put("all_3_rarities", new Mission("Rarity Hunter",        "Get all 3 rarities (Common+Rare+Legendary)",  400,  Mission.Type.ALL_RARITIES));
    }

    private void initAchievements() {
        achievements.put("First Summon",          false);
        achievements.put("Summon 10 characters",  false);
        achievements.put("Summon 50 characters",  false);
        achievements.put("Summon 100 characters", false);
        achievements.put("Summon 500 characters", false);
        achievements.put("Get first Legendary",   false);
        achievements.put("Collect 5 Legendaries", false);
        achievements.put("Collect 20 Legendaries",false);
        achievements.put("First Constellation",   false);
        achievements.put("10 Constellations",     false);
        achievements.put("50 Constellations",     false);
        achievements.put("Fill inventory 50%",    false);
        achievements.put("Fill inventory 100%",   false);
        achievements.put("Login 7 days streak",   false);
        achievements.put("Login 30 days streak",  false);
    }

    // === MISSION TRACKING (called by GameEngine) ===
    public void onSummon(int count, Rarity rarity) {
        todayPulls += count;
        completeMissionProgress("summon_1", 1);
        if (count >= 10) completeMissionProgress("summon_10", 1);
        if (rarity == Rarity.RARE || rarity == Rarity.LEGENDARY) completeMissionProgress("get_rare", 1);
        if (rarity == Rarity.LEGENDARY) {
            todayLegendaryPulls++;
            completeMissionProgress("get_legendary", 1);
        }
        if (rarity == Rarity.RARE) todayRarePulls++;
        if (rarity == Rarity.COMMON) todayCommonPulls++;
        if (todayPulls >= 5) completeMissionProgress("pulls_5", 1);
        if (todayPulls >= 20) completeMissionProgress("pulls_20", 1);
        if (todayLegendaryPulls > 0 && todayRarePulls > 0 && todayCommonPulls > 0) {
            completeMissionProgress("all_3_rarities", 1);
        }
    }

    public void onConstellation() {
        todayConstellations++;
        completeMissionProgress("constellation", 1);
    }

    public void onLevelUp() {
        todayLevelUps++;
        completeMissionProgress("level_up", 1);
    }

    public void onEnhance() {
        todayEnhances++;
        completeMissionProgress("enhance", 1);
    }

    public void onInventoryCheck() {
        todayInventoryChecks++;
        completeMissionProgress("check_inv", 1);
    }

    // === DAILY LOGIN ===
    public String claimDailyLogin() {
        long now = System.currentTimeMillis();
        long todayStart = (now / 86400000) * 86400000;

        if (lastDailyClaim >= todayStart) {
            return "Already claimed today! Come back tomorrow.";
        }

        if (lastDailyClaim >= todayStart - 86400000) {
            loginStreak++;
        } else {
            loginStreak = 1;
        }

        totalDaysPlayed++;
        lastDailyClaim = now;
        resetDailyMissions();

        int reward = calculateDailyReward(loginStreak);
        player.addGems(reward);
        player.addCommonCurrency(80);
        player.addRareCurrency(8);
        completeMissionProgress("first_login", 1);

        String msg = String.format("Day %d Login: +%,d Gems, +80 C, +8 R! (Streak: %d days)%s",
            totalDaysPlayed, reward, loginStreak,
            loginStreak % 7 == 0 ? " BONUS WEEK!" : "");
        rewardHistory.add("[Daily] " + msg);
        checkAchievement("Login 7 days streak", loginStreak >= 7);
        checkAchievement("Login 30 days streak", loginStreak >= 30);
        return msg;
    }

    private int calculateDailyReward(int streak) {
        int base = 100;
        int streakBonus = Math.min(streak - 1, 6) * 50;
        int weekBonus = (streak % 7 == 0 && streak > 0) ? 500 : 0;
        return base + streakBonus + weekBonus;
    }

    // === MISSION SYSTEM ===
    private void completeMissionProgress(String id, int amount) {
        Mission m = dailyMissions.get(id);
        if (m != null && !m.completed) {
            m.progress += amount;
            if (m.progress >= m.required) {
                m.completed = true;
                missionsCompleted++;
                player.addGems(m.reward);
                player.addCommonCurrency(25);
                if (m.completed && m.reward >= 120) player.addRareCurrency(3);
                String msg = String.format("Mission: %s -> +%,d Gems, +25 C%s!", m.name, m.reward, m.reward >= 120 ? ", +3 R" : "");
                rewardHistory.add("[Mission] " + msg);
                checkAllMissionsBonus();
            }
        }
    }

    private void checkAllMissionsBonus() {
        long done = dailyMissions.values().stream().filter(m -> m.completed).count();
        long total = dailyMissions.size();
        if (done == total) {
            int bonus = 500;
            player.addGems(bonus);
            rewardHistory.add(String.format("[BONUS] ALL %d missions done! +%,d Gems!", total, bonus));
        }
    }

    private void resetDailyMissions() {
        todayPulls = 0;
        todayLegendaryPulls = 0;
        todayRarePulls = 0;
        todayCommonPulls = 0;
        todayConstellations = 0;
        todayLevelUps = 0;
        todayEnhances = 0;
        todayInventoryChecks = 0;
        todayUsedReferral = false;
        for (Mission m : dailyMissions.values()) {
            m.progress = 0;
            m.completed = false;
        }
    }

    // === ACHIEVEMENTS ===
    public String checkAndUnlockAchievement(String name, boolean condition) {
        if (!achievements.containsKey(name) || achievements.get(name) || !condition) return "";
        achievements.put(name, true);
        achievementsUnlocked++;
        int reward = getAchievementReward(name);
        player.addGems(reward);
        String msg = String.format("Achievement: %s -> +%,d Gems!", name, reward);
        rewardHistory.add("[Achievement] " + msg);
        return msg;
    }

    private void checkAchievement(String name, boolean condition) {
        checkAndUnlockAchievement(name, condition);
    }

    private int getAchievementReward(String name) {
        if (name.contains("500")) return 2000;
        if (name.contains("100")) return 1000;
        if (name.contains("50")) return 1000;
        if (name.contains("20 Leg")) return 2000;
        if (name.contains("Legendary")) return 500;
        if (name.contains("Constellation")) return 400;
        if (name.contains("100%")) return 1500;
        if (name.contains("50%")) return 300;
        if (name.contains("30 days")) return 3000;
        return 200;
    }

    // === QUEST PACKS ===
    public String claimQuestReward(String questType) {
        int reward;
        String desc;
        switch (questType) {
            case "beginner"   -> { reward = 2000;  desc = "Beginner Pack"; }
            case "adventurer" -> { reward = 5000;  desc = "Adventurer Pack"; }
            case "veteran"    -> { reward = 10000; desc = "Veteran Pack"; }
            case "whale"      -> { reward = 50000; desc = "Whale Pack"; }
            default           -> { return "Unknown quest type."; }
        }
        player.addGems(reward);
        player.addCommonCurrency(reward / 20);
        player.addRareCurrency(Math.max(5, reward / 500));
        String msg = String.format("Quest [%s]: +%,d Gems, +%d C, +%d R!", desc, reward, reward / 20, Math.max(5, reward / 500));
        rewardHistory.add("[Quest] " + msg);
        return msg;
    }

    // === REFERRAL ===
    public String useReferralCode(String code) {
        if (todayUsedReferral) return "Already used a referral today.";
        if (code == null || code.length() < 3) return "Invalid code.";
        todayUsedReferral = true;
        int reward = 1600;
        player.addGems(reward);
        String msg = String.format("Referral: +%,d Gems!", reward);
        rewardHistory.add("[Referral] " + msg);
        return msg;
    }

    // === CONVERT DUPLICATES ===
    public String convertDuplicateToGems(int count) {
        if (count <= 0) return "No duplicates.";
        int gems = count * 50;
        player.addGems(gems);
        String msg = String.format("Converted %d dupes -> +%,d Gems!", count, gems);
        rewardHistory.add("[Convert] " + msg);
        return msg;
    }

    // === Getters ===
    public int getLoginStreak() { return loginStreak; }
    public int getTotalDaysPlayed() { return totalDaysPlayed; }
    public int getMissionsCompleted() { return missionsCompleted; }
    public int getAchievementsUnlocked() { return achievementsUnlocked; }
    public List<String> getRewardHistory() { return rewardHistory; }
    public Map<String, Boolean> getAchievements() { return achievements; }
    public Map<String, Mission> getDailyMissions() { return Collections.unmodifiableMap(dailyMissions); }

    public String getDailyMissionsDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("======= DAILY MISSIONS =======\n");
        for (Map.Entry<String, Mission> e : dailyMissions.entrySet()) {
            Mission m = e.getValue();
            String status = m.completed ? "[DONE]" : "[" + m.progress + "/" + m.required + "]";
            String gemStr = String.format("%d", m.reward);
            sb.append("  " + status + "  " + String.format("%-28s", m.name) + " " + m.description + "  +" + gemStr + " Gems\n");
        }
        long done = dailyMissions.values().stream().filter(m -> m.completed).count();
        sb.append(String.format("\n  Progress: %d/%d missions done\n", done, dailyMissions.size()));
        if (done == dailyMissions.size()) {
            sb.append("  ALL MISSIONS COMPLETE! +500 Bonus Gems!\n");
        }
        return sb.toString();
    }

    public String getResourceSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("======= RESOURCES =======\n");
        sb.append(String.format("Gems: %,d | C: %,d | R: %,d\n", player.getGems(), player.getCommonCurrency(), player.getRareCurrency()));
        sb.append(String.format("Login Streak: %d days (Day %d overall)\n", loginStreak, totalDaysPlayed));
        sb.append(String.format("Today Pulls: %d (L:%d R:%d C:%d)\n",
            todayPulls, todayLegendaryPulls, todayRarePulls, todayCommonPulls));
        sb.append(String.format("Achievements: %d/%d\n", achievementsUnlocked, achievements.size()));
        return sb.toString();
    }

    // === INNER CLASS: Mission ===
    public static class Mission {
        public enum Type { LOGIN, SUMMON, MULTI_SUMMON, GET_RARE, GET_LEGENDARY,
            CONSTELLATION, LEVEL_UP, ENHANCE, TOTAL_PULLS, CHECK_INV, ALL_RARITIES }
        public String name;
        public String description;
        public int reward;
        public Type type;
        public int progress;
        public int required;
        public boolean completed;

        public Mission(String name, String description, int reward, Type type) {
            this(name, description, reward, type, 1);
        }

        public Mission(String name, String description, int reward, Type type, int required) {
            this.name = name;
            this.description = description;
            this.reward = reward;
            this.type = type;
            this.progress = 0;
            this.required = required;
            this.completed = false;
        }
    }
}