package server.service;

import server.model.abstracts.Character;
import server.model.characters.Hero;
import server.model.player.Inventory;
import server.exception.DuplicateCharacterException;

public class ConstellationSystem {

    public String processDuplicate(Inventory inv, Hero existingHero, Character duplicate) {
        try {
            inv.addCharacter(duplicate);
            return "Character added normally";
        } catch (DuplicateCharacterException e) {
            existingHero.upgradeConstellation();
            return "Constellation upgrade! " + existingHero.getName() + " is now C" + existingHero.getConstellation();
        } catch (Exception e) {
            return "Error processing duplicate: " + e.getMessage();
        }
    }

    public int getConstellationLevel(Hero hero) {
        return hero.getConstellation();
    }

    public boolean isMaxConstellation(Hero hero) {
        return hero.getConstellation() >= 6;
    }
}
