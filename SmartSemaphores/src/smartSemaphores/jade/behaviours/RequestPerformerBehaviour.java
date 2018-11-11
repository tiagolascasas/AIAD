/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.jade.behaviours;

import java.util.Random;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.AID;
import sajas.core.behaviours.CyclicBehaviour;
import smartSemaphores.jade.SemaphoreStates;
import smartSemaphores.jade.SemaphoricAgent;

/**
 * The Class RequestPerformerBehaviour.
 */
public class RequestPerformerBehaviour extends CyclicBehaviour {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4771134109565630310L;

    /** The Constant REQUEST_ID. */
    private static final String REQUEST_ID = "Request-priority";

    /** The Constant INFORM_ID. */
    private static final String INFORM_ID = "Inform-priority";

    /** The step. */
    int step = 0;

    /** The replys that accept the proposel . */
    int accepted = 0;

    /** The replies count. */
    int repliesCnt = 0;

    /** The SemaphoricAgent of this behaviour. */
    private SemaphoricAgent thisAgent;

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
	    if (thisAgent.getCurrentState().equals(SemaphoreStates.GREEN)) {
		return;
	    }

	    double priority = PriorityCalculator.calculatePriority(thisAgent);

	    double ratio = priority / PriorityCalculator.EMERGENCY_PRIORITY;

	    Random generator = new Random(System.currentTimeMillis());

	    if (generator.nextDouble() <= ratio) {

		ACLMessage requestMSG = new ACLMessage(ACLMessage.PROPOSE);

		for (int i = 0; i < thisAgent.getNeighbours().size(); i++) {

		    String name = thisAgent.getNeighbours().get(i);
		    requestMSG.addReceiver(new AID(name, AID.ISLOCALNAME));
		}

		requestMSG.setContent(Double.toString(priority));
		requestMSG.setConversationId(REQUEST_ID);
		myAgent.send(requestMSG);
		step = 1;
	    }

	    break;
	case 1:
	    ACLMessage reply;
	    MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
		    MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL));

	    while ((reply = myAgent.receive(mt)) != null) {

		repliesCnt++;
		if (reply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL)
		    accepted++;

		if (repliesCnt >= thisAgent.getNeighbours().size()) {
		    step = 2;
		    break;
		}
	    }

	    break;
	case 2:
	    ACLMessage informMSG;
	    if (accepted >= repliesCnt) {
		informMSG = new ACLMessage(ACLMessage.CONFIRM);
		thisAgent.switchState(SemaphoreStates.GREEN);
	    } else
		informMSG = new ACLMessage(ACLMessage.DISCONFIRM);

	    for (int i = 0; i < thisAgent.getNeighbours().size(); i++) {
		String name = thisAgent.getNeighbours().get(i);
		informMSG.addReceiver(new AID(name, AID.ISLOCALNAME));
	    }
	    informMSG.setConversationId(INFORM_ID);
	    myAgent.send(informMSG);
	    step = 0;
	    accepted = 0;
	    repliesCnt = 0;
	    break;
	}
    }
}