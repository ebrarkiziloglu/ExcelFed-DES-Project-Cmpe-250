import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

// javac Project2/src/*.java -d Project2/bin --release 16
// java -cp Project2/bin project2main input_5.txt myoutput.txt > cancelled5.txt

// java -cp Project2/bin project2main Project2_test_cases/input_1.txt myoutput1.txt

// This main class is used to read from the input, process the given data by using an object of the ExcelFedManager class and printing the relevant output.
public class project2main {

	// Following two fields store the data taken from the input file.
    protected static ArrayList<Player> players;
    protected static MyHeap<Attempt> attempts;

    public static void main(String[] args) throws FileNotFoundException {
    	
    	players = new ArrayList<Player>();
        Scanner sc = new Scanner(new File(args[0]));
        Scanner scInt = new Scanner(new File(args[0]));
        PrintStream print = new PrintStream(new File(args[1]));

        // Players' data are read:
        int N = scInt.nextInt();
        scInt.nextLine();
        sc.nextLine();
        for(int i = 0; i < N; i++){
            String s = sc.nextLine();
            String[] input = s.split(" ");
            int id = Integer.parseInt(input[0]);
            int skill = Integer.parseInt(input[1]);
            Player player = new Player(id, skill);
            players.add(player);
            scInt.nextLine();     }
        
        // Attempts' data are read:
        int A = scInt.nextInt();
        sc.nextLine();
        attempts = new MyHeap<Attempt>(A+5);
        for(int i = 0; i < A; i++){
            String s = sc.nextLine();
            String[] input = s.split(" ");
            String type = input[0];
            int id = Integer.parseInt(input[1]);
            double arrivalTime = Double.parseDouble(input[2]);
            double duration = Double.parseDouble(input[3]);
            Attempt newAttempt = new Attempt(id, arrivalTime, type, duration);
            attempts.insert(newAttempt);       }
        
        // Physiotherapy, training, and massage providers' data are read:
        String[] physInput = sc.nextLine().split(" ");
        int physNumber = Integer.parseInt(physInput[0]);
        
//        String[] input2 = sc.nextLine().split(" ");
//        int coachNumber = Integer.parseInt(input2[0]);
//        int masseurNumber = Integer.parseInt(input2[1]);
        
        int coachNumber = sc.nextInt();
        int masseurNumber = sc.nextInt();
        
        Attempt.setCancelledAttempts(0);
        Attempt.setInvalidAttempts(0);
        
        // Following object of the ExcelFedMAnager class takes the information provided in the input file and process everything within it's methods. 
        ExcelFedManager fedSimulation = new ExcelFedManager(players, attempts, coachNumber, physNumber, masseurNumber, physInput);

        // The information is processed: Simulation runs until there is no more attempts waiting. 
        fedSimulation.runSimulator();

        // To provide the relevant output statistics, information is taken from the some of the fields of the ExcelFedManager object:
        // Statistics 1-3:
        int maxLengthOfTrainingQueue = fedSimulation.maxLengthOfTrainingQueue;
        int maxLengthOfPsychQueue = fedSimulation.maxLengthOfPsychQueue;
        int maxLengthOfMassageQueue = fedSimulation.maxLengthOfMassageQueue;
        
        // Statistics 4-6:
        double total = 0;
        for(double e : fedSimulation.getTrainingQueueWaitingTime()) total += e;
        double averageTrainingQueueTime = total/fedSimulation.getTrainingQueueWaitingTime().size();
        if(fedSimulation.getTrainingQueueWaitingTime().size() == 0) averageTrainingQueueTime = 0;
        total = 0;
        for(double e : fedSimulation.getPsychQueueWaitingTime()) total += e;
        double averagePsychQueueTime = total/fedSimulation.getPsychQueueWaitingTime().size();
        if(fedSimulation.getPsychQueueWaitingTime().size() == 0) averagePsychQueueTime = 0;
        total = 0;
        for(double e : fedSimulation.getMassageQueueWaitingTime()) total += e;
        double averageMassageQueueTime = total/fedSimulation.getMassageQueueWaitingTime().size();
        if(fedSimulation.getMassageQueueWaitingTime().size() == 0) averageMassageQueueTime = 0;
        total = 0;
        
        // Statistics 7-10:
        for(double e : fedSimulation.getTrainingTime()) total += e;
        double averageTrainingTime = total/fedSimulation.getTrainingTime().size();     
        if(fedSimulation.getTrainingTime().size() == 0) averageTrainingTime = 0;
        total = 0;
        for(double e : fedSimulation.getPsychTime()) total += e;
        double averagePsychTime = total/fedSimulation.getPsychTime().size();
        if(fedSimulation.getPsychTime().size() == 0) averagePsychTime = 0;
        total = 0; 
        for(double e : fedSimulation.getMassageTime()) total += e;
        double averageMassageTime = total/fedSimulation.getMassageTime().size();
        if(fedSimulation.getMassageTime().size() == 0) averageMassageTime = 0;
        total = 0;
        for(double e : fedSimulation.getTurnaroundTime()) total += e;
        double averageTurnaroundTime = total/fedSimulation.getTurnaroundTime().size();
        if(fedSimulation.getTurnaroundTime().size() == 0) averageTurnaroundTime = 0;
 
//        System.out.println("turnaround count: " + fedSimulation.getTotalTrainingArrivals());
//        System.out.println("current count : "+ fedSimulation.getTurnaroundTime().size());
//        System.out.println("total: " + total);
        
        // Statistics 11-12:
        double[] result1 = Player.mostTimeInPsychQueue();
        int id1 = (int) result1[0];
        double duration1 = result1[1];        
        double[] result2 = Player.leastTimeInMassageQueue();
        int id2 = (int) result2[0];
        double duration2 = result2[1];     
        

        // Statistics are printed:
        print.println(maxLengthOfTrainingQueue);
        print.println(maxLengthOfPsychQueue);
        print.println(maxLengthOfMassageQueue);
        String pattern = "%.3f";        
        print.printf(pattern, averageTrainingQueueTime); print.println();
        print.printf(pattern, averagePsychQueueTime); print.println();
        print.printf(pattern, averageMassageQueueTime); print.println();
        print.printf(pattern, averageTrainingTime); print.println();
        print.printf(pattern, averagePsychTime); print.println();
        print.printf(pattern, averageMassageTime); print.println();
        print.printf(pattern, averageTurnaroundTime); print.println();     
        
        print.print(id1 + " ");
        print.printf(pattern, duration1);
        print.println();
        print.print(id2 + " ");
        print.printf(pattern, duration2);
        print.println();
        print.println(Attempt.getInvalidAttempts());
        print.println(Attempt.getCancelledAttempts());
        print.printf(pattern,fedSimulation.getTime());
        print.println();

    }
}
