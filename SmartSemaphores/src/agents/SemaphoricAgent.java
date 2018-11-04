package agents;

import java.util.ArrayList;

import behaviours.StateCommunicationBehaviour;
import sajas.core.Agent;
import sajas.core.behaviours.OneShotBehaviour;
import sajas.core.behaviours.TickerBehaviour;
import smartSemaphores.jade.SemaphoreStates;
import smartSemaphores.jade.SinkAgent;

public class SemaphoricAgent extends Agent {

	private final static int SECONDS_TO_MS = 1000; // 5 seconds

	private final int roadSpaceLimit;
	private int numberOfVehicles;
	private int secondsPassedOnState;
	private int emergencyVehicles;

	private ArrayList<String> neighbours;
	private ArrayList<String> sinkAgentNeighbours;
	private int id;

	private SemaphoreStates state = SemaphoreStates.RED;

	public SemaphoricAgent(int id, int[] neighboursIDs, int[] sinkIDs, int roadSpaceLimit) {
		super();
		this.id = id;
		this.roadSpaceLimit = roadSpaceLimit;
		this.emergencyVehicles = 0;
		this.secondsPassedOnState = 0;
		this.numberOfVehicles = 0;
		this.neighbours = new ArrayList<>();
		this.sinkAgentNeighbours = new ArrayList<>();

		for (int nID : neighboursIDs) {
			if (nID == id)
				continue;
			neighbours.add(makeFullName(nID));
		}
		for (int nID : sinkIDs) {
			sinkAgentNeighbours.add(SinkAgent.makeFullName(nID));
		}

	}

	@Override
	protected void setup() {
		// Printout a welcome message
		System.out.println("HELLO");

		addBehaviour(new TickerBehaviour(this, SECONDS_TO_MS) {
			private static final long serialVersionUID = 1054989232567187192L;

			protected void onTick() {
				secondsPassedOnState++;
			}
		});

		// TODO verificações
		addBehaviour(new TickerBehaviour(this, 5 * SECONDS_TO_MS) {
			private static final long serialVersionUID = -5946442955577436810L;

			protected void onTick() {
				myAgent.addBehaviour(new StateCommunicationBehaviour());
			}
		});

	}

	@Override
	protected void takeDown() {
		// TODO Terminate code
		System.out.println("TERMINATE CODE");
	}

	public void addCars(int numberOfCars) {
		addBehaviour(new OneShotBehaviour() {
			private static final long serialVersionUID = -7369901379045364101L;

			public void action() {
				numberOfVehicles += numberOfCars;
			}
		});
	}

	public void removeCars(int numberOfCars) {
		addBehaviour(new OneShotBehaviour() {
			private static final long serialVersionUID = 4265760409244225294L;

			public void action() {
				numberOfVehicles -= numberOfCars;
			}
		});
	}

	public void addEmergencyVehicle() {
		addBehaviour(new OneShotBehaviour() {
			private static final long serialVersionUID = -7597919196549492252L;

			public void action() {
				emergencyVehicles++;
			}
		});
	}

	public SemaphoreStates getState() {
		return state;
	}

	public boolean hasEmergencyVehicles() {
		return emergencyVehicles > 0;
	}

	public float carRoadRatio() {
		return ((float) numberOfVehicles) / ((float) roadSpaceLimit);
	}

	public int getSecondsPassedOnState() {
		return secondsPassedOnState;
	}

	private static String makeFullName(int nID) {
		return "Semaphoric agent " + nID + "@SmartSemaphores";
	}

	/**
	 * @return the numberOfVehicles
	 */
	public int getNumberOfVehicles() {
		return numberOfVehicles;
	}

	/**
	 * @return the neighbours
	 */
	public ArrayList<String> getNeighbours() {
		return neighbours;
	}

	/**
	 * @return the sinkAgentNeighbours
	 */
	public ArrayList<String> getSinkAgentNeighbours() {
		return sinkAgentNeighbours;
	}

	/**
	 * @return the emergencyVehicles
	 */
	public int getEmergencyVehicles() {
		return emergencyVehicles;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
}
