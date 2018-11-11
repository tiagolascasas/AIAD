/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.jade;

import java.util.ArrayList;
import java.util.Queue;

import sajas.core.Agent;
import smartSemaphores.repast.EmergencyVehicle;
import smartSemaphores.repast.NormalVehicle;

/**
 * The Class RoadAgent.
 */
public abstract class RoadAgent extends Agent {

    /** The id. */
    protected int id;

    /** The vehicles. */
    protected Queue<NormalVehicle> vehicles;

    /** The emergency. */
    protected Queue<EmergencyVehicle> emergency;

    /** The elapsed time. */
    protected int elapsedTime = 0;

    /**
     * Instantiates a new road agent.
     *
     * @param id
     *            the id
     */
    protected RoadAgent(int id) {
	this.id = id;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getID() {
	return this.id;
    }

    /**
     * Adds the cars.
     *
     * @param newCars
     *            the new cars
     */
    public abstract void addCars(ArrayList<NormalVehicle> newCars);

    /**
     * Adds the emergency vehicle.
     *
     * @param vehicle
     *            the vehicle
     */
    public abstract void addEmergencyVehicle(EmergencyVehicle vehicle);

    /**
     * Gets the availabe space.
     *
     * @param increment
     *            the increment
     * @return the availabe space
     */
    public abstract int getAvailabeSpace(int increment);

    /**
     * Gets the current normal vehicles.
     *
     * @return the current normal vehicles
     */
    public int getCurrentNormalVehicles() {
	return vehicles.size();
    }

    /**
     * Gets the current emergency vehicles.
     *
     * @return the current emergency vehicles
     */
    public int getCurrentEmergencyVehicles() {
	return emergency.size();
    }

    /**
     * Gets the normal vehicles.
     *
     * @return the normal vehicles
     */
    public Queue<NormalVehicle> getNormalVehicles() {
	return vehicles;
    }

    /**
     * Gets the emergency vehicles.
     *
     * @return the emergency vehicles
     */
    public Queue<EmergencyVehicle> getEmergencyVehicles() {
	return emergency;
    }

    /**
     * Make full name.
     *
     * @param id
     *            the id
     * @return the string
     */
    public static String makeFullName(int id) {
	return "Agent " + id + "@SmartSemaphores";
    }

    /**
     * Make semaphore name.
     *
     * @param id
     *            the id
     * @return the string
     */
    public static String makeSemaphoreName(int id) {
	return "Agent " + id;
    }

    /**
     * Increments time by one.
     */
    public void incrementTime() {
	this.elapsedTime++;
    }
}
