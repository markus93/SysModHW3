package ut.systems.modelling.BPMN;


public class Task extends Node{

    private String name;

    public Task(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
