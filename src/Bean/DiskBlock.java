package Bean;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import Utiliy.FATUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 磁盘块
 * @author w
 *
 */
public class DiskBlock implements Serializable {

	private static final long serialVersionUID = 1L;//版本控件，可序列化类

	private int no;//盘块号，
	private int index;//255表示结束，0表示空闲，-1表示损坏
	private String type;//当前块存储的类型
	private Object object;//所在的根目录

	private boolean begin;

	private transient StringProperty noP = new SimpleStringProperty();
	private transient StringProperty indexP = new SimpleStringProperty();
	private transient StringProperty typeP = new SimpleStringProperty();
	private transient StringProperty objectP = new SimpleStringProperty();

	/**
	 * UI获取property的方法
	 * @return
	 */
	public StringProperty noPProperty() {
		return noP;
	}
	public StringProperty indexPProperty() {
		return indexP;
	}
	public StringProperty typePProperty() {
		return typeP;
	}
	public StringProperty objectPProperty() {
		return objectP;
	}

	private void setNoP() {
		this.noP.set(String.valueOf(no));
	}
	private void setIndexP() {
		this.indexP.set(String.valueOf(index));
	}
	private void setTypeP() {
		this.typeP.set(type);
	}
	private void setObjectP() {
		this.objectP.set(object == null ? "" : object.toString());
	}

	public DiskBlock(int no, int index, String type, Object object) {
		super();
		this.no = no;
		this.index = index;
		this.type = type;
		this.object = object;
		this.begin = false;
		setNoP();
		setIndexP();
		setTypeP();
		setObjectP();
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
		setNoP();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
		setIndexP();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		setTypeP();
	}

	public Object getObject() {
		return object;
	}


	/**
	 * 需要判断生成的对象是文件还是文件夹
	 */
	public void setObject(Object object) {
		this.object = object;
		if (object instanceof File) {
			this.objectP.bind(((File)object).fileNamePProperty());
		} else if (object instanceof Folder){
			this.objectP.bind(((Folder)object).folderNamePProperty());
		} else {
			this.objectP.unbind();
			setObjectP();
		}
	}

	public boolean isBegin() {
		return begin;
	}

	public void setBegin(boolean begin) {
		this.begin = begin;
	}

	/**
	 * 分配内存块
	 */
	public void allocBlock(int index, String type, Object object, boolean begin) {
		setIndex(index);
		setType(type);
		setObject(object);
		setBegin(begin);
	}

	/**
	 * 清理磁盘
	 */
	public void clearBlock() {
		setIndex(0);
		setType(FATUtil.EMPTY);
		setObject(null);
		setBegin(false);
	}

	/**
	 * 为空
	 */
	public boolean isFree() {
		return index == 0;
	}

	/**
	 *读取对象
	 */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    	s.defaultReadObject();
    	noP = new SimpleStringProperty(String.valueOf(no));
    	indexP = new SimpleStringProperty(String.valueOf(index));
    	typeP = new SimpleStringProperty(type);
    	objectP = new SimpleStringProperty(object == null ? "" : object.toString());
    	setObject(object);
    }

	@Override
	public String toString() {
		Object object = getObject();
		if (object instanceof File) {
			return ((File)object).toString();
		} else {
			return ((Folder)object).toString();
		}
	}

}
