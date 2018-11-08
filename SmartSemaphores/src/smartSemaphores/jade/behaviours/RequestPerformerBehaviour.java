package smartSemaphores.jade.behaviours;

import java.util.Random;

import jade.lang.acl.ACLMessage;
import sajas.core.AID;
import sajas.core.Agent;
import sajas.core.behaviours.Behaviour;
import sajas.core.behaviours.CyclicBehaviour;
import smartSemaphores.jade.SemaphoreStates;
import smartSemaphores.jade.SemaphoricAgent;

public class RequestPerformerBehaviour extends Behaviour {
	private static final long serialVersionUID = -4771134109565630310L;
	private static final String REQUEST_ID = "Request-priority";
	private static final String INFORM_ID = "Inform-priority";

	int step;
	int accepted;
	int repliesCnt;
	private SemaphoricAgent thisAgent;

	public RequestPerformerBehaviour() {
		super();
		step = 0;
		accepted = 0;
		repliesCnt = 0;
	}

	@Override
	public void action() {
		System.out.println(step);
		thisAgent = (SemaphoricAgent) myAgent;

		switch (step) {
		case 0:
			if (((SemaphoricAgent) myAgent).getCurrentState() == SemaphoreStates.GREEN) {
				step = 3;
				return;
			}

			double priority = PriorityCalculator.calculatePriority(thisAgent);
			double ratio = priority / PriorityCalculator.EMERGENCY_PRIORITY; // 12 é o max priority

			Random generator = new Random(System.currentTimeMillis());

			if (generator.nextDouble() <= ratio) {
				ACLMessage requestMSG = new ACLMessage(ACLMessage.PROPOSE);

				for (int i = 0; i < ((SemaphoricAgent) myAgent).getNeighbours().size(); i++) {
					String name = ((SemaphoricAgent) myAgent).getNeighbours().get(i);

					String id = name.replaceAll("\\D+", "");
					Agent neighbour = thisAgent.getNeighbourByID(id);

					requestMSG.addReceiver(neighbour.getAID());
					// requestMSG.addReceiver(new AID(name, false));
				}

				requestMSG.setContent(Double.toString(priority));
				requestMSG.setConversationId(REQUEST_ID);
				myAgent.send(requestMSG);
				step = 1;
			} else
				step = 3;

			break;
		case 1:
			ACLMessage reply = myAgent.receive();
			if (reply != null) {
				// Reply received
				if (reply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL
						|| reply.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
					repliesCnt++;
					if (reply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL)
						accepted++;
				}

				if (repliesCnt >= ((SemaphoricAgent) myAgent).getNeighbours().size()) {
					step = 2;
				}
			} else {
				block();
			}
			break;
		case 2:
			ACLMessage informMSG;
			if (accepted >= repliesCnt) {
				informMSG = new ACLMessage(ACLMessage.CONFIRM);
				((SemaphoricAgent) myAgent).switchState(SemaphoreStates.GREEN);
			} else
				informMSG = new ACLMessage(ACLMessage.DISCONFIRM);

			for (int i = 0; i < ((SemaphoricAgent) myAgent).getNeighbours().size(); i++) {
				String name = ((SemaphoricAgent) myAgent).getNeighbours().get(i);
				informMSG.addReceiver(new AID(name, false));
			}
			informMSG.setConversationId(INFORM_ID);
			myAgent.send(informMSG);
			step = 3;
			break;
		}
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return step == 3;
	}

}
