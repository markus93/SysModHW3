package ut.systems.modelling.BPMN;


public class Event extends Node {

    public enum Type{
        START, END
    }

    private Type type;

    public Event(Type type){
        this.type = type;
    }
}
