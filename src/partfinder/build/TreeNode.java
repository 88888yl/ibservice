package partfinder.build;

import java.io.PrintWriter;
import java.util.List;

public class TreeNode {

	public TreeNode(int level, Object data) {
		this.level = level;
		this.data = data;
	}

	private int level = -1;
	private Object data = null;
	private List<TreeNode> children = null;

	public boolean isTree() {
		if (children != null && !children.isEmpty()) {
			return true;
		}

		return false;

	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

	public void print() {
		System.out.println(this.level);

		if (children != null) {
			for (TreeNode n : children) {
				n.print();
			}
		}

	}

	public void toXML(PrintWriter p) {
		XMLElement d = (XMLElement) data;
		if (isTree()) {
			p.println(d.xmlbegin);
			if (children != null) {
				for (TreeNode n : children) {
					n.toXML(p);
				}
			}
			p.println(d.xmlend);
		} else {

			p.println("  " + d.xmlbody);
		}

	}

	public String toXMLString() {
		XMLElement d = (XMLElement) data;
		StringBuilder sb = new StringBuilder();
		// if (isTree()) {
		sb.append(d.xmlbegin);
		if (children != null) {
			for (TreeNode n : children) {
				// n.toXML(p);
				sb.append(n.toXMLString());
			}
		}
		sb.append(d.xmlend);
		// p.println(d.xmlend);
		// }
		return sb.toString();
	}
}