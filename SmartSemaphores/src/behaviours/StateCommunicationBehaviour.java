package behaviours;

import java.util.ArrayList;
import java.util.Arrays;

import agents.SemaphoricAgent;
import jade.lang.acl.ACLMessage;
import sajas.core.AID;
import sajas.core.behaviours.Behaviour;
import smartSemaphores.jade.SemaphoreStates;

public class StateCommunicationBehaviour extends Behaviour {

	private static final String CONVERSATION_ID = "Inform-priority";

	private static final long serialVersionUID = 2495229105259679220L;

	private static final String DELIMITER = "-";

	private static final int EMERGENCY_PRIORITY = 12;
	private static final int ROAD_OVERFLOW_PRIORITY = 11;
	private static final int GREEN_MAX_TIME_PRIORITY = 0;
	private static final int GREEN_MIN_TIME_PRIORITY = 10;
	private static final int RED_MAX_TIME_PRIORITY = 10;

	private static final int RED_MAX_TIME = 180;
	private static final int GREEN_MAX_TIME = 120;
	private static final int GREEN_MIN_TIME = 20;

	private ArrayList<String[]> allPriorityInformation;

	private int repliesCnt = 0; // The counter of replies from seller agents
	private int step = 0;
	private int priority = 0;
	SemaphoricAgent thisAgent;

	@Override
	public void action() {
		thisAgent = (SemaphoricAgent) myAgent;
		switch (step) {
		case 0:
			this.priority = calculatePriority();
			ACLMessage InformMsg = new ACLMessage(ACLMessage.INFORM);
			for (int i = 0; i < thisAgent.getNeighbours().size(); ++i) {
				InformMsg.addReceiver(new AID(thisAgent.getNeighbours().get(i), AID.ISLOCALNAME)); // TODO recheck if
																									// AID is
																									// full name
			}
			InformMsg.setContent(priorityToString());
			InformMsg.setConversationId(CONVERSATION_ID);
			myAgent.send(InformMsg);
			step++;
			break;
		case 1:
			ACLMessage msg = myAgent.receive();
			if (msg != null && msg.getConversationId().equals(CONVERSATION_ID)) {
				repliesCnt++;
				addPriorityInformation(msg.getSender().getName(), msg.getContent());
				if (repliesCnt >= thisAgent.getNeighbours().size()) {
					step++;
				}
			} else {
				block();
			}
			break;
		case 2:
			// Decidir se tem de mudar o estado - Descobrir semáforo com maior prioridade
			// TODO TER EM ATENÇÃO QUE O SEMÁFORO DO LADO OPOSTO TMB LIGA!!!!

			step++;
			break;
		}
		return;

	}

	private void addPriorityInformation(String aid, String content) {
		String[] splitContent = content.split(DELIMITER);
		ArrayList<String> agentPriorityInfo = new ArrayList<>();
		agentPriorityInfo.addAll(Arrays.asList(splitContent));
		agentPriorityInfo.add(aid.replaceAll("\\D+", "")); // gets the id of the name

		allPriorityInformation.add(agentPriorityInfo.toArray(new String[0]));
	}

	private String priorityToString() {
		String priorityStr = new Integer(priority).toString();
		String emergencyVehiclesStr = new Integer(thisAgent.getEmergencyVehicles()).toString();
		String numberOfVehiclesStr = new Integer(thisAgent.getNumberOfVehicles()).toString();
		// TODO peões
		String stateStr = thisAgent.getState().name();

		return priorityStr + DELIMITER + emergencyVehiclesStr + DELIMITER + numberOfVehiclesStr + DELIMITER + stateStr;
	}

	@Override
	public boolean done() {
		return step == 3;
	}

	private int calculatePriority() {
		if (thisAgent.hasEmergencyVehicles())
			return EMERGENCY_PRIORITY;

		if (thisAgent.carRoadRatio() >= 1.0)
			return ROAD_OVERFLOW_PRIORITY;

		if (thisAgent.getState() == SemaphoreStates.GREEN)
			return calculatePriorityGreen();
		else if (thisAgent.getState() == SemaphoreStates.RED)
			return calculatePriorityRed();

		return -1; // TODO SE ISTO ACONTECER SOMETHING IS REALLY WRONG....
	}

	private int calculatePriorityGreen() {
		if (thisAgent.getSecondsPassedOnState() >= GREEN_MAX_TIME)
			return GREEN_MAX_TIME_PRIORITY;
		else if (thisAgent.getSecondsPassedOnState() >= GREEN_MIN_TIME)
			return GREEN_MIN_TIME_PRIORITY;
		else
			return 0; // fórmula

	}

	private int calculatePriorityRed() {
		if (thisAgent.getSecondsPassedOnState() >= RED_MAX_TIME)
			return RED_MAX_TIME_PRIORITY;

		return 0; // fórmula
	}
}
