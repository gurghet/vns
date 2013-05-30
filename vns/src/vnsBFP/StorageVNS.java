package vnsBFP;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

public class StorageVNS {
	// ArrayList che memorizza tutti i job divisi per macchine.
	private ArrayList<ArrayList<Job>> allMachines = null;

	// HashMap che memorizza in quale macchina sono schedulati i job.
	private HashMap<Job, Integer> jobMap = null;

	// TWT attuale.
	private long twt;

	private int nMachines = 0;

	// strutture per calculatetwt
	HashMap<Job, Long> scheduledJobs;
	int[] currentJobIndex;

	@SuppressWarnings("unchecked")
	public StorageVNS(int nMachines) {
		allMachines = new ArrayList<ArrayList<Job>>();
		jobMap = new HashMap<Job, Integer>();

		for (int x = 0; x < nMachines; x++) {
			allMachines.add(new ArrayList<Job>());
		}
		this.nMachines = nMachines;
		currentJobIndex = new int[nMachines];
		scheduledJobs = new HashMap<Job, Long>();
	}

	public ArrayList<ArrayList<Job>> getAllMachines() {
		return allMachines;
	}

	public long getTwt() {
		return twt;
	}

	public int getNumberOfMachines() {
		return allMachines.size();
	}

	public int getNumberOfJobsOnMachine(int machineNumber) {
		return allMachines.get(machineNumber).size();
	}

	public ArrayList<Job> getMachine(int position) {
		return allMachines.get(position);
	}

	// Metodi di inserimento e cancellazione

	public void addJobOnMachine(int machineNumber, Job j) {
		allMachines.get(machineNumber).add(j);
		jobMap.put(j, machineNumber);
	}

	public void removeJobFromMachine(int position, int machineNumber) {
		Job j = allMachines.get(machineNumber).remove(position);
		jobMap.remove(j);
	}

	// Non usato
	public void removeAllJobsFromMachine(int machineNumber) {
		for (int x = 0; x < allMachines.get(machineNumber).size(); x++) {
			allMachines.get(machineNumber).remove(x);
		}
	}

	// Mosse

	/**
	 * @param range
	 *            raggio in cui verranno effettuati gli swap
	 * @param repeat
	 *            quante volte verr������ ripetuta questa mossa alla fine sto
	 *            metodo fa un po������� le cose a casaccio, ma in fondo �����
	 *            un algoritmo random no?
	 */
	public boolean swapOnOneMachine(int range, int repeat) {
		boolean miglioramentoAvvenuto = false;
		for (int r = 0; r < repeat; r++) {
			for (int i = 0; i < this.getNumberOfMachines(); i++) {
				if (range == 0)	range = this.getNumberOfJobsOnMachine(i);
				for (int j = 0; j < this.getNumberOfJobsOnMachine(i); j++) {
					miglioramentoAvvenuto = miglioramentoAvvenuto
							|| _swapOnOneMachine_onMachine_onPosition(range,
									allMachines.get(i), j);
				}
			}
		}

		return miglioramentoAvvenuto;
	}

	public boolean _swapOnOneMachine_onMachine_onPosition(int range,
			ArrayList<Job> machine, int position) {
		Main.log("eseguo swaponone");
		Job consideredJob = machine.get(position);

		int leftLimitCurrent = calculateLeftLimit(consideredJob, machine, position, range);
		int rightLimitCurrent = calculateRightLimit(consideredJob, machine, position,
				range);

		// Calcolo la posizione con cui fare lo swap
		int distance = rightLimitCurrent - leftLimitCurrent;
		int posInRange = (int) (Math.random() * distance) + 1;
		int newPos = leftLimitCurrent + posInRange;

		// Effettuo lo swap
		Job substitutedJob = machine.get(newPos);
		
		/*questo codice va a controllare le precedenze dall’altra parte*/
		int leftLimitDest = calculateLeftLimit(substitutedJob, machine, newPos, machine.size());
		int rightLimitDest = calculateRightLimit(substitutedJob, machine, newPos, machine.size());
		if (position < leftLimitDest || position > rightLimitDest) {
			Main.logf("Swap Non Effettuato");
			return false;
		}

		machine.set(position, substitutedJob);
		machine.set(newPos, consideredJob);

		long newTwt = calculateTwt();

		if (newTwt < twt) {
			twt = newTwt;
			Main.log("Swap Effettuato");
			return true;
		} else {
			machine.set(position, consideredJob);
			machine.set(newPos, substitutedJob);
			Main.log("Swap Non Effettuato");
			return false;
		}
	}

	public boolean transferOnOneMachine(int range, int repeat) {
		boolean miglioramentoAvvenuto = false;
		for (int r = 0; r < repeat; r++) {
			for (int i = 0; i < this.getNumberOfMachines(); i++) {
				if (range == 0)	range = this.getNumberOfJobsOnMachine(i);
				for (int j = 0; j < this.getNumberOfJobsOnMachine(i); j++) {
					miglioramentoAvvenuto = miglioramentoAvvenuto
							|| _transferOnOneMachine_onMachine_onPosition(
									range, allMachines.get(i), j);
				}
			}
		}
		return miglioramentoAvvenuto;
	}

	public boolean _transferOnOneMachine_onMachine_onPosition(int range,
			ArrayList<Job> machine, int position) {
		Job toBeInsertedJob = machine.remove(position);

		int leftLimit = calculateLeftLimit(toBeInsertedJob, machine, position, range);
		int rightLimit = calculateRightLimit(toBeInsertedJob, machine, position, range);

		// Calcolo la posizione in cui fare il transfert
		int distance = rightLimit - leftLimit;
		int posInRange = (int) (Math.random() * distance) + 1;
		int newPos = leftLimit + posInRange;

		machine.add(newPos, toBeInsertedJob);

		long newTwt = calculateTwt();

		if (newTwt < twt) {
			twt = newTwt;
			Main.log("Transfer Effettuato");
			return true;
		} else {
			// rimetto il job dove stava prima
			Job toBePutBackJob = machine.remove(newPos);
			machine.add(position, toBePutBackJob);
			Main.log("Transfer Non Effettuato");
			return false;
		}

	}

	public boolean swapAcrossMachines(int range, int repeat) {
		boolean miglioramentoAvvenuto = false;
		for (int r = 0; r < repeat; r++) {
			for (int i = 0; i < this.getNumberOfMachines(); i++) {
				if (range == 0)	range = this.getNumberOfJobsOnMachine(i);
				for (int j = 0; j < this.getNumberOfJobsOnMachine(i); j++) {
					miglioramentoAvvenuto = miglioramentoAvvenuto
							|| _swapAcrossMachines_onMachine_onPosition(range,
									allMachines.get(i), j);
				}
			}
		}

		return miglioramentoAvvenuto;
	}

	public boolean _swapAcrossMachines_onMachine_onPosition(int range,
			ArrayList<Job> sourceMachine, int position) {
		Main.log("Eseguo swap across");
		Job consideredJob = sourceMachine.get(position);
		int index = allMachines.indexOf(sourceMachine);
		int numberOfMachines = allMachines.size();
		int destMachineNumber = (int) (Math.random() * numberOfMachines); // potrebbe essere sè stessa

		// range = 0 ����� come dire range = nmax
		if (range == 0)
			range = this.getNumberOfJobsOnMachine(destMachineNumber);

		ArrayList<Job> destMachine = allMachines.get(destMachineNumber);

		int leftLimit = calculateLeftLimit(consideredJob, destMachine, position, range);
		int rightLimit = calculateRightLimit(consideredJob, destMachine, position, range);

		// Calcolo la osizione con cui fare lo swap all'interno del range
		int rangeSize = rightLimit - leftLimit;
		int posInRange = (int) (Math.random() * rangeSize) + 1;
		int swapPos = leftLimit + posInRange;

		Job substitutedJob = destMachine.get(swapPos);
		
		/*questo codice va a controllare le precedenze dall’altra parte*/
		int leftLimitDest = calculateLeftLimit(substitutedJob, sourceMachine, position, destMachine.size());
		int rightLimitDest = calculateRightLimit(substitutedJob, sourceMachine, position, destMachine.size());
		if (position < leftLimitDest || position > rightLimitDest) {
			Main.logf("SwapAcross Non Effettuato");
			return false;
		}

		// Effettuo lo scambio
		destMachine.set(swapPos, consideredJob);
		sourceMachine.set(position, substitutedJob);

		long newTwt = calculateTwt();

		if (newTwt < twt) {
			twt = newTwt;
			return true;
		} else {
			destMachine.set(swapPos, substitutedJob);
			jobMap.remove(consideredJob);
			jobMap.put(consideredJob, destMachineNumber);
			jobMap.remove(substitutedJob);
			jobMap.put(substitutedJob, allMachines.indexOf(sourceMachine));
			sourceMachine.set(position, consideredJob);
			return false;
		}
	}

	public boolean transferAcrossMachines(int range, int repeat) {
		boolean miglioramentoAvvenuto = false;
		for (int r = 0; r < repeat; r++) {
			for (int i = 0; i < this.getNumberOfMachines(); i++) {
				if (range == 0)	range = this.getNumberOfJobsOnMachine(i);
				for (int j = 0; j < this.getNumberOfJobsOnMachine(i); j++) {
					miglioramentoAvvenuto = miglioramentoAvvenuto
							|| _transferAcrossMachines_onMachine_onPosition(
									range, allMachines.get(i), j);
				}
			}
		}

		return miglioramentoAvvenuto;
	}

	public boolean _transferAcrossMachines_onMachine_onPosition(int range,
			ArrayList<Job> sourceMachine, int position) {
		Main.log("Eseguo transfer across");

		int index = allMachines.indexOf(sourceMachine);
		int machineNumber = index;

		// Calcolo randomicamente la macchina con cui fare il transfert.
		// accetto anche la stessa macchina su cui sto prelevando
		machineNumber = (int) (Math.random() * allMachines.size());

		ArrayList<Job> destMachine = allMachines.get(machineNumber);

		Job toBeTransferedJob = sourceMachine.remove(position);

		int leftLimit = calculateLeftLimit(toBeTransferedJob, destMachine, position, range);
		int rightLimit = calculateRightLimit(toBeTransferedJob, destMachine, position, range);

		// Calcolo la posizione con cui fare il transfert.
		int distance = rightLimit - leftLimit;
		int posInRange = (int) (Math.random() * distance) + 1;
		int newPos = leftLimit + posInRange;

		destMachine.add(newPos, toBeTransferedJob);

		long newTwt = calculateTwt();

		if (newTwt < twt) {
			twt = newTwt;
			jobMap.remove(toBeTransferedJob);
			jobMap.put(toBeTransferedJob, machineNumber);
			return true;
		} else {
			sourceMachine.add(position, destMachine.remove(newPos));
			return false;
		}
	}

	private int calculateLeftLimitFast(ArrayList<Job> destMachine,
			int position, int range) {
		int positionOnDestMachine = Math.min(position, destMachine.size() - 1);
		if ((positionOnDestMachine - range) > -1) {
			return positionOnDestMachine - range;
		} else {
			return -1;
		}
	}

	private int calculateRightLimitFast(ArrayList<Job> destMachine,
			int position, int range) {
		if ((position + range) < destMachine.size()) {
			return position + range - 1;
		} else {
			return destMachine.size() - 1;
		}
	}
	
	private int calculateLeftLimit(Job j, ArrayList<Job> destMachine,
			int position, int range) {
		int indexOfDestMachine = allMachines.indexOf(destMachine);
		ArrayList<Job> predecessors = j.getAllIndirectPredecessors();
		int maxPredecessorIndex = Integer.MIN_VALUE;
		for (Job predecessor : predecessors) {
			int machineIndex = jobMap.get(predecessor);
			if (machineIndex == indexOfDestMachine) {
				maxPredecessorIndex = Math.max(maxPredecessorIndex,
						destMachine.indexOf(predecessor));
			}
		}
		int positionOnDestMachine = Math.min(position, destMachine.size() - 1);
		if ((positionOnDestMachine - range) > -1) {
			return Math.max(maxPredecessorIndex, positionOnDestMachine - range);
		} else {
			return Math.max(maxPredecessorIndex, -1);
		}
	}

	private int calculateRightLimit(Job j, ArrayList<Job> destMachine,
			int position, int range) {
		int indexOfDestMachine = allMachines.indexOf(destMachine);
		ArrayList<Job> successors = j.getAllIndirectSuccessors();
		int minSuccessorIndex = Integer.MAX_VALUE;
		for (Job successor : successors) {
			int machineIndex = jobMap.get(successor);
			if (machineIndex == indexOfDestMachine) {
				minSuccessorIndex = Math.min(minSuccessorIndex,
						destMachine.indexOf(successor) + 1);
			}
		}
		if ((position + range) < destMachine.size()) {
			return Math.min(minSuccessorIndex, position + range) - 1;
		} else {
			return Math.min(minSuccessorIndex, destMachine.size()) - 1;
		}
	}

	// Funzioni per la realizzazione delle mosse

	public int calculateTwt() {
		int twt = 0;

		scheduledJobs.clear();
		int currentMachineIndex = 0;
		Job currentJob = null;
		Job lastScheduledJob[] = new Job[nMachines];
		long currentTime = 0;
		int deadlockDetector = 0;
		long s, r, p;

		for (int i = 0; i < currentJobIndex.length; i++) {
			currentJobIndex[i] = 0;
			lastScheduledJob[i] = null;
		}

		boolean notFinishedScheduling = true;
		
		

NEXTMACHINE:
	while (notFinishedScheduling) {
		currentJob = getCurrentJobOnMachine(currentJobIndex, currentMachineIndex);
		if (currentJob.getJobID() == 245) {
			int i = 4;
		}
			if (lastScheduledJob[currentMachineIndex] == null) {
				currentTime = 0;
			} else {
				int currentJobSetupTime = currentJob
						.getSetupTimes(lastScheduledJob[currentMachineIndex].getJobID());
				long timeLastJobFinished = scheduledJobs
						.containsKey(lastScheduledJob[currentMachineIndex]) ? scheduledJobs
						.get(lastScheduledJob[currentMachineIndex]) : 0;
				s = timeLastJobFinished + currentJobSetupTime;
				r = currentJob.getReleaseTime();
				p = 0;
				// controlla che non abbia predecessori
				if (currentJob.hasImmediatePredecessors()) {
					ArrayList<Job> predecessors = currentJob.getImmediatelyPreviousJobs();
					for (Job currentPredecessor : predecessors) {
						if (scheduledJobs.containsKey(currentPredecessor)) {
							p = Math.max(p, currentPredecessor.getEndingTime());
						} else {
							Main.log("il job "+ currentJob +", nella macchina "+ jobMap.get(currentJob) +", ha un predecessore (il "+ currentPredecessor +") non schedulato nella macchina "+ jobMap.get(currentPredecessor) +", attualmente il job corrente è alla posizione "+ allMachines.get(currentMachineIndex).indexOf(currentJob) +" mentre il job non schedulato è nella posizione "+ allMachines.get(jobMap.get(currentPredecessor)).indexOf(currentPredecessor) +".");
							currentMachineIndex = jobMap.get(currentPredecessor);
							deadlockDetector++;
							if (deadlockDetector == allMachines.size()) {
								return Integer.MAX_VALUE;
							}
							continue NEXTMACHINE;
						}
					}
				}

				currentTime = Math.max(Math.max(p, s), r);
			}
			currentTime += currentJob.getExecutionTime();
			// aggiorna il tempo di fine dentro il job
			scheduledJobs.put(currentJob, currentTime);
			lastScheduledJob[currentMachineIndex] = currentJob;
			twt += Math.max(
					scheduledJobs.get(currentJob) - currentJob.getDueDate(), 0)
					* currentJob.getWeight();

			if (!hasNextOnMachine(currentJobIndex, currentMachineIndex)) {
				notFinishedScheduling = false;
				for (int i = 0; i < allMachines.size(); i++) {
					if (hasNextOnMachine(currentJobIndex, i)) {
						// passa al prossimo job su un���altra macchina
						currentMachineIndex = i;
						currentJob = getNextJobOnMachine(currentJobIndex, currentMachineIndex);
						notFinishedScheduling = true;
						break;
					}
				}
			} else {
				// passa al prossimo job sulla stessa macchina
				currentJob = getNextJobOnMachine(currentJobIndex, currentMachineIndex);
			}
			// qui almeno una mossa �� stata effettuata senza cambiare macchina
			deadlockDetector = 0;
		}
		return twt;
	}

	private boolean hasNextOnMachine(int[] currentJobIndex2,
			int currentMachineIndex) {
		return (currentJobIndex2[currentMachineIndex] < allMachines.get(currentMachineIndex).size()-1);
	}

	private Job getCurrentJobOnMachine(int[] currentJobIndex2, int currentMachineIndex) {
		return allMachines.get(currentMachineIndex).get(currentJobIndex2[currentMachineIndex]);
	}
	
	private Job getNextJobOnMachine(int[] currentJobIndex2, int currentMachineIndex) {
		return allMachines.get(currentMachineIndex).get(currentJobIndex2[currentMachineIndex]++);
	}

	public void setInitialTwt() {
		twt = calculateTwt();
	}

	/**
	 * @param k
	 *            il numero del neighborhood da esplorare
	 * @return uno <b>StorageVNS</b> con la nuova soluzione
	 */
	public boolean muoviCasualmenteNelNeighborhood(int k) {

		// parametro r ����� il range della mossa
		int range;
		// parametro l ����� il numero di ripetizioni della mossa
		int repeat;
		// parametro che indica il codice della mossa
		int moveCode;
		Method move = null;

		if (nMachines > 1)
			moveCode = k % 4; // ci sono solo 4 mosse numerate da 0 a 3
		else
			moveCode = k % 2;

		range = ((int) (Math.floor(k / 4))) % 4; // ci sono solo 4 possibilita
		// [2,5,10,n_max]
		repeat = (((int) (Math.floor(k / 16))) % 3) + 1; // ci sono solo 3
															// possibilit������
		// [1,2,3]

		if (range == 0)
			range = 2;
		if (range == 1)
			range = 5;
		if (range == 2)
			range = 10;
		if (range == 3)
			range = 0; // nella funzione vale come nmax

		try {
			if (moveCode == 0)
				move = this.getClass().getMethod("transferOnOneMachine",
						int.class, int.class);
			else if (moveCode == 1)
				move = this.getClass().getMethod("swapOnOneMachine", int.class,
						int.class);
			else if (moveCode == 2 && nMachines != 1)
				move = this.getClass().getMethod("transferAcrossMachines",
						int.class, int.class);
			else if (moveCode == 3 && nMachines != 1)
				move = this.getClass().getMethod("swapAcrossMachines",
						int.class, int.class);
			else
				throw new NoSuchMethodException();
		} catch (NoSuchMethodException | SecurityException e) {
			Main.log("la riflessività non ha funzionato");
			e.printStackTrace();
			System.exit(1);
		}

		boolean mossaMigliorativa = false;
		try {
			mossaMigliorativa = (boolean) move.invoke(this, range, repeat);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			Main.log("la riflessività non ha funzionato nemmeno qui");
			e.printStackTrace();
			System.exit(1);
		}

		return mossaMigliorativa;
	}

	/**
	 * stampa delle robe se ci sono lavori uguali o non sono 300
	 */
	public void check_consistency() {
		int count = 0;
		for (ArrayList<Job> machine : allMachines) {
			for (int i = 0; i < machine.size(); i++) {
				count++;
				for (ArrayList<Job> machine2 : allMachines) {
					for (int j = 0; j < machine2.size(); j++) {
						if (machine == machine2) {
							if (i != j) {
								if (machine.get(i) == machine.get(j))
									Main.log("2 job uguali sulla stessa macchina");
							}
						} else {
							if (machine.get(i) == machine2.get(j))
								Main.log("2 job uguali su due macchine diverse");
						}
					}
				}
			}
		}
		if (count != 300)
			Main.log("i job non sono 300, sono " + count);
	}

	public String toString() {
		return allMachines.toString() + "\nCosto soluzione finale = "
				+ this.calculateTwt();
	}
}
