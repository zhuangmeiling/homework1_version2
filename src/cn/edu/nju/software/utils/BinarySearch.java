package cn.edu.nju.software.utils;


public class BinarySearch {

	public static int getBinaryObj(int start,int end){
		if(start > end){
			return -1;
		}
		int mid = (start + end) / 2;
		System.out.println("getBinaryObj[start,mid,end]:[" + start + "," + mid + "," + end + "]");
		return mid;
	}
}
