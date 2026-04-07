package location_voiture;

public interface Critere {
    /**
     * @param v la voiture dont on teste la conformité
     * @return true si et seulement si la voiture satisfait le critère
     */
    public boolean estSatisfaitPar(Voiture v);
}