package cn.com.prime.common.proxy;

import java.util.Stack;

/**
 * 单个线程方法栈持有者
 * @author Rain
 *
 */
public class MethodStackHolder {
	private static ThreadLocal<Stack<String>> threadLocal = new ThreadLocal<Stack<String>>();
	
	public static void push(String object){
		Stack<String> stack = threadLocal.get();
		if(stack==null){
			stack = new Stack<String>();
			threadLocal.set(stack);
		}
		stack.add(object);
	}
	
	public static String pull(){
		Stack<String> stack = (Stack<String>)threadLocal.get();
		if(stack!=null && !stack.isEmpty()){
			return (String)stack.pop();
		}
		return null;
	}
	
	public static boolean isEmpty(){
		Stack<String> stack = (Stack<String>)threadLocal.get();
		if(stack == null|| stack.isEmpty()){
			return true;
		}
		return false;
	}
	
	public static void remove(){
		threadLocal.remove();
	}
	
	public static Stack<String> get(){
		return (Stack<String>)threadLocal.get();
	}
	
	
}
