package server.factory;

import server.model.abstracts.Character;
import server.model.characters.Hero;
import server.model.characters.Support;
import server.model.characters.Antagonist;
import server.model.enums.*;

import java.util.Random;

public class CharacterFactory implements SummonFactory<Character> {
    private Random random = new Random();

    private static final String[] HERO_NAMES = {
        "Rimuru", "Goku", "Naruto", "Luffy", "Ichigo",
        "Saitama", "Tanjiro", "Gojo", "Levi", "Eren"
    };
    private static final String[] SUPPORT_NAMES = {
        "Sakura", "Orihime", "Robin", "Bulma", "Hinata",
        "Nezuko", "ZeroTwo", "Rem", "Mikasa", "Asuna"
    };
    private static final String[] ANTAGONIST_NAMES = {
        "Frieza", "Madara", "Aizen", "Muzan", "Sukuna",
        "AllForOne", "Grimmjow", "Pain", "Dio", "Cell"
    };
    private static final String[] BOSS_TITLES = {
        "The Destroyer", "Dark Lord", "Shadow King", "Void Emperor", "Chaos Bringer"
    };

    @Override
    public Character create(Rarity rarity) {
        Element element = Element.values()[random.nextInt(Element.values().length)];
        WeaponType weapon = WeaponType.values()[random.nextInt(WeaponType.values().length)];
        int level = Math.max(rarity.getMinLevel(), random.nextInt(90) + 1);
        int type = random.nextInt(3);

        return switch (type) {
            case 0 -> new Hero(pickName(HERO_NAMES), rarity, element, level, weapon);
            case 1 -> new Support(pickName(SUPPORT_NAMES), rarity, element, level,
                random.nextBoolean() ? "HoT" : "Instant", 1.5 + random.nextDouble() * 2.0);
            default -> new Antagonist(pickName(ANTAGONIST_NAMES), rarity, element, level,
                BOSS_TITLES[random.nextInt(BOSS_TITLES.length)], rarity == Rarity.LEGENDARY);
        };
    }

    private String pickName(String[] names) {
        return names[random.nextInt(names.length)];
    }
}
