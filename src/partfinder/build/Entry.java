package partfinder.build;

public class Entry {
	public Entry(int level, Object data) {
		this.treeLevel = level;
		this.data = data;
	}

	public int treeLevel;
	public Object data;

	public String toString() {
		return this.treeLevel + ":" + this.data;
	}

	public Object getData() {
		return data;
	}

}