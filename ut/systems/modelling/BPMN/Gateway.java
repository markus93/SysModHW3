package ut.systems.modelling.BPMN;


public class Gateway extends Node {

    public enum Type{
        XORSPLIT, XORJOIN, ANDSPLIT, ANDJOIN
    }

    private Type type;

    public Gateway(Type type){
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
