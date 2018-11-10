package smartSemaphores.jade.behaviours;

import java.util.ArrayList;
import smartSemaphores.jade.SemaphoricAgent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.AID;

import sajas.core.behaviours.CyclicBehaviour;
import smartSemaphores.jade.SemaphoreStates;

public class StateCommunicationBehaviour extends CyclicBehaviour {
	private static final int PRIORITY_VALUE_POSITION = 0;

	private static final int ID_POSITION = 3;

	private static final String CONVERSATION_ID = "Inform-priority";

	private static final long serialVersionUID = 2495229105259679220L;

	private static final String DELIMITER = "/";
	private static final int INFO_PRIORITY_LENGTH = 4;

	private ArrayList<String[]> allPriorityInformation;

	private int repliesCnt = 0; // The counter of replies from seller agents
	private int step = 0;
	private double priority = 0;
	SemaphoricAgent thisAgent;

	@Override
	public void action() {
		thisAgent = (SemaphoricAgent) myAgent;
		switch (step) {
		case 0:
			System.out.println("Chego ao 0");
			allPriorityInformation = new ArrayList<>();
			this.priority = PriorityCalculator.calculatePriority(thisAgent);
			ACLMessage InformMsg = new ACLMessage(ACLMessage.INFORM);
			for (int i = 0; i < thisAgent.getNeighbours().size(); ++i) {
				InformMsg.addReceiver(new AID(thisAgent.getNeighbours().get(i), AID.ISLOCALNAME));
			}
			InformMsg.setContent(priorityToString());
			InformMsg.setConversationId(CONVERSATION_ID);
			myAgent.send(InformMsg);
			step++;
			break;
		case 1:
			System.out.println("Chego ao 1");
			MessageTemplate mt = MessageTemplate.MatchConversationId(CONVERSATION_ID);
			ACLMessage msg;
			while (((msg = myAgent.receive(mt)) != null)) {

				repliesCnt++;
				addPriorityInformation(msg.getSender().getName(), msg.getContent());
				if (repliesCnt >= thisAgent.getNeighbours().size()) {
					processMessages();
					step = 0;
					repliesCnt = 0;
					break;
				}
			}
			break;
		}
		return;
	}

	private void processMessages() {
		ArrayList<String> greenCandidates = new ArrayList<>();

		boolean isCandidate = findIfGreenCandidate(greenCandidates);

		if (isCandidate && greenCandidates.isEmpty()) // Se for o �nico com m�ximo valor de prioridade
														// calculada
														// passa a verde
			thisAgent.switchState(SemaphoreStates.GREEN);
		else if (isCandidate && greenTieBreaker(greenCandidates)) // Se tiver valor m�ximo de prioridade
																	// calculada e
																	// Passar no desempate passa a verde
			thisAgent.switchState(SemaphoreStates.GREEN);
		else // Se n�o tiver valor m�ximo ou n�o passar no desempate passa a vermelho
			thisAgent.switchState(SemaphoreStates.RED);
	}

	private boolean greenTieBreaker(ArrayList<String> greenCandidates) {
		int[] agentVariables = { thisAgent.getCurrentEmergencyVehicles(), thisAgent.getCurrentNormalCars(),
				thisAgent.getID() };

		for (int i = 1; i < INFO_PRIORITY_LENGTH; i++) { // i=1 because priority was already evaluated
			if (greenCandidates.isEmpty())
				break;

			for (int j = 0; j < allPriorityInformation.size(); j++) {
				if (!greenCandidates.contains(allPriorityInformation.get(j)[ID_POSITION]))
					continue;
				int priorityI = Integer.parseInt(allPriorityInformation.get(j)[i]);

				if (priorityI > agentVariables[i - 1]) // i -1 because agentVariables doesn't contain priority;
					return false;

				if (priorityI < agentVariables[i - 1])
					greenCandidates.remove(allPriorityInformation.get(j)[ID_POSITION]);

			}
		}
		return true;
	}

	private boolean findIfGreenCandidate(ArrayList<String> greenCandidates) {
		for (int i = 0; i < allPriorityInformation.size(); i++) {
			System.out.print(Integer.toString(i));
			for (int j = 0; j < INFO_PRIORITY_LENGTH; j++)
				System.out.print("-" + allPriorityInformation.get(i)[j]);
			System.out.println();
		}

		for (int i = 0; i < allPriorityInformation.size(); i++) {
			double priorityI = Double.parseDouble(allPriorityInformation.get(i)[PRIORITY_VALUE_POSITION]);
			if (priorityI > priority) {
				return false;
			}
			if (priorityI == priority) {
				greenCandidates.add(allPriorityInformation.get(i)[ID_POSITION]);
			}

		}
		return true;
	}

	private void addPriorityInformation(String aid, String content) {
		String[] splitContent = content.split(DELIMITER);

		allPriorityInformation.add(splitContent);
	}

	private String priorityToString() {
		String priorityStr = Double.toString(priority);
		String emergencyVehiclesStr = Integer.toString(thisAgent.getCurrentEmergencyVehicles());
		String numberOfVehiclesStr = Integer.toString(thisAgent.getCurrentNormalVehicles());
		// TODO pe�es
		String idStr = Integer.toString(thisAgent.getID());

		return priorityStr + DELIMITER + emergencyVehiclesStr + DELIMITER + numberOfVehiclesStr + DELIMITER + idStr;
	}

}