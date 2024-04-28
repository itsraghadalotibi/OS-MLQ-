package os_project;

class PCB {
    private int processID;
    private int priority;
    private int arrivalTime;
    private int burstTime;
    private int startTime;
    private int terminationTime;
    private int turnaroundTime;
    private int waitingTime;
    private int responseTime;
    private int CpuBurstTime;
    

    public PCB(int processID, int priority, int arrivalTime, int burstTime) {
        this.processID = processID;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        // Initialize other attributes to default values
        this.startTime = -1;
        this.terminationTime = -1;
        this.turnaroundTime = 0;
        this.waitingTime = 0;
        this.responseTime = -1;
        this.CpuBurstTime = burstTime;
        
    }

    // Getters and setters for attributes
    public int getProcessID() {
        return processID;
    }

    public void setProcessID(int processID) {
        this.processID = processID;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }
    public int getCpuBurstTime() {
        return CpuBurstTime;
    }


    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
        if (this.responseTime == -1) {
            this.responseTime = startTime - arrivalTime; // Response time set when process starts
        }
    }

    public int getTerminationTime() {
        return terminationTime;
    }

    public void setTerminationTime(int terminationTime) {
        this.terminationTime = terminationTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime= turnaroundTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }
    public void setWaitingTime(int waitingTime) {
        this.waitingTime= waitingTime;
    }
    public int getResponseTime() {
        return responseTime;
    }
    public void setResponseTime(int responseTime) {
        this.responseTime= responseTime;
    }

    public int calculateTurnaroundTime() {
        return terminationTime - arrivalTime;
    }

    // Properly calculate waiting time
    public int calculateWaitingTime() {
        return turnaroundTime - CpuBurstTime;
    }

    // Properly calculate response time
    public int calculateResponseTime() {
        return startTime - arrivalTime;
    }
    public void printDetails() {
        System.out.println("Process ID: " + this.processID);
        System.out.println("Priority: " + this.priority);
        System.out.println("Arrival Time: " + this.arrivalTime);
        System.out.println("CPU Burst Time: " + this.burstTime);
        System.out.println("Start Time: " + this.startTime);
        System.out.println("Termination Time: " + this.terminationTime);
        System.out.println("Turnaround Time: " + this.turnaroundTime);
        System.out.println("Waiting Time: " + this.waitingTime);
        System.out.println("Response Time: " + this.responseTime);
    }

}