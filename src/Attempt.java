

// In order to sort attempts in the attempts MyHeap object of the ExcelFedManager class, this class implements Comparable interface
public class Attempt implements Comparable<Attempt> {
	
//	protected static int i=0;
	// Following 2 fields are store the results of the statistic 13 and 14.
    private static int invalidAttempts = 0;
    private static int cancelledAttempts = 0;
    // Following 3 properties are given in the input
    private int playerId;
    protected double startTime;
    private double duration;

    // There are 5 different types of attempts as stated below:
    // "enterTraining", "enterMassage", "leaveTraining and enterPhys", "leavePhys", "leaveMassage"
    // "t": training attempt
    // "p": leave training and enter physiotherapy attempt
    // "lp": leave physiotherapy
    // "m": massage attempt
    // "lm": leave massage
    private String attemptType;
    
    // Getters and Setters:
    public double getDuration() {return duration;}
    public void setDuration(double duration) { this.duration = duration;  }
    public double getStartTime() {return startTime;}
    public int getPlayerId() {return playerId;}
    public String getAttemptType() {return attemptType;}
    public static int getInvalidAttempts() {return Attempt.invalidAttempts;}
    public static int getCancelledAttempts() {return Attempt.cancelledAttempts;}
    public static void setInvalidAttempts(int invalidAttempts) { Attempt.invalidAttempts = invalidAttempts;	}
	public static void setCancelledAttempts(int cancelledAttempts) { Attempt.cancelledAttempts = cancelledAttempts; }
	// Constructor:
    Attempt(int playerId, double startTime, String type, double duration){
        this.playerId = playerId;
        this.startTime = startTime;
        this.attemptType = type;
        this.duration = duration;    }
    
    // This method determines whether an attempt is valid.
    public boolean isValid(){
        if(attemptType.equals("m")){
            if(project2main.players.get(playerId).getMassageCount() == 3) {
//            	System.out.println(playerId + " idli kisinin invalid islemi: " + this.startTime);
                invalidAttempts++;
                return false;
            }
        } return true;
    }

    // This method determines whether an attempt should be cancelled or not.
    public boolean isCancelled(Player player){
        // checks is player is available:
        if(player.isAvaliable())
            return false;
//        System.out.println(playerId + " idli kisinin cancelled islemi: " + this.startTime);
        cancelledAttempts++;
        return true;
    }

    
    @Override
    public int compareTo(Attempt o) {
    	if(Math.abs(this.startTime - o.getStartTime()) < 0.0000000001) { 
    		
    		if(this.playerId == o.getPlayerId()) {
    			if(this.attemptType.equals("lm") || this.attemptType.equals("lp")) return -1;
        		else if(o.attemptType.equals("lp") || o.attemptType.equals("lm")) return 1;
        		else if(this.attemptType.equals("p")) return -1;
        		else if(o.attemptType.equals("p")) return 1;
        		else return 0;
    		}
    		return this.playerId - o.getPlayerId();
    		
//    		if(this.attemptType.equals(o.attemptType)) return this.playerId - o.getPlayerId();
//    		
//    		else if(this.attemptType.equals("p")) return -1;
//    		else if(o.attemptType.equals("p")) return 1;
//    		
//    		else if(this.attemptType.equals("lp")) return -1;
//    		else if(o.attemptType.equals("lp")) return 1;
//    		
//    		else if(this.attemptType.equals("lm")) return -1;
//    		else if(o.attemptType.equals("lm")) return 1;
//    		
//    		else return this.playerId - o.getPlayerId();
    		

    	}
        if(this.startTime < o.getStartTime()) return -1;
        else return 1;

    }

}
