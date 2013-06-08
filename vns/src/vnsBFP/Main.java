package vnsBFP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Andrea Passaglia
 * @version 0.1
 */
public class Main {

	/**
	 * Questa variabile va modificata manualmente, se impostata a true il
	 * programma stampa il log effettuato con la funzione static log(oggetto) o
	 * Main.log(oggetto).
	 */
	private static final boolean VERBOSE = false;

	public static boolean timeout = true;

	static int[] tempi = { 5000, 10000, 30000 };
	static int[] numMacchine = { 2, 4, 10, 20 };
	static String filePath;
	static String setupPath;
	static String releasePath;
	static String constraintsPath;

	/**
	 * @param args
	 *            String[] contenente: nome file istanza, numero macchine,
	 *            numero mosse
	 */
	public static void main(String[] args) {
		Main m = new Main();

		String benchmarkPath = "/Users/gurghet/Desktop/benchmark.csv"; 
		
		File file = new File(benchmarkPath);
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);
		
		for (int i = 0; i < tempi.length; i++) {
			for (int j = 0; j < numMacchine.length; j++) {
				for (int k = 1; k <= 125; k++) {
					filePath = "/Users/gurghet/Dropbox/Progetto Paolucci/Istanze Test/JobData/wt300_"
							+ String.format("%03d", k) + ".dat";
					setupPath = "/Users/gurghet/Dropbox/Progetto Paolucci/Istanze Test/SetupData/st300_"
							+ String.format("%03d", k) + ".dat";
					releasePath = "/Users/gurghet/Dropbox/Progetto Paolucci/Istanze Test/ReleaseData/rt300_04_"
							+ String.format("%03d", k) + ".dat";
					constraintsPath = "/Users/gurghet/Dropbox/Progetto Paolucci/Istanze Test/ConstraintsData/ct300_"
							+ String.format("%03d", k) + ".dat";
					try {
						bw.append(String.valueOf(k));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					for (int h = 0; h < 5; h++) {
						int result = m.effettuaProva(numMacchine[j],tempi[i]);
						try {
							bw.append(", "+String.valueOf(result));
							bw.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					try {
						bw.append('\n');
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// fine delle 125 istanze
				try {
					bw.append("\n\n\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	

		// stampa risultato su un file
		// TODO per ora lo stampa a console
		//logf(soluzione);
		//logf("Costo soluzione iniziale: " + twtIniziale);
		//logf("Costo soluzione finale:   " + soluzione.getTwt());
		//logf("Numero mosse eseguite:  " + counter);
		//soluzione.check_consistency();
	}
	
	private int effettuaProva(int num, int sec) {
		// inizializza la classe manager con le macchine previste
		StorageVNS soluzione = initialSolution.CreateInitialSolution("ATC",
				num, filePath, setupPath, releasePath, constraintsPath);/*
																				 * new
																				 * StorageVNS
																				 * (
																				 * numMacchine
																				 * )
																				 */
		;
		//logf("Soluzione iniziale:");
		//logf(soluzione);
		// inizializzaSoluzione(soluzione, filePath);
		soluzione.setInitialTwt();
		// soluzione.printResult();
		// soluzione.checkPriority();

		//long twtIniziale = soluzione.getTwt();
		int counter = 0;

		// cuore dell'algoritmo
		// TODO mettere un timeout
		// FINE:
		final Timer timer = new Timer();

		class RemindTask extends TimerTask {
			public void run() {
				//logf("Time's up!");
				timeout = false;
				timer.cancel(); // Terminate the timer thread
			}
		}

		timer.schedule(new RemindTask(), sec);

		while (timeout) {
			int k = 0;
			int kmax = 47;
			// while k<=k_{max}
			while (k <= kmax) {
				//log(k);
				// shaking: select a random solution
				// x'���������������������������Nk(s)
				boolean andiamoAvanti = soluzione
						.muoviCasualmenteNelNeighborhood(k);
				counter++;
				// if(counter > maxIterations || soluzione.getTwt() == 0) break
				// FINE;
				// Move or not:
				// if solution x' is better than s
				if (andiamoAvanti) {
					// s=x'; k=1;
					//logf(counter + ":TWT=" + soluzione.getTwt());
					k = 0;
				} else {
					// k=k%k_{max}+1
					k = k % (kmax + 1) + 1;
				}
			}
		}
		timeout = true;
		logf("Numero mosse eseguite:  " + counter);
		return (int) soluzione.getTwt();
	}

	/**
	 * @param element
	 *            Object elemento da stampare se la variabile VERBOSE
	 *            ������������������ true
	 */
	public static void log(Object element) {
		if (VERBOSE) {
			System.out.println(element);
			System.out.flush();
		}
	}

	/**
	 * @param element
	 *            Object elemento da stampare
	 */
	public static void logf(Object element) {
		System.out.println(element);
	}

}