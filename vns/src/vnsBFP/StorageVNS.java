package vnsBFP;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

public class StorageVNS {
	// ArrayList che memorizza tutti i job divisi per macchine.
	private ArrayList<ArrayList<Job>> allMachines = null;

	// Matrice dei setup.
	private int[][] setupMatrix = null;

	// HashMap che memorizza in quale macchina sono schedulati i job.
	private HashMap<Job, Integer> jobMap = null;

	// TWT attuale.
	private long twt;

	private int nMachines = 0;

	// strutture per calculatetwt
	HashMap<Job, Long> scheduledJobs;
	ListIterator<Job>[] jobIterator;

	@SuppressWarnings("unchecked")
	public StorageVNS(int nMachines) {
		allMachines = new ArrayList<ArrayList<Job>>();
		jobMap = new HashMap<Job, Integer>();

		for (int x = 0; x < nMachines; x++) {
			allMachines.add(new ArrayList<Job>());
		}
		this.nMachines = nMachines;
		jobIterator = (ListIterator<Job>[]) new ListIterator[allMachines.size()];
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

	public void setSetupMatrix(int[][] matrixSetup) {
		setupMatrix = matrixSetup;
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
				// range = 0 ����� come dire range = nmax
				if (range == 0)
					range = this.getNumberOfJobsOnMachine(i);
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
				// range = 0 è come dire range = nmax
				if (range == 0)
					range = this.getNumberOfJobsOnMachine(i);
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
		int numberOfMachines = allMachines.size();
		machineNumber = (int) (Math.random() * numberOfMachines);

		// TODO: spostarlo nella chiamata principale
		// range = 0 è come dire range = nmax
		if (range == 0)
			range = this.getNumberOfJobsOnMachine(machineNumber);

		ArrayList<Job> destMachine = allMachines.get(machineNumber);

		Job toBeTransferedJob = sourceMachine.remove(position);

		int leftLimit = calculateLeftLimitFast(destMachine, position, range);
		int rightLimit = calculateRightLimitFast(destMachine, position, range);

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

	// Funzioni per la realizzazione delle mosse

	public int calculateTwt() {
		int twt = 0;

		scheduledJobs.clear();
		int currentMachineIndex = 0;
		Job currentJob = null;
		Job previousJob = null;
		long currentTime = 0;
		int indexOfPreviousJob = -1;
		int deadlockDetector = 0;
		long s, r, p;

		for (int i = 0; i < jobIterator.length; i++) {
			jobIterator[i] = allMachines.get(i).listIterator();
		}

		currentJob = jobIterator[0].next();
		boolean notFinishedScheduling = true;

		while (notFinishedScheduling) {
			// per convenzione a questo punto i currentJob sono schedulati
			// imposta lo startingTime al tempo di termine dell���ultimo job
			// schedulato
			WHILE: if (!jobIterator[currentMachineIndex].hasPrevious()) {
				currentTime = 0;
			} else {
				int currentJobSetupTime = currentJob
						.getSetupTimes(indexOfPreviousJob);
				long timeLastJobFinished = scheduledJobs
						.containsKey(previousJob) ? scheduledJobs
						.get(previousJob) : 0;
				s = timeLastJobFinished + currentJobSetupTime;
				r = currentJob.getReleaseTime();
				p = 0;
				// controlla che non abbia predecessori
				if (currentJob.hasPredecessor()) {
					ArrayList<Job> predecessors = currentJob.getPredecessors();
					for (Job predecessor : predecessors) {
						if (scheduledJobs.containsKey(predecessor)) {
							p = Math.max(p, predecessor.getEndingTime());
						} else {
							currentMachineIndex = jobMap.get(predecessor);
							currentJob = jobIterator[currentMachineIndex]
									.next();
							previousJob = jobIterator[currentMachineIndex]
									.previous();
							indexOfPreviousJob = allMachines.get(
									currentMachineIndex).indexOf(previousJob);
							deadlockDetector++;
							if (deadlockDetector == allMachines.size()) {
								return Integer.MAX_VALUE;
							}
							break WHILE;
						}
					}
				}

				currentTime = Math.max(Math.max(p, s), r);
			}
			currentTime += currentJob.getExecutionTime();
			// aggiorna il tempo di fine dentro il job
			scheduledJobs.put(currentJob, currentTime);
			twt += Math.max(
					scheduledJobs.get(currentJob) - currentJob.getDueDate(), 0)
					* currentJob.getWeight();

			if (!jobIterator[currentMachineIndex].hasNext()) {
				notFinishedScheduling = false;
				for (int i = 0; i < allMachines.size(); i++) {
					if (jobIterator[i].hasNext()) {
						// passa al prossimo job su un���altra macchina
						currentMachineIndex = i;
						currentJob = jobIterator[currentMachineIndex].next();
						previousJob = jobIterator[currentMachineIndex]
								.previous();
						indexOfPreviousJob = allMachines.get(
								currentMachineIndex).indexOf(previousJob);
						notFinishedScheduling = true;
						break;
					}
				}
			} else {
				// passa al prossimo job sulla stessa macchina
				previousJob = currentJob;
				currentJob = jobIterator[currentMachineIndex].next();
				indexOfPreviousJob++;
			}
			// qui almeno una mossa �� stata effettuata senza cambiare macchina
			deadlockDetector = 0;
		}
		return twt;
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
			// TODO c������������ qualcosa che non va se finisce qui
			Main.log("la riflessivit������ non ha funzionato");
			e.printStackTrace();
			System.exit(1);
		}

		boolean mossaMigliorativa = false;
		try {
			mossaMigliorativa = (boolean) move.invoke(this, range, repeat);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO c������������ qualcosa che non va se finisce qui
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
		return allMachines.toString() + "\nCosto soluzione finale = "
				+ this.calculateTwt();
	}
}
