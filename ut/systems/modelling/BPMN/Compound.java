package ut.systems.modelling.BPMN;


public class Compound extends Task {

    BPMN compoundBPMN;

    public Compound(BPMN compoundBPMN, String name){
        super(name); //TODO should this task have name
        this.compoundBPMN = compoundBPMN;
    }

    public BPMN getCompoundBPMN() {
        return compoundBPMN;
    }
}
