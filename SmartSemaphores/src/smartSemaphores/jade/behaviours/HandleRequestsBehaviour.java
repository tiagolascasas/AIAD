package smartSemaphores.jade.behaviours;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.behaviours.CyclicBehaviour;
import smartSemaphores.jade.SemaphoreStates;
import smartSemaphores.jade.SemaphoricAgent;

public class HandleRequestsBehaviour extends CyclicBehaviour
{
	private static final String DELIMITER = "/";
	private static final int INFO_PRIORITY_LENGTH = 4;
	private static final long serialVersionUID = -6969060109297684076L;
	private static final String REQUEST_ID = "Request-priority";
	private static final String INFORM_ID = "Inform-priority";

	int step = 0;

	@Override
	public void action()
	{
		switch (step)
		{
			case 0:
				System.out.println("HandleRequest0");
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null)
				{
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
				System.out.println("HandleRequest1");
				MessageTemplate mtConfirmAndDisconfirm = MessageTemplate.MatchConversationId(INFORM_ID);

				ACLMessage informMSG = myAgent.receive(mtConfirmAndDisconfirm);
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