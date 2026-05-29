package server.service;

import server.model.player.Player;
import java.util.*;

public class ResourceSystem {
    private Player player;
    private int loginStreak;
    private int totalDaysPlayed;
    private int missionsCompleted;
    private int achievementsUnlocked;
    private Map<String, Boolean> dailyMissionStatus;
    private Map<String, Boolean> achievements;
    private List<String> rewardHistory;
    private long lastLoginDate;
    private long lastDailyClaim;

    public ResourceSystem(Player player) {
        this.player = player;
        this.loginStreak = 0;
        this.totalDaysPlayed = 0;
        this.missionsCompleted = 0;
        this.achievementsUnlocked = 0;
        this.dailyMissionStatus = new LinkedHashMap<>();
        this.achievements = new LinkedHashMap<>();
        this.rewardHistory = new ArrayList<>();
        this.lastLoginDate = 0;
        this.lastDailyClaim = 0;
        initDailyMissions();
        initAchievements();
    }

    private void initDailyMissions() {
        dailyMissionStatus.put("Summon 1 character", false);
        dailyMissionStatus.put("Perform a 10x summon", false);
        dailyMissionStatus.put("Get a Rare or better", false);
        dailyMissionStatus.put("Check inventory", false);
        dailyMissionStatus.put("Level up a character", false);
    }

    private void initAchievements() {
        achievements.put("First Summon", false);
        achievements.put("Summon 10 characters", false);
        achievements.put("Summon 50 characters", false);
        achievements.put("Summon 100 characters", false);
        achievements.put("Get first Legendary", false);
        achievements.put("Collect 5 Legendaries", false);
        achievements.put("First Constellation", false);
        achievements.put("10 Constellations", false);
        achievements.put("Fill inventory to 50%", false);
        achievements.put("Login 7 days streak", false);
    }

    // === DAILY LOGIN REWARD ===
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

        int reward = calculateDailyReward(loginStreak);
        player.addGems(reward);

        String msg = String.format("Day %d Login Reward: +%,d Gems! (Streak: %d days)",
            totalDaysPlayed, reward, loginStreak);
        rewardHistory.add("[Daily] " + msg);
        checkAchievement("Login 7 days streak", loginStreak >= 7);
        return msg;
    }

    private int calculateDailyReward(int streak) {
        int base = 100;
        int bonus = Math.min(streak - 1, 6) * 50;
        if (streak % 7 == 0) return base + bonus + 500;
        return base + bonus;
    }

    // === DAILY MISSIONS ===
    public String completeMission(String missionName) {
        if (!dailyMissionStatus.containsKey(missionName)) {
            return "Unknown mission: " + missionName;
        }
        if (dailyMissionStatus.get(missionName)) {
            return "Mission already completed: " + missionName;
        }

        dailyMissionStatus.put(missionName, true);
        missionsCompleted++;

        int reward = getMissionReward(missionName);
        player.addGems(reward);

        String msg = String.format("Mission done: %s -> +%,d Gems!", missionName, reward);
        rewardHistory.add("[Mission] " + msg);

        if (allMissionsDone()) {
            player.addGems(300);
            rewardHistory.add("[Bonus] All daily missions completed: +300 Gems!");
            msg += " | ALL MISSIONS BONUS: +300 Gems!";
        }

        return msg;
    }

    private int getMissionReward(String mission) {
        if (mission.contains("10x")) return 80;
        if (mission.contains("Rare")) return 100;
        if (mission.contains("Level")) return 120;
        return 60;
    }

    private boolean allMissionsDone() {
        return dailyMissionStatus.values().stream().allMatch(Boolean::booleanValue);
    }

    // === ACHIEVEMENTS ===
    public String checkAndUnlockAchievement(String name, boolean condition) {
        if (!achievements.containsKey(name)) return "";
        if (achievements.get(name)) return "";
        if (!condition) return "";

        achievements.put(name, true);
        achievementsUnlocked++;

        int reward = getAchievementReward(name);
        player.addGems(reward);

        String msg = String.format("Achievement Unlocked: %s -> +%,d Gems!", name, reward);
        rewardHistory.add("[Achievement] " + msg);
        return msg;
    }

    private void checkAchievement(String name, boolean condition) {
        checkAndUnlockAchievement(name, condition);
    }

    private int getAchievementReward(String name) {
        if (name.contains("Legendary")) return 500;
        if (name.contains("50") || name.contains("100")) return 1000;
        if (name.contains("Constellation")) return 400;
        if (name.contains("inventory")) return 300;
        return 200;
    }

    // === QUEST BOARD (Weekly-style) ===
    public String claimQuestReward(String questType) {
        int reward;
        String desc;
        switch (questType) {
            case "beginner" -> { reward = 2000; desc = "Beginner Pack"; }
            case "adventurer" -> { reward = 5000; desc = "Adventurer Pack"; }
            case "veteran" -> { reward = 10000; desc = "Veteran Pack"; }
            default -> { return "Unknown quest type."; }
        }
        player.addGems(reward);
        String msg = String.format("Quest Reward [%s]: +%,d Gems!", desc, reward);
        rewardHistory.add("[Quest] " + msg);
        return msg;
    }

    // === REFERRAL ===
    public String useReferralCode(String code) {
        if (code == null || code.length() < 3) {
            return "Invalid referral code.";
        }
        int reward = 1600;
        player.addGems(reward);
        String msg = String.format("Referral Bonus: +%,d Gems!", reward);
        rewardHistory.add("[Referral] " + msg);
        return msg;
    }

    // === CONVERTER (duplicates -> gems) ===
    public String convertDuplicateToGems(int duplicateCount) {
        if (duplicateCount <= 0) return "No duplicates to convert.";
        int gems = duplicateCount * 50;
        player.addGems(gems);
        String msg = String.format("Converted %d duplicates -> +%,d Gems!", duplicateCount, gems);
        rewardHistory.add("[Convert] " + msg);
        return msg;
    }

    // === Getters ===
    public int getLoginStreak() { return loginStreak; }
    public int getTotalDaysPlayed() { return totalDaysPlayed; }
    public int getMissionsCompleted() { return missionsCompleted; }
    public int getAchievementsUnlocked() { return achievementsUnlocked; }
    public Map<String, Boolean> getDailyMissions() { return dailyMissionStatus; }
    public Map<String, Boolean> getAchievements() { return achievements; }
    public List<String> getRewardHistory() { return rewardHistory; }

    public String getResourceSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("======= RESOURCES =======\n");
        sb.append(String.format("Gems: %,d\n", player.getGems()));
        sb.append(String.format("Login Streak: %d days (Day %d overall)\n", loginStreak, totalDaysPlayed));
        sb.append(String.format("Daily Missions: %d/%d completed\n",
            dailyMissionStatus.values().stream().filter(Boolean::booleanValue).count(),
            dailyMissionStatus.size()));
        sb.append(String.format("Achievements: %d/%d unlocked\n",
            achievementsUnlocked, achievements.size()));
        sb.append(String.format("Total Rewards Earned: %d\n", rewardHistory.size()));
        return sb.toString();
    }
}
