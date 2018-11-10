package smartSemaphores.jade.behaviours;

import java.util.ArrayList;
import smartSemaphores.jade.SemaphoricAgent;
import jade.lang.acl.ACLMessage;
import sajas.core.AID;
import sajas.core.behaviours.Behaviour;
import sajas.core.behaviours.CyclicBehaviour;
import smartSemaphores.jade.SemaphoreStates;


public class StateCommunicationBehaviour extends CyclicBehaviour
{
	private static final int PRIORITY_VALUE_POSITION = 0;

	private static final int ID_POSITION = 3;

	private static final String CONVERSATION_ID = "Inform-priority";

	private static final long serialVersionUID = 2495229105259679220L;

	private static final String DELIMITER = "/";
	private static final int INFO_PRIORITY_LENGTH = 4;

	private static final double EMERGENCY_PRIORITY = 12.0;
	private static final double ROAD_OVERFLOW_PRIORITY = 11.0;
	private static final double GREEN_MAX_TIME_PRIORITY = 0.0;
	private static final double GREEN_MIN_TIME_PRIORITY = 10.0;
	private static final double RED_MAX_TIME_PRIORITY = 10.0;

	private static final int RED_MAX_TIME = 180;
	private static final int GREEN_MAX_TIME = 120;
	private static final int GREEN_MIN_TIME = 20;

	private ArrayList<String[]> allPriorityInformation;

	private int repliesCnt = 0; // The counter of replies from seller agents
	private int step = 0;
	private double priority = 0;
	SemaphoricAgent thisAgent;

	@Override
	public void action()
	{
		thisAgent = (SemaphoricAgent) myAgent;
		switch (step)
		{
			case 0:
				System.out.println("Chego ao 0");
				allPriorityInformation= new ArrayList<>();
				this.priority = calculatePriority();
				ACLMessage InformMsg = new ACLMessage(ACLMessage.INFORM);
				for (int i = 0; i < thisAgent.getNeighbours().size(); ++i)
				{
					InformMsg.addReceiver(new AID(thisAgent.getNeighbours().get(i), AID.ISLOCALNAME)); 
				}
				InformMsg.setContent(priorityToString());
				InformMsg.setConversationId(CONVERSATION_ID);
				myAgent.send(InformMsg);
				step++;
				break;
			case 1:
				System.out.println("Chego ao 1");
				ACLMessage msg = myAgent.receive();
				if (msg != null && msg.getConversationId().equals(CONVERSATION_ID))
				{
					repliesCnt++;
					addPriorityInformation(msg.getSender().getName(), msg.getContent());
					if (repliesCnt >= thisAgent.getNeighbours().size())
					{
						step=2;
						repliesCnt=0;
					}
				} else
				{
					block();
				}
				break;
			case 2:
				System.out.println("Chego ao 2");
				// Decidir se tem de mudar o estado - Descobrir sem�foro com maior prioridade
				ArrayList<String> greenCandidates = new ArrayList<>();

				boolean isCandidate = findIfGreenCandidate(greenCandidates);

				if (isCandidate && greenCandidates.isEmpty()) // Se for o �nico com m�ximo valor de prioridade calculada
																// passa a verde
					thisAgent.switchState(SemaphoreStates.GREEN);
				else if (isCandidate && greenTieBreaker(greenCandidates)) // Se tiver valor m�ximo de prioridade
																			// calculada e
																			// Passar no desempate passa a verde
					thisAgent.switchState(SemaphoreStates.GREEN);
				else // Se n�o tiver valor m�ximo ou n�o passar no desempate passa a vermelho
					thisAgent.switchState(SemaphoreStates.RED);

				step=0;
				break;
		}
		return;
	}

	private boolean greenTieBreaker(ArrayList<String> greenCandidates)
	{
		int[] agentVariables = { thisAgent.getCurrentEmergencyVehicles(), thisAgent.getCurrentNormalCars(), thisAgent.getID() };

		for (int i = 1; i < INFO_PRIORITY_LENGTH; i++)
		{ // i=1 because priority was already evaluated
			if (greenCandidates.isEmpty())
				break;

			for (int j = 0; j < allPriorityInformation.size(); j++)
			{
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

	private boolean findIfGreenCandidate(ArrayList<String> greenCandidates)
	{
		for (int i = 0; i < allPriorityInformation.size(); i++)
			for(int j=0; j<INFO_PRIORITY_LENGTH;j++)
				System.out.println(Integer.toString(i) + "-" + allPriorityInformation.get(i)[j]);

		for (int i = 0; i < allPriorityInformation.size(); i++)
		{
			double priorityI = Double.parseDouble(allPriorityInformation.get(i)[PRIORITY_VALUE_POSITION]);
			if (priorityI > priority)
			{
				return false;
			}
			if (priorityI == priority)
			{
				greenCandidates.add(allPriorityInformation.get(i)[ID_POSITION]);
			}

		}
		return true;
	}

	private void addPriorityInformation(String aid, String content)
	{
		String[] splitContent = content.split(DELIMITER);

		allPriorityInformation.add(splitContent);
	}

	private String priorityToString()
	{
		String priorityStr = Double.toString(priority);
		String emergencyVehiclesStr = Integer.toString(thisAgent.getCurrentEmergencyVehicles());
		String numberOfVehiclesStr = Integer.toString(thisAgent.getCurrentNormalVehicles());
		// TODO pe�es
		String idStr = Integer.toString(thisAgent.getID());

		return priorityStr + DELIMITER + emergencyVehiclesStr + DELIMITER + numberOfVehiclesStr + DELIMITER + idStr;
	}


	private double calculatePriority()
	{
		if (thisAgent.hasEmergencyVehicles())
			return EMERGENCY_PRIORITY;

		if (thisAgent.carRoadRatio() >= 1.0)
			return ROAD_OVERFLOW_PRIORITY;

		if (thisAgent.getCurrentState() == SemaphoreStates.GREEN)
			return calculatePriorityGreen();
		else if (thisAgent.getCurrentState() == SemaphoreStates.RED)
			return calculatePriorityRed();

		return -1.0; // TODO SE ISTO ACONTECER SOMETHING IS REALLY WRONG.... HOWEVER IT SHOULD
						// PROCEED AS NORMAL?????
	}

	private double calculatePriorityGreen()
	{
		if (thisAgent.getSecondsPassedOnState() >= GREEN_MAX_TIME)
			return GREEN_MAX_TIME_PRIORITY;
		else if (thisAgent.getSecondsPassedOnState() >= GREEN_MIN_TIME)
			return GREEN_MIN_TIME_PRIORITY;
		else
			return ((-0.0002 * Math.pow(thisAgent.getSecondsPassedOnState(), 2))
					- (0.0243 * thisAgent.getSecondsPassedOnState()) + 5.7327) + (thisAgent.carRoadRatio() * 5);

	}

	private double calculatePriorityRed()
	{
		if (thisAgent.getSecondsPassedOnState() >= RED_MAX_TIME)
			return RED_MAX_TIME_PRIORITY;

		return ((0.0002 * Math.pow(thisAgent.getSecondsPassedOnState(), 2))
				+ (0.0005 * thisAgent.getSecondsPassedOnState()) - 0.0578) + (thisAgent.carRoadRatio() * 5);
	}
}