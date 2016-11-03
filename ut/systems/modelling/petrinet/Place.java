package ut.systems.modelling.petrinet;


import java.util.ArrayList;
import java.util.List;

public class Place {

    private String label;
    private Boolean token = false;
    private String type; //TODO is type used for start and end?
    private List<Transition> targetTransition = new ArrayList<Transition>();

    public Place(String label){
        this.label = label;
    }

    public List<Transition> getTargetTransitions(){
        return targetTransition;
    }

    void addTarget (Transition trans){
        targetTransition.add(trans);
    }

    public String getLabel(){
        return label;
    }

}
