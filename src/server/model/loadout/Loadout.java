package server.model.loadout;

import server.model.abstracts.Character;
import server.model.items.Artifact;
import server.model.items.Weapon;

public class Loadout {
    private String characterId;
    private String characterName;
    private String weaponId;
    private String weaponName;
    private String artifactId;
    private String artifactName;

    public Loadout(Character character, Weapon weapon, Artifact artifact) {
        update(character, weapon, artifact);
    }

    public void update(Character character, Weapon weapon, Artifact artifact) {
        if (character == null) throw new IllegalArgumentException("Character is required");
        this.characterId = character.getId();
        this.characterName = character.getName();
        this.weaponId = weapon != null ? weapon.getId() : null;
        this.weaponName = weapon != null ? weapon.getName() : null;
        this.artifactId = artifact != null ? artifact.getId() : null;
        this.artifactName = artifact != null ? artifact.getName() : null;
    }

    public String getCharacterId() { return characterId; }
    public String getCharacterName() { return characterName; }
    public String getWeaponId() { return weaponId; }
    public String getWeaponName() { return weaponName; }
    public String getArtifactId() { return artifactId; }
    public String getArtifactName() { return artifactName; }

    public String getSummary() {
        return characterName + " | Weapon: " + (weaponName != null ? weaponName : "None") +
            " | Artifact: " + (artifactName != null ? artifactName : "None");
    }
}
