package location_voiture;
import java.util.Objects;

public class Voiture {
    private String marque;
    private String modele;
    private int annee;
    private int prixJournee;

    public Voiture(String marque, String modele, int annee, int prixJournee) {
        this.marque = marque;
        this.modele = modele;
        this.annee = annee;
        this.prixJournee = prixJournee;
    }

    public String getMarque()   { return marque; }
    public String getModele()   { return modele; }
    public int getAnnee()       { return annee; }
    public int getPrixJournee() { return prixJournee; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Voiture)) return false;
        Voiture autre = (Voiture) obj;
        return annee == autre.annee && prixJournee == autre.prixJournee
            && Objects.equals(marque, autre.marque)
            && Objects.equals(modele, autre.modele);
    }

    @Override public int hashCode() { return Objects.hash(marque, modele, annee, prixJournee); }

    @Override public String toString() { return marque + " " + modele + " (" + annee + ") — " + prixJournee + " DH/j"; }
}