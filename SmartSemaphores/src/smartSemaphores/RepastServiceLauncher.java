/**
 * 
 */
package smartSemaphores;

import jade.core.Profile;
import jade.core.ProfileImpl;
import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import sajas.core.Runtime;
import sajas.sim.repasts.RepastSLauncher;

/**
 *  Launcher for Repast
 */
public class RepastServiceLauncher extends RepastSLauncher {

	/* (non-Javadoc)
	 * @see sajas.sim.repasts.RepastSLauncher#getName()
	 */
	@Override
	public String getName() {
		return "Smart Semaphores";
	}

	/* (non-Javadoc)
	 * @see sajas.sim.repasts.RepastSLauncher#launchJADE()
	 */
	@SuppressWarnings("unused")
	@Override
	protected void launchJADE() {
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		/*mainContainer = rt.createMainContainer(p1);
		
		if(SEPARATE_CONTAINERS) {
			Profile p2 = new ProfileImpl();
			agentContainer = rt.createAgentContainer(p2);
		} else {
			agentContainer = mainContainer;
		}
		
		launchAgents();*/
	}
	
	@Override
	public Context<?> build(Context<Object> context) {
		// http://repast.sourceforge.net/docs/RepastJavaGettingStarted.pdf
		
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("Smart Semaphores network", context, true);
		netBuilder.buildNetwork();
		
		return super.build(context);
	}

}
