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
    
	// HashMap che memorizza dove sono schedulati i job.
	private HashMap<String, Integer> jobMap = null;
	
	// TWT attuale.
	private long twt;
	
	private int nMachines = 0;
    
	public StorageVNS(int nMachines)
	{
		allMachines = new ArrayList<ArrayList<Job>>();
		jobMap = new HashMap<String,Integer>();
        
		for (int x = 0; x < nMachines; x++)
		{
			allMachines.add(new ArrayList<Job>());
		}
		this.nMachines = nMachines;
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
    
	public void setSetupMatrix(int[][] matrixSetup)
	{
		setupMatrix = matrixSetup;
	}
    
	
	// Metodi di inserimento e cancellazione
	
	public void addJobOnMachine(int machineNumber, Job j)
	{
		allMachines.get(machineNumber).add(j);
		jobMap.put(j.getName(), machineNumber);
	}
    
	public void removeJobFromMachine(int position, int machineNumber)
	{
		Job j = allMachines.get(machineNumber).remove(position);
		jobMap.remove(j.getName());
	}
    
	// Non usato
	public void removeAllJobsFromMachine(int machineNumber)
	{
		for (int x = 0; x < allMachines.get(machineNumber).size(); x++) {
			allMachines.get(machineNumber).remove(x);
		}
	}
    
	// Mosse
    
	/**
	 * @param range
	 *            raggio in cui verranno effettuati gli swap
	 * @param repeat
	 *            quante volte verr√† ripetuta questa mossa alla fine sto metodo
	 *            fa un po‚Äô le cose a casaccio, ma in fondo √® un algoritmo
	 *            random no?
	 */
	public boolean swapOnOneMachine(int range, int repeat) {
		boolean miglioramentoAvvenuto = false;
		for (int r = 0; r < repeat; r++) {
			for (int i = 0; i < this.getNumberOfMachines(); i++) {
				// range = 0 √® come dire range = nmax
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
    
	public boolean _swapOnOneMachine_onMachine_onPosition(int range, ArrayList<Job> machine, int position)
	{
		System.out.println("eseguo swaponone");
		Job consideredJob = machine.get(position);
        /*
         int leftLimit = calculateLeftLimit(position, range, consideredJob, machine);
         int rightLimit = calculateRightLimit(position, range, consideredJob, machine);
         */
		int leftLimit = 0;
		int rightLimit = machine.size();
        
		// Imposto il limite sinistro del range da considerare
		if ((position - range) > 0)
		{
			leftLimit = position - range;
		}
        
		// Imposto il limite destro del range da considerare
		if ((position + range) < rightLimit)
		{
			rightLimit = position + range;
		}
        
		
		// Calcolo la posizione con cui fare lo swap
		int distance = rightLimit - leftLimit;
		int posInRange = (int) (Math.random() * distance);
		int newPos = leftLimit + posInRange;
		
		long prevTwt = calculateTwt();
		
		// Effettuo lo swap
		Job substitutedJob = machine.get(newPos);
		machine.set(position, substitutedJob);
		machine.set(newPos, consideredJob);
		
		long newTwt = calculateTwt();
        
		//System.out.println(prevTwt+" - "+newTwt);
		
		if (newTwt < twt /*&& isMoveValid*/) // manca condizione di validit‡ in base alla priorit‡
		{
			twt = newTwt;
			System.out.println("Swap Effettuato");
			return true;
		}
		else
		{
			machine.set(position, consideredJob);
			machine.set(newPos, substitutedJob);
			System.out.println("Swap Non Effettuato");
			return false;
		}
	}
    
	public boolean transferOnOneMachine(int range, int repeat) {
		boolean miglioramentoAvvenuto = false;
		for (int r = 0; r < repeat; r++) {
			for (int i = 0; i < this.getNumberOfMachines(); i++) {
				// range = 0 √® come dire range = nmax
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
    
	public boolean _transferOnOneMachine_onMachine_onPosition(int range, ArrayList<Job> machine, int position)
	{
        int leftLimit = 0;
		int rightLimit = machine.size() - 1;
        
		if ((position - range) > 0)
		{
			// IMPOSTARE IL LIMITE AL PRIMO PREDECESSORE SU QUESTA MACCHINA
			// calculateLeftLimit
			leftLimit = position - range;
		}
        
		if ((position + range) < rightLimit)
		{
			// IMPOSTARE IL LIMITE AL PRIMO SUCCESSORE SU QUESTA MACCHINA
			// calculateRightLimit
			rightLimit = position + range;
		}
        
		
		// Calcolo la posizione in cui fare il transfert
		int distance = rightLimit - leftLimit;
		int posInRange = (int) (Math.random() * distance);
		int newPos = leftLimit + posInRange;
        
		// ELIMINARE
		//if(!verificaTransferOnOne(machine, position, newPos))
		//	return false;
		
		// ELIMINARE
		//long prevTwt = calculateTwt();
		
		Job toBeInsertedJob = machine.remove(position);
		machine.add(newPos, toBeInsertedJob);
		
		// ELIMINARE
		//aggiornaTempi(machine, null);

		long newTwt = calculateTwt();
		
		if (newTwt < twt)
		{
			twt = newTwt;
			Main.log("Transfer Effettuato");
			return true;
		}
		else
		{
			// rimetto il job dove stava prima
			Job toBePutBackJob = machine.remove(newPos);
			machine.add(position, toBePutBackJob);
			// eliminare
			//aggiornaTempi(machine,null);
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
    
	public boolean _swapAcrossMachines_onMachine_onPosition(int range, ArrayList<Job> sourceMachine, int position)
	{
		System.out.println("Eseguo swap across");
		Job consideredJob = sourceMachine.get(position);
		int index = allMachines.indexOf(sourceMachine);
		int numberOfMachines = allMachines.size();
		int machineNumber = (int) (Math.random() * numberOfMachines);
		
		// Calcolo randomicamente la macchina con cui fare il transfert. non accetto la stessa da cui prendo il job
		while(machineNumber == index)
		{
			machineNumber = (int) (Math.random() * numberOfMachines);
		}
        
		// range = 0 √® come dire range = nmax
		if (range == 0)
			range = this.getNumberOfJobsOnMachine(machineNumber);
        
		ArrayList<Job> destMachine = allMachines.get(machineNumber);
		
		
		/*
         int leftLimit = calculateLeftLimit(position, range, consideredJob, sourceMachine);
         int rightLimit = calculateRightLimit(position, range, consideredJob, sourceMachine);
         */
		
		
		int leftLimit = 0;
		int rightLimit = destMachine.size() - 1;
        
		// Imposto il limite sinistro del range da considerare
		if ((position - range) > 0)
		{
			leftLimit = position - range;
		}
        
		// Imposto il limite destro del range da considerare
		if ((position + range) < rightLimit)
		{
			rightLimit = position + range;
		}
		
		// Calcolo la osizione con cui fare lo swap all'interno del range
		int rangeSize = rightLimit - leftLimit;
		int posInRange = (int) (Math.random() * rangeSize);
		int swapPos = leftLimit + posInRange;
        
		// Provo a fare lo swap
		if (swapPos >= destMachine.size())
		{
			// system.out.println("Swap non effettuato perchË la macchina destinazione non ha job nella posizione selezionata");
			return false;
		}
		else
		{
			Job substitutedJob = destMachine.get(swapPos);
			
			// Effettuo lo scambio
			destMachine.set(swapPos, consideredJob);
			sourceMachine.set(position, substitutedJob);
            
			long newTwt = calculateTwt();
			
			if (newTwt < twt)
			{
				twt = newTwt;
				return true;
			}
			else
			{
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
    
	public boolean _transferAcrossMachines_onMachine_onPosition(int range, ArrayList<Job> sourceMachine, int position)
	{
		System.out.println("Eseguo transfer across");
		
		int index = allMachines.indexOf(sourceMachine);
		int machineNumber = index;
        
		// Calcolo randomicamente la macchina con cui fare il transfert. non accetto la stessa macchina su cui sto prelevando
		while (machineNumber == index)
		{
			int numberOfMachines = allMachines.size();
			machineNumber = (int) (Math.random() * numberOfMachines);
		}
        
		// range = 0 √® come dire range = nmax
		if (range == 0)
			range = this.getNumberOfJobsOnMachine(machineNumber);
        
		ArrayList<Job> destMachine = allMachines.get(machineNumber);
        /*
         int leftLimit = calculateLeftLimit(position, range, sourceMachine.get(position), sourceMachine);
         int rightLimit = calculateRightLimit(position, range, sourceMachine.get(position), sourceMachine);
         */
        
		// Calcolo i limiti determinati dal range
		int rightLimit = position + range;
		int leftLimit = 0;
        
		if ((position - range) > 0)
		{
			leftLimit = position - range;
		}
        
		if ((position + range) > (destMachine.size() - 1))
		{
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
        
		long newTwt = calculateTwt();
        
		//System.out.println(prevTwt+" - "+newTwt);
		
		// Se il twt migliora lascio tutto com‚Äô√®, altrimenti rimetto come prima
		if (newTwt < twt /*&& isMoveValid*/)
		{
			twt = newTwt;
			return true;
		}
		else
		{
			sourceMachine.add(position, destMachine.remove(newPos));
			return false;
		}
	}
	
	// Funzioni per la realizzazione delle mosse

    
	public int calculateTwt()
	{
		int Twt = 0;
		HashMap<Job, Long> scheduledJobs = new HashMap<Job, Long>();
		
		@SuppressWarnings("unchecked")
		ListIterator<Job>[] jobIterator = (ListIterator<Job>[]) new ListIterator[allMachines.size()];
		int currentMachineIndex = 0;
		Job currentJob = null;
		Job previousJob = null;
		long currentTime = 0;
		int deadlockDetector = 0;
		long s, r, p;
		
		for(int i = 0; i < jobIterator.length ; i++) {
			jobIterator[i] = allMachines.get(i).listIterator();
		}
		
		currentJob = jobIterator[0].next();
		boolean notFinishedScheduling = true;
		
		while(notFinishedScheduling) {
			// per convenzione a questo punto i currentJob sono schedulati
			// imposta lo startingTime al tempo di termine dell’ultimo job schedulato
WHILE:
			if (!jobIterator[currentMachineIndex].hasPrevious()) {
				currentTime = 0;
			} else {
				int indexOfPreviousJob = allMachines.get(currentMachineIndex).indexOf(previousJob);
				int indexOfCurentJob = allMachines.get(currentMachineIndex).indexOf(previousJob);
				int currentJobSetupTime = setupMatrix[indexOfPreviousJob][indexOfCurentJob];
				s = scheduledJobs.get(previousJob) + currentJobSetupTime;
				r = currentJob.getReleaseTime();
				p = 0;
				// controlla che non abbia predecessori
				if (currentJob.hasPredecessor()) {
					ArrayList<Job> predecessors = currentJob.getPredecessors();
					for (Job predecessor : predecessors) {
						if (scheduledJobs.containsKey(predecessor)) {
							p = Math.max(p, predecessor.getEndingTime());
						} else {
							currentMachineIndex = predecessor.getMachine();
							currentJob = jobIterator[currentMachineIndex].next();
							previousJob = jobIterator[currentMachineIndex].previous();
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
			
			if (!jobIterator[currentMachineIndex].hasNext()) {
				notFinishedScheduling = false;
				for (int i = 0; i < allMachines.size(); i++) {
					if (jobIterator[i].hasNext()) {
						currentMachineIndex = i;
						notFinishedScheduling = true;
						break;
					} 
				}
			}
			
			// passa al prossimo job sulla macchina
			deadlockDetector = 0;
			previousJob = currentJob;
			currentJob = jobIterator[currentMachineIndex].next();
		}
		
		return Twt;
	}
	
	public void printResult() {
		// system.out.println("");
		// system.out.println("ORDINE DELLE MACCHINE:");
		for (int x = 0; x < allMachines.size(); x++) {
			ArrayList<Job> alj = allMachines.get(x);
			System.out.println("Macchina #"+x);
			String s = "";
			for (int y = 0; y < alj.size(); y++) {
				s = s + alj.get(y).getName() + ", ";
			}
            System.out.println(s);
		}
		// system.out.println("FINE");
		// system.out.println("");
	}
    
	public void setInitialTwt()
	{
		twt = calculateTwt();
	}
	

	// Metodo temporaneo
	
	public boolean effettuaMossaACaso(int k)
	{
		Method move = null;
		int nMossa = (int) (Math.random() * 4);
		try{
			if(nMossa == 0)
			{
				move = this.getClass().getMethod("transferOnOneMachine", int.class, int.class);
			}
			else if(nMossa == 1)
			{
				move = this.getClass().getMethod("swapOnOneMachine", int.class, int.class);
			}
			else if(nMossa == 2)
			{
				move = this.getClass().getMethod("transferAcrossMachines", int.class, int.class);
			}
			else
			{
				move = this.getClass().getMethod("swapAcrossMachines", int.class, int.class);
			}
		}
		catch (NoSuchMethodException | SecurityException e) {
			// TODO c‚Äô√® qualcosa che non va se finisce qui
			Main.log("la riflessivit√† non ha funzionato");
			e.printStackTrace();
			System.exit(1);
		}
		
		int range = ((int) (Math.floor(k / 4))) % 4; // ci sono solo 4 possibilita
		// [2,5,10,n_max]
		int repeat = (((int) (Math.floor(k / 16))) % 3) + 1; // ci sono solo 3 possibilit√†
		// [1,2,3]
		
		boolean mossaMigliorativa = false;
		try {
			mossaMigliorativa = (boolean) move.invoke(this, range, repeat);
		} catch (IllegalAccessException | IllegalArgumentException
                 | InvocationTargetException e) {
			// TODO c‚Äô√® qualcosa che non va se finisce qui
			Main.log("la riflessivit√† non ha funzionato nemmeno qui");
			e.printStackTrace();
			System.exit(1);
		}
        
		return mossaMigliorativa;
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
        
		// parametro r √® il range della mossa
		int range;
		// parametro l √® il numero di ripetizioni della mossa
		int repeat;
		// parametro che indica il codice della mossa
		int moveCode;
		Method move = null;
        
		if (nMachines > 1) moveCode = k % 4; // ci sono solo 4 mosse numerate da 0 a 3
		else moveCode = k % 2;
		
		range = ((int) (Math.floor(k / 4))) % 4; // ci sono solo 4 possibilita
        // [2,5,10,n_max]
		repeat = (((int) (Math.floor(k / 16))) % 3) + 1; // ci sono solo 3 possibilit√†
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
			// TODO c‚Äô√® qualcosa che non va se finisce qui
			Main.log("la riflessivit√† non ha funzionato");
			e.printStackTrace();
			System.exit(1);
		}
        
		boolean mossaMigliorativa = false;
		try {
			mossaMigliorativa = (boolean) move.invoke(this, range, repeat);
		} catch (IllegalAccessException | IllegalArgumentException
                 | InvocationTargetException e) {
			// TODO c‚Äô√® qualcosa che non va se finisce qui
			Main.log("la riflessivit√† non ha funzionato nemmeno qui");
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
	
	public void checkPriority()
	{
		for(int x = 0; x < allMachines.size(); x++)
		{
			ArrayList<Job> alj = allMachines.get(x);
			for(int y = 0 ; y < alj.size(); y++)
			{
				Job j = alj.get(y);
				if(alj.get(y).getPreviousJob() != null)
				{
					ArrayList<String> prev = alj.get(y).getPreviousJob();
					for(int k = 0; k < prev.size(); k++)
					{
						String name = prev.get(k);
						int index = jobMap.get(name);
						for(int h = 0; h < allMachines.get(index).size(); h++)
						{
							Job j1 = allMachines.get(index).get(h);
							if(j1.getName().equals(name))
							{
								if(j1.getEndingTime() > j.getStartingTime())
									System.out.println("Il job "+j1.getName()+", predecessore di "+j.getName()+" finisce al tempo: "+j1.getEndingTime()+". il suo successore inizia al tempo"+j.getStartingTime());
								break;
							}
						}
					}
				}
				
				if(alj.get(y).getNextJob() != null)
				{
					ArrayList<String> succ = alj.get(y).getNextJob();
					for(int k = 0; k < succ.size(); k++)
					{
						String name = succ.get(k);
						int index = jobMap.get(name);
						for(int h = 0; h < allMachines.get(index).size(); h++)
						{
							Job j1 = allMachines.get(index).get(h);
							if(j1.getName().equals(name))
							{
								if(j1.getStartingTime() < j.getEndingTime())
									System.out.println("Il job "+j1.getName()+", successore di "+j.getName()+" inizia al tempo: "+j1.getStartingTime()+". il suo predecessore finisce al tempo"+j.getEndingTime());
								break;
							}
						}
					}
				}
			}
		}
	}
    
	public String toString() {
		return allMachines.toString() + "\nCosto soluzione finale = "
        + this.calculateTwt();
	}
}
