package vnsBFP;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class StorageVNS
{
	// ArrayList che memorizza tutti i job divisi per macchine.
	private ArrayList<ArrayList<Job>> allMachines = null;

	// Array delle posizioni correnti.
	private int[] currentPositionArray = null;

	// TWT attuale.
	private float twt;



	public StorageVNS(int nMachines)
	{
		currentPositionArray = new int[nMachines];
		allMachines = new ArrayList<ArrayList<Job>>();

		for(int x = 0; x < nMachines ; x++)
		{
			allMachines.add(new ArrayList<Job>());
			currentPositionArray[x] = 0;
		}
	}

	/**
	 * Costruttore di copia
	 * @param storageVNS StorageVNS da clonare
	 */
	public StorageVNS(StorageVNS storageVNS) {
		this.allMachines = new ArrayList<ArrayList<Job>>();
		for(int x = 0; x < storageVNS.getNumberOfMachines() ; x++)
		{
			allMachines.add(new ArrayList<Job>(storageVNS.getMachine(x)));
		}
		this.currentPositionArray = storageVNS.getCurrentPositionArray().clone();
		this.twt = storageVNS.getTwt();
	}

	protected ArrayList<ArrayList<Job>> getAllMachines() {
		return allMachines;
	}

	protected int[] getCurrentPositionArray() {
		return currentPositionArray;
	}

	protected float getTwt() {
		return twt;
	}

	public int getNumberOfMachines()
	{
		return allMachines.size();
	}

	public int getNumberOfJobsOnMachine(int machineNumber)
	{
		return allMachines.get(machineNumber).size();
	}

	public ArrayList<Job> getMachine(int position)
	{
		return allMachines.get(position);
	}

	// Metodi di inserimento e cancellazione
	/**
	 * @param machineNumber numero della macchina
	 * @param j Job da aggiungere
	 */
	public void addJobOnMachine(int machineNumber, Job j)
	{
		allMachines.get(machineNumber).add(j);
	}

	public void removeJobFromMachine(int position, int machineNumber)
	{
		allMachines.get(machineNumber).remove(position);
	}

	public void removeAllJobsFromMachine(int machineNumber)
	{
		for(int x = 0; x < allMachines.get(machineNumber).size(); x++)
		{
			allMachines.get(machineNumber).remove(x);
		}
	}

	// Mosse

	/**
	 * @param range raggio in cui verranno effettuati gli swap
	 * @param repeat quante volte verrà ripetuta questa mossa
	 * alla fine sto metodo fa un po’ le cose a casaccio, ma in
	 * fondo è un algoritmo random no?
	 */
	public boolean swapOnOneMachine(int range, int repeat) {
		boolean miglioramentoAvvenuto = false;
		for (int i = 0; i < this.getNumberOfMachines(); i++) {
			// range = 0 è come dire range = nmax
			if (range == 0) range = this.getNumberOfJobsOnMachine(i);
			for (int j = 0; j < this.getNumberOfJobsOnMachine(i); j++) {
				miglioramentoAvvenuto =
						miglioramentoAvvenuto || _swapOnOneMachine_onMachine_onPosition(range, repeat, allMachines.get(i), j);
			}
		}
		return miglioramentoAvvenuto;
	}

	public boolean _swapOnOneMachine_onMachine_onPosition(int range, int repeat, ArrayList<Job> machine, int position)
	{
		Job consideredJob = machine.get(position);
		int leftLimit = 0;
		int rightLimit = machine.size();
		
		// Imposto il limite sinistro del range da considerare
		if((position - range) > 0)
		{
			leftLimit = position - range;
		}
		
		// Imposto il limite destro del range da considerare
		if((position + range) < rightLimit)
		{
			rightLimit = position + range;
		}
		
		int distance = rightLimit - leftLimit;
		int posInRange = (int)(Math.random()*distance);
		int newPos = leftLimit + posInRange;
		
		Job substitutedJob = machine.get(newPos);

		machine.set(position, substitutedJob);
		machine.set(newPos, consideredJob);

		float newTwt = calculateTwt();
		
		if(newTwt < twt) // manca condizione di validit� in base alla priorit�.
		{
			twt = newTwt;
			return true;
		}
		else
		{
			machine.set(position, consideredJob);
			machine.set(newPos, substitutedJob);
			return false;
		}
	}
	
	public boolean transferOnOneMachine(int range, int repeat) {
		boolean miglioramentoAvvenuto = false;
		for (int i = 0; i < this.getNumberOfMachines(); i++) {
			// range = 0 è come dire range = nmax
			if (range == 0) range = this.getNumberOfJobsOnMachine(i);
			for (int j = 0; j < this.getNumberOfJobsOnMachine(i); j++) {
				miglioramentoAvvenuto =
						miglioramentoAvvenuto || _transferOnOneMachine_onMachine_onPosition(range, repeat, allMachines.get(i), j);
			}
		}
		return miglioramentoAvvenuto;
	}

	public boolean _transferOnOneMachine_onMachine_onPosition(int range, int repeat, ArrayList<Job> machine, int position)
	{
		//system.out.println("Twt iniziale: "+ twt);
		
			//aggiungere le ripetizioni e il calcolo del twt
			int leftLimit = 0;
			int rightLimit = machine.size() - 1;
			
			// Imposto il limite sinistro del range da considerare e copio la sottolista se alcune posizioni
			// rimarranno invariate rispetto alla mossa perch� il range � troppo piccolo
			if((position - range) > 0)
			{
				leftLimit = position - range;
			}
			
			// Imposto il limite destro del range da considerare
			if((position + range) < rightLimit)
			{
				rightLimit = position + range;
			}
			
			
			// Calcolo la posizione in cui fare il transfert
			int distance = rightLimit - leftLimit;
			int posInRange = (int)(Math.random()*distance);
			int newPos = leftLimit + posInRange;
			
			Job toBeInsertedJob = machine.remove(position);
			machine.add(newPos, toBeInsertedJob);
			
			// e calcolo il twt alla nuova configurazione
			float newTwt = calculateTwt();
			
			// se ho un miglioramento aggiorno il twt e mantengo la configurazione, altrimenti rimetto la precedente.
			if(newTwt < twt)
			{
				twt = newTwt;
				return true;
			}
			else
			{
				// rimetto il job dove stava prima
				Job toBePutBackJob = machine.remove(newPos);
				machine.add(position, toBePutBackJob);
				return false;
			}
		
	}
	
	public boolean swapAcrossMachines(int range, int repeat) {
		boolean miglioramentoAvvenuto = false;
		for (int i = 0; i < this.getNumberOfMachines(); i++) {
			// range = 0 è come dire range = nmax
			if (range == 0) range = this.getNumberOfJobsOnMachine(i);
			for (int j = 0; j < this.getNumberOfJobsOnMachine(i); j++) {
				miglioramentoAvvenuto =
						miglioramentoAvvenuto || _swapAcrossMachines_onMachine_onPosition(range, repeat, allMachines.get(i), j);
			}
		}
		return miglioramentoAvvenuto;
	}

	public boolean _swapAcrossMachines_onMachine_onPosition(int range, int repeat, ArrayList<Job> originalMachine, int position)
	{
		Job consideredJob = originalMachine.get(position);
		
		// Calcolo randomicamente la macchina con cui fare il transfert
		int numberOfMachines = allMachines.size() - 1;
		int machineNumber = (int)(Math.random()*numberOfMachines);
		//system.out.println("La macchina con cui far� il transfer �: "+machineNumber);
		ArrayList<Job> machine = allMachines.get(machineNumber);
			
		int leftLimit = 0;
		int rightLimit = machine.size() - 1;
		
		// Imposto il limite sinistro del range da considerare
		if((position - range) > 0)
		{
			leftLimit = position - range;
		}
				
		// Imposto il limite destro del range da considerare
		if((position + range) < rightLimit)
		{
			rightLimit = position + range;
		}
		
		
		//Calcolo la osizione con cui fare lo swap all'interno del range
		int rangeSize = rightLimit - leftLimit;
		int posInRange = (int)(Math.random()*rangeSize);
		int swapPos = leftLimit + posInRange;
		
		// Provo a fare lo swap
		if(swapPos >= machine.size())
		{
			//system.out.println("Swap non effettuato perch� la macchina destinazione non ha job nella posizione selezionata");
			return false;
		}
		else
		{
			// Effettuo lo scambio
			Job substitutedJob = machine.get(swapPos);
			machine.set(swapPos, consideredJob);
			originalMachine.set(position, substitutedJob);
			// Calcolo il twt
			float newTwt = calculateTwt();
			//system.out.println("Tentativo:");
			//printResult();
			//system.out.println("TWT: "+newTwt);
			//system.out.println("Fine tentativo.");
			
			// Se il twt non migliora rimetto tutto a posto e ritorno false.
			
			// Altrimenti lascio tutto cos� e cambio il twt con quello nuovo.
			if (newTwt < twt)
			{
				twt = newTwt;
				//printResult();
				return true;
			} else {
				machine.set(swapPos, substitutedJob);
				originalMachine.set(position, consideredJob);
				//printResult();
				return false;
			}
		}		
	}

	public boolean TransferAcrossMachines(int range, int repeat, ArrayList<Job> originalMachine, int position)
	{
		ArrayList<Job> result = new ArrayList<Job>();
		int index = allMachines.indexOf(originalMachine);
		String s = "";
		int machineNumber = index;
		
		// Calcolo randomicamente la macchina con cui fare il transfert. non accetto la stessa macchina su cui sto prelevando
		while(machineNumber == index)
		{
			int numberOfMachines = allMachines.size() - 1;
			machineNumber = (int)(Math.random()*numberOfMachines);
		}
		//system.out.println("La macchina con cui far� il transfer �: "+machineNumber);
		ArrayList<Job> machine = allMachines.get(machineNumber);
		
		// Calcolo i limiti determinati dal range
		int rightLimit = position + range;
		int leftLimit = 0;
		
		if((position - range) > 0)
		{
			leftLimit = position - range;
		}
		
		if((position + range) > (machine.size() -1))
		{
			rightLimit = machine.size();
		}
		
		// Calcolo la posizione con cui fare il transfert.
		int distance = rightLimit - leftLimit;
		int posInRange = (int)(Math.random()*distance);
		int newPos = leftLimit + posInRange;
		//system.out.println("La posizione con cui faccio il transfer �: "+newPos);
		
		
		//Inizio a riempire result con le posizioni fino a leftLimit.
		for(int x = 0; x < leftLimit; x++)
		{
			result.add(machine.get(x));
			s = s + machine.get(x).getName() + "; ";
		}
		
		// Ora continuo a riempire fino alla posizione newPos.		
		
		for(int x = leftLimit; x < newPos; x++)
		{
			result.add(machine.get(x));
			s = s + machine.get(x).getName() + "; ";
		}
		
		// Metto in newPos il job che va trasferito
		result.add(originalMachine.get(position));
		s = s + originalMachine.get(position).getName() + "; ";
		
		// Aggiungo tutti gli altri fino in fondo
		for(int x = newPos; x < machine.size(); x++)
		{
			result.add(machine.get(x));
			s = s + machine.get(x).getName() + "; ";
		}
		
		// imposto la nuova macchina al posto di quella estratta.
		allMachines.set(machineNumber, result);
		
		// Creo una copia della originalMachine senza il job da spostare e la metto al posto dell'originale.
		ArrayList<Job> orig = new ArrayList<Job>();
		for(int x = 0; x < originalMachine.size(); x++)
		{
			if(x != position)
			{
				orig.add(originalMachine.get(x));
			}
		}
		allMachines.set(index, orig);
		
		// Calcolo il twt
		float newTwt = calculateTwt();
		
		// Se il twt migliora lascio tutto com'�, altrimenti rimetto come prima
		if(newTwt < twt)
		{
			twt = newTwt;
			printResult();
			return true;
		}
		else
		{
			allMachines.set(index, originalMachine);
			allMachines.set(machineNumber, machine);
			printResult();
			return false;
		}
	}
	// Metodi temporanei per il funzionamento provvisorio


	public float calculateTwt()
	{
		// TODO questa funzione calcola il twt anche se è già stato calcolato
		//      è possibile che il twt non cambi tra due chiamate o cambia per forza?

		float resultingTwt = 0;
		for(int a = 0; a < allMachines.size(); a++)
		{
			ArrayList<Job> machine = allMachines.get(a);
			////system.out.println("Calcolo il twt su: "+machine.size()+" job");
			Job j = null;
			float time = 0;
			for(int b = 0; b < machine.size(); b++)
			{
				// Calcolo il tempo in cui termina il job
				j = machine.get(b);
				time = time + j.getExecutionTime();
				if(time > j.getDueDate())
				{
					float paid = (time - j.getDueDate())*j.getWeight();
					resultingTwt = resultingTwt + paid;
				}
			}
		}
		//Main.log("TWT = " + resultingTwt);
		return resultingTwt;
	}

	public void printResult()
	{
		//system.out.println("");
		//system.out.println("ORDINE DELLE MACCHINE:");
		for(int x = 0; x < allMachines.size(); x++)
		{
			ArrayList<Job> alj = allMachines.get(x);
			//system.out.println("Macchina #"+x);
			String s = "";
			for(int y = 0; y < alj.size(); y++)
			{
				s = s + alj.get(y).getName() + ", ";
			}
			//system.out.println(s);
		}
		//system.out.println("FINE");
		//system.out.println("");
	}

	/**
	 * @param jobArray ArrayList dei job in ordine sparso
	 */
	public void inizializzaCoiJob(ArrayList<Job> jobArray) {
		// just put all the jobs on a machine for now
		for (Job job : jobArray) {
			addJobOnMachine(0, job);
		}
		twt = calculateTwt();
	}

	/**
	 * @param k il numero del neighborhood da esplorare
	 * @return uno <b>StorageVNS</b> con la nuova soluzione 
	 */
	public boolean muoviCasualmenteNelNeighborhood(int k) {
		// TODO crea una deep-copy e la muove, meglio sarebbe avere sempre
		//      due soluzioni e avere delle mosse che si possono annullare
		//      in modo da non dover sempre istanziare nuove classi (quindi
		//      ad esempio fare una mossa sulla soluzione 2 e se non porta
		//      a niente di buono annullare solo la mossa e non buttare via
		//      tutta la classe

		// parametro r è il range della mossa
		int range;
		// parametro l è il numero di ripetizioni della mossa
		int repeat;
		// parametro che indica il codice della mossa
		int moveCode; Method move = null;

		moveCode = k % 4; // ci sono solo 4 mosse numerate da 0 a 3
		range = ((int) (Math.floor(k / 4))) % 4; // ci sono solo 4 possibilita [2,5,10,n_max]
		repeat = ((int)  (Math.floor(k/16))) % 3; // ci sono solo 3 possibilità [1,2,3]

		if (range == 0) range = 2;
		if (range == 1) range = 5;
		if (range == 2) range = 10;
		if (range == 3) range = 0; // nella funzione vale come nmax

		try {
			if (moveCode == 0) move = this.getClass().getMethod("swapOnOneMachine", int.class, int.class);
			if (moveCode == 1) move = this.getClass().getMethod("transferOnOneMachine", int.class, int.class);
			if (moveCode == 2) move = this.getClass().getMethod("swapOnOneMachine", int.class, int.class);
			if (moveCode == 3) move = this.getClass().getMethod("swapOnOneMachine", int.class, int.class);
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

	public String toString() {
		return allMachines.toString() + "\nCosto soluzione finale = " + this.calculateTwt();
	}
}
