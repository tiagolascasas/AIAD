package smartSemaphores.jade.behaviours;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.behaviours.Behaviour;
import sajas.core.behaviours.CyclicBehaviour;
import smartSemaphores.jade.SemaphoreStates;
import smartSemaphores.jade.SemaphoricAgent;

public class HandleRequestsBehaviour extends Behaviour {
	private static final String DELIMITER = "-";
	private static final int INFO_PRIORITY_LENGTH = 4;
	private static final long serialVersionUID = -6969060109297684076L;

	int step;
	public HandleRequestsBehaviour(){
		super();
		step=0;
	};
	@Override
	public void action() {
		System.out.println("21423343454sdafssvga<gva<dfrga<wergazergazsregaerga");
		switch (step) {
		case 0:
			ACLMessage msg = myAgent.receive();
			if (msg != null) {
				System.out.println("sdafssvga<gva<dfrga<wergazergazsregaerga");
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
			System.out.println("asdasdasdasdad");
			ACLMessage informMSG = myAgent.receive();
			if (informMSG != null && (informMSG.getPerformative() == ACLMessage.CONFIRM
					|| informMSG.getPerformative() == ACLMessage.DISCONFIRM)) {
				if (informMSG.getPerformative() == ACLMessage.CONFIRM)
					((SemaphoricAgent) myAgent).switchState(SemaphoreStates.RED);
				step = 2;
			} else
				block();
			break;
		}
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return step == 2;
	}

}
