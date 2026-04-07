package location_voiture;
import java.util.*;
public class InterCritere implements Critere {
    private List<Critere> lesCriteres = new ArrayList<>();
    public void addCritere(Critere c) { lesCriteres.add(c); }
    public boolean estSatisfaitPar(Voiture v) {
        for (Critere c : lesCriteres) if (!c.estSatisfaitPar(v)) return false;
        return true;
    }
}