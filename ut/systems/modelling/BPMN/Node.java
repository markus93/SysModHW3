package ut.systems.modelling.BPMN;


import java.util.ArrayList;
import java.util.List;

public class Node {

    List<SequenceFlow> outGoingFlows = new ArrayList<SequenceFlow>();

    public Node(){
    }

    public List<SequenceFlow> getOutGoingFlows() {
        return outGoingFlows;
    }

    public void addOutGoingFlow(SequenceFlow flow) {
        outGoingFlows.add(flow);
    }

}
