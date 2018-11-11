/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.jade.behaviours;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.behaviours.CyclicBehaviour;
import smartSemaphores.jade.SemaphoreStates;
import smartSemaphores.jade.SemaphoricAgent;

/**
 * The Class HandleRequestsBehaviour.
 */
public class HandleRequestsBehaviour extends CyclicBehaviour {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6969060109297684076L;

    /** The Constant INFORM_ID. */
    private static final String INFORM_ID = "Inform-priority";

    /** The current step. */
    int step = 0;

    /*
     * (non-Javadoc)
     * 
     * @see sajas.core.behaviours.Behaviour#action()
     */
    @Override
    public void action() {
	switch (step) {
	case 0:
	    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
	    ACLMessage msg = myAgent.receive(mt);
	    if (msg != null) {
		double priorityReceived = Double.parseDouble(msg.getContent());
		double priority = 0.0;
		priority = PriorityCalculator.calculatePriority((SemaphoricAgent) myAgent);
		ACLMessage reply = msg.createReply();
		if (priorityReceived >= priority)
		    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
		else
		    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
		myAgent.send(reply);
		step = 1;
	    } else
		block();
	    break;
	case 1:
	    MessageTemplate mtConfirmAndDisconfirm = MessageTemplate.MatchConversationId(INFORM_ID);

	    ACLMessage informMSG = myAgent.receive(mtConfirmAndDisconfirm);
	    if (informMSG != null && (informMSG.getPerformative() == ACLMessage.CONFIRM
		    || informMSG.getPerformative() == ACLMessage.DISCONFIRM)) {
		if (informMSG.getPerformative() == ACLMessage.CONFIRM)
		    ((SemaphoricAgent) myAgent).switchState(SemaphoreStates.RED);
		step = 0;
	    } else
		block();
	    break;
	}
    }
}