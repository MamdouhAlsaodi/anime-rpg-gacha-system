package server.exception;

public class DuplicateCharacterException extends GachaBaseException {
    private String characterName;

    public DuplicateCharacterException(String characterName) {
        super("Duplicate character: " + characterName + "! Converting to constellation.", 1003);
        this.characterName = characterName;
    }

    @Override
    public String getErrorCategory() { return "CONSTELLATION"; }

    public String getCharacterName() { return characterName; }
}
