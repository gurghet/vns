package vnsBFP;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;


public class initialSolution 
{
	
	public static String typeOfSolution; //Mi salvo il tipo di soluzione fra ATC, ATCR e ATCRS
	
	public static int numberOfJob; //ci salvo il numero di Job che conterr� il file
	public static int numberOfMachines; //numero di macchine
	
	
	public static String dataPath = null;
	public static String setupPath = null;
	public static String releasePath = null;
	public static String constraintsPath = null;
	
	public static ArrayList<Job> jobList = null; //Inizializzo l'arraylist che conterr� la lista di tutti i job
	public static ArrayList<Job> jobList2 = null;
	public static int tMachine[]; //vettore dove salvo il t attuale di ogni macchina per calcolare ATCRS
	
	//possibili valori k1 {0.2, 0.6, 0.8, 1, 1.2, 1.4, 1.6, 1.8, 2, 2.4, 2.8, 3.2, 3.6, 4.0, 4.4, 4.8, 5.2, 5.6, 6.0, 6.4, 6.8, 7.2}
	public static final float k1 = 2.2f;
	
	//possibili valori k2 {0.1, 0.3, 0.5, 0.7, 0.9, 1.1, 1.3, 1.5, 1.7, 1.9, 2.1}
	public static final float k2 = 1.1f;
	
	//possibili valori k3 {0.001, 0.0025, 0.004, 0.005, 0.025, 0.04, 0.05, 0.25, 0.4, 0.6, 0.8, 1.0, 1.2}
	public static final float k3 = 0.4f;
	
	public static float pMedio = 0; //processing time medio per il calcolo di ATCSR
	
	public static int indexMachine; // L'indice della macchina dove scheduler� il Job
	public static int tMin; // t minimo della macchina sul quale scheduler� il prossimo Job
	
	public static int indexJob; //L'indice del Job che sheduler� per prossimo
	
	public static StorageVNS s; //Struttura che contiene i dati
	
	public static Job lastJob = null; //Ultimo job schedulato sulla macchina scelta
	
	public static int[][] matrixOfSetup = new int[300][300];
	
	public static double[] vectorOfIndexPriority;
	public static int indexOfJob;
	
	public static long gap;
	public static long gapMin;
	
	
	
	public static StorageVNS CreateInitialSolution(String tipo, int nMachines, String pathJobs, String pathSetup, String pathRelease, String pathConstraints)
	{
		
		typeOfSolution = tipo;
		numberOfMachines = nMachines;
		tMachine = new int[numberOfMachines];
		dataPath = pathJobs;
		setupPath = pathSetup;
		releasePath = pathRelease;
		constraintsPath = pathConstraints;

		getJobData(); //Recupero informazioni dai file di testo
		getSetupData(); //Recupero i dati dei Setup
		getReleaseData(); //Recupero i dati delle Release
		getConstraintsData(); //Recupero i vincoli di precedenza
		
		s = new StorageVNS(numberOfMachines); //Struttura dati che contiene lo schedule
		
		//Finch� non esaurisco la lista di job da schedulare
		while( jobList.size() > 0)
		{ 
			
			
			//seleziono la macchina su cui schedulare e il T di quella macchina
			selectMachine();
			
			//calcolo l'indice di priorit� scelto per i Job ancora da schedulare e salvo la posizione in jobList e il valore
			if(typeOfSolution == "ATC")
			{
				
				calculateATC();
				
			}
			else if(typeOfSolution == "ATCS")
			{
				
				calculateATCS();
				
			}
			else
			{
				
				calculateATCSR();
				
			}
			

			//Scelgo il job a seconda di indice di priorit� e che rispetti i vincoli di precedenza
			selectJob();
			
			
			//Aggiorno il T della macchina per sapere a che punto sono arrivato su ogni macchina e salvo lo starting e ending time
			if(lastJob != null)
			{
				indexOfJob = jobList.get(indexJob).getIndexOfJob();
				jobList.get(indexJob).setStartingTime( Math.max(tMachine[indexMachine] + lastJob.getSetupTimes(jobList.get(indexJob).getIndexOfJob()), jobList.get(indexJob).getReleaseTime()) + gapMin );
				jobList.get(indexJob).setEndingTime(jobList.get(indexJob).getStartingTime() + jobList.get(indexJob).getExecutionTime());
				tMachine[indexMachine] = (int)jobList.get(indexJob).getEndingTime();
				jobList2.get(indexOfJob).setStartingTime( Math.max(tMachine[indexMachine] + lastJob.getSetupTimes(jobList.get(indexJob).getIndexOfJob()), jobList.get(indexJob).getReleaseTime()) +gapMin );
				jobList2.get(indexOfJob).setEndingTime(jobList.get(indexJob).getStartingTime() + jobList.get(indexJob).getExecutionTime());
			}
			else
			{
				indexOfJob = jobList.get(indexJob).getIndexOfJob();
				jobList.get(indexJob).setStartingTime(Math.max(tMachine[indexMachine], jobList.get(indexJob).getReleaseTime()) + gapMin);
				jobList.get(indexJob).setEndingTime(jobList.get(indexJob).getStartingTime() + jobList.get(indexJob).getExecutionTime());
				tMachine[indexMachine] = (int)jobList.get(indexJob).getEndingTime();
				jobList2.get(indexOfJob).setStartingTime(Math.max(tMachine[indexMachine], jobList.get(indexJob).getReleaseTime()) + gapMin);
				jobList2.get(indexOfJob).setEndingTime(jobList.get(indexJob).getStartingTime() + jobList.get(indexJob).getExecutionTime());
			}
			
			//Aggiorno su che macchina � e la posizione sulla macchina
			jobList.get(indexJob).setMachine(indexMachine);
			jobList.get(indexJob).setNumberOnMachine(s.getMachine(indexMachine).size());
			jobList2.get(indexOfJob).setMachine(indexMachine);
			jobList2.get(indexOfJob).setNumberOnMachine(s.getMachine(indexMachine).size());
			
			
			//Aggiungo il scelto nella struttura di schedule
			s.addJobOnMachine(indexMachine, jobList.get(indexJob));
			
			//Rimuovo il job schedulato dalla lista di quelli ancora schedulabili
			jobList.remove(indexJob);
			
		}
		
		
		//DEBUG stampa della stuttura di schedule
		///*
		s.printResult();
		for(int i = 0; i < numberOfMachines; i++)
		{
			
			System.out.println(tMachine[i]);
			
		}
		System.out.println(s.calculateTwt());
		//*/
		
		return s;
				
	}
	
	
	
	
	public static void getJobData()
	{
		
		int executionTime;
		int dueDate;
		int weight;
		int releaseTime = 0; //release time ora settata a 0
		String name;
		
		StringTokenizer tok = null; //Inizializzo il tokenizer per leggere il file
		StringTokenizer tok2 = null; // Per leggere la riga
		String  percorso = new String(); //stringa nella quale salvo il pecorso del file passato dall'utente
		
		try
		{
			//InputStreamReader lettore = new InputStreamReader (System.in);
            //BufferedReader inputpercorso = new BufferedReader (lettore);
            //System.out.println("Inserisci il percorso del file: ");
            //percorso = inputpercorso.readLine();
			percorso = dataPath;
            FileInputStream fstream = new FileInputStream(percorso);
            DataInputStream in = new DataInputStream(fstream);
            @SuppressWarnings("resource")
			BufferedReader buffer = new BufferedReader(new InputStreamReader(in)); //Ho aperto il file specificato dal percorso
            String riga; //ci salvo una riga del file alla volta
            
            riga = buffer.readLine(); //leggo la prima riga che contiene il numero di job
            tok= new StringTokenizer(riga);
            numberOfJob = Integer.parseInt(tok.nextToken());
            //System.out.println("Numero di Job = " + numberOfJob);
            jobList = new ArrayList<Job>(numberOfJob);
            jobList2 = new ArrayList<Job>(numberOfJob);
            
            for(int i = 0; i < numberOfJob; i++)
            {
            	riga = buffer.readLine();
            	tok2 = new StringTokenizer(riga);//faccio partire il tokenizer
        		
            	name = "J"+i;
            	executionTime = Integer.parseInt(tok2.nextToken());
            	dueDate = (int)(Integer.parseInt(tok2.nextToken()) / numberOfMachines);
            	weight = Integer.parseInt(tok2.nextToken());
            	
            	jobList.add(new Job(name, releaseTime, executionTime, dueDate, weight, numberOfJob, i));
            	jobList2.add(new Job(name, releaseTime, executionTime, dueDate, weight, numberOfJob, i));
            	//System.out.println("Job = " + name + " " + executionTime + " " + dueDate + " " + weight);
            }
            
		}catch (Exception e){System.err.println("Errore: " + e.getMessage());}
		
	}

	
	public static void getSetupData()
	{
		
		int setupValue = 0;
		int setupTotale;
		
		StringTokenizer tok = null; // Per leggere la riga
		String  percorso = new String(); //stringa nella quale salvo il pecorso del file passato dall'utente
		String riga; //ci salvo una riga del file alla volta
		
		try
		{
			//InputStreamReader lettore = new InputStreamReader (System.in);
            //BufferedReader inputpercorso = new BufferedReader (lettore);
            //System.out.println("Inserisci il percorso del file: ");
            //percorso = inputpercorso.readLine();
			percorso = setupPath;
            FileInputStream fstream = new FileInputStream(percorso);
            DataInputStream in = new DataInputStream(fstream);
            @SuppressWarnings("resource")
			BufferedReader buffer = new BufferedReader(new InputStreamReader(in)); //Ho aperto il file specificato dal percorso
            
            
            for(int i = 0; i < numberOfJob; i++)
            {
            	setupTotale = 0;
            	riga = buffer.readLine();
            	tok = new StringTokenizer(riga);//faccio partire il tokenizer
        		
            	for (int j = 0; j < numberOfJob; j++)
            	{
            		
            		setupValue = Integer.parseInt(tok.nextToken());
            		jobList.get(i).setSetupTimes(j, setupValue);
            		jobList2.get(i).setSetupTimes(j, setupValue);
            		matrixOfSetup[i][j] = setupValue;
            		
            		if(setupValue >= 0)
            		{
            			
            			setupTotale = setupTotale + setupValue;
            		
            		}
            			
            	}
            	
            	jobList.get(i).setSetupMedio((float)setupTotale / (numberOfJob - 1));
            	jobList2.get(i).setSetupMedio((float)setupTotale / (numberOfJob - 1));
            	
            }
            
            
		}catch (Exception e){System.err.println("Errore: " + e.getMessage());}
		
	}
	
	
	public static void getReleaseData()
	{
		
	
		long releaseTime = 0;
		
		StringTokenizer tok = null; // Per leggere la riga
		String  percorso = new String(); //stringa nella quale salvo il pecorso del file passato dall'utente
		String riga; //ci salvo una riga del file alla volta
		
		try
		{
			//InputStreamReader lettore = new InputStreamReader (System.in);
            //BufferedReader inputpercorso = new BufferedReader (lettore);
            //System.out.println("Inserisci il percorso del file: ");
            //percorso = inputpercorso.readLine();
			percorso = releasePath;
            FileInputStream fstream = new FileInputStream(percorso);
            DataInputStream in = new DataInputStream(fstream);
            @SuppressWarnings("resource")
			BufferedReader buffer = new BufferedReader(new InputStreamReader(in)); //Ho aperto il file specificato dal percorso
            
            
            for(int i = 0; i < numberOfJob; i++)
            {
            	
            	riga = buffer.readLine();
            	tok = new StringTokenizer(riga);//faccio partire il tokenizer
        		
            		
        		releaseTime = Integer.parseInt(tok.nextToken());
        		jobList.get(i).setRelaseTime(releaseTime);
        		jobList2.get(i).setRelaseTime(releaseTime);
        		
        		//System.out.println(jobList.get(i).getRelaseTime());
            }
            
		}catch (Exception e){System.err.println("Errore: " + e.getMessage());}
		
	}
	
	
	public static void getConstraintsData()
	{
		
		
		int firstJob;
		int secondJob;
		
		StringTokenizer tok = null; // Per leggere la riga
		String  percorso = new String(); //stringa nella quale salvo il pecorso del file passato dall'utente
		String riga; //ci salvo una riga del file alla volta
		
		try
		{
			//InputStreamReader lettore = new InputStreamReader (System.in);
            //BufferedReader inputpercorso = new BufferedReader (lettore);
            //System.out.println("Inserisci il percorso del file: ");
            //percorso = inputpercorso.readLine();
			percorso = constraintsPath;
            FileInputStream fstream = new FileInputStream(percorso);
            DataInputStream in = new DataInputStream(fstream);
            @SuppressWarnings("resource")
			BufferedReader buffer = new BufferedReader(new InputStreamReader(in)); //Ho aperto il file specificato dal percorso
            
            
            for(int i = 0; i < 90; i++)
            {
            	
            	riga = buffer.readLine();
            	tok = new StringTokenizer(riga);//faccio partire il tokenizer
        		
            		
        		firstJob = Integer.parseInt(tok.nextToken());
        		secondJob = Integer.parseInt(tok.nextToken());

        		//System.out.println(firstJob + " " + secondJob);
        		jobList.get(firstJob).setJobSuccessivi(secondJob);
        		jobList.get(firstJob).setNextJob(jobList.get(secondJob).getName());
        		jobList.get(secondJob).setJobPrecedenti(firstJob);
        		jobList.get(secondJob).setPreviousJob(jobList.get(firstJob).getName());
        		
        		jobList2.get(firstJob).setJobSuccessivi(secondJob);
        		jobList2.get(firstJob).setNextJob(jobList.get(secondJob).getName());
        		jobList2.get(secondJob).setJobPrecedenti(firstJob);
        		jobList2.get(secondJob).setPreviousJob(jobList.get(firstJob).getName());

        		//System.out.println(jobList.get(i).getRelaseTime());
            }
            
		}catch (Exception e){System.err.println("Errore: " + e.getMessage());}
		
	}
	
	
	public static void calculateATC()
	{
		
		double ATC = 0; 
		float argument = 0;
		float firstTerm = 0;
		float firstTermMax = 0;
		
		vectorOfIndexPriority = new double[jobList.size()];
		
		//Prendo l'ultimo job schedulato sulla macchina (Se c'�)
		if(s.getMachine(indexMachine).size() != 0)
		{
			
			lastJob = s.getMachine(indexMachine).get((s.getMachine(indexMachine).size() - 1));
		
		}
		
		for (int i = 0; i < jobList.size(); i++)
		{
			
			argument = (float)jobList.get(i).getWeight() / (float)jobList.get(i).getExecutionTime();
			//System.out.println(argument);
			calculatePMedio();			
			
			//Calcolo del primo termine
			firstTermMax = Math.max((float)jobList.get(i).getDueDate() - (float)jobList.get(i).getExecutionTime() - tMin, 0);
			firstTerm = -(firstTermMax/(k1 * pMedio));
			//System.out.println(firstTerm);
			
			
			ATC = ((argument) * (Math.exp((double)firstTerm)));
			//System.out.println(ATC);
			
			jobList.get(i).setPriorityIndex(ATC);
			jobList2.get(i).setPriorityIndex(ATC);
			vectorOfIndexPriority[i] = ATC;
			
			//DEBUG
			//System.out.println(jobList.get(i).getName() + "  " + jobList.get(i).getPriorityIndex());
			
		}
		
	}
	
	
	public static void calculateATCS()
	{
		
		double ATCS = 0; 
		float argument = 0;
		float firstTerm = 0;
		float firstTermMax = 0;
		float secondTerm = 0;
		float numeratoreSecondTerm = 0;
		
		vectorOfIndexPriority = new double[jobList.size()];

		
		//Prendo l'ultimo job schedulato sulla macchina (Se c'�)
		if(s.getMachine(indexMachine).size() != 0)
		{
			
			lastJob = s.getMachine(indexMachine).get((s.getMachine(indexMachine).size() - 1));
		
		}
		
		
		for (int i = 0; i < jobList.size(); i++)
		{
			
			argument = (float)jobList.get(i).getWeight() / (float)jobList.get(i).getExecutionTime();
			//System.out.println(argument);
			calculatePMedio();			
			
			//Calcolo del primo termine
			firstTermMax = Math.max((float)jobList.get(i).getDueDate() - (float)jobList.get(i).getExecutionTime() - tMin, 0);
			firstTerm = -(firstTermMax/(k1 * pMedio));
			//System.out.println(firstTerm);
			
			//Calcolo del secondo termine
			if(s.getMachine(indexMachine).size() == 0)
			{
				
				numeratoreSecondTerm = 0; //E' il primo e quindi non lo precede nessuno e non ha tempo di setup
				//System.out.println(numeratoreSecondTerm);

			}
			else
			{
				
				numeratoreSecondTerm = lastJob.getSetupTimes(jobList.get(i).getIndexOfJob());
				//System.out.println(numeratoreSecondTerm);
				
			}
			
			secondTerm = -(numeratoreSecondTerm / (k2 * jobList.get(i).getSetupMedio())); 
			//System.out.println(secondTerm);
			
			
			ATCS = ((argument) * (Math.exp((double)firstTerm)) * (Math.exp((double)secondTerm)));
			//System.out.println(ATCS);
			
			jobList.get(i).setPriorityIndex(ATCS);
			jobList2.get(i).setPriorityIndex(ATCS);
			vectorOfIndexPriority[i] = ATCS;
			
			//DEBUG
			//System.out.println(jobList.get(i).getName() + "  " + jobList.get(i).getPriorityIndex());
			
		}
		
	}
	
	
	public static void calculateATCSR()
	{
		
		double ATCSR = 0; 
		float argument = 0;
		float firstTerm = 0;
		float firstTermFirstMax = 0;
		float firstTermSecondMax = 0;
		float secondTerm = 0;
		float numeratoreSecondTerm = 0;
		float thirdTerm = 0;
		
		vectorOfIndexPriority = new double[jobList.size()];

		
		//Prendo l'ultimo job schedulato sulla macchina (Se c'�)
		if(s.getMachine(indexMachine).size() != 0)
		{
			
			lastJob = s.getMachine(indexMachine).get((s.getMachine(indexMachine).size() - 1));
		
		}
		
		for (int i = 0; i < jobList.size(); i++)
		{
			
			argument = (float)jobList.get(i).getWeight() / (float)jobList.get(i).getExecutionTime();
			//System.out.println(argument);
			calculatePMedio();			
			
			//Calcolo del primo termine
			firstTermFirstMax = Math.max(jobList.get(i).getReleaseTime(), tMin);
			firstTermSecondMax = Math.max(jobList.get(i).getDueDate() - jobList.get(i).getExecutionTime() - firstTermFirstMax, 0);
			firstTerm = -(firstTermSecondMax/(k1 * pMedio));
			//System.out.println(firstTerm);
			
			
			//Calcolo del secondo termine
			if(s.getMachine(indexMachine).size() == 0)
			{
				
				numeratoreSecondTerm = 0; //E' il primo e quindi non lo precede nessuno e non ha tempo di setup
				//System.out.println(numeratoreSecondTerm);

			}
			else
			{
				
				numeratoreSecondTerm = lastJob.getSetupTimes(jobList.get(i).getIndexOfJob());
				//System.out.println(numeratoreSecondTerm);
				
			}
			
			secondTerm = -(numeratoreSecondTerm / (k2 * jobList.get(i).getSetupMedio())); 
			//System.out.println(secondTerm);
			
			
			//Calcolo del terzo termine
			thirdTerm = -(Math.max((float)jobList.get(i).getReleaseTime() - (float)tMin, 0)) / (k3 * pMedio);
			//System.out.println(thirdTerm);
			
			
			ATCSR = ((argument) * (Math.exp((double)firstTerm)) * (Math.exp((double)secondTerm)) * (Math.exp((double)thirdTerm)));
			//System.out.println(ATCSR);
			
			jobList.get(i).setPriorityIndex(ATCSR);
			jobList2.get(i).setPriorityIndex(ATCSR);
			vectorOfIndexPriority[i] = ATCSR;
			
			//DEBUG
			//System.out.println(jobList.get(i).getName() + "  " + jobList.get(i).getPriorityIndex());
			
		}
		
	}
	
	
	public static void calculatePMedio()
	{
		
		float PTot = 0;
		
		for (int i = 0; i<jobList.size(); i++)
		{
			
			PTot = PTot + jobList.get(i).getExecutionTime();
			
		}
		
		pMedio = PTot / jobList.size();
		//System.out.println(pMedio);
		
	}
	
	
	public static void selectMachine()
	{
		
		int tMinTemp = 999999999;
		ArrayList<Integer> machineWhitEqualTMin = new ArrayList<Integer>();
		int x = 0;
		
		
		//Seleziono la macchina con t minore e il valore di t
		for(int i = 0; i<numberOfMachines; i++)
		{
			
			if (tMachine[i]<tMinTemp)
			{
				
				tMinTemp = tMachine[i];
				
			}
			
		}
		
		
		//Salvo nell'array list l'indice delle macchine che hanno tMin uguale
		for(int i = 0; i<numberOfMachines; i++)
		{ 
			
			if (tMachine[i] == tMinTemp)
			{
				
				machineWhitEqualTMin.add(i);
				
			}
			
		}
		
		if (machineWhitEqualTMin.size() == 1)
		{
			
			tMin = tMinTemp;
			indexMachine = machineWhitEqualTMin.get(0);
			
			//System.out.println(tMin);
			//System.out.println(indexMachine);
			
		}
		else
		{
			
			tMin = tMinTemp;
			
			//Genero un numero casuale tra 0 e size di machineWhitEqualTMin che sar� l'indice del vettore
			x = (int)( Math.random() * (machineWhitEqualTMin.size()-0.01)); // il meno 0.01 � per non far venire l'intero superiore
			
			indexMachine = machineWhitEqualTMin.get(x);
			
			//DEBUG			
			//System.out.println("Tmin " + tMin);
			//System.out.println(indexMachine);
			
		}

	}
	
	
	public static void selectJob()
	{
		
		ArrayList<Integer> precedenti; //job che devon essere schedulati prima di quello scelto
		int x = jobList.size(); // indice che mi serve per pescare dal vettore di indici di priorit�
		Arrays.sort(vectorOfIndexPriority); // Ordino il vettore con gli indici di priorit�
		boolean jobOk = false; // Vedo se il job pu� essere schedulato
		int count; //variabile che conta quanti vinoli di precedenza rispettare
		long start; // tempo di inizio
		gap = 0; //il job che deve venire prima � in lavorazione quindi devo aspettare che finisca
		
		//Itero finch� il Job non va bene
		while(jobOk == false)
		{
			
			gapMin = 0; //il gap minimo che devo aspettare
			count = 0;
		
			//Recupero l'indice del job con ATC maggiore
			for (int i = 0;i<jobList.size();i++)
			{
				
				if(vectorOfIndexPriority[x-1] == jobList.get(i).getPriorityIndex())
				{
					indexJob = i;
					break;
				}
				
			}
			
			// recupero i suoi precedenti
			precedenti = jobList.get(indexJob).getjobPrecedenti(); 

			
			//Se ho precedenti devo vedere se sono stati schedulati
			if(precedenti != null)
			{
				
				//Calcolo il tempo di start del job scelto
				if(lastJob != null)
				{
					start = Math.max(tMachine[indexMachine] + lastJob.getSetupTimes(jobList.get(indexJob).getIndexOfJob()) , jobList.get(indexJob).getReleaseTime());
				}
				else
				{
					start = Math.max(tMachine[indexMachine], jobList.get(indexJob).getReleaseTime());
				}
				
				
				//Scorro i precedenti
				for(int j = 0;j<precedenti.size(); j++)
				{
					
					//Se il precedente non � ancora stato schedulato scelgo un altro job
					if(jobList2.get(precedenti.get(j)).getEndingTime() == 0)
					{
						break;
					}
					//se � stato schedulato invece
					else
					{
								
						//Controllo che il job scelto parta dopo la sua fine. Se si aumento count
						if(start >= jobList2.get(precedenti.get(j)).getEndingTime())
						{									
							count++;
						}
						//Se no mi salvo un gap che devo aggiungere allo start time per evitare che parta prima del job in esecuzione
						else
						{
							gap = jobList2.get(precedenti.get(j)).getEndingTime() - start;
							if(gap > gapMin)
							{
								gapMin = gap;								
							}
							count++;
						}
					}
							
				}
			
				
				//Se tutti i job che dovevano finire prima finiscono effettivamente prima d� OK a quel job e lo schedulo
				if (count == precedenti.size())
				{
					
					//System.out.println("Schedulo il job " + jobList.get(indexJob).getName());
					jobOk = true;
					
				}
				else
				{
					
					//System.out.println("Non va bene il job " + jobList.get(indexJob).getName());
					x--;
					
				}
				
				
			}
			else
			{
				
				//System.out.println("Schedulo il job " + jobList.get(indexJob).getName());
				jobOk = true;
				
			}
			
			//DEBUG per le release
			/*
			System.out.println("Release del Job " + jobList.get(indexJob).getRelaseTime());
			System.out.println("tMin " + tMin);
			
			if(jobList.get(indexJob).getRelaseTime() > tMin)
			{
				System.out.println("ERRORE");
			}
			*/
			
			//DEBUG per vedere esattezza tempi di setup
			/*
			if(s.getMachine(indexMachine).size() != 0){
				
				System.out.println(lastJob.getSetupTimes(jobList.get(indexJob).getIndexOfJob()) + "  " + jobList.get(indexJob).getName());
				
			}
			*/
			//DEBUG
			//System.out.println(indexJob);
			//System.out.println(jobList.get(indexJob).getName() + "  " + jobList.get(indexJob).getPriorityIndex());
		
		}
	
	}

	
	public static int[][] getMatrixOfSetup()
	{
		
		return matrixOfSetup;
		
	}
	
	
	public static ArrayList<Job> getListOfJob()
	{
		
			return jobList2;		
		
	}
	

}
