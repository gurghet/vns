package vnsBFP;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class StorageVNS {
	// ArrayList che memorizza tutti i job divisi per macchine.
	private ArrayList<ArrayList<Job>> allMachines = null;

	// HashMap che memorizza in quale macchina sono schedulati i job.
	private HashMap<Job, Integer> jobMap = null;

	// TWT attuale.
	private int twt;
	
	// 0ATC / 1ATCS / 2ATCSR
	int type = -1;
	public static final int ATC = 0;
	public static final int ATCS = 1;
	public static final int ATCSR = 2;

	private int nMachines = 0;

	// strutture per calculatetwt
	Boolean[] scheduledJobs;
	int[] currentJobIndex;
	
	// statistiche profiling
	long durationCalculateTwt = 0;
	int counterCalculateTwt = 0;
	long totalWhile = 0;
	long totalSelf0 = 0;
	long totalSelf2 = 0;
	long totalPutting = 0;

	public StorageVNS(int nMachines, String type) {
		allMachines = new ArrayList<ArrayList<Job>>();
		jobMap = new HashMap<Job, Integer>();

		for (int x = 0; x < nMachines; x++) {
			allMachines.add(new ArrayList<Job>());
		}
		this.nMachines = nMachines;
		currentJobIndex = new int[nMachines];
		scheduledJobs = new boolean[300]; // TODO: togliere l'hard coding
		
		switch (type) {
		case "ATC":
			this.type = ATC;
			break;
		case "ATCS":
			this.type = ATCS;
			break;
		case "ATCSR":
			this.type = ATCSR;
			break;
		default:
			break;
		
		for (int i = 0; i < nMachines; i++) {
			(new Thread(new _Runner(i), "Macchina "+i)).start();
		}
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
	 *            quante volte verr������������������������������������������������������ ripetuta questa mossa alla fine sto
	 *            metodo fa un po��������������������������������������������������������������� le cose a casaccio, ma in fondo ���������������������������������������������
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

		int leftLimitCurrent = calculateLeftLimitFast(machine, position, range);
		int rightLimitCurrent = calculateRightLimitFast(machine, position,
				range);

		// Calcolo la posizione con cui fare lo swap
		int distance = rightLimitCurrent - leftLimitCurrent;
		int posInRange = (int) (Math.random() * distance) + 1;
		int newPos = leftLimitCurrent + posInRange;

		// Effettuo lo swap
		Job substitutedJob = machine.get(newPos);

		machine.set(position, substitutedJob);
		machine.set(newPos, consideredJob);

		int newTwt = 0;
		if (type == ATCSR) newTwt = calculateTwt();
		else if (type == ATC) newTwt = calculateTwtSimple();

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

		int leftLimit = calculateLeftLimitFast(machine, position, range);
		int rightLimit = calculateRightLimitFast(machine, position, range);

		// Calcolo la posizione in cui fare il transfert
		int distance = rightLimit - leftLimit;
		int posInRange = (int) (Math.random() * distance) + 1;
		int newPos = leftLimit + posInRange;

		machine.add(newPos, toBeInsertedJob);

		int newTwt = 0;
		if (type == ATCSR) newTwt = calculateTwt();
		else if (type == ATC) newTwt = calculateTwtSimple();

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
		int numberOfMachines = allMachines.size();
		int destMachineNumber = (int) (Math.random() * numberOfMachines); // potrebbe essere s������ stessa

		// range = 0 ��������������������������������������������� come dire range = nmax
		if (range == 0)
			range = this.getNumberOfJobsOnMachine(destMachineNumber);

		ArrayList<Job> destMachine = allMachines.get(destMachineNumber);

		int leftLimit = calculateLeftLimitFast(destMachine, position, range);
		int rightLimit = calculateRightLimitFast(destMachine, position, range);

		// Calcolo la osizione con cui fare lo swap all'interno del range
		int rangeSize = rightLimit - leftLimit;
		int posInRange = (int) (Math.random() * rangeSize) + 1;
		int swapPos = leftLimit + posInRange;

		Job substitutedJob = destMachine.get(swapPos);

		// Effettuo lo scambio
		destMachine.set(swapPos, consideredJob);
		sourceMachine.set(position, substitutedJob);

		int newTwt = 0;
		if (type == ATCSR) newTwt = calculateTwt();
		else if (type == ATC) newTwt = calculateTwtSimple();

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

		int leftLimit = calculateLeftLimitFast(destMachine, position, range);
		int rightLimit = calculateRightLimitFast(destMachine, position, range);

		// Calcolo la posizione con cui fare il transfert.
		int distance = rightLimit - leftLimit;
		int posInRange = (int) (Math.random() * distance) + 1;
		int newPos = leftLimit + posInRange;

		destMachine.add(newPos, toBeTransferedJob);

		int newTwt = 0;
		if (type == ATCSR) newTwt = calculateTwt();
		else if (type == ATC) newTwt = calculateTwtSimple();

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

	// Funzioni per la realizzazione delle mosse
	

	public int calculateTwtSimple() 
	{
		long resultingTwt = 0;
		Job j = null;
		for(int a = 0; a < allMachines.size(); a++)
		{
			ArrayList<Job> machine = allMachines.get(a);
			
			long time = 0;
			for(int b = 0; b < machine.size(); b++)
			{
				// Calcolo il tempo in cui termina il job
				j = machine.get(b);
				time = time + j.getExecutionTime();
				if(time > j.getDueDate())
				{
					long paid = (time - j.getDueDate())*j.getWeight();
					resultingTwt += paid;
				}
			}
		}
		return (int) resultingTwt;
	}

	
	public int calculateTwt() {
		//long startCalculateTwt = System.nanoTime();
		int twt = 0;

		scheduledJobs = new boolean[300]; // TODO: togliere l���hard coding
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
		//long startWhile = System.nanoTime();
		currentJob = getCurrentJobOnMachine(currentJobIndex, currentMachineIndex);
			if (lastScheduledJob[currentMachineIndex] == null) {
				currentTime = 0;
			} else {
				//long startSelf0 = System.nanoTime();
				int currentJobSetupTime = currentJob
						.getSetupTimes(lastScheduledJob[currentMachineIndex].getJobID());
				long timeLastJobFinished = lastScheduledJob[currentMachineIndex].getEndingTime();
				s = timeLastJobFinished + currentJobSetupTime;
				r = currentJob.getReleaseTime();
				p = 0;
				//totalSelf0 += System.nanoTime() - startSelf0;
				// controlla che non abbia predecessori
				if (currentJob.hasImmediatePredecessors()) {
					ArrayList<Job> predecessors = currentJob.getImmediatelyPreviousJobs();
					for (Job currentPredecessor : predecessors) {
						if (scheduledJobs[currentPredecessor.getJobID()]) {
							p = Math.max(p, currentPredecessor.getEndingTime());
						} else {
							//Main.log("il job "+ currentJob +", nella macchina "+ jobMap.get(currentJob) +", ha un predecessore (il "+ currentPredecessor +") non schedulato nella macchina "+ jobMap.get(currentPredecessor) +", attualmente il job corrente ������ alla posizione "+ allMachines.get(currentMachineIndex).indexOf(currentJob) +" mentre il job non schedulato ������ nella posizione "+ allMachines.get(jobMap.get(currentPredecessor)).indexOf(currentPredecessor) +".");
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
			//long startSelf2 = System.nanoTime();
			currentTime += currentJob.getExecutionTime();
			scheduledJobs[currentJob.getJobID()] = true;
			currentJob.setEndingTime(currentTime);
			lastScheduledJob[currentMachineIndex] = currentJob;
			twt += Math.max(
					currentTime - currentJob.getDueDate(), 0)
					* currentJob.getWeight();

			if (!hasNextOnMachine(currentJobIndex, currentMachineIndex)) {
				notFinishedScheduling = false;
				for (int i = 0; i < allMachines.size(); i++) {
					if (hasNextOnMachine(currentJobIndex, i)) {
						// passa al prossimo job su un���������������������������altra macchina
						currentMachineIndex = i;
						currentJob = getNextJobOnMachine(currentJobIndex, currentMachineIndex);
						notFinishedScheduling = true;
						break;
					}
				}
			} else {
				// passa al prossimo job sulla stessa macchina
				//long startPutting = System.nanoTime();
				currentJob = getNextJobOnMachine(currentJobIndex, currentMachineIndex);
				//totalPutting += System.nanoTime() - startPutting;
			}
			// qui almeno una mossa ������������������ stata effettuata senza cambiare macchina
			deadlockDetector = 0;
			//totalWhile += System.nanoTime() - startWhile;
			//totalSelf2 += System.nanoTime() - startSelf2;
		}
		//durationCalculateTwt = (durationCalculateTwt * counterCalculateTwt + System.nanoTime() - startCalculateTwt)/(++counterCalculateTwt); 
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
		if (type == ATCSR) twt = calculateTwt();
		else if (type == ATC) twt = calculateTwtSimple();
	}

	/**
	 * @param k
	 *            il numero del neighborhood da esplorare
	 * @return uno <b>StorageVNS</b> con la nuova soluzione
	 */
	public boolean muoviCasualmenteNelNeighborhood(int k) {

		// parametro r ��������������������������������������������� il range della mossa
		int range;
		// parametro l ��������������������������������������������� il numero di ripetizioni della mossa
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
															// possibilit������������������������������������������������������
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
			Main.log("la riflessivit������ non ha funzionato");
			e.printStackTrace();
			System.exit(1);
		}

		boolean mossaMigliorativa = false;
		try {
			mossaMigliorativa = (boolean) move.invoke(this, range, repeat);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			Main.log("la riflessivit������ non ha funzionato nemmeno qui");
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
		return allMachines.toString()// + "\nDurata media twt = "
				//+ durationCalculateTwt + "\nDurata totale while = "
				//+ totalWhile + "\nDurata totale self0 = "
				//+ totalSelf0 + "\nDurata totale self2 = "
				//+ totalSelf2 + "\nDurata totale put   = "
				//+ totalPutting
				;
	}
}
