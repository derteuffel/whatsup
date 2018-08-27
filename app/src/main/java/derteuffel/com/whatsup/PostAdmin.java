package derteuffel.com.whatsup;

import java.util.List;

public class PostAdmin {

    private String titre, description, userName;
    private List<String> pieces;

    public PostAdmin() {
    }

    public PostAdmin(String titre, String description, String userName, List<String> pieces) {
        this.titre = titre;
        this.description = description;
        this.userName = userName;
        this.pieces = pieces;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getPieces() {
        return pieces;
    }

    public void setPieces(List<String> pieces) {
        this.pieces = pieces;
    }
}
