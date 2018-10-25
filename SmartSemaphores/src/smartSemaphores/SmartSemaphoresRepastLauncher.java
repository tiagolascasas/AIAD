/**
 * 
 */
package smartSemaphores;

import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.tools.rma.rma;
import jade.wrapper.StaleProxyException;
import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.visualizationOGL2D.DefaultEdgeStyleOGL2D;
import sajas.core.Agent;
import sajas.core.Runtime;
import sajas.sim.repasts.RepastSLauncher;
import sajas.wrapper.ContainerController;

/**
 * Launcher for Repast
 */
public class SmartSemaphoresRepastLauncher extends RepastSLauncher
{
	private ContainerController mainContainer;
	private ContainerController crossContainerA;
	private ContainerController crossContainerB;
	private ContainerController crossContainerC;

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
		Profile p0 = new ProfileImpl();
		Profile p1 = new ProfileImpl();
		Profile p2 = new ProfileImpl();
		Profile p3 = new ProfileImpl();

		this.mainContainer = rt.createMainContainer(p0);
		this.crossContainerA = rt.createAgentContainer(p1);
		this.crossContainerB = rt.createAgentContainer(p2);
		this.crossContainerC = rt.createAgentContainer(p3);
		
		launchAgents();
	}

	private void launchAgents()
	{
		try
		{
			// Semaphoric agents on road cross A
			int[] crossAagents = { 1, 11, 4, 8 };
			for (int i : crossAagents)
			{
				SemaphoricAgent agent = new SemaphoricAgent(i, crossAagents);
				this.crossContainerA.acceptNewAgent("Semaphoric agent " + i, agent).start();
			}

			// Semaphoric agents on road cross B
			int[] crossBagents = { 3, 13, 6, 10 };
			for (int i : crossBagents)
			{
				SemaphoricAgent agent = new SemaphoricAgent(i, crossBagents);
				this.crossContainerB.acceptNewAgent("Semaphoric agent " + i, agent).start();
			}

			// Semaphoric agents on road cross C
			int[] crossCagents = { 14, 17, 15, 12 };
			for (int i : crossCagents)
			{
				SemaphoricAgent agent = new SemaphoricAgent(i, crossCagents);
				this.crossContainerC.acceptNewAgent("Semaphoric agent " + i, agent).start();
			}

			// Sink agents (two sinks per cross)
			this.crossContainerA.acceptNewAgent("Sink agent 2", new SinkAgent(2)).start();
			this.crossContainerA.acceptNewAgent("Sink agent 7", new SinkAgent(7)).start();

			this.crossContainerB.acceptNewAgent("Sink agent 5", new SinkAgent(5)).start();
			this.crossContainerB.acceptNewAgent("Sink agent 9", new SinkAgent(9)).start();

			this.crossContainerC.acceptNewAgent("Sink agent 16", new SinkAgent(16)).start();
			this.crossContainerC.acceptNewAgent("Sink agent 18", new SinkAgent(18)).start();

		} catch (StaleProxyException e)
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

	public static Agent getAgent(Context<?> context, String name)
	{
		for (Object obj : context.getObjects(Agent.class))
		{
			if (((Agent) obj).getAID().getName().equals(name))
			{
				return (Agent) obj;
			}
		}
		return null;
	}

}
