package com.taobao.mrsstd.hiveudf.calculate;

public class Stack<T> {
	public StackNode<T> stackTop;
	public int count;

	public void push(T info) {
		StackNode<T> node = new StackNode<T>();
		node.info = info;
		node.link = stackTop;
		stackTop = node;
		count++;
	}

	public void pop() {
		if (stackTop == null) {
			System.out.println("null stack");
		} else {
			stackTop = stackTop.link;
			count--;
		}

	}

	public boolean isEmpty() {
		return count == 0;
	}

	public T top() {
		if (stackTop == null) {
			return null;
		}
		return stackTop.info;
	}

	public String toString() {
		Stack<T> other = new Stack<T>();
		while (count != 0) {
			T top = top();
			pop();
			other.push(top);
		}
		StringBuffer buf = new StringBuffer();
		while (other.count != 0) {
			buf.append(other.top());
			other.pop();
		}
		return buf.toString();
	}
}