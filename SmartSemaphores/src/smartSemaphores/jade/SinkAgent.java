/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.jade;

import java.util.ArrayList;
import java.util.LinkedList;

import smartSemaphores.repast.EmergencyVehicle;
import smartSemaphores.repast.NormalVehicle;
import smartSemaphores.repast.SimulationManager;

/**
 * The Class SinkAgent.
 */
public class SinkAgent extends RoadAgent {

    /**
     * Instantiates a new sink agent.
     *
     * @param id
     *            the id
     */
    public SinkAgent(int id) {
	super(id);
	this.emergency = new LinkedList<>();
	this.vehicles = new LinkedList<>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see smartSemaphores.jade.RoadAgent#addCars(java.util.ArrayList)
     */
    @Override
    public void addCars(ArrayList<NormalVehicle> newCars) {
	for (NormalVehicle car : newCars) {
	    car.setEndPoint(this.id);
	    car.setEndTick(SimulationManager.currentTick);
	}

	this.vehicles.addAll(newCars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * smartSemaphores.jade.RoadAgent#addEmergencyVehicle(smartSemaphores.repast.
     * EmergencyVehicle)
     */
    @Override
    public void addEmergencyVehicle(EmergencyVehicle vehicle) {
	vehicle.setEndPoint(this.id);
	vehicle.setEndTick(SimulationManager.currentTick);

	this.emergency.add(vehicle);
    }

    /*
     * (non-Javadoc)
     * 
     * @see smartSemaphores.jade.RoadAgent#getAvailabeSpace(int)
     */
    @Override
    public int getAvailabeSpace(int increment) {
	return increment;
    }
}
