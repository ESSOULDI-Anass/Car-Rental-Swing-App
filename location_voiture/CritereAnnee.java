package location_voiture;

public class CritereAnnee implements Critere {
    private int annee;

    public CritereAnnee(int annee) {
        this.annee = annee;
    }

    public boolean estSatisfaitPar(Voiture v) {
        return v.getAnnee() == annee;
    }
}
