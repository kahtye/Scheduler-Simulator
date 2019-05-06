import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
public class Assignment1{
	public static void main(String args[])  throws Exception{
		for (int i = 1; i <=4; i++){
			String dataName = "testdata" + i;
			ArrayList<process> processes = readData(dataName + ".txt");
			fcfs(processes,false,dataName);
			
			processes = readData(dataName + ".txt");
			sjf(processes,dataName);
			
			processes = readData(dataName + ".txt");
			rr(processes,20,dataName,0,false);
			
			processes = readData(dataName + ".txt");
			rr(processes,40,dataName,0,false);
			
			processes = readData(dataName + ".txt");
			lottery(processes,40,dataName);
		}
	}
	
	static ArrayList<process> readData(String fileName) throws Exception{
		ArrayList<process> processes = new ArrayList<process>();
		File file = new File(fileName);
		Scanner sc = new Scanner(file);
		
		while (sc.hasNextLine()){
			process temp = new process();
			temp.setPid(Integer.parseInt(sc.nextLine()));
			temp.setBurstTime(Integer.parseInt(sc.nextLine()));
			temp.setPriority(Integer.parseInt(sc.nextLine()));
			
			processes.add(temp);
		}
		sc.close();
		return processes;
	}
	
	static void fcfs(ArrayList<process> processes, boolean isSJF,String dataName){
		double turnaroundTime = 0.0;
		int size = processes.size();
		int[] completionTime = new int[size];
		int[] cpu = new int[size];
		int[] pid = new int[size];
		int[] startbt = new int[size];
		int[] endbt = new int[size];
		cpu[0] = 0;
		for (int i = 0; i < size; i++){
			process p = processes.get(i);
			pid[i] = i+1;
			if (i == 0){
				cpu[i] = 0;
				startbt[i] = p.getBurstTime();
				endbt[i] = 0;
				completionTime[i] = p.getBurstTime();
			}
			else{
				cpu[i] = 3 + completionTime[i-1];
				startbt[i] = p.getBurstTime();
				endbt[i] = 0;
				completionTime[i] = cpu[i] + startbt[i];
			}
		}
			
		for (int i = 0; i < completionTime.length; i++){
			turnaroundTime += completionTime[i];
		}
		String fileName = "";
		if (isSJF){
			fileName = "SJF-" + dataName + ".csv";
		}
		else{
			fileName = "FCFS-" + dataName + ".csv";
		}
		writeToFile(cpu,pid,startbt,endbt,completionTime,fileName,turnaroundTime,processes.size());
	}
	
	static void sjf(ArrayList<process> processes,String dataName){
		ArrayList<process> sorted = sort(processes);
		fcfs(sorted,true,dataName);
	}
	
	static void rr(ArrayList<process> processes, int timeQ, String dataName, int startProcess, boolean lottery){
		int size = processes.size();
		ArrayList<process> temp = processes;
		double turnaroundTime = 0;
		ArrayList<Integer> cpu = new ArrayList<Integer>();
		ArrayList<Integer> pid = new ArrayList<Integer>();
		ArrayList<Integer> start = new ArrayList<Integer>();
		ArrayList<Integer> end = new ArrayList<Integer>();
		ArrayList<Integer> completion = new ArrayList<Integer>();
		
		boolean first = true;
		int count = 0;
		int processCount = startProcess;
		
		while (temp.size() != 0){
			process p = temp.get(processCount);
			if (first){
				cpu.add(0);
				pid.add(p.getPid());
				start.add(p.getBurstTime());
				end.add(p.getBurstTime()-timeQ);
				
				//update process bt
				process tempProcess = p;
				tempProcess.setBurstTime(end.get(count));
				temp.set(processCount, tempProcess);
				completion.add(0);
				
				first = false;
			}
			else{
				if(completion.get(count-1) == 0){
					cpu.add(cpu.get(count-1)+3+timeQ);
				}
				else{
					cpu.add(completion.get(count-1) +3);
				}
				pid.add(p.getPid());
				start.add(p.getBurstTime());
				
				//not subtracted by 5!!!!!!
				if (p.getBurstTime() - timeQ <= 0){
					end.add(0);
					completion.add(cpu.get(count) + start.get(count));
					temp.remove(processCount);
				}
				else{
					end.add(start.get(count) - timeQ);
					process tempProcess = p;
					tempProcess.setBurstTime(end.get(count));
					temp.set(processCount, tempProcess);
					completion.add(0);
				}	
				
			}
			count++;
			if (processCount+1 >= temp.size()){
				processCount = 0;
			}
			else{
				processCount++;
			}
			
		}
		
		//Converting Arraylist to arrays
		int tableSize = cpu.size();
		int[] cpuArr = new int[tableSize];
		int[] pidArr = new int[tableSize];
		int[] startArr = new int[tableSize];
		int[] endArr = new int[tableSize];
		int[] completionArr = new int[tableSize];
		for (int i = 0; i < tableSize; i++){
			cpuArr[i] = cpu.get(i);
			pidArr[i] = pid.get(i);
			startArr[i] = start.get(i);
			endArr[i] = end.get(i);
			completionArr[i] = completion.get(i);
		}
		
		for (int i = 0; i < completionArr.length; i++){
			turnaroundTime += completionArr[i];
		}
		
		String fileName = "";
		if (!lottery)
			fileName += "RR_TimeQuantum_" + timeQ + "-" + dataName + ".csv";
		else
			fileName += "Lottery_" + timeQ + "-" + dataName + ".csv";
		writeToFile(cpuArr,pidArr,startArr,endArr,completionArr,fileName,turnaroundTime,size);
	}
	
	static void lottery(ArrayList<process> processes, int timeQ, String dataName){
		//Get random number [0-(sum on all priorities)]
		int prioritySum = 0;
		for (int i = 0; i < processes.size(); i++){
			process p = processes.get(i);
			prioritySum += p.getPriority();
		}
		int randNum = (int)(Math.random() * (prioritySum+1));
		int startProcess = 0; 
		for (int i = 0; i < processes.size(); i++){
			randNum -= processes.get(i).getPriority();
			if (randNum <= 0){
				startProcess = i;
				i = processes.size();
			}
		}
		rr(processes,timeQ,dataName,startProcess,true);
		
	}
	
	static void writeToFile(int[] cpu, int[] pid, int[] startbt, int[] endbt, int[] completionTime, String fileName, double time, int numProcesses){
		try (PrintWriter writer = new PrintWriter(new File(fileName))) {

		      StringBuilder sb = new StringBuilder();
		      sb.append("CPU Time,PID,Start Bt,End Bt,Completion Time,\n");
		      for (int i = 0; i < cpu.length;i++){
		    	  sb.append(cpu[i]);
		    	  sb.append(",");
		    	  sb.append(pid[i]);
		    	  sb.append(",");
		    	  sb.append(startbt[i]);
		    	  sb.append(",");
		    	  sb.append(endbt[i]);
		    	  sb.append(",");
		    	  sb.append(completionTime[i]);
		    	  sb.append("\n");
		      }
		      sb.append("\n");
		      sb.append("Turn Around Time,,,");
		      sb.append(time);
		      sb.append("\n");
		      sb.append("Average Turn Around Time,,,");
		      sb.append(time/numProcesses);
		      
		      writer.write(sb.toString());

		      System.out.println(fileName + ": was successful");

		    } catch (FileNotFoundException e) {
		    	System.out.println("Error");
		    }

		  
	}
	
	static ArrayList<process> sort(ArrayList<process> p){
		ArrayList<process> sorted = p;
		
		int n = sorted.size();
		for (int i = 0; i < n-1; i++){
			for (int j = 0; j < n-i-1; j++){
				if( sorted.get(j).getBurstTime() > sorted.get(j+1).getBurstTime()){
					process temp = sorted.get(j);
					sorted.set(j, sorted.get(j+1));
					sorted.set(j+1, temp);
				}
			}
		}
		return sorted;
	}
	
	static void printProcesses(ArrayList<process> processes){
		for (int i = 0; i < processes.size(); i++){
			process p = processes.get(i);
			System.out.print(p.getPid() + " ");
			System.out.print(p.getBurstTime() + " ");
			System.out.print(p.getPriority());
			System.out.println();
		}
	}
}
