package ut.systems.modelling.petrinet;

import java.util.ArrayList;
import java.util.List;

public class Transition {

    private String label;
    private List<Place> targetPlaces = new ArrayList<Place>();;


    public Transition(String label){
        this.label = label;
    }

    //Add target to transition
    void addTarget (Place place){
        targetPlaces.add(place);
    }

    public List<Place> getTargetPlaces(){
        return targetPlaces;
    }

    public String getLabel(){
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
