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

    //Add target to place
    void addTarget (Transition trans){
        targetTransition.add(trans);
    }

    public List<Transition> getTargetTransitions(){
        return targetTransition;
    }

    public String getLabel(){
        return label;
    }

}
