package ut.systems.modelling.BPMN;

import java.util.ArrayList;
import java.util.List;


public class SequenceFlow {

    List<Node> sourceNodes = new ArrayList<Node>();
    List<Node> targetNodes = new ArrayList<Node>();

    public SequenceFlow() {
    }


    public List<Node> getTargetNodes() {
        return targetNodes;
    }

    public void setTargetNodes(List<Node> targetNodes) {
        this.targetNodes = targetNodes;
    }

    public List<Node> getSourceNodes() {
        return sourceNodes;
    }

    public void setSourceNodes(List<Node> sourceNodes) {
        this.sourceNodes = sourceNodes;
    }
}
