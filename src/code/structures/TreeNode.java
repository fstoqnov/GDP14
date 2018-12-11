package code.structures;
import java.util.ArrayList;
import java.util.Comparator;


public class TreeNode<T> {

	private int level;
	private T element;
	
	public TreeNode(int level, T element) {
		
		this.element = element;
		this.level =level;
	}
	
	public T getElement() {
		return this.element;
	}
	
	public int getLevel() {
		return this.level;
	}



	
	
}
