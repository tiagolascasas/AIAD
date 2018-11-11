package smartSemaphores.repast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import smartSemaphores.SmartSemaphores;
import smartSemaphores.jade.SemaphoreStates;
import smartSemaphores.jade.SemaphoricAgent;

public class StatisticReportsCreator
{
	public static void generateVariablesReport(String uniqueID)
	{
		StringBuilder string = new StringBuilder();
		
		string.append("Simulation Type, Hours, Exit rate (cars/second), Max. ticks, Emergency prob., Pedestrian prob.\n")
		.append(SmartSemaphores.SIMULATION_TYPE).append(",")
		.append(SmartSemaphores.HOURS).append(",")
		.append(SmartSemaphores.EXIT_RATE).append(",")
		.append(SmartSemaphores.MAX_TICKS).append(",")
		.append(SmartSemaphores.EMERGENCY_PROBABILITY).append(",")
		.append(SmartSemaphores.PEDESTRIAN_PROBABILITY).append("\n");
		
		String filename = makePath(uniqueID) + "_parameters.csv";
		
		saveToFile(filename, string.toString());
	}
	
	public static void generateAverageTimesDatasets(String uniqueID, ArrayList<Vehicle> exitedNormal,
			ArrayList<Vehicle> exitedEmer)
	{
		TimesTable t1 = new TimesTable(exitedNormal);
		TimesTable t2 = new TimesTable(exitedEmer);

		String report1 = "source,sink,average_time\n" + t1.toString();
		String report2 = "source,sink,average_time\n" + t2.toString();

		String filename = makePath(uniqueID) + "_avg_time_";

		saveToFile(filename + "normal.csv", report1);
		saveToFile(filename + "emergency.csv", report2);
	}

	public static void generateAllTimesDatasets(String uniqueID, ArrayList<Vehicle> exitedNormal,
			ArrayList<Vehicle> exitedEmer)
	{
		StringBuilder string1 = new StringBuilder();
		StringBuilder string2 = new StringBuilder();

		string1.append("origin,exit,elapsed_time\n");

		for (Vehicle v : exitedNormal)
			string1.append(v.getOriginPoint()).append(",").append(v.getEndPoint()).append(",")
					.append(v.getElapsedTime()).append("\n");

		for (Vehicle v : exitedEmer)
			string2.append(v.getOriginPoint()).append(",").append(v.getEndPoint()).append(",")
					.append(v.getElapsedTime()).append("\n");

		String filename = makePath(uniqueID) + "_total_time_";

		saveToFile(filename + "normal.csv", string1.toString());
		saveToFile(filename + "emergency.csv", string2.toString());
	}

	public static void generateSemaphoreDataset(String uniqueID, ArrayList<SemaphoricAgent> agents)
	{
		HashMap<Integer, ArrayList<SemaphoreStates>> trackers = new HashMap<>();
		ArrayList<String> header = new ArrayList<>();
		header.add("tick");

		for (SemaphoricAgent agent : agents)
		{
			trackers.put(agent.getID(), agent.getStateTracker());
			header.add("" + agent.getID());
		}

		StringBuilder string = new StringBuilder();
		string.append(String.join(",", header)).append("\n");

		for (int i = 0; i < SmartSemaphores.MAX_TICKS; i++)
		{
			string.append(i);

			for (Integer id : trackers.keySet())
				string.append(",").append(trackers.get(id).get(i));
			string.append("\n");
		}

		String filename = makePath(uniqueID) + "_semaphores_state.csv";

		saveToFile(filename, string.toString());
	}
	
	private static void saveToFile(String filename, String content)
	{
		try
		{
			File file = new File(filename);
			file.getParentFile().mkdir();
			file.createNewFile();
			PrintWriter f = new PrintWriter(file);
			f.write(content);
			f.close();

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private static String makePath(String uniqueID)
	{
		return "results/" + uniqueID + "/" + uniqueID;
	}
}
