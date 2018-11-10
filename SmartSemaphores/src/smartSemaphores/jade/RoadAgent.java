package smartSemaphores.jade;

import java.util.ArrayList;
import java.util.Queue;

import sajas.core.Agent;
import smartSemaphores.repast.EmergencyVehicle;
import smartSemaphores.repast.NormalVehicle;

public abstract class RoadAgent extends Agent
{
	protected int id;
	protected Queue<NormalVehicle> vehicles;
	protected Queue<EmergencyVehicle> emergency;
	
	protected RoadAgent(int id)
	{
		this.id = id;
	}
	
	public int getID()
	{
		return this.id;
	}
	
	public abstract void addCars(ArrayList<NormalVehicle> newCars);
	
	public abstract void addEmergencyVehicle(EmergencyVehicle vehicle);
	
	public abstract int getAvailabeSpace(int increment);
	
	public int getCurrentNormalVehicles()
	{
		return vehicles.size();
	}
	
	public int getCurrentEmergencyVehicles()
	{
		return emergency.size();
	}

	public Queue<NormalVehicle> getNormalVehicles()
	{
		return vehicles;
	}

	public Queue<EmergencyVehicle> getEmergencyVehicles()
	{
		return emergency;
	}
	
	public static String makeFullName(int id)
	{
		return "Agent " + id;
	}
	
	public static String makeFullName2(int id)
	{
		return "Agent " + id + "@SmartSemaphores";
	}

}
