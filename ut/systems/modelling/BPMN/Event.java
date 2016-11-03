package ut.systems.modelling.BPMN;


public class Event extends Node {

    private String type; //TODO refactor to enum?

    public Event(String type){
        this.type = type;
    }
}
