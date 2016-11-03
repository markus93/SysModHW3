package ut.systems.modelling.petrinet;

import java.util.ArrayList;
import java.util.List;

public class Petrinet {

    private List<Place> places = new ArrayList<Place>();
    private List<Transition> transitions  = new ArrayList<Transition>();;

    public Petrinet(){
    }

    //Creates new place and adds target transition
    void addNewPlace(Transition trans){
        String label = "P" + places.size(); //TODO better to use counter?
        Place place = new Place(label);
        place.addTarget(trans);
        places.add(place);
    }

    //Creates new transition and adds target place
    void addNewTransition(Place place, String label){
        Transition trans = new Transition(label);
        trans.addTarget(place);
        transitions.add(trans);
    }

    void insertTask(Place src, String label){

    }

    void insertXORSplit(Place src, Integer targetCount){

    }

    void insertANDSplit(Place src, Integer targetCount){

    }

    void insertXORJoin(List<Place> srcs){

    }

    void insertANDJoin(List<Place> srcs){

    }

    void joinPetrinets(Place srcPlace, Petrinet petrinetSub){

    }

    public List<Place> getPlaces() {
        return places;
    }
}
