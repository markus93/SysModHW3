package ut.systems.modelling.BPMN;

import java.util.ArrayList;
import java.util.List;

public class BPMN {

    private List<Node> nodes = new ArrayList<Node>();
    private List<SequenceFlow> sequenceFlows = new ArrayList<SequenceFlow>();

    public BPMN(){

    }

    public BPMN(List<Node> nodes, List<SequenceFlow> sequenceFlows){
        this.nodes = nodes;
        this.sequenceFlows = sequenceFlows;
    }

    public SequenceFlow findFirstFlow(){

        List<SequenceFlow> flows = sequenceFlows;

        int i = 0;

        while (i < flows.size()){
            Node srcNode = flows.get(i).getSourceNode();

            if (srcNode instanceof Event){
                return flows.get(i);
            }

            i++;
        }

        return null;
    }

    public void addSequenceFlow(SequenceFlow flow) {
        sequenceFlows.add(flow);
    }
    public void addNode(Node node) {
        nodes.add(node);
    }
}
