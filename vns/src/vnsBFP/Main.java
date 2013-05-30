package vnsBFP;


/**
 * @author Andrea Passaglia
 * @version 0.1
 */
public class Main {

	/**
	 * Questa variabile va modificata manualmente, se impostata a true
	 * il programma stampa il log effettuato con la funzione static
	 * log(oggetto) o Main.log(oggetto).
	 */
	private static final boolean VERBOSE = false;
	
	/**
	 * @param args	String[] contenente: nome file istanza, numero macchine, numero mosse
	 */
	public static void main(String[] args) {
		String filePath = "/Users/gurghet/Dropbox/Progetto Paolucci/Istanze Test/JobData/wt300_050.dat";
		String setupPath = "/Users/gurghet/Dropbox/Progetto Paolucci/Istanze Test/SetupData/st300_050.dat";
		String releasePath = "/Users/gurghet/Dropbox/Progetto Paolucci/Istanze Test/ReleaseData/rt300_04_050.dat";
		String constraintsPath = "/Users/gurghet/Dropbox/Progetto Paolucci/Istanze Test/ConstraintsData/ct300_050.dat";
		int numMacchine = 2;
		int maxIterations = 5000;
		if (args.length == 3) {
			filePath = args[0];
			numMacchine = Integer.parseInt(args[1]);
			maxIterations = Integer.parseInt(args[2]);			
		} else if (args.length == 2) {
			filePath = args[0];
			numMacchine = Integer.parseInt(args[1]);
		} else if (args.length == 1) {
			filePath = args[0];
		}
		if (filePath == null) {
			System.out.println("File istanza di problema non specificato");
			System.exit(0);
		}

		// inizializza la classe manager con le macchine previste
		StorageVNS soluzione = initialSolution.CreateInitialSolution("ATCSR", numMacchine, filePath, setupPath, releasePath, constraintsPath);/*new StorageVNS(numMacchine)*/;
		logf("Soluzione iniziale:");
		logf(soluzione);
		//inizializzaSoluzione(soluzione, filePath);
		soluzione.setInitialTwt();
		//soluzione.printResult();
		//soluzione.checkPriority();
		
		long twtIniziale = soluzione.calculateTwt();
		int counter = 0;
		
		// cuore dell'algoritmo
		// TODO mettere un timeout
FINE:
		while (counter < maxIterations) {
			int k = 0;
			int kmax = 47;
			// while k<=k_{max}
			while (k<=kmax) {
				log(k);
				// shaking: select a random solution x'���Nk(s)
				boolean andiamoAvanti = soluzione.muoviCasualmenteNelNeighborhood(k);
				counter++;
				if(counter > maxIterations || soluzione.getTwt() == 0) break FINE;
				// Move or not:
				// if solution x' is better than s
				if (andiamoAvanti) {
					// s=x'; k=1;
					logf(counter + ": TWT=" + soluzione.getTwt());
					k = 0;
				} else {
					// k=k%k_{max}+1
					k = k % (kmax + 1) + 1;
				}
			}
		}

		// stampa risultato su un file
		// TODO per ora lo stampa a console
		logf(soluzione);
		logf("Costo soluzione iniziale: " + twtIniziale);
		logf("Numero mosse eseguite = " + counter);
		soluzione.check_consistency();
	}
	
	/**
	 * @param element	Object elemento da stampare se la variabile VERBOSE �� true
	 */
	public static void log(Object element) {
		if (VERBOSE) {
			System.out.println(element);
		}
	}
	
	/**
	 * @param element	Object elemento da stampare
	 */
	public static void logf(Object element) {
		System.out.println(element);
	}

}