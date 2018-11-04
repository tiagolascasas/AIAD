package smartSemaphores.jade;

import java.util.ArrayList;
import java.util.LinkedList;

import smartSemaphores.repast.EmergencyVehicle;
import smartSemaphores.repast.NormalVehicle;
import smartSemaphores.repast.SimulationManager;

public class SinkAgent extends RoadAgent
{
	public SinkAgent(int id)
	{
		super(id);
		this.emergency = new LinkedList<>();
		this.vehicles = new LinkedList<>();
	}
	
	@Override
	public void addCars(ArrayList<NormalVehicle> newCars)
	{
		for (NormalVehicle car : newCars)
		{
			car.setEndPoint(this.id);
			car.setEndTick(SimulationManager.currentTick);
		}
		
		this.vehicles.addAll(newCars);
	}

	@Override
	public void addEmergencyVehicle(EmergencyVehicle vehicle)
	{
		vehicle.setEndPoint(this.id);
		vehicle.setEndTick(SimulationManager.currentTick);
		
		this.emergency.add(vehicle);
	}

	@Override
	public int getAvailabeSpace(int increment)
	{
		return increment;
	}
}
