/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package tests;

import java.util.ArrayList;

import junit.framework.TestCase;
import smartSemaphores.jade.behaviours.StateCommunicationBehaviour;

/**
 * The Class ConsensusAlgorithmTest.
 */
public class ConsensusAlgorithmTest extends TestCase {

    /**
     * Test find if green candidate.
     */
    public void testFindIfGreenCandidate() {
	ArrayList<String> greenCandidates = new ArrayList<>();
	ArrayList<String[]> allPriorityInformation = new ArrayList<>();
	double priority = 8.998;
	String[] stringArray1 = { "4.777", "0", "100", "2" };
	String[] stringArray2 = { "8.999", "0", "100", "3" };
	String[] stringArray3 = { "0.0", "0", "100", "4" };

	allPriorityInformation.add(stringArray1);
	allPriorityInformation.add(stringArray2);
	allPriorityInformation.add(stringArray3);
	// Test that there are higher priority's
	assertFalse(
		StateCommunicationBehaviour.findIfGreenCandidate(greenCandidates, allPriorityInformation, priority));
	assertTrue(greenCandidates.isEmpty());

	// Test that this has the highest priority
	priority = 11.0;
	assertTrue(StateCommunicationBehaviour.findIfGreenCandidate(greenCandidates, allPriorityInformation, priority));
	assertTrue(greenCandidates.isEmpty());

	// Test that this is in a tie to highest priority
	priority = 8.999;
	assertTrue(StateCommunicationBehaviour.findIfGreenCandidate(greenCandidates, allPriorityInformation, priority));
	assertFalse(greenCandidates.isEmpty());
	assertTrue(greenCandidates.size() == 1);

	greenCandidates = new ArrayList<>();
	String[] stringArray4 = { "8.999", "0", "100", "5" };
	allPriorityInformation.add(stringArray4);
	assertTrue(StateCommunicationBehaviour.findIfGreenCandidate(greenCandidates, allPriorityInformation, priority));
	assertFalse(greenCandidates.isEmpty());
	assertTrue(greenCandidates.size() == 2);
    }

    /**
     * Test green tie breaker by emergency vehicles.
     */
    public void testGreenTieBreakerByEmergencyVehicles() {
	// Test GreenTieBreaker by Emergency Vehicles
	ArrayList<String> greenCandidates = new ArrayList<>();
	ArrayList<String[]> allPriorityInformation = new ArrayList<>();
	double priority = 12.0;
	String[] stringArray1 = { "12.0", "1", "100", "2" };
	String[] stringArray2 = { "12.0", "2", "100", "3" };
	String[] stringArray3 = { "12.0", "3", "100", "4" };

	allPriorityInformation.add(stringArray1);
	allPriorityInformation.add(stringArray2);
	allPriorityInformation.add(stringArray3);

	assertTrue(StateCommunicationBehaviour.findIfGreenCandidate(greenCandidates, allPriorityInformation, priority));
	assertFalse(greenCandidates.isEmpty());

	int[] agentInfo = { 0, 100, 5 };
	assertFalse(StateCommunicationBehaviour.greenTieBreaker(greenCandidates, allPriorityInformation, agentInfo));
	agentInfo[0] = 4;
	assertTrue(StateCommunicationBehaviour.greenTieBreaker(greenCandidates, allPriorityInformation, agentInfo));
    }

    /**
     * Test green tie breaker by number of vehicles.
     */
    public void testGreenTieBreakerByNumberOfVehicles() {
	ArrayList<String> greenCandidates = new ArrayList<>();
	ArrayList<String[]> allPriorityInformation = new ArrayList<>();
	double priority = 12.0;
	String[] stringArray1 = { "12.0", "1", "70", "2" };
	String[] stringArray2 = { "12.0", "1", "80", "3" };
	String[] stringArray3 = { "12.0", "1", "90", "4" };
	allPriorityInformation.add(stringArray1);
	allPriorityInformation.add(stringArray2);
	allPriorityInformation.add(stringArray3);

	assertTrue(StateCommunicationBehaviour.findIfGreenCandidate(greenCandidates, allPriorityInformation, priority));
	assertFalse(greenCandidates.isEmpty());

	int[] agentInfo = { 1, 89, 5 };
	assertFalse(StateCommunicationBehaviour.greenTieBreaker(greenCandidates, allPriorityInformation, agentInfo));
	agentInfo[1] = 91;
	assertTrue(StateCommunicationBehaviour.greenTieBreaker(greenCandidates, allPriorityInformation, agentInfo));
    }

    /**
     * Test green tie breaker by ID.
     */
    public void testGreenTieBreakerByID() {

	// Test GreenTieBreaker by ID
	ArrayList<String> greenCandidates = new ArrayList<>();
	ArrayList<String[]> allPriorityInformation = new ArrayList<>();
	double priority = 12.0;
	String[] stringArray1 = { "12.0", "1", "70", "2" };
	String[] stringArray2 = { "12.0", "1", "70", "3" };
	String[] stringArray3 = { "12.0", "1", "70", "4" };
	allPriorityInformation.add(stringArray1);
	allPriorityInformation.add(stringArray2);
	allPriorityInformation.add(stringArray3);

	assertTrue(StateCommunicationBehaviour.findIfGreenCandidate(greenCandidates, allPriorityInformation, priority));
	assertFalse(greenCandidates.isEmpty());

	int[] agentInfo = { 1, 70, 1 };
	assertFalse(StateCommunicationBehaviour.greenTieBreaker(greenCandidates, allPriorityInformation, agentInfo));
	agentInfo[2] = 5;
	assertTrue(StateCommunicationBehaviour.greenTieBreaker(greenCandidates, allPriorityInformation, agentInfo));
    }

}
