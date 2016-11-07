package ut.systems.modelling;

import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.*;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

import ut.systems.modelling.BPMN.BPMN;
import ut.systems.modelling.BPMN.Node;
import ut.systems.modelling.BPMN.SequenceFlow;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    static BPMN getMyBPMNModel(BPMNDiagram promBPMN) {

        List<Node> ourNodes = new ArrayList<Node>();
        List<SequenceFlow> ourFlows = new ArrayList<SequenceFlow>();

        // Lets find the start event
        BPMNNode promNode = null;
        for (Flow promFlow : promBPMN.getFlows()) {
            promNode = promFlow.getSource();
            if (promNode instanceof Event) {
                break;
            }
        }

        ut.systems.modelling.BPMN.Node ourStart = new ut.systems.modelling.BPMN.Event(ut.systems.modelling.BPMN.Event.Type.START);
        ourNodes.add(ourStart);

        BPMN ourBPMN = new BPMN();

        BPMNconverter(promNode, ourStart, promBPMN, ourBPMN, Boolean.FALSE, null);

        return ourBPMN;
    }

    static ut.systems.modelling.BPMN.Gateway BPMNconverter(BPMNNode promIn, ut.systems.modelling.BPMN.Node ourIn,
                                                           BPMNDiagram promBPMN, BPMN ourBPMN,
                                                           Boolean joining, ut.systems.modelling.BPMN.Gateway joinGateway) {

        ut.systems.modelling.BPMN.Node ourOut;
        ut.systems.modelling.BPMN.SequenceFlow ourFlow;
        for (Flow promFlow : promBPMN.getFlows()) {
            if (promIn.equals(promFlow.getSource())) {
                BPMNNode promOut = promFlow.getTarget();


                if (promOut instanceof Event) {
                    // End of the recursion
                    ourOut = new ut.systems.modelling.BPMN.Event(ut.systems.modelling.BPMN.Event.Type.END);
                    ourBPMN.addNode(ourOut);
                    ourFlow = new ut.systems.modelling.BPMN.SequenceFlow(ourIn, ourOut);
                    ourIn.addOutGoingFlow(ourFlow);
                    ourBPMN.addSequenceFlows(ourFlow);

                    return null;

                } else if (promOut instanceof Activity) {

                    if (promOut instanceof SubProcess) {
                        // Compound task
                        ourOut = new ut.systems.modelling.BPMN.Compound(getMyBPMNModel((BPMNDiagram) promOut.getGraph()), promOut.getLabel());
                    } else {
                        ourOut = new ut.systems.modelling.BPMN.Simple(promOut.getLabel());
                    }


                    ourBPMN.addNode(ourOut);
                    ourFlow = new ut.systems.modelling.BPMN.SequenceFlow(ourIn, ourOut);
                    ourIn.addOutGoingFlow(ourFlow);
                    ourBPMN.addSequenceFlows(ourFlow);

                    if (promIn instanceof Gateway && joining) {

                        //follow the flows
                        if (joinGateway == null) {
                            joinGateway = BPMNconverter(promOut, ourOut, promBPMN, ourBPMN, Boolean.TRUE, null);
                        } else {
                            BPMNconverter(promOut, ourOut, promBPMN, ourBPMN, Boolean.TRUE, joinGateway);
                        }

                    } else {

                        //follow the flows
                        return BPMNconverter(promOut, ourOut, promBPMN, ourBPMN, joining, joinGateway);
                    }

                } else if (promOut instanceof Gateway) {

                    if (joining == Boolean.FALSE) {
                        if(((Gateway) promOut).getGatewayType() == Gateway.GatewayType.PARALLEL){
                            ourOut = new ut.systems.modelling.BPMN.Gateway(ut.systems.modelling.BPMN.Gateway.Type.ANDSPLIT);
                        } else {
                            ourOut = new ut.systems.modelling.BPMN.Gateway(ut.systems.modelling.BPMN.Gateway.Type.XORSPLIT);
                        }

                        ourBPMN.addNode(ourOut);
                        ourFlow = new ut.systems.modelling.BPMN.SequenceFlow(ourIn, ourOut);
                        ourIn.addOutGoingFlow(ourFlow);
                        ourBPMN.addSequenceFlows(ourFlow);

                        return BPMNconverter(promOut, ourOut, promBPMN, ourBPMN, Boolean.TRUE, null);


                    } else {
                        // only the first join will add gateway to nodes, others use the same one.
                        if(joinGateway == null) {
                            if(((Gateway) promOut).getGatewayType() == Gateway.GatewayType.PARALLEL){
                                ourOut = new ut.systems.modelling.BPMN.Gateway(ut.systems.modelling.BPMN.Gateway.Type.ANDJOIN);
                            } else {
                                ourOut = new ut.systems.modelling.BPMN.Gateway(ut.systems.modelling.BPMN.Gateway.Type.XORJOIN);
                            }

                            ourBPMN.addNode(ourOut);
                            ourFlow = new ut.systems.modelling.BPMN.SequenceFlow(ourIn, ourOut);
                            ourIn.addOutGoingFlow(ourFlow);
                            ourBPMN.addSequenceFlows(ourFlow);

                            BPMNconverter(promOut, ourOut, promBPMN, ourBPMN, Boolean.TRUE, null);

                            return (ut.systems.modelling.BPMN.Gateway) ourOut;

                        } else {
                            ourFlow = new ut.systems.modelling.BPMN.SequenceFlow(ourIn, joinGateway);
                            ourIn.addOutGoingFlow(ourFlow);
                            ourBPMN.addSequenceFlows(ourFlow);

                            return null;
                        }
                    }
                }
            }
        }

        return null;
    }

    static Petrinet getPROMPetriNet(ut.systems.modelling.petrinet.Petrinet ourPN){

        Petrinet promPN = new PetrinetImpl("Mis siia läheb?");


        //Lets find "P0"
        ut.systems.modelling.petrinet.Place ourStartPlace = null;
        System.out.println("PLACCES: " + ourPN.getPlaces().toString());
        for (ut.systems.modelling.petrinet.Place ourPlace : ourPN.getPlaces()) {
            if(ourPlace.getLabel().equals("P0")) {
                ourStartPlace = ourPlace;
                break;
            }
        }

        Place promStartPlace = promPN.addPlace(ourStartPlace.getLabel());

        PNConverter(ourStartPlace, promStartPlace, promPN, null, Boolean.FALSE, null);

        return promPN;
    }

    static PetrinetNode PNConverter(ut.systems.modelling.petrinet.Place ourIn, Place promIn, Petrinet promPN, PetrinetNode joinNode,
                                    Boolean joining, Boolean andBranch){

        Transition promOut;
        for(ut.systems.modelling.petrinet.Transition ourOut: ourIn.getTargetTransitions()) {

            if (ourOut.getLabel() == "") {

                if (joining) {

                    if (andBranch) {
                        // AND JOIN

                        if (joinNode == null) {

                            promOut = promPN.addTransition("");
                            promPN.addArc(promIn, promOut);
                            PNConverter(ourOut, promOut, promPN, null, Boolean.FALSE, null);

                            return promOut;

                        } else {

                            promPN.addArc(promIn, (Transition) joinNode);
                            return null;

                        }

                    } else {
                        // XOR JOIN (REGULAR)

                        promOut = promPN.addTransition("");
                        promPN.addArc(promIn, promOut);

                        return PNConverter(ourOut, promOut, promPN, joinNode, joining, andBranch);

                    }

                } else {

                    if (ourIn.getTargetTransitions().size() == 1) {
                        // AND SPLIT (REGULAR)

                        promOut = promPN.addTransition("");
                        promPN.addArc(promIn, promOut);

                        return PNConverter(ourOut, promOut, promPN, joinNode, joining, Boolean.TRUE);

                    } else {
                        // XOR SPLIT

                        if (joinNode == null) {
                            promOut = promPN.addTransition("");
                            promPN.addArc(promIn, promOut);

                            joinNode = PNConverter(ourOut, promOut, promPN, null, Boolean.TRUE, Boolean.FALSE);

                        } else {

                            promOut = promPN.addTransition("");
                            promPN.addArc(promIn, promOut);
                            PNConverter(ourOut, promOut, promPN, joinNode, Boolean.TRUE, Boolean.FALSE);

                        }
                    }

                }

            } else {

                // Regular case, no splits or joins

                promOut = promPN.addTransition(ourOut.getLabel());
                promPN.addArc(promIn, promOut);

                return PNConverter(ourOut, promOut, promPN, joinNode, joining, andBranch);
            }

        }

        return null;
    }

    static PetrinetNode PNConverter(ut.systems.modelling.petrinet.Transition ourIn, Transition promIn, Petrinet promPN, PetrinetNode joinNode,
                                  Boolean joining, Boolean andBranch) {

        Place promOut;
        for (ut.systems.modelling.petrinet.Place ourOut : ourIn.getTargetPlaces()) {

            if (ourIn.getLabel() == "") {

                if (joining) {
                    if (andBranch) {

                        // AND JOIN (REGULAR)
                        promOut = promPN.addPlace(ourOut.getLabel());
                        promPN.addArc(promIn, promOut);
                        return PNConverter(ourOut, promOut, promPN, null, Boolean.FALSE, null);

                    } else {

                        // XOR JOIN

                        if (joinNode == null) {
                            promOut = promPN.addPlace(ourOut.getLabel());
                            promPN.addArc(promIn, promOut);
                            PNConverter(ourOut, promOut, promPN, null, Boolean.FALSE, null);
                            return promOut;

                        } else {
                            promPN.addArc(promIn, (Place) joinNode);
                            return null;
                        }

                    }

                } else {

                    if (andBranch) {

                        //AND SPLIT

                        if (joinNode == null) {
                            promOut = promPN.addPlace(ourOut.getLabel());
                            promPN.addArc(promIn, promOut);
                            joinNode = PNConverter(ourOut, promOut, promPN, null, Boolean.TRUE, Boolean.TRUE);
                        } else {
                            promOut = promPN.addPlace(ourOut.getLabel());
                            promPN.addArc(promIn, promOut);
                            PNConverter(ourOut, promOut, promPN, joinNode, Boolean.TRUE, Boolean.TRUE);
                        }

                    } else {

                        //XOR SPLIT (REGULAR)
                        promOut = promPN.addPlace(ourOut.getLabel());
                        promPN.addArc(promIn, promOut);
                        return PNConverter(ourOut, promOut, promPN, joinNode, Boolean.TRUE, Boolean.FALSE);

                    }

                }

            } else {

                // Regular case, no splits or joins
                promOut = promPN.addPlace(ourOut.getLabel());
                promPN.addArc(promIn, promOut);
                return PNConverter(ourOut, promOut, promPN, joinNode, joining, andBranch);

            }

        }
        return null;
    }
}
