package cn.edu.nju.software.utils;

public class Setting {
	
	public static final String INPUT_FILE_PATH = "input/test5.txt";	//	输入所在的文件路径
	private static int currentTime = 0;								//	当前时间，TaskRunner中的task应该是在currentTime之前或currentTime的时候到达的
	public static final int LOW = -1;								//	-1:表示当前尝试的这个优先级使得任务X的结束时间小于期望值
	public static final int EUQ = 0;								//	0:表示当前尝试的这个优先级使得任务X的结束时间刚好等于期望值
	public static final int HIGH = 1;								//	1:表示当前尝试的这个优先级使得任务X的结束时间大于期望值
	
	public static void increaseTime(){
		currentTime++;
	}
	
	public static int getCurrentTime(){
		return currentTime;
	}
	
	public static void recoverTime(){
		currentTime = 0;
	}
}
