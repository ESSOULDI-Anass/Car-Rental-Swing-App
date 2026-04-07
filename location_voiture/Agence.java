package location_voiture;
import java.util.*;

public class Agence {
    private List<Voiture> voitures = new ArrayList<>();
    private Map<Client, Voiture> locations = new TreeMap<>();

    public void ajouterVoiture(Voiture v) { voitures.add(v); }
    public void supprimerVoiture(Voiture v) { voitures.remove(v); }
    public List<Voiture> getVoitures() { return Collections.unmodifiableList(voitures); }
    public Map<Client, Voiture> getLocations() { return Collections.unmodifiableMap(locations); }

    public List<Voiture> selectionne(Critere c) {
        List<Voiture> sel = new ArrayList<>();
        for (Voiture v : voitures) if (c.estSatisfaitPar(v)) sel.add(v);
        return sel;
    }

    public void loueVoiture(Client client, Voiture v)
            throws VoitureInexistanteException, VoitureDejaBoueeException {
        if (!voitures.contains(v)) throw new VoitureInexistanteException("Voiture introuvable dans l'agence.");
        if (estLoue(v)) throw new VoitureDejaBoueeException("Cette voiture est déjà louée.");
        locations.put(client, v);
    }

    public boolean estLoueur(Client c) { return locations.containsKey(c); }
    public boolean estLoue(Voiture v)  { return locations.containsValue(v); }
    public void rendVoiture(Client c)  { locations.remove(c); }

    public List<Client> getClients() { return new ArrayList<>(locations.keySet()); }
    public Voiture getVoitureDeClient(Client c) { return locations.get(c); }
}