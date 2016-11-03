package ut.systems.modelling;

import org.processmining.framework.util.Pair;
import ut.systems.modelling.BPMN.*;
import ut.systems.modelling.petrinet.Petrinet;
import ut.systems.modelling.petrinet.Place;

import java.util.ArrayList;
import java.util.List;

public class Converter {

    static Petrinet convert(BPMN bpmn){

        Petrinet petrinet = new Petrinet();

        SequenceFlow flow = bpmn.findFirstFlow();

        genPetrinet(bpmn, petrinet, flow);

        return petrinet;
    }

    public static void genPetrinet(BPMN bpmn, Petrinet petrinet, SequenceFlow flow){
        genPetrinet(bpmn, petrinet, flow, null);
    }

    //TODO is petrinet actually filled?
    static Pair<Place, SequenceFlow> genPetrinet(BPMN bpmn, Petrinet petrinet, SequenceFlow flow, Place src){

        while (true){
            Node node = flow.getTargetNode(); //Only one outgoing flow from start event
            List<SequenceFlow> outGoingFlows = node.getOutGoingFlows();

            //In this case reached end and petrinet is generated
            if(node instanceof Event){

                return null; //No need to return actual Flow and Place pair.
            }
            else if(node instanceof Task){

                if(node instanceof  Simple){

                    src = petrinet.insertTask(src, ((Simple) node).getName());
                }
                else{ //Compound task

                    BPMN bpmnSub = ((Compound) node).getCompoundBPMN();

                    Petrinet petrinetSub = convert(bpmnSub);

                    src = petrinet.joinPetrinets(src, petrinetSub);
                }
            }
            else if(node instanceof Gateway){
                Gateway.Type type = ((Gateway) node).getType();

                //Didn't use switch to make it easier to read.
                if(type == Gateway.Type.XORSPLIT){

                    List<Place> srcs = petrinet.insertXORSplit(src, outGoingFlows.size());
                    List<Place> srcsNew = new ArrayList<>();

                    for (int i = 0; i < srcs.size(); i++) {

                        Pair<Place, SequenceFlow> pairPlaceFlow = genPetrinet(bpmn, petrinet, outGoingFlows.get(i), srcs.get(i));
                        src = pairPlaceFlow.getFirst();
                        flow = pairPlaceFlow.getSecond();

                        srcsNew.add(src);
                    }

                    petrinet.insertXORJoin(srcsNew);

                }
                else if(type == Gateway.Type.ANDSPLIT){

                    List<Place> srcs = petrinet.insertANDSplit(src, outGoingFlows.size());
                    List<Place> srcsNew = new ArrayList<>();

                    for (int i = 0; i < srcs.size(); i++) {

                        Pair<Place, SequenceFlow> pairPlaceFlow = genPetrinet(bpmn, petrinet, outGoingFlows.get(i), srcs.get(i));
                        src = pairPlaceFlow.getFirst();
                        flow = pairPlaceFlow.getSecond();

                        srcsNew.add(src);
                    }

                    petrinet.insertANDJoin(srcsNew);

                }
                else{ //In this case XORJOIN or ANDJOIN

                    flow = outGoingFlows.get(0); //TODO Why first outgoing flow?

                    return new Pair<>(src, flow);

                }
            }

        }
    }
}
