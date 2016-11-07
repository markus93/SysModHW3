package ut.systems.modelling.BPMN;

import java.util.ArrayList;
import java.util.List;


public class SequenceFlow {

    Node sourceNode = null;
    Node targetNode = null;

    public SequenceFlow(Node sourceNode, Node targetNode) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
    }


    public Node getTargetNode() {
        return targetNode;
    }

    public Node getSourceNode() {
        return sourceNode;
    }
}
