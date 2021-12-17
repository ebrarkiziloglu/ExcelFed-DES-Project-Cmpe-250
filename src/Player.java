import java.util.ArrayList;
import java.util.Comparator;

// In order to sort Players in the physiotherapy queue according to their training time, this Comparator method will be used.
class PlayerComparator implements Comparator<Player>{

    @Override
    public int compare(Player o1, Player o2) {
    	if(Math.abs(o1.getTrainingDuration()-o2.getTrainingDuration()) < 0.0000000001 ) {
    		if(Math.abs(o1.getPsychQueueEnteranceTime() - o2.getPsychQueueEnteranceTime()) < 0.0000000001)
    			return o1.getID() - o2.getID();
    		else if(o1.getPsychQueueEnteranceTime() < o2.getPsychQueueEnteranceTime()) return -1;
    		else return 1;}
    	else if(o1.getTrainingDuration() > o2.getTrainingDuration()) return -1;
        else return 1;
    }

}

public class Player implements Comparable<Player>{
    private final int ID;
    private final int skill;
    private int massageCount;
    private boolean isAvaliable;
    private String whereIsCuurently;
    // following fields store data to calculate waiting times in the queues for the individual players.
    private double trainingDuration; // given in the training Attempt input
    // following field is used to calculate TurnAroundTime
    private double trainingQueueEnteranceTime;
    private double psychQueueEnteranceTime;
    private double massageQueueEnteranceTime;
    
    // following 2 array lists are used to determine the output of 11 and 12th statistics, 
    // which are the player who spent the most time in physiotherapy queue and the player who spent the least time in the massage queue.
    private ArrayList<Double> psychQueueWaitingTime;
	private ArrayList<Double> massageQueueWaitingTime;
	// following field stores the id of the server that Player is currently taking
    private int serviceId;

    // Following methods are Getters and Setters
    public double getTrainingQueueEnteranceTime() {return trainingQueueEnteranceTime;}
    public void setTrainingQueueEnteranceTime(double trainingQueueEnteranceTime) {this.trainingQueueEnteranceTime = trainingQueueEnteranceTime;}
    
    public double getPsychQueueEnteranceTime() {return psychQueueEnteranceTime;}
    public void setPsychQueueEnteranceTime(double psychQueueEnteranceTime) {this.psychQueueEnteranceTime = psychQueueEnteranceTime;}
    
    public double getMassageQueueEnteranceTime() {return massageQueueEnteranceTime;}
    public void setMassageQueueEnteranceTime(double massageQueueEnteranceTime) {this.massageQueueEnteranceTime = massageQueueEnteranceTime;}
    
    public int getServiceId() {return serviceId;}
    public void setServiceId(int serviceId) {this.serviceId = serviceId;}

    public int getID() {return ID;}
    public int getSkill() {return skill;}
    public int getMassageCount() {return massageCount;}
    public void incrementMassageCount() {this.massageCount++;}
    public void setAvaliable(boolean avaliable) {isAvaliable = avaliable;}
    public boolean isAvaliable(){return isAvaliable;}
    
    public String getWhereIsCuurently() {return whereIsCuurently;}
	public void setWhereIsCuurently(String whereIsCuurently) {this.whereIsCuurently = whereIsCuurently;}
    
    public double getTrainingDuration() {return trainingDuration;}
    public void setTrainingDuration(double d) {this.trainingDuration = d;}
    
    public void addPsychQueueTime(double d){psychQueueWaitingTime.add(d);}
    public void addMassageQueueTime(double d){massageQueueWaitingTime.add(d);}
  
	public ArrayList<Double> getPsychQueueWaitingTime() {return psychQueueWaitingTime;}
	public ArrayList<Double> getMassageQueueWaitingTime() {return massageQueueWaitingTime;}

	// The constructor:
    Player(int id, int skill){
    	this.whereIsCuurently = "free";
        this.ID = id;
        this.skill = skill;
        this.massageCount = 0;
        this.isAvaliable = true;
        this.trainingDuration = 0;
        this.serviceId = -1;
        this.psychQueueWaitingTime = new ArrayList<Double>();
        this.massageQueueWaitingTime = new ArrayList<Double>();
    }


    // Compare to method is used to sort players according to their skill levels in the Massage queue.
    @Override
    public int compareTo(Player o) {
        if(this.skill != o.getSkill()) return o.skill - this.getSkill();
        else {
        	if(Math.abs(this.getMassageQueueEnteranceTime() - o.getMassageQueueEnteranceTime()) < 0.0000000001)
        		return this.ID - o.getID();
        	else if(this.getMassageQueueEnteranceTime() < o.getMassageQueueEnteranceTime()) return -1;
        	else return 1;
        	}
    }


    public String toString(){
        return "This player has the id " + this.ID + ", and her availability is " + this.isAvaliable + ", " + this.trainingQueueEnteranceTime 
        		+ " | " + this.psychQueueEnteranceTime + " | " + this.whereIsCuurently;
    }

    // This method is used to determine statistic 11
    // It traverses all Players in the static players array list of the ExcelFedManager class. And it finds the player who spent the most time in the physiotherapy queue.
    public static double[] mostTimeInPsychQueue() {
    	double[] result = new double[2];
    	double max = 0;
    	double id = 0;
    	ArrayList<Player> players = ExcelFedManager.players;
    	for(Player p : players) {
    		double sum = 0;
    		ArrayList<Double> times = p.getPsychQueueWaitingTime();
    		for(double e : times) {
    			sum += e;
    		}
    		if(sum > max) {
				max = sum;
				id = (double) p.getID();
			}
    	}
    	result[0] = id;
    	result[1] = max;
    	return result;
    }
    
    // This method is used to determine statistic 12
    // It traverses all Players in the static players array list of the ExcelFedManager class. And it finds the player who spent the least time in the massage queue.
    public static double[] leastTimeInMassageQueue() {
    	double[] result = new double[2];
    	double min = 1000000000;
    	double id = 0;
    	boolean isThere3Massages = false;
    	ArrayList<Player> players = ExcelFedManager.players;
    	for(Player p : players) {
    		if(p.getMassageCount() == 3) {
    			isThere3Massages = true;
    			double sum = 0;
        		ArrayList<Double> times = p.getMassageQueueWaitingTime();
        		for(double e : times) {
        			sum += e;
        		}
        		if(sum < min) {
    				min = sum;
    				id = (double) p.getID();
    			}
    		}	
    		
    	}
    	result[0] = id;
    	result[1] = min;
    	if(! isThere3Massages) {
    		result[0] = -1;
    		result[1] = -1;
    	}
    	return result;
    }
    

}
