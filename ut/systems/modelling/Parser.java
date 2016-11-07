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


        // Lets find the start event
        BPMNNode promNode = null;
        for (Flow promFlow : promBPMN.getFlows()) {
            promNode = promFlow.getSource();
            if (promNode instanceof Event) {
                break;
            }
        }

        ut.systems.modelling.BPMN.Node ourStart = new ut.systems.modelling.BPMN.Event(ut.systems.modelling.BPMN.Event.Type.START);

        BPMN ourBPMN = new BPMN();
        ourBPMN.addNode(ourStart);

        BPMNconverter(promNode, ourStart, promBPMN, ourBPMN, null);

        return ourBPMN;
    }

    static ut.systems.modelling.BPMN.Gateway BPMNconverter(BPMNNode promIn, ut.systems.modelling.BPMN.Node ourIn,
                                                           BPMNDiagram promBPMN, BPMN ourBPMN,
                                                           ut.systems.modelling.BPMN.Gateway joinGateway) {

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

                    if (promIn instanceof Gateway && !isBPMNJoining((Gateway) promIn, promBPMN)) {

                        //follow the flows
                        if (joinGateway == null) {
                            joinGateway = BPMNconverter(promOut, ourOut, promBPMN, ourBPMN, null);
                        } else {
                            BPMNconverter(promOut, ourOut, promBPMN, ourBPMN, joinGateway);
                        }

                    } else {

                        //follow the flows
                        return BPMNconverter(promOut, ourOut, promBPMN, ourBPMN, joinGateway);
                    }

                } else if (promOut instanceof Gateway) {

                    if (isBPMNJoining((Gateway) promOut, promBPMN)) {
                        if(((Gateway) promOut).getGatewayType() == Gateway.GatewayType.PARALLEL){
                            ourOut = new ut.systems.modelling.BPMN.Gateway(ut.systems.modelling.BPMN.Gateway.Type.ANDSPLIT);
                        } else {
                            ourOut = new ut.systems.modelling.BPMN.Gateway(ut.systems.modelling.BPMN.Gateway.Type.XORSPLIT);
                        }

                        ourBPMN.addNode(ourOut);
                        ourFlow = new ut.systems.modelling.BPMN.SequenceFlow(ourIn, ourOut);
                        ourIn.addOutGoingFlow(ourFlow);
                        ourBPMN.addSequenceFlows(ourFlow);

                        return BPMNconverter(promOut, ourOut, promBPMN, ourBPMN, null);


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

                            BPMNconverter(promOut, ourOut, promBPMN, ourBPMN, null);

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

    static Boolean isBPMNJoining(Gateway gateway, BPMNDiagram bpmn) {

        int outCount = 0;
        for(Flow flow : bpmn.getFlows()) {
            if(flow.getSource().equals(gateway)) {
                outCount += 1;
            }
        }

        return outCount == 1;

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

        PNConverter(ourStartPlace, promStartPlace, promPN, ourPN, null);

        return promPN;
    }

    static PetrinetNode PNConverter(ut.systems.modelling.petrinet.Place ourIn, Place promIn, Petrinet promPN,
                                    ut.systems.modelling.petrinet.Petrinet ourPN, PetrinetNode joinNode){

        Transition promOut;
        for(ut.systems.modelling.petrinet.Transition ourOut: ourIn.getTargetTransitions()) {

            if (ourOut.getLabel().equals("")) {

                if(pnNodeOutCount(ourOut) > 1) {
                    // AND SPLIT (REGULAR)

                    promOut = promPN.addTransition("");
                    promPN.addArc(promIn, promOut);

                    return PNConverter(ourOut, promOut, promPN, ourPN, joinNode);

                } else if(pnNodeOutCount(ourIn) > 1) {
                    // XOR SPLIT

                    if (joinNode == null) {
                        promOut = promPN.addTransition("");
                        promPN.addArc(promIn, promOut);

                        joinNode = PNConverter(ourOut, promOut, promPN, ourPN, null);

                    } else {

                        promOut = promPN.addTransition("");
                        promPN.addArc(promIn, promOut);
                        PNConverter(ourOut, promOut, promPN, ourPN, joinNode);

                    }

                } else if(pnNodeInCount(ourOut, ourPN) > 1) {
                    // AND JOIN

                    if (joinNode == null) {

                        promOut = promPN.addTransition("");
                        promPN.addArc(promIn, promOut);
                        PNConverter(ourOut, promOut, promPN, ourPN, null);

                        return promOut;

                    } else {

                        promPN.addArc(promIn, (Transition) joinNode);
                        return null;

                    }

                } else {
                    // XOR JOIN (REGULAR)

                    promOut = promPN.addTransition("");
                    promPN.addArc(promIn, promOut);

                    return PNConverter(ourOut, promOut, promPN, ourPN, joinNode);
                }

            } else {

                // Regular case, no splits or joins

                promOut = promPN.addTransition(ourOut.getLabel());
                promPN.addArc(promIn, promOut);

                return PNConverter(ourOut, promOut, promPN, ourPN, joinNode);
            }

        }

        return null;
    }

    static PetrinetNode PNConverter(ut.systems.modelling.petrinet.Transition ourIn, Transition promIn, Petrinet promPN,
                                    ut.systems.modelling.petrinet.Petrinet ourPN, PetrinetNode joinNode) {

        Place promOut;
        for (ut.systems.modelling.petrinet.Place ourOut : ourIn.getTargetPlaces()) {

            if (ourIn.getLabel().equals("")) {

                if(pnNodeOutCount(ourIn) > 1) {
                    // AND SPLIT

                    if (joinNode == null) {
                        promOut = promPN.addPlace(ourOut.getLabel());
                        promPN.addArc(promIn, promOut);
                        joinNode = PNConverter(ourOut, promOut, promPN, ourPN, null);
                    } else {
                        promOut = promPN.addPlace(ourOut.getLabel());
                        promPN.addArc(promIn, promOut);
                        PNConverter(ourOut, promOut, promPN, ourPN, joinNode);
                    }

                } else if(pnNodeInCount(ourIn, ourPN) > 1) {

                    // AND JOIN (REGULAR)
                    promOut = promPN.addPlace(ourOut.getLabel());
                    promPN.addArc(promIn, promOut);
                    return PNConverter(ourOut, promOut, promPN, ourPN, null);

                } else if (pnNodeInCount(ourOut, ourPN) > 1) {
                    // XOR JOIN

                    if (joinNode == null) {
                        promOut = promPN.addPlace(ourOut.getLabel());
                        promPN.addArc(promIn, promOut);
                        PNConverter(ourOut, promOut, promPN, ourPN, null);
                        return promOut;

                    } else {
                        promPN.addArc(promIn, (Place) joinNode);
                        return null;
                    }

                } else {
                    // XOR SPLIT

                    promOut = promPN.addPlace(ourOut.getLabel());
                    promPN.addArc(promIn, promOut);
                    return PNConverter(ourOut, promOut, promPN, ourPN, joinNode);
                }

            } else {

                // Regular case, no splits or joins
                promOut = promPN.addPlace(ourOut.getLabel());
                promPN.addArc(promIn, promOut);
                return PNConverter(ourOut, promOut, promPN, ourPN, joinNode);

            }

        }
        return null;
    }

    static int pnNodeOutCount(ut.systems.modelling.petrinet.Transition node) {
        return node.getTargetPlaces().size();
    }

    static int pnNodeOutCount(ut.systems.modelling.petrinet.Place node) {
        return node.getTargetTransitions().size();
    }

    static int pnNodeInCount(ut.systems.modelling.petrinet.Transition node, ut.systems.modelling.petrinet.Petrinet ourPN) {
        int count = 0;

        for(ut.systems.modelling.petrinet.Place place : ourPN.getPlaces()) {
            for(ut.systems.modelling.petrinet.Transition trans : place.getTargetTransitions()) {
                if(trans.equals(node)) {
                    count += 1;
                }
            }
        }
        return count;
    }

    static int pnNodeInCount(ut.systems.modelling.petrinet.Place node, ut.systems.modelling.petrinet.Petrinet ourPN) {
        int count = 0;

        for(ut.systems.modelling.petrinet.Transition trans : ourPN.getTransitions()) {
            for(ut.systems.modelling.petrinet.Place place : trans.getTargetPlaces()) {
                if(place.equals(node)) {
                    count += 1;
                }
            }
        }

        return count;
    }

}
