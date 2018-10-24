/**
 * 
 */
package smartSemaphores;

import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import sajas.core.Agent;
import sajas.core.Runtime;
import sajas.sim.repasts.RepastSLauncher;
import sajas.wrapper.ContainerController;

/**
 * Launcher for Repast
 */
public class SmartSemaphoresRepastLauncher extends RepastSLauncher
{
	private ContainerController cross1;
	private ContainerController cross2;
	private ContainerController cross3;

	/*
	 * (non-Javadoc)
	 * 
	 * @see sajas.sim.repasts.RepastSLauncher#getName()
	 */
	@Override
	public String getName()
	{
		return "SmartSemaphores";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sajas.sim.repasts.RepastSLauncher#launchJADE()
	 */
	@SuppressWarnings("unused")
	@Override
	protected void launchJADE()
	{
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();

		cross1 = rt.createMainContainer(p1);
		//cross2 = rt.createMainContainer(p1);
		//cross3 = rt.createMainContainer(p1);

		launchAgents();
	}

	private void launchAgents()
	{
		try
		{
			SemaphoricAgent agent = new SemaphoricAgent();
			this.cross1.acceptNewAgent("Semaphoric agent", agent).start();
		} 
		catch (StaleProxyException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Context<?> build(Context<Object> context)
	{
		// http://repast.sourceforge.net/docs/RepastJavaGettingStarted.pdf

		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("SmartSemaphores Road Network", context, true);
		netBuilder.buildNetwork();

		return super.build(context);
	}

}
