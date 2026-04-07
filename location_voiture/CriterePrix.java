package location_voiture;

//Critère satisfait si le prix est INFÉRIEUR au prix max fixé
public class CriterePrix implements Critere {
 private int prixMax;

 public CriterePrix(int prixMax) {
     this.prixMax = prixMax;
 }

 public boolean estSatisfaitPar(Voiture v) {
     return v.getPrixJournee() < prixMax;
 }
}