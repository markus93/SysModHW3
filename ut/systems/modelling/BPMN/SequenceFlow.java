package ut.systems.modelling.BPMN;

import java.util.ArrayList;
import java.util.List;


public class SequenceFlow {

    Node sourceNode = null;
    Node targetNode = null;

    public SequenceFlow() {
    }


    public Node getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }

    public Node getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(Node sourceNode) {
        this.sourceNode = sourceNode;
    }
}
