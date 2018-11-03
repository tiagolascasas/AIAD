package smartSemaphores.jade;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;
import sajas.core.Agent;
import sajas.core.behaviours.WakerBehaviour;
import smartSemaphores.SmartSemaphoresRepastLauncher;

public class SemaphoricAgent extends Agent {
	
	public static enum semaphoreStates {
		ON, OFF
	};
	
	public final int roadSpaceLimit;
	

	private ArrayList<String> neighbours;
	private semaphoreStates state = semaphoreStates.OFF;

	public SemaphoricAgent(int id, int[] neighboursIDs, int roadSpacelimit) {
		this.neighbours = new ArrayList<>();
		for (int nID : neighboursIDs) {
			if (nID == id)
				continue;
			neighbours.add(makeFullName(nID));
		}
		this.roadSpaceLimit=roadSpacelimit;
		this.addBehaviour(new TestBehaviour());
	}

	private String makeFullName(int nID) {
		return "Semaphoric agent " + nID + "@SmartSemaphores";
	}

	public void switchState(semaphoreStates wantedState) {
		if (wantedState == this.state)
			return;

		for (String agentName : this.neighbours) {
			Context<?> context = ContextUtils.getContext(this);
			Agent targetAgent = SmartSemaphoresRepastLauncher.getAgent(context, agentName);
			Network<Object> net = (Network<Object>) ContextUtils.getContext(this)
					.getProjection("SmartSemaphores Road Network");
			if (wantedState== semaphoreStates.ON) {
				net.addEdge(this, targetAgent);
			} else {
				RepastEdge<Object> edge = net.getEdge(this, targetAgent);
				net.removeEdge(edge);
			}
		}
		this.state = wantedState;
	}

	public semaphoreStates getCurrentState() {
		return this.state;
	}
}
