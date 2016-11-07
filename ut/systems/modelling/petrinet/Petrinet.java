package ut.systems.modelling.petrinet;

import java.util.ArrayList;
import java.util.List;

public class Petrinet {

    private List<Place> places = new ArrayList<>();
    private List<Transition> transitions  = new ArrayList<>();

    public Petrinet(){
    }

    public Place addPlace(){
        String pLabel = "P" + places.size();
        Place place = new Place(pLabel);
        places.add(place);
        return place;
    }

    //Creates new place and adds it to transition target list
    Place addNewPlace(Transition trans){
        String label = "P" + places.size(); //Adds names based how many items in list (no deletion allowed)
        Place place = new Place(label);
        trans.addTarget(place); //Add new place to transition
        places.add(place);
        return place;
    }

    //Creates new transition and adds it to place target list
    Transition addNewTransition(Place place, String label){
        Transition trans = new Transition(label);
        place.addTarget(trans);
        transitions.add(trans);
        return trans;
    }

    public Place insertTask(Place src, String label){

        Transition trans = addNewTransition(src, label);
        Place target = addNewPlace(trans);
        return target;
    }

    public List<Place> insertXORSplit(Place src, Integer targetCount){

        List<Place> targetPlaces = new ArrayList<Place>(targetCount);

        for (int i = 0; i < targetCount; i++) {

            Place target = insertTask(src, ""); //Invisible transition
            targetPlaces.add(i,target);
        }

        return targetPlaces;
    }

    public List<Place> insertANDSplit(Place src, Integer targetCount){

        List<Place> targetPlaces = new ArrayList<Place>(targetCount);

        Transition trans = addNewTransition(src, "");  //Invisible transition

        for (int i = 0; i < targetCount; i++) {

            Place target = addNewPlace(trans);
            targetPlaces.add(i,target);
        }

        return targetPlaces;
    }

    public Place insertXORJoin(List<Place> sourcePlaces){

        Transition trans = addNewTransition(sourcePlaces.get(0), ""); //add new invisible transition

        Place targetPlace = addNewPlace(trans); //All new transitions share this targetPlace

        //For each element in srcs create new invisible transition and target these to targetPlace
        for (int i = 1; i < sourcePlaces.size(); i++) { //first src already done
            trans = addNewTransition(sourcePlaces.get(i), "");
            trans.addTarget(targetPlace);
        }

        return targetPlace;

    }

    public Place insertANDJoin(List<Place> sourcePlaces){

        Transition trans = addNewTransition(sourcePlaces.get(0), ""); //add new invisible transition

        //For each element in srcs, set these target to trans
        for (int i = 1; i < sourcePlaces.size(); i++) { //first src already done
            Place place = sourcePlaces.get(i);
            place.addTarget(trans);
        }

        //Adds new place to invisible transition
        Place targetPlace = addNewPlace(trans);

        return targetPlace;
    }

    // Joins 2 petrinets together by adding all transitions of subpetrinet start to element(srcPlace) given by argument
    public Place joinPetrinets(Place srcPlace, Petrinet petrinetSub){

        List<Place> placesSub = petrinetSub.getPlaces();

        Place startPlaceSub = placesSub.get(0); //Start place of sub petrinet

        List<Transition> transOfStartPlace = startPlaceSub.getTargetTransitions(); //Get all the transitions from start place

        //Start place is left out, because it is connection point of 2 petrinets
        placesSub = placesSub.subList(1, placesSub.size());

        //Set new labels for places, so any names wouldn't repeat
        int placeCount = places.size();
        for (int i = 0; i < placesSub.size(); i++) {
            String newLabel = "P" + (placeCount + i);
            placesSub.get(i).setLabel(newLabel);
        }

        places.addAll(placesSub);

        //Add transitions from start place of the sub petrinet to srcPlace
        for (int i = 0; i < transOfStartPlace.size(); i++) {
            Transition trans = transOfStartPlace.get(i);
            srcPlace.addTarget(trans);
        }

        //Return last place as new target (In our case only one end point and places added in order)
        return places.get(places.size()-1);

    }

    public List<Place> getPlaces() {
        return places;
    }
    public List<Transition> getTransitions() {
        return transitions;
    }
}
