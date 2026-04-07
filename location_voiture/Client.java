package location_voiture;
import java.util.Objects;

public class Client implements Comparable<Client> {
    private String nom, prenom, cin, civilite;

    public Client(String civilite, String nom, String prenom, String cin) {
        this.civilite = civilite; this.nom = nom; this.prenom = prenom; this.cin = cin;
    }

    public String getNom()      { return nom; }
    public String getPrenom()   { return prenom; }
    public String getCin()      { return cin; }
    public String getCivilite() { return civilite; }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof Client)) return false;
        return cin.equals(((Client)obj).cin);
    }
    @Override public int hashCode() { return cin.hashCode(); }
    @Override public int compareTo(Client autre) {
        int c = this.nom.compareTo(autre.nom);
        return c != 0 ? c : this.prenom.compareTo(autre.prenom);
    }
    @Override public String toString() { return civilite + " " + nom + " " + prenom + " [" + cin + "]"; }
}