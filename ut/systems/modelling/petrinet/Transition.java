package ut.systems.modelling.petrinet;

import java.util.ArrayList;
import java.util.List;

public class Transition {

    private String label;
    private List<Place> targetPlaces = new ArrayList<Place>();;

    public Transition(String label){
        this.label = label;
    }

    void addTarget (Place place){
        targetPlaces.add(place);
    }

    public List<Place> getTargetPlaces(){
        return targetPlaces;
    }

    public String getLabel(){
        return label;
    }

}
