
package os_project;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Scheduler1 {
    private static final int Q1_PRIORITY = 1;
    private static final int Q2_PRIORITY = 2;
    private static final int TIME_QUANTUM_Q1 = 3;
    private static int numProcesses;

    private static List<PCB> q1 = new ArrayList<>();
    private static List<PCB> q2 = new ArrayList<>();
    private static List<PCB> schedulingOrder0 = new ArrayList<>();
    private static List<PCB>  sTurnaroundTime = new ArrayList<>();
    private static List<PCB>  sWaitingTime = new ArrayList<>();
    private static List<PCB>  sResponseTime = new ArrayList<>();
    private static List<PCB> schedulingProcesses = new ArrayList<>();
    private static List<PCB> allProcesses = new ArrayList<>();
    private static int currentTime = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("Menu:");
            System.out.println("1. Enter process information");
            System.out.println("2. Report detailed information");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    enterProcessInformation(scanner);
                    break;
                case 2:
                    reportDetailedInformation();
                    break;
                case 3:
                    System.out.println("Exiting the program...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);

        scanner.close();
    }

    private static void enterProcessInformation(Scanner scanner) {
        System.out.print("Enter the number of processes: ");
         numProcesses = scanner.nextInt();

        for (int i = 1; i <= numProcesses; i++) {
            System.out.println("Enter information for Process " + i + ":");
            System.out.print("Priority (1 or 2): ");
            int priority = scanner.nextInt();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("CPU Burst Time: ");
            int cpuBurstTime = scanner.nextInt();

            PCB process = new PCB(i, priority, arrivalTime, cpuBurstTime);
            if (priority == Q1_PRIORITY) {
                q1.add(process);
                allProcesses.add(process);
                

            } else if (priority == Q2_PRIORITY) {
                q2.add(process);
                allProcesses.add(process);
            }
            
        }

        Collections.sort(q1, Comparator.comparingInt(PCB::getArrivalTime));
        Collections.sort(q2, Comparator.comparingInt(PCB::getArrivalTime));
        Collections.sort(allProcesses, Comparator.comparingInt(PCB::getArrivalTime));
    }

    private static void schedule() {
        while (!q1.isEmpty() || !q2.isEmpty()) {
            //its already ordered
            PCB firstReadyQ1 = getFirstProcessReady(q1,currentTime);
            if (firstReadyQ1 != null && firstReadyQ1.getArrivalTime() <= currentTime) {
                // Handle Q1 processes with time quantum
                PCB process = q1.remove(q1.indexOf(firstReadyQ1)); // Take the first process from Q1
                int burstTime = Math.min(TIME_QUANTUM_Q1, process.getBurstTime()); // Determine the burst for this round
                if (process.getStartTime() == -1) {
                    process.setStartTime(currentTime);
                }
                currentTime += burstTime; // Advance the current time
                int remainingBurst = process.getBurstTime() - burstTime; // Remaining burst time
                
                // If process has remaining burst time, reinsert to Q1 with original instance
                if (remainingBurst > 0) {
                    process.setBurstTime(remainingBurst); // Update the burst time
                    q1.add(process); // Add back to the queue (without changing arrival time)
                    schedulingOrder0.add(process); 
                } else {
                    process.setTerminationTime(currentTime); // If completed, set termination time
                    schedulingOrder0.add(process); // Add to processes order
                    schedulingProcesses.add(process);// Add to scheduled processes
                }
            } else {
                 // Ensure q2 isn't empty before proceeding
                    PCB shortestJob = getShortestJob(q2, currentTime); // Get the shortest job with ArrivalTime <= currentTime
                    
                    if (shortestJob != null && shortestJob.getArrivalTime() <= currentTime) {
                        // Handle SJF process from Q2, with check for preemption by new Q1 processes
                        
                        int remainingBurst = shortestJob.getBurstTime();
                        if (shortestJob.getStartTime() == -1) {
                            shortestJob.setStartTime(currentTime);
                        }
                        // Process in 1-unit increments, checking for new Q1 arrivals
                        while (remainingBurst > 0) {
                            currentTime++; // Increment the current time
                            remainingBurst--; // Decrease the remaining burst time
            
                            // Check if a new Q1 process has arrived
                            if (!q1.isEmpty() && q1.get(0).getArrivalTime() <= currentTime) {
                                // If preempted, reinsert with updated burst time
                                shortestJob.setBurstTime(remainingBurst); // Update burst time
                                q2.add(0, shortestJob); // Reinsert at the front of Q2
                                schedulingOrder0.add(shortestJob); // Add to scheduling order
                                break; // Preempt to process Q1
                            }
                        }
            
                        if (remainingBurst <= 0) {
                            // If the burst is completed, set termination time and add to scheduled processes
                            shortestJob.setTerminationTime(currentTime);
                            schedulingOrder0.add(shortestJob); // Add to scheduling order
                            schedulingProcesses.add(shortestJob); // Add to scheduled processes if completed
                        }
                    }
                    else{
                        // If no processes are ready, move currentTime to next arrival time
                    currentTime = Math.max(currentTime, allProcesses.get(0).getArrivalTime());
                    }
                
            }
            

        } }

    private static PCB getShortestJob(List<PCB> queue, int currentTime) {
        if (queue.isEmpty()) {
            return null; // Return null if the queue is empty
        }
    
        PCB shortestJob = null;
    
        // Loop through all processes in the queue
        for (PCB process : queue) {
            // Consider only those processes whose arrival time is <= currentTime
            if (process.getArrivalTime() <= currentTime) {
                // If this is the first valid process or has a shorter burst, set it as the shortest job
                if (shortestJob == null || process.getBurstTime() < shortestJob.getBurstTime()) {
                    shortestJob = process; // Update the shortest job
                }
            }
        }
    
        if (shortestJob != null) {
            queue.remove(shortestJob); // Remove the selected shortest job from the queue
        }
    
        return shortestJob; // Return the shortest job with arrival time <= currentTime
    }
    

    private static PCB getFirstProcessReady(List<PCB> queue, int currentTime) {
        // Loop over the queue to find the first process ready for execution
        for (PCB process : queue) {
            if (process.getArrivalTime() <= currentTime) {
                return process; // Return the first process that meets the condition
            }
        }
        return null; // Return null if no process is ready
    }

    private static void reportDetailedInformation() {
    try (FileWriter writer = new FileWriter("Report.txt")) {
        schedule(); // Schedule processes before reporting

        // Output scheduling order
        StringBuilder schedulingOrder = new StringBuilder("\nScheduling Order:\n[");
        for (PCB process : schedulingOrder0) {
            schedulingOrder.append("P").append(process.getProcessID()).append(" | ");
        }
        schedulingOrder.delete(schedulingOrder.length() - 3, schedulingOrder.length());
        schedulingOrder.append("]");
        
        String schedulingOrderString = schedulingOrder.toString();
        writer.write(schedulingOrder.toString() +"\n---------------------------------------- \n");
        writer.write("info :\n");
        
        System.out.println(schedulingOrderString);
        System.out.println("----------------------------------------");
        System.out.println("info :");

        // Ensure termination time is set before calculating turnaround and waiting times
        for (PCB process : schedulingProcesses) {
            if (process.getTerminationTime() == -1) {
                process.setTerminationTime(currentTime);
            }

            process.setTurnaroundTime(process.calculateTurnaroundTime()); // Set turnaround time
            int index = Collections.binarySearch(sTurnaroundTime, process, Comparator.comparingInt(PCB::getTurnaroundTime));
            if (index < 0) {
            index = -(index + 1); // Adjust index for insertion point
            }
            sTurnaroundTime.add(index, process);
            
            // Set waiting time and add process to the list
            process.setWaitingTime(process.calculateWaitingTime());
            if (sWaitingTime.isEmpty()) {
            sWaitingTime.add(process); // If the list is empty, add the process directly
            } else {
            // Find the correct position to insert the process based on waiting time
            int index1 = 0;
            while (index1 < sWaitingTime.size() && sWaitingTime.get(index1).getWaitingTime() < process.getWaitingTime()) {
            index1++;
            }
            // Insert the process at the correct position
            sWaitingTime.add(index1, process);
            }

            
            
            process.setResponseTime(process.calculateResponseTime()); // Set response time
            sResponseTime.add(process);
            Collections.sort(sResponseTime, Comparator.comparingInt(PCB::getResponseTime));


            // Write process details to the report
            writer.write("Process ID: " + process.getProcessID()+", Priority: "+process.getPriority()+", Arrival Time: " + process.getArrivalTime()+", Cpu Burst Time: " + process.getCpuBurstTime()
            +", Start Time: " + process.getStartTime()+", Termination Time: " + process.getTerminationTime()+", Turnaround Time: " + process.getTurnaroundTime()
            +", Waiting Time: " + process.getWaitingTime()+", Response Time: " + process.getResponseTime()+"\n");
            
            writer.write("----------------------------------------\n");

            // Print process details to terminal
            System.out.println("Process ID: " + process.getProcessID()+", Priority: "+process.getPriority()+", Arrival Time: " + process.getArrivalTime()+", Cpu Burst Time: " + process.getCpuBurstTime()
            +", Start Time: " + process.getStartTime()+", Termination Time: " + process.getTerminationTime()+", Turnaround Time: " + process.getTurnaroundTime()
            +", Waiting Time: " + process.getWaitingTime()+", Response Time: " + process.getResponseTime() );
            System.out.println("----------------------------------------");
            
        }

        double totalTurnaroundTime = 0;
        double totalWaitingTime = 0;
        double totalResponseTime = 0;

        // Calculate total turnaround, waiting, and response times for averaging
        for (PCB process : schedulingProcesses) {
            totalTurnaroundTime += process.getTurnaroundTime();
            totalWaitingTime += process.getWaitingTime();
            totalResponseTime += process.getResponseTime();
        }

        double averageTurnaroundTime = totalTurnaroundTime / numProcesses ;
        double averageWaitingTime = totalWaitingTime / numProcesses;
        double averageResponseTime = totalResponseTime / numProcesses;


        writer.write("Average Turnaround Time: " + averageTurnaroundTime + "\n");
        writer.write("Average Waiting Time: " + averageWaitingTime + "\n");
        writer.write("Average Response Time: " + averageResponseTime + "\n");
        writer.write("----------------------------------------\n");

        // Print averages to terminal
        
        System.out.println("Average Turnaround Time: " + averageTurnaroundTime);
        System.out.println("Average Waiting Time: " + averageWaitingTime);
        System.out.println("Average Response Time: " + averageResponseTime);
        System.out.println("----------------------------------------");
        
        // Output scheduling Turnaround Time
        StringBuilder schedulingOrder1 = new StringBuilder("Scheduling by Turnaround Time:\n[");
        for (PCB process :  sTurnaroundTime) {
            schedulingOrder1.append("P").append(process.getProcessID()).append(" | ");
        }
        schedulingOrder1.delete(schedulingOrder1.length() - 3, schedulingOrder1.length());
        schedulingOrder1.append("]");
        
        String schedulingOrderString1 = schedulingOrder1.toString();
        writer.write(schedulingOrder1.toString() +"\n");
            
        System.out.println(schedulingOrderString1);
        
        
        // Output scheduling WaitingTime
        StringBuilder schedulingOrder2 = new StringBuilder("\nScheduling by Waiting Time:\n[");
        for (PCB process :  sWaitingTime) {
            schedulingOrder2.append("P").append(process.getProcessID()).append(" | ");
        }
        schedulingOrder2.delete(schedulingOrder2.length() - 3, schedulingOrder2.length());
        schedulingOrder2.append("]");
        
        String schedulingOrderString2 = schedulingOrder2.toString();
        writer.write(schedulingOrder2.toString() +"\n");
            
        System.out.println(schedulingOrderString2);
        
        
        // Output scheduling Response Time
        StringBuilder schedulingOrder3 = new StringBuilder("\nScheduling by Response Time:\n[");
        for (PCB process :  sResponseTime) {
            schedulingOrder3.append("P").append(process.getProcessID()).append(" | ");
        }
        schedulingOrder3.delete(schedulingOrder3.length() - 3, schedulingOrder3.length());
        schedulingOrder3.append("]");
        
        String schedulingOrderString3 = schedulingOrder3.toString();
        writer.write(schedulingOrder3.toString() +"\n");
            
        System.out.println(schedulingOrderString3);
       
   
  

        System.out.println("----------------------------------------");
        System.out.println("Report generated successfully and saved to Report.txt\n");
        
    } catch (IOException e) {
        System.out.println("Error writing to file: " + e.getMessage());
    }
}
}