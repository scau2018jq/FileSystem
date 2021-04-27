package Bean;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 路径
 * @author w
 *
 */

public class Path implements Serializable {

	private static final long serialVersionUID = 1L;

	private String pathName;//路径名
	private Path parent;//父节点
	private List<Path> children;//子节点

	public Path(String name, Path parent) {
		this.setPathName(name);
		this.setParent(parent);
		this.children = new ArrayList<Path>();
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public Path getParent() {
		return parent;
	}

	public void setParent(Path parent) {
		this.parent = parent;
	}

	public boolean hasParent() {
		return (parent == null) ? false : true;
	}

	public List<Path> getChildren() {
		return children;
	}

	public void setChildren(List<Path> children) {
		this.children = children;
	}

	public void addChildren(Path child) {
		this.children.add(child);
	}

	public void removeChildren(Path child) {
		this.children.remove(child);
	}

	public boolean hasChild() {
		return children.isEmpty() ? false : true;
	}

	@Override
	public String toString() {
		return "Path [pathName=" + pathName + "]";
	}

}
