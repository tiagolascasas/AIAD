/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.jade.behaviours;

import java.util.ArrayList;
import smartSemaphores.jade.SemaphoricAgent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.AID;
import sajas.core.behaviours.CyclicBehaviour;
import smartSemaphores.jade.SemaphoreStates;

/**
 * The Class StateCommunicationBehaviour.
 */
public class StateCommunicationBehaviour extends CyclicBehaviour {

    /** The Constant PRIORITY_VALUE_POSITION. */
    private static final int PRIORITY_VALUE_POSITION = 0;

    /** The Constant ID_POSITION. */
    private static final int ID_POSITION = 3;

    /** The Constant CONVERSATION_ID. */
    private static final String CONVERSATION_ID = "Inform-priority";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2495229105259679220L;

    /** The Constant DELIMITER. */
    private static final String DELIMITER = "/";

    /** The Constant INFO_PRIORITY_LENGTH. */
    private static final int INFO_PRIORITY_LENGTH = 4;

    /** The all priority information. */
    private ArrayList<String[]> allPriorityInformation;

    /** The replies count. */
    private int repliesCnt = 0; // The counter of replies from seller agents

    /** The step. */
    private int step = 0;

    /** The priority. */
    private double priority = 0;

    /** The Semaphoric Agent of this behaviour. */
    SemaphoricAgent thisAgent;

    /*
     * (non-Javadoc)
     * 
     * @see sajas.core.behaviours.Behaviour#action()
     */
    @Override
    public void action() {
	thisAgent = (SemaphoricAgent) myAgent;
	switch (step) {
	case 0:
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
	    MessageTemplate mt = MessageTemplate.MatchConversationId(CONVERSATION_ID);
	    ACLMessage msg;
	    while (((msg = myAgent.receive(mt)) != null)) {
		repliesCnt++;
		addPriorityInformation(msg.getContent());
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

    /**
     * Process messages.
     */
    private void processMessages() {
	ArrayList<String> greenCandidates = new ArrayList<>();

	boolean isCandidate = findIfGreenCandidate(greenCandidates, allPriorityInformation, priority);

	int[] agentInfo = new int[] { thisAgent.getCurrentEmergencyVehicles(), thisAgent.getCurrentNormalCars(),
		thisAgent.getID() };

	if (isCandidate && greenCandidates.isEmpty())
	    thisAgent.switchState(SemaphoreStates.GREEN);
	else if (isCandidate && greenTieBreaker(greenCandidates, allPriorityInformation, agentInfo))
	    thisAgent.switchState(SemaphoreStates.GREEN);
	else
	    thisAgent.switchState(SemaphoreStates.RED);
    }

    /**
     * Green tie breaker.
     *
     * @param greenCandidates
     *            the green candidates
     * @param allPriorityInformation
     *            all priority information
     * @param agentVariables
     *            this agent variables
     * @return true, if won tie breaker
     */
    public static boolean greenTieBreaker(ArrayList<String> greenCandidates, ArrayList<String[]> allPriorityInformation,
	    int[] agentVariables) {

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

    /**
     * Find if green candidate.
     *
     * @param greenCandidates
     *            the green candidates
     * @param allPriorityInformation
     *            all priority information
     * @param priority
     *            the priority
     * @return true, if green candidate
     */
    public static boolean findIfGreenCandidate(ArrayList<String> greenCandidates,
	    ArrayList<String[]> allPriorityInformation, double priority) {
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

    /**
     * Adds the priority information.
     *
     * @param content
     *            the content
     */
    private void addPriorityInformation(String content) {
	String[] splitContent = content.split(DELIMITER);

	allPriorityInformation.add(splitContent);
    }

    /**
     * Priority to string.
     *
     * @return the string
     */
    private String priorityToString() {
	String priorityStr = Double.toString(priority);
	String emergencyVehiclesStr = Integer.toString(thisAgent.getCurrentEmergencyVehicles());
	String numberOfVehiclesStr = Integer.toString(thisAgent.getCurrentNormalVehicles());
	String idStr = Integer.toString(thisAgent.getID());

	return priorityStr + DELIMITER + emergencyVehiclesStr + DELIMITER + numberOfVehiclesStr + DELIMITER + idStr;
    }

}