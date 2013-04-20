package vnsBFP;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class StorageVNS {
	// ArrayList che memorizza tutti i job divisi per macchine.
	private ArrayList<ArrayList<Job>> allMachines = null;

	// Array delle posizioni correnti.
	private int[] currentPositionArray = null;

	// TWT attuale.
	private long twt;
	
	private int nMachines = 0;

	public StorageVNS(int nMachines) {
		currentPositionArray = new int[nMachines];
		allMachines = new ArrayList<ArrayList<Job>>();

		for (int x = 0; x < nMachines; x++) {
			allMachines.add(new ArrayList<Job>());
			currentPositionArray[x] = 0;
		}
		this.nMachines = nMachines;
	}

	protected ArrayList<ArrayList<Job>> getAllMachines() {
		return allMachines;
	}

	protected int[] getCurrentPositionArray() {
		return currentPositionArray;
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
	/**
	 * @param machineNumber
	 *            numero della macchina
	 * @param j
	 *            Job da aggiungere
	 */
	public void addJobOnMachine(int machineNumber, Job j) {
		allMachines.get(machineNumber).add(j);
	}

	public void removeJobFromMachine(int position, int machineNumber) {
		allMachines.get(machineNumber).remove(position);
	}

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
	 *            quante volte verrà ripetuta questa mossa alla fine sto metodo
	 *            fa un po’ le cose a casaccio, ma in fondo è un algoritmo
	 *            random no?
	 */
	public boolean swapOnOneMachine(int range, int repeat) {
		boolean miglioramentoAvvenuto = false;
		for (int r = 0; r < repeat; r++) {
			for (int i = 0; i < this.getNumberOfMachines(); i++) {
				// range = 0 è come dire range = nmax
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
		Job consideredJob = machine.get(position);
		int leftLimit = 0;
		int rightLimit = machine.size();

		// Imposto il limite sinistro del range da considerare
		if ((position - range) > 0) {
			leftLimit = position - range;
		}

		// Imposto il limite destro del range da considerare
		if ((position + range) < rightLimit) {
			rightLimit = position + range;
		}

		int distance = rightLimit - leftLimit;
		int posInRange = (int) (Math.random() * distance);
		int newPos = leftLimit + posInRange;

		Job substitutedJob = machine.get(newPos);

		machine.set(position, substitutedJob);
		machine.set(newPos, consideredJob);

		long newTwt = calculateTwt();

		if (newTwt < twt) // manca condizione di validit� in base alla priorit�.
		{
			twt = newTwt;
			return true;
		} else {
			machine.set(position, consideredJob);
			machine.set(newPos, substitutedJob);
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
		// system.out.println("Twt iniziale: "+ twt);

		// aggiungere le ripetizioni e il calcolo del twt
		int leftLimit = 0;
		int rightLimit = machine.size() - 1;

		// Imposto il limite sinistro del range da considerare e copio la
		// sottolista se alcune posizioni
		// rimarranno invariate rispetto alla mossa perch� il range � troppo
		// piccolo
		if ((position - range) > 0) {
			leftLimit = position - range;
		}

		// Imposto il limite destro del range da considerare
		if ((position + range) < rightLimit) {
			rightLimit = position + range;
		}

		// Calcolo la posizione in cui fare il transfert
		int distance = rightLimit - leftLimit;
		int posInRange = (int) (Math.random() * distance);
		int newPos = leftLimit + posInRange;

		Job toBeInsertedJob = machine.remove(position);
		machine.add(newPos, toBeInsertedJob);

		// e calcolo il twt alla nuova configurazione
		long newTwt = calculateTwt();

		// se ho un miglioramento aggiorno il twt e mantengo la configurazione,
		// altrimenti rimetto la precedente.
		if (newTwt < twt) {
			twt = newTwt;
			return true;
		} else {
			// rimetto il job dove stava prima
			Job toBePutBackJob = machine.remove(newPos);
			machine.add(position, toBePutBackJob);
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
		Job consideredJob = sourceMachine.get(position);

		// Calcolo randomicamente la macchina con cui fare il transfert
		int numberOfMachines = allMachines.size() - 1;
		int machineNumber = (int) (Math.random() * numberOfMachines);

		// range = 0 è come dire range = nmax
		if (range == 0)
			range = this.getNumberOfJobsOnMachine(machineNumber);

		ArrayList<Job> destMachine = allMachines.get(machineNumber);
		// TODO controllare che invece che istanziare un nuovo array non sia
		// più veloce creare una contromossa

		int leftLimit = 0;
		int rightLimit = destMachine.size() - 1;

		// Imposto il limite sinistro del range da considerare
		if ((position - range) > 0) {
			leftLimit = position - range;
		}

		// Imposto il limite destro del range da considerare
		if ((position + range) < rightLimit) {
			rightLimit = position + range;
		}

		// Calcolo la osizione con cui fare lo swap all'interno del range
		int rangeSize = rightLimit - leftLimit;
		int posInRange = (int) (Math.random() * rangeSize);
		int swapPos = leftLimit + posInRange;

		// Provo a fare lo swap
		if (swapPos >= destMachine.size()) {
			// system.out.println("Swap non effettuato perch� la macchina destinazione non ha job nella posizione selezionata");
			return false;
		} else {
			// Effettuo lo scambio
			Job substitutedJob = destMachine.get(swapPos);
			destMachine.set(swapPos, consideredJob);
			sourceMachine.set(position, substitutedJob);
			// Calcolo il twt
			long newTwt = calculateTwt();

			if (newTwt < twt) {
				twt = newTwt;
				return true;
			} else {
				destMachine.set(swapPos, substitutedJob);
				sourceMachine.set(position, consideredJob);
				return false;
			}
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
		int index = allMachines.indexOf(sourceMachine);
		int machineNumber = index;

		// Calcolo randomicamente la macchina con cui fare il transfert. non
		// accetto la stessa macchina su cui sto prelevando
		while (machineNumber == index) {
			int numberOfMachines = allMachines.size();
			machineNumber = (int) (Math.random() * numberOfMachines);
			// TODO esistono funzioni molto più veloci
		}

		// range = 0 è come dire range = nmax
		if (range == 0)
			range = this.getNumberOfJobsOnMachine(machineNumber);

		// system.out.println("La macchina con cui far� il transfer �: "+machineNumber);
		ArrayList<Job> destMachine = allMachines.get(machineNumber);

		// Calcolo i limiti determinati dal range
		int rightLimit = position + range;
		int leftLimit = 0;

		if ((position - range) > 0) {
			leftLimit = position - range;
		}

		if ((position + range) > (destMachine.size() - 1)) {
			rightLimit = destMachine.size();
		}

		// Calcolo la posizione con cui fare il transfert.
		int distance = rightLimit - leftLimit;
		int posInRange = (int) (Math.random() * distance);
		int newPos = leftLimit + posInRange;

		if (newPos > destMachine.size() - 1)
			newPos = destMachine.size();

		// trasferisco il job
		destMachine.add(newPos, sourceMachine.remove(position));

		// Calcolo il twt
		long newTwt = calculateTwt();

		// Se il twt migliora lascio tutto com’è, altrimenti rimetto come prima
		if (newTwt < twt) {
			twt = newTwt;
			return true;
		} else {
			sourceMachine.add(position, destMachine.remove(newPos));
			return false;
		}
	}

	public long calculateTwt() {
		// TODO questa funzione calcola il twt anche se è già stato calcolato
		// è possibile che il twt non cambi tra due chiamate o cambia per forza?

		long resultingTwt = 0;
		for (int a = 0; a < allMachines.size(); a++) {
			ArrayList<Job> machine = allMachines.get(a);
			// //system.out.println("Calcolo il twt su: "+machine.size()+" job");
			Job j = null;
			long time = 0;
			for (int b = 0; b < machine.size(); b++) {
				// Calcolo il tempo in cui termina il job
				j = machine.get(b);
				time = time + j.getExecutionTime();
				if (time > j.getDueDate()) {
					long paid = (time - j.getDueDate()) * j.getWeight();
					resultingTwt += paid;
				}
			}
		}
		// Main.log("TWT = " + resultingTwt);
		return resultingTwt;
	}

	public void printResult() {
		// system.out.println("");
		// system.out.println("ORDINE DELLE MACCHINE:");
		for (int x = 0; x < allMachines.size(); x++) {
			ArrayList<Job> alj = allMachines.get(x);
			// system.out.println("Macchina #"+x);
			String s = "";
			for (int y = 0; y < alj.size(); y++) {
				s = s + alj.get(y).getName() + ", ";
			}
			// system.out.println(s);
		}
		// system.out.println("FINE");
		// system.out.println("");
	}

	/**
	 * @param jobArray
	 *            ArrayList dei job in ordine sparso
	 */
	public void inizializzaCoiJob(ArrayList<Job> jobArray) {
		// TODO just put all the jobs on a machine for now
		for (Job job : jobArray) {
			addJobOnMachine(0, job);
		}
		twt = calculateTwt();
	}

	/**
	 * @param k
	 *            il numero del neighborhood da esplorare
	 * @return uno <b>StorageVNS</b> con la nuova soluzione
	 */
	public boolean muoviCasualmenteNelNeighborhood(int k) {
		// TODO crea una deep-copy e la muove, meglio sarebbe avere sempre
		// due soluzioni e avere delle mosse che si possono annullare
		// in modo da non dover sempre istanziare nuove classi (quindi
		// ad esempio fare una mossa sulla soluzione 2 e se non porta
		// a niente di buono annullare solo la mossa e non buttare via
		// tutta la classe

		// parametro r è il range della mossa
		int range;
		// parametro l è il numero di ripetizioni della mossa
		int repeat;
		// parametro che indica il codice della mossa
		int moveCode;
		Method move = null;

		if (nMachines > 1) moveCode = k % 4; // ci sono solo 4 mosse numerate da 0 a 3
		else moveCode = k % 2;
		
		range = ((int) (Math.floor(k / 4))) % 4; // ci sono solo 4 possibilita
													// [2,5,10,n_max]
		repeat = (((int) (Math.floor(k / 16))) % 3) + 1; // ci sono solo 3 possibilità
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
			else
			if (moveCode == 1)
				move = this.getClass().getMethod("swapOnOneMachine", int.class,
						int.class);
			else
			if (moveCode == 2 && nMachines != 1)
				move = this.getClass().getMethod("transferAcrossMachines",
						int.class, int.class);
			else
			if (moveCode == 3 && nMachines != 1)
				move = this.getClass().getMethod("swapAcrossMachines",
						int.class, int.class);
			else throw new NoSuchMethodException();
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO c’è qualcosa che non va se finisce qui
			Main.log("la riflessività non ha funzionato");
			e.printStackTrace();
			System.exit(1);
		}

		boolean mossaMigliorativa = false;
		try {
			mossaMigliorativa = (boolean) move.invoke(this, range, repeat);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO c’è qualcosa che non va se finisce qui
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
