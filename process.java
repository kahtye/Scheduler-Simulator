
public class process {
	int pid;
	int burst_time;
	int priority;
	public process(){
	}
	
	void setPid(int num){
		pid = num;
	}
	void setBurstTime(int num){
		burst_time = num;
	}
	void setPriority(int num){
		priority = num;
	}
	
	int getPid(){
		return pid;
	}
	int getBurstTime(){
		return burst_time;
	}
	int getPriority(){
		return priority;
	}
}
