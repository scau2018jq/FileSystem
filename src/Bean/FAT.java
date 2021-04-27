package Bean;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Utiliy.FATUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 文件分配表类，包含管理盘块、文件、路径等相关操作
 * @author w
 *
 */

public class FAT implements Serializable{

	private static final long serialVersionUID = 1L;//版本控件，可序列化类


	private DiskBlock[] diskBlocks;//磁盘块
	private transient ObservableList<File> openedFiles;//打开的文件
	private Folder c;//c盘
	private Path rootPath = new Path("C:", null);//根路径
	private List<Path> paths;//路径

	public FAT() {
		c = new Folder("C:", "root", 0, null);
		diskBlocks = new DiskBlock[128];
		diskBlocks[0] = new DiskBlock(0, FATUtil.END, FATUtil.DISK, c);//磁盘的第 0 块被系统数据占用
		diskBlocks[0].setBegin(true);
		diskBlocks[1] = new DiskBlock(1, FATUtil.END, FATUtil.DISK, c);
		for (int i = 2; i < 128; i++) {
			diskBlocks[i] = new DiskBlock(i, FATUtil.FREE, FATUtil.EMPTY, null);
		}
		openedFiles = FXCollections.observableArrayList(new ArrayList<File>());
		paths = new ArrayList<Path>();
		paths.add(rootPath);
		c.setPath(rootPath);
	}

	/**
	 * 被打开的文件
	 * @param block
	 */
	public void addOpenedFile(DiskBlock block) {
		File thisFile = (File) block.getObject();
		openedFiles.add(thisFile);
		thisFile.setOpened(true);
	}

	/**
	 * 关闭打开的文件
	 * @param block
	 */
	public void removeOpenedFile(DiskBlock block) {
		File thisFile = (File) block.getObject();
		for (int i = 0; i < openedFiles.size(); i++) {
			if (openedFiles.get(i) == thisFile) {
				openedFiles.remove(i);
				thisFile.setOpened(false);
				break;
			}
		}
	}

	public void closeOpenedFile(DiskBlock block) {
		File thisFile = (File) block.getObject();
		thisFile.setOpened(false);
	}

	/**
	 * 判断指定盘块中的文件是否已打开
	 * @param block
	 * @return
	 */
	public boolean isOpenedFile(DiskBlock block) {
		if (block.getObject() instanceof Folder) {
			return false;
		}
		return ((File) block.getObject()).isOpened();
	}

	/**
	 * 在指定路径下创建文件夹
	 * @param path当前路径
	 * @return分配的块索引
	 */
	public int createFolder(String path) {
		String folderName = null;
		boolean canName = true;
		int index = 1;
		// 得到文件夹名
		do {
			folderName = "文件夹";
			canName = true;
			folderName += index;

			//判断改文件名是否已经存在
			for (int i = 2; i < diskBlocks.length; i++) {
				if (!diskBlocks[i].isFree()) {
					if (diskBlocks[i].getType().equals(FATUtil.FOLDER)) {
						Folder folder = (Folder) diskBlocks[i].getObject();
						if (path.equals(folder.getLocation())) {
							if (folderName.equals(folder.getFolderName())) {
								canName = false;
							}
						}
					}
				}
			}
			index++;
		} while (!canName);
		int index2 = searchEmptyDiskBlock();
		//块是否损坏
		if (index2 == FATUtil.ERROR) {
			return FATUtil.ERROR;
		} else {
			Folder parent = getFolder(path);//父文件夹
			Folder folder = new Folder(folderName, path, index2, parent);
			//添加到父文件夹里
			if (parent instanceof Folder) {
				parent.addChildren(folder);
			}
			//分配空间
			diskBlocks[index2].allocBlock(FATUtil.END, FATUtil.FOLDER, folder, true);
			//该文件夹的路径
			Path parentP = getPath(path);
			Path thisPath = new Path(path + "\\" + folderName, parentP);
			if (parentP != null) {
				parentP.addChildren(thisPath);
			}
			paths.add(thisPath);
			folder.setPath(thisPath);
		}
		return index2;
	}

	/**
	 * 在指定路径下创建文件
	 * @param path
	 * @return
	 */
	public int createFile(String path) {
		String fileName = null;
		boolean canName = true;
		int index = 1;
		// 得到文件名
		do {
			fileName = "文件";
			canName = true;
			fileName += index;
			//判断文件名是否存在
			for (int i = 2; i < diskBlocks.length; i++) {
				if (!diskBlocks[i].isFree()) {
					if (diskBlocks[i].getType().equals(FATUtil.FILE)) {
						File file = (File) diskBlocks[i].getObject();
						if (path.equals(file.getLocation())) {
							if (fileName.equals(file.getFileName())) {
								canName = false;
							}
						}
					}
				}
			}
			index++;
		} while (!canName);
		int index2 = searchEmptyDiskBlock();
		if (index2 == FATUtil.ERROR) {
			return FATUtil.ERROR;
		} else {
			Folder parent = getFolder(path);
			File file = new File(fileName, path, index2, parent);
			file.setFlag(FATUtil.FLAGWRITE);
			if (parent instanceof Folder) {
				parent.addChildren(file);
			}
			//分配块
			diskBlocks[index2].allocBlock(FATUtil.END, FATUtil.FILE, file, true);
		}
		return index2;
	}

	/**
	 * 返回第一个空闲盘块的盘块号
	 * @return
	 */
	public int searchEmptyDiskBlock() {
		for (int i = 2; i < diskBlocks.length; i++) {
			if (diskBlocks[i].isFree()) {
				return i;
			}
		}
		return FATUtil.ERROR;
	}

	/**
	 * 计算已使用盘块数
	 * @return
	 */
	public int usedBlocksCount() {
		int n = 0;
		for (int i = 2; i < diskBlocks.length; i++) {
			if (!diskBlocks[i].isFree()) {
				n++;
			}
		}
		return n;
	}

	/**
	 * 计算空闲盘块数
	 * @return
	 */
	public int freeBlocksCount() {
		int n = 0;
		for (int i = 2; i < diskBlocks.length; i++) {
			if (diskBlocks[i].isFree()) {
				n++;
			}
		}
		return n;
	}

	/**
	 * 文件长度变更时重新分配盘块
	 * @param num
	 * @param block
	 * @return
	 */
	public boolean reallocBlocks(int num, DiskBlock block) {
		File thisFile = (File) block.getObject();
		int begin = thisFile.getDiskNum();
		int index = diskBlocks[begin].getIndex();
		int oldNum = 1;
		//计算文件的长度
		while (index != FATUtil.END) {
			oldNum++;
			if (diskBlocks[index].getIndex() == FATUtil.END) {
				begin = index;
			}
			index = diskBlocks[index].getIndex();
		}

		if (num > oldNum) {
			// 增加磁盘块
			int n = num - oldNum;
			if (freeBlocksCount() < n) {
				// 超过磁盘容量
				return false;
			}
			int space = searchEmptyDiskBlock();
			diskBlocks[begin].setIndex(space);
			for (int i = 1; i <= n; i++) {
				space = searchEmptyDiskBlock();
				if (i == n) {
					diskBlocks[space].allocBlock(FATUtil.END, FATUtil.FILE, thisFile, false);
				} else {
					diskBlocks[space].allocBlock(FATUtil.END, FATUtil.FILE, thisFile, false);// 同一个文件的所有磁盘块拥有相同的对象
					int space2 = searchEmptyDiskBlock();
					diskBlocks[space].setIndex(space2);
				}
				System.out.println(thisFile);
			}
		} else if (num < oldNum) {
			// 减少磁盘块
			int end = thisFile.getDiskNum();
			while (num > 1) {
				end = diskBlocks[end].getIndex();
				num--;
			}
			int next = 0;
			for (int i = diskBlocks[end].getIndex(); i != FATUtil.END; i = next) {
				next = diskBlocks[i].getIndex();
				diskBlocks[i].clearBlock();
			}
			diskBlocks[end].setIndex(FATUtil.END);
		} else {
			// 不变
		}
		thisFile.setLength(num);
		return true;
	}

	/**
	 * 返回指定路径下所有文件夹
	 * @param path
	 * @return
	 */
	public List<Folder> getFolders(String path) {
		List<Folder> list = new ArrayList<Folder>();
		for (int i = 2; i < diskBlocks.length; i++) {
			if (!diskBlocks[i].isFree()) {
				if (diskBlocks[i].getObject() instanceof Folder) {
					if (((Folder) (diskBlocks[i].getObject())).getLocation().equals(path)) {
						list.add((Folder) diskBlocks[i].getObject());
					}
				}
			}
		}
		return list;
	}

	/**
	 * 返回指定路径所有文件
	 * @param path
	 * @return
	 */
	public List<File> getFiles(String path){
		List<File> list = new ArrayList<File>();
		for (int i = 2; i < diskBlocks.length; i++) {
			if (!diskBlocks[i].isFree()) {
				if (diskBlocks[i].getObject() instanceof File) {
					if (((File) (diskBlocks[i].getObject())).getLocation().equals(path)
							&& diskBlocks[i].isBegin()) {
						list.add((File) diskBlocks[i].getObject());
					}
				}
			}
		}
		return list;
	}

	/**
	 * 返回当前路劲文件和文件夹名
	 * @param Path
	 * @return
	 */
	public List<String> getFilesAndFoldersName(String Path){
		List<String> listName = new ArrayList<String>();
		List<Folder> folder = getFolders(Path);
		List<File> file = getFiles(Path);
		for(Folder f : folder){
			listName.add(f.getFolderName());
		}
		for(File f : file){
			listName.add(f.getFileName());
		}
		return listName;
	}

	/**
	 * 返回指定路径所有文件夹和文件的起始盘块
	 * @param path
	 * @return
	 */
	public List<DiskBlock> getBlockList(String path) {
		List<DiskBlock> bList = new ArrayList<DiskBlock>();
		for (int i = 2; i < diskBlocks.length; i++) {
			if (!diskBlocks[i].isFree()) {
				if (diskBlocks[i].getObject() instanceof Folder) {
					if (((Folder) (diskBlocks[i].getObject())).getLocation().equals(path)
							&& diskBlocks[i].isBegin()) {
						bList.add(diskBlocks[i]);
					}
				}
			}
		}
		for (int i = 2; i < diskBlocks.length; i++) {
			if (!diskBlocks[i].isFree()) {
				if (diskBlocks[i].getObject() instanceof File) {
					if (((File) (diskBlocks[i].getObject())).getLocation().equals(path)
							&& diskBlocks[i].isBegin()) {
						bList.add(diskBlocks[i]);
					}
				}
			}
		}
		return bList;
	}

	/**
	 * 返回指定路径指向的文件夹
	 * @param path
	 * @return
	 */
	public Folder getFolder(String path) {
		if (path.equals("C:")) {
			return c;
		}
		int split = path.lastIndexOf('\\');
		String location = path.substring(0, split);
		String folderName = path.substring(split + 1);
		List<Folder> folders = getFolders(location);
		for (Folder folder : folders) {
			if (folder.getFolderName().equals(folderName)) {
				return folder;
			}
		}
		return null;
	}

	/**
	 * 给出路径名返回路径对象
	 * @param path
	 * @return
	 */
	public Path getPath(String path) {
		for (Path p : paths) {
			if (p.getPathName().equals(path)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * 删除文件或文件夹
	 * @param block
	 * @return
	 */
	public int delete(DiskBlock block) {
		if (block.getObject() instanceof File) {
			if (isOpenedFile(block)) {
				// 文件已打开，不能删除
				return 3;
			}

			File thisFile = (File) block.getObject();
			Folder parent = thisFile.getParent();
			if (parent instanceof Folder) {
				parent.removeChildren(thisFile);
				parent.setSize(FATUtil.getFolderSize(parent));
				while (parent.hasParent()) {
					parent = parent.getParent();
					parent.setSize(FATUtil.getFolderSize(parent));
				}
			}
			for (int i = 2; i < diskBlocks.length; i++) {
				if (!diskBlocks[i].isFree() && diskBlocks[i].getObject() instanceof File) {
					System.out.println("yes");
					if (((File) diskBlocks[i].getObject()).equals(thisFile)) {// 同一个对象
						System.out.println("yes2");
						diskBlocks[i].clearBlock();
					}
				}
			}
			return 1;//文件删除成功
		} else {
			String folderPath = ((Folder) block.getObject()).getLocation() + "\\"
					+ ((Folder) block.getObject()).getFolderName();
			int index = 0;
			for (int i = 2; i < diskBlocks.length; i++) {
				if (!diskBlocks[i].isFree()) {
					Object obj = diskBlocks[i].getObject();
					if (diskBlocks[i].getType().equals(FATUtil.FOLDER)) {
						if (((Folder) obj).getLocation().equals(folderPath)) {
							// 文件夹不为空，不能删除
							return 2;
						}
					} else {
						if (((File) obj).getLocation().equals(folderPath)) {
							// 文件夹不为空，不能删除
							return 2;
						}
					}
					if (diskBlocks[i].getType().equals(FATUtil.FOLDER)) {
						if (((Folder) diskBlocks[i].getObject()).equals(block.getObject())) {
							index = i;
						}
					}
				}
			}
			Folder thisFolder = (Folder) block.getObject();
			Folder parent = thisFolder.getParent();
			if (parent instanceof Folder) {
				parent.removeChildren(thisFolder);
				parent.setSize(FATUtil.getFolderSize(parent));
			}
			paths.remove(getPath(folderPath));
			diskBlocks[index].clearBlock();
			return 0;//文件夹删除成功
		}
	}

	public DiskBlock[] getDiskBlocks() {
		return diskBlocks;
	}

	public void setDiskBlocks(DiskBlock[] diskBlocks) {
		this.diskBlocks = diskBlocks;
	}

	/**
	 * 按盘块号查找盘块
	 * @param index
	 * @return
	 */
	public DiskBlock getBlock(int index) {
		return diskBlocks[index];
	}

	public ObservableList<File> getOpenedFiles() {
		return openedFiles;
	}

	public void setOpenedFiles(ObservableList<File> openFiles) {
		this.openedFiles = openFiles;
	}

	public List<Path> getPaths() {
		return paths;
	}

	public void setPaths(List<Path> paths) {
		this.paths = paths;
	}

	public void addPath(Path path) {
		paths.add(path);
	}

	/**
	 * s删除路径
	 * @param path
	 */
	public void removePath(Path path) {
		paths.remove(path);
		if (path.hasParent()) {
			path.getParent().removeChildren(path);
		}
	}

	/**
	 * 修改路径
	 * @param oldPath
	 * @param newName
	 */
	public void replacePath(Path oldPath, String newName) {
		oldPath.setPathName(newName);
	}

	/**
	 * 判断路径是否存在
	 * @param path
	 * @return
	 */
	public boolean hasPath(Path path) {
		for (Path p : paths) {
			if (p.equals(path)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断指定路径下是否有同名文件夹或文件
	 * @param path
	 * @param name
	 * @return
	 */
	public boolean hasName(String path, String name) {
		Folder thisFolder = getFolder(path);
		for (Object child : thisFolder.getChildren()) {
			if (child.toString().equals(name)) {
				return true;
			}
		}
		return false;
	}

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    	s.defaultReadObject();
		openedFiles = FXCollections.observableArrayList(new ArrayList<File>());
    }



}
