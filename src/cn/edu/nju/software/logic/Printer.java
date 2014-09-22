package cn.edu.nju.software.logic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import cn.edu.nju.software.entity.InputUnit;
import cn.edu.nju.software.entity.OutputUnit;
import cn.edu.nju.software.entity.Task;
import cn.edu.nju.software.utils.BinarySearch;
import cn.edu.nju.software.utils.Setting;

public class Printer {
	private boolean isContinuedByThisPriority;
	private int isFoundSolutionByThisPriority;				
	private InputUnit inputUnit;
	private OutputUnit outputUnit;
	private List<Task> allTasks;							//所有任务
	private PriorityQueue<Task> tasksPriorityQueue = null;
	
	public Printer(InputUnit inputUnit,OutputUnit outputUnit){
		init(inputUnit,outputUnit);
	}
	
	private void init(InputUnit inputUnit,OutputUnit outputUnit){
		tasksPriorityQueue = new PriorityQueue<Task>(11, new Comparator<Task>() {
			@Override
			public int compare(Task o1, Task o2) {
				int result = o2.getPriority() - o1.getPriority();
				return result;
			}
		});
		this.inputUnit = inputUnit;
		this.outputUnit = outputUnit;
	}
	
	/**
	 * 打印机执行任务
	 */
	public void run(){
		// 如果不再继续该递归，返回
		if(!isContinuedByThisPriority){
			return;
		}
		
		// 判断最大堆中是否有元素以及allTasks中是否还有元素
		// 如果优先队列中的元素为空，且allTasks中的元素也为空，说明执行完毕，不再继续执行
		if(tasksPriorityQueue.isEmpty()){
			if(allTasks.size() == 0){
				isContinuedByThisPriority = false;
				isFoundSolutionByThisPriority = Setting.EUQ;
			}
		}
		
		// 将当前时间增加1
		Setting.increaseTime();
		// 如果优先队列中的元素为空，但是allTasks还有元素
		if(!tasksPriorityQueue.isEmpty()){
			// 将优先队列队首元素的剩余时间-1
			Task taskMaxPriority = tasksPriorityQueue.poll();
			int taskRemainingTime = taskMaxPriority.getRemainingTime() - 1;
			taskMaxPriority.setRemainingTime(taskRemainingTime);
			if(taskRemainingTime != 0){
				tasksPriorityQueue.add(taskMaxPriority);
			}
			else{
				taskMaxPriority.setFinishTime(Setting.getCurrentTime());
				// 如果当前的任务已经执行完毕的话，就将该任务的finishTime设置了
				boolean isTaskX = inputUnit.setFinishTimeOfTask(taskMaxPriority);
				// 如果找到是未知优先级的任务，且该结束时间不是要求，则赋值新的优先级
				if(isTaskX){
					int isCorrect = isCorrectSolution(taskMaxPriority.getFinishTime());
					System.out.println("run[taskMaxPriority]:" + taskMaxPriority.toString());
					if(isCorrect != Setting.EUQ){
						isContinuedByThisPriority = false;
						isFoundSolutionByThisPriority = isCorrect;
						return;
					}
				}
			}
		}
	
		// 遍历allTasks，看看是否有在当前时间进入的
		checkAllTasks();
		// 继续拿出最大堆中的根元素运行
		run();
	}
	
	
	private void setAllTasks(){
		List<Task> tasksOfInput = inputUnit.getTasks();
		allTasks = new ArrayList<Task>();
		for(Task tmp : tasksOfInput){
			Task newTask = new Task(tmp);
			allTasks.add(newTask);
		}
	}
	
	
	/**
	 * 开始执行任务
	 */
	public int start(){
		List<Integer> taskXPriorityForcast = inputUnit.getTaskXPriorityForcast();
		if(taskXPriorityForcast==null || taskXPriorityForcast.size()==0){
			return -1;
		}
		
		int start = 0;
		int end = taskXPriorityForcast.size() -1;
		int midPos = BinarySearch.getBinaryObj(start, end);
		setAllTasks();
		int objPriority = -1;
		int priority = -1;
		while(midPos != -1){
			isContinuedByThisPriority = true;
			isFoundSolutionByThisPriority = Setting.LOW;
			priority = taskXPriorityForcast.get(midPos);
			System.out.println("start[priority]:" + priority);
			Setting.recoverTime();
			setPriorityOfTaskX(priority);
			checkAllTasks();
			run();
			if(isFoundSolutionByThisPriority == Setting.EUQ){
				objPriority = priority;
				break;
			}
			setAllTasks();
			tasksPriorityQueue.clear();
			if(isFoundSolutionByThisPriority == Setting.LOW){
				midPos = BinarySearch.getBinaryObj(start, midPos - 1);
			}
			else if(isFoundSolutionByThisPriority == Setting.HIGH){
				midPos = BinarySearch.getBinaryObj(midPos + 1, end);
			}
		}
		
		if(objPriority != -1){
			inputUnit.setPriorityOfTaskX(objPriority);
			outputUnit.setPriority(objPriority);
			outputUnit.setFinishTimeOfTasks(inputUnit.getTasks());
			return 0;
		}
		
		return -1;
	
	}
	
	
	/**
	 * @check true
	 * @param priority
	 */
	private void setPriorityOfTaskX(int priority){
		for(Task task : allTasks){
			if(task.getPriority() == -1){
				task.setPriority(priority);
				break;
			}
		}
	}
	

	private void checkAllTasks(){
		List<Task> temp = new ArrayList<Task>();
		for(Task task : allTasks){
			if(Setting.getCurrentTime() == task.getArriveTime()){
				tasksPriorityQueue.add(task);
				temp.add(task);
			}
		}
		for(Task task : temp){
			allTasks.remove(task);
		}
	}
	
	/**
	 * 判断taskX结束的时间点(>,=,<)期望值
	 * @param finishTimeXForcast
	 * @return
	 */
	private int isCorrectSolution(int finishTimeXForcast){
		if(finishTimeXForcast == inputUnit.getTimeOfTaskXFinished())
			return Setting.EUQ;
		if(finishTimeXForcast < inputUnit.getTimeOfTaskXFinished())
			return Setting.LOW;
		return Setting.HIGH;
	}
	
}
