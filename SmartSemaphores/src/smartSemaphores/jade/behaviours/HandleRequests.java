package smartSemaphores.jade.behaviours;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.behaviours.CyclicBehaviour;
import smartSemaphores.jade.SemaphoreStates;
import smartSemaphores.jade.SemaphoricAgent;

public class HandleRequests extends CyclicBehaviour
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6969060109297684076L;

	int step = 0;

	@Override
	public void action()
	{
		switch (step)
		{
			case 0:
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null)
				{
					double priorityReceived = Double.parseDouble(msg.getContent());
					double priority = 0.0;
					// priority = calculatePriority(); usar função disponivel no
					// STateCommunicationBehaviour
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
				ACLMessage informMSG = myAgent.receive();
				if (informMSG != null && (informMSG.getPerformative() == ACLMessage.CONFIRM
						|| informMSG.getPerformative() == ACLMessage.DISCONFIRM))
				{
					if (informMSG.getPerformative() == ACLMessage.CONFIRM)
						((SemaphoricAgent) myAgent).switchState(SemaphoreStates.RED);
					step = 0;
				} else
					block();
				break;
		}
	}

}
