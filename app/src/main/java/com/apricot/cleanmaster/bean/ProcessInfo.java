package com.apricot.cleanmaster.bean;



// TODO: Auto-generated Javadoc
/**

 */
public class ProcessInfo {

	/**
	 * The user id of this process.
	 */
	public String uid;
	
	/** The name of the process that this object is associated with. */
	public String processName;

	/** The pid of this process; 0 if none. */
	public int pid;

	/**  占用的内存 B. */
	public long memory;
	
	/**  占用的CPU. */
	public String cpu;
	
	/**  进程的状态，其中S表示休眠，R表示正在运行，Z表示僵死状态，N表示该进程优先值是负数. */
	public String status;
	
	/**  当前使用的线程数. */
	public String threadsCount;
	
	/**
	 * Instantiates a new ab process info.
	 */
	public ProcessInfo() {
		super();
	}

	/**
	 * Instantiates a new ab process info.
	 *
	 * @param processName the process name
	 * @param pid the pid
	 */
	public ProcessInfo(String processName, int pid) {
		super();
		this.processName = processName;
		this.pid = pid;
	}


}
