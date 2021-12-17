import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

// This is the main class that all of the commands are processed and data/statistics are stored.

public class ExcelFedManager {

	
	// Following fields store the necessary data to use later or to print to the output file. 
	// In most of the case, the field name implicates the duty of the field.
    protected static ArrayList<Player> players;
    protected MyHeap<Attempt> attempts;
    protected int coachNumber, physNumber, masseurNumber;
    private double[] psychotherapists;
    private boolean[] psychAvailability, coachAvailability, masseurAvailability;
    private double time;
    protected int maxLengthOfTrainingQueue;
    protected int maxLengthOfPsychQueue;
    protected int maxLengthOfMassageQueue;
    private int totalTrainingArrivals;
    
    // following 3 queues are the main 3 queues of the Simulation:
    protected Queue<Player> trainingQueue;
    protected PriorityQueue<Player> physQueue;
    protected MyHeap<Player> masseurQueue;
    // protected PriorityQueue<Player> masseurQueue;

    // Following array lists store the waiting times in the queues and the service times for all players. 
    // After the simulation ends, average of the data of these arrays will be taken and printed in the main (project2main) class.
    private ArrayList<Double> trainingQueueWaitingTime;
    private ArrayList<Double> psychQueueWaitingTime;
    private ArrayList<Double> massageQueueWaitingTime;
    private ArrayList<Double> trainingTime;
    private ArrayList<Double> psychTime;
    private ArrayList<Double> massageTime;
    private ArrayList<Double> turnaroundTime;

    // Getters and Setters:
    public ArrayList<Double> getTrainingQueueWaitingTime() {return this.trainingQueueWaitingTime;}

    public ArrayList<Double> getPsychQueueWaitingTime() {return this.psychQueueWaitingTime;}

    public ArrayList<Double> getMassageQueueWaitingTime() {return this.massageQueueWaitingTime;}

    public ArrayList<Double> getTrainingTime() {return trainingTime;}

    public ArrayList<Double> getPsychTime() {return psychTime;}

    public ArrayList<Double> getMassageTime() {return massageTime;}

    public ArrayList<Double> getTurnaroundTime() {return turnaroundTime;}

    public double getTime() {return this.time;}

    public int getTotalTrainingArrivals() { return totalTrainingArrivals;}

	// Constructor:
    ExcelFedManager(ArrayList<Player> players, MyHeap<Attempt> attempts,
                    int coachNumber, int physNumber, int masseurNumber, String[] physInput ){
        this.time = 0;
        ExcelFedManager.players = players;
        this.attempts = attempts;
        this.coachNumber = coachNumber;
        this.physNumber = Integer.parseInt(physInput[0]);
        this.masseurNumber = masseurNumber;
        this.psychotherapists = new double[physNumber];
        this.psychAvailability = new boolean[physNumber];
        this.coachAvailability = new boolean[coachNumber];
        this.masseurAvailability = new boolean[masseurNumber];
        for(int i = 1; i <= physNumber; i++) {
            psychotherapists[i-1] = Double.parseDouble(physInput[i]);
            psychAvailability[i-1] = true;
        }
        for(int i = 0; i < this.coachNumber; i++) {
            this.coachAvailability[i] = true;
        }
        for(int i = 0; i < this.masseurNumber; i++) {
            this.masseurAvailability[i] = true;
        }
        this.trainingQueue = new LinkedList<Player>();
        this.physQueue = new PriorityQueue<Player>(new PlayerComparator());
        this.masseurQueue = new MyHeap<Player>(players.size()+5);
        //this.masseurQueue = new PriorityQueue<Player>();
        this.maxLengthOfTrainingQueue = 0;
        this.maxLengthOfPsychQueue = 0;
        this.maxLengthOfMassageQueue = 0;
        this.totalTrainingArrivals = 0;
        this.trainingQueueWaitingTime = new ArrayList<Double>();
        this.psychQueueWaitingTime = new ArrayList<Double>();
        this.massageQueueWaitingTime = new ArrayList<Double>();
        this.turnaroundTime = new ArrayList<Double>();
        this.trainingTime = new ArrayList<Double>();
        this.psychTime = new ArrayList<Double>();
        this.massageTime = new ArrayList<Double>();
    }


    // This method is the backbone of the implementation.
    // It polls the most urgent attempt each time and processes it, until there is no more attempt waiting to be processed.
    public void runSimulator() {
    
        while(! attempts.isEmpty()) {
            Attempt nextAttempt = attempts.deleteMin();
            String type = nextAttempt.getAttemptType();

            // "t": training attempt
            // "m": massage attempt
            // "p": leave training and physiotheraphy attempt
            // "lp": leave physiotheraphy
            // "lm": leave massage
            if(type.equals("t")) enterTraining(nextAttempt);
            else if(type.equals("m")) enterMassage(nextAttempt);
            else if(type.equals("p")) enterPsychotheraphy(nextAttempt);
            else if(type.equals("lp")) leavePsychotheraphy(nextAttempt);
            else if(type.equals("lm")) leaveMassage(nextAttempt);
        }
    }
    
    // If there is a coach available, match the Player with the coach. Otherwise, add the Player to the TrainingQueue:
    public void enterTraining(Attempt attempt){
    	time = attempt.startTime;
        if(! attempt.isCancelled(players.get(attempt.getPlayerId()))) {
        	this.totalTrainingArrivals++;
            double duration = attempt.getDuration();
            this.trainingTime.add(duration);
            Player player = players.get(attempt.getPlayerId());
            player.setTrainingDuration(duration);
            player.setAvaliable(false);
            player.setTrainingQueueEnteranceTime(time);
            for(int i=0; i < coachNumber; i++){
                if(coachAvailability[i]){
                    coachAvailability[i] = false;
                    this.trainingQueueWaitingTime.add((double) 0);
                    player.setServiceId(i);
                    player.setWhereIsCuurently("training");
                    Attempt enterPsychoteraphy = new Attempt(player.getID(), time+attempt.getDuration(), "p", -2);
                    attempts.insert(enterPsychoteraphy);
                    i = coachNumber+1;
                }
            } if(player.getServiceId() == -1) {
                trainingQueue.add(player);
                player.setWhereIsCuurently("trainingQueue");
            }
        }
    }

    // Take the Player from her Training, if there is a Player waiting in the queue, match the Player with the coach. 
    // Otherwise, set the Coach free. If there is a physiotherapist that is available, match the Player with them.
    // Otherwise, add the Player to the PhysQueue:
    public void enterPsychotheraphy(Attempt attempt){
        // player will leave her training and enter the physiotheraphy
        time = attempt.startTime;
        Player player = players.get(attempt.getPlayerId());
        int coachId = player.getServiceId();
        player.setServiceId(-1);
        // check whether a physiotherapist is available:
        for(int i=0; i < physNumber; i++){
            if(psychAvailability[i]){
            	player.setWhereIsCuurently("pyschiotherpy");
                psychAvailability[i] = false;
                double psychDuration = psychotherapists[i];
                this.psychTime.add(psychDuration);
                this.psychQueueWaitingTime.add((double) 0);
                player.addPsychQueueTime((double)0);
                player.setServiceId(i);
                Attempt leavePsychoteraphy = new Attempt(player.getID(), time+psychDuration, "lp", -2);
                attempts.insert(leavePsychoteraphy);
                i = physNumber+1;
                }
        } // If the player has not been matched yet, add them to the Queue: 
        if(player.getServiceId() == -1) {
                player.setPsychQueueEnteranceTime(time);
                player.setWhereIsCuurently("pyschiotherpyQueue");
                physQueue.add(player);
        }
        
        // If there is someone in the TrainingQueue, match them with the coach that has just been set free:
        if(trainingQueue.isEmpty()) coachAvailability[coachId] = true;
        else{
            Player nextplayer = trainingQueue.poll();
            trainingQueueWaitingTime.add(time - nextplayer.getTrainingQueueEnteranceTime());
            // next 4 lines are to calculate the maximum length of the queue:
            if(time - nextplayer.getTrainingQueueEnteranceTime() > 0) {
            	int length = trainingQueue.size() + 1;
            	if(length > maxLengthOfTrainingQueue) maxLengthOfTrainingQueue = length;
            }
            nextplayer.setServiceId(coachId);
            Attempt enterPsychoteraphy = new Attempt(nextplayer.getID(), time+nextplayer.getTrainingDuration(), "p", -2);
            attempts.insert(enterPsychoteraphy);
        }
    }
    // Take the Player from the physiotherapy service, set them free. 
    // If there is someone waiting in the PhysQueue, match them with the physiotherapist. Otherwise, set physiotherapist free.
    public void leavePsychotheraphy(Attempt attempt){
        time = attempt.startTime;
        Player player = players.get(attempt.getPlayerId());
        int psychId = player.getServiceId();
        player.setAvaliable(true);
        player.setServiceId(-1);
        player.setWhereIsCuurently("free");
        double turnaroundTime = time - player.getTrainingQueueEnteranceTime();
        this.turnaroundTime.add(turnaroundTime);
        player.setTrainingQueueEnteranceTime(-1);
        player.setPsychQueueEnteranceTime(-1);
        if(physQueue.isEmpty()) psychAvailability[psychId] = true;
        else{
            Player nextplayer = physQueue.poll();
            double startTime = nextplayer.getPsychQueueEnteranceTime();
            this.psychQueueWaitingTime.add(time - startTime);
            // next 4 lines are to calculate the maximum length of the queue:
            if(time - nextplayer.getPsychQueueEnteranceTime() > 0) {
            	int length = physQueue.size() + 1;
            	if(length > maxLengthOfPsychQueue) maxLengthOfPsychQueue = length;
            }
            nextplayer.addPsychQueueTime(time-startTime);
            nextplayer.setPsychQueueEnteranceTime(-1);
            nextplayer.setServiceId(psychId);
            double psychDuration = psychotherapists[psychId];
            this.psychTime.add(psychDuration);
            Attempt leavePsychoteraphy = new Attempt(nextplayer.getID(), time+psychDuration, "lp", -2);
            attempts.insert(leavePsychoteraphy);
        }
    }
    
    // If there is a masseur available, match them with the Player. Otherwise, add Player to the massage queue:
    public void enterMassage(Attempt attempt){
    	time = attempt.getStartTime();
        if(attempt.isValid()){
            if(! attempt.isCancelled(players.get(attempt.getPlayerId()))){
                double duration = attempt.getDuration();
                Player player = players.get(attempt.getPlayerId());
                player.setTrainingDuration(duration);
                player.setAvaliable(false);
                player.incrementMassageCount();
                for(int i = 0; i < masseurNumber; i++){
                    if(masseurAvailability[i]){
                    	player.setWhereIsCuurently("massage");
                        masseurAvailability[i] = false;
                        player.setServiceId(i);
                        this.massageQueueWaitingTime.add((double) 0);
                        player.addMassageQueueTime((double) 0);
                        this.massageTime.add(duration);
                        Attempt leaveMassage = new Attempt(player.getID(), time+duration, "lm", -2);
                        attempts.insert(leaveMassage);
                        i = masseurNumber+1;
                    }
                } if(player.getServiceId() == -1){
                	player.setMassageQueueEnteranceTime(time);
                	player.setWhereIsCuurently("massageQueue");
                    masseurQueue.insert(player);
                }
            }
        } 
    }
    
    // Take the Player from the massage service, set them free. 
    // If there is someone waiting in the massageQueue, match them with the masseur. Otherwise, set the masseur free.
    public void leaveMassage(Attempt attempt){
        time = attempt.startTime;
        Player player = players.get(attempt.getPlayerId());
        int masseurId = player.getServiceId();
        player.setServiceId(-1);
        player.setWhereIsCuurently("free");
        player.setMassageQueueEnteranceTime(-1);
        player.setAvaliable(true);
        if(masseurQueue.isEmpty()) masseurAvailability[masseurId] = true;
        else{
            Player nextplayer = masseurQueue.deleteMin();
            double startTime = nextplayer.getMassageQueueEnteranceTime();
            nextplayer.addMassageQueueTime(time-startTime);
            this.massageQueueWaitingTime.add(time - startTime);
            // next 4 lines are to calculate the maximum length of the queue:
            if(time - nextplayer.getMassageQueueEnteranceTime() > 0) {
            	int length = masseurQueue.getSize() + 1;
            	if(length > maxLengthOfMassageQueue) maxLengthOfMassageQueue = length;
            }
            nextplayer.setServiceId(masseurId);
            this.massageTime.add(nextplayer.getTrainingDuration());
            Attempt leaveMassage = new Attempt(nextplayer.getID(), time+nextplayer.getTrainingDuration(), "lm", -2);
            attempts.insert(leaveMassage);
        }
    }
}
