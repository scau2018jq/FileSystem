package Utiliy;

import java.util.List;

import Bean.File;
import Bean.Folder;

/**
 * FAT工具
 * @author w
 *
 */
public class FATUtil {

	public static final String ICO = "gif/icon.png";//标签
	public static final String FOLDER_IMG = "gif/folder.png";//文件夹
	public static final String FILE_IMG = "gif/file.png";//文件
	public static final String DISK_IMG = "gif/disk.png";//磁盘
	public static final String TREE_NODE_IMG = "gif/node.png";//文件夹小
	public static final String FORWARD_IMG = "gif/forward.png";//前进
	public static final String BACK_IMG = "gif/back.png";//退回
	public static final String SAVE_IMG = "gif/save.png";//保存
	public static final String CLOSE_IMG = "gif/close.png";//关闭
	public static final String CLOSEX_IMG = "gif/closex.png";//关闭小
	public static final String FILEX_IMG = "gif/filex.png";//文件小
	public static final String SMALL_IMG = "/gif/small.png";//最小化
	public static final String RESET_IMG = "/gif/reset.png";//还原
	public static final String SEARCH_IMG = "/gif/search.png";//搜索

	public static final int END = 255;//本实验用255代替-1表示文件结束
	public static final int ERROR = -1;//损坏
	public static final int FREE = 0;//空闲

	/*
	 * 属性
	 */
	public static final String DISK = "磁盘";
	public static final String FOLDER = "文件夹";
	public static final String FILE = "文件";
	public static final String EMPTY = "空";

	/*
	 * 文件状态
	 */
	public static final int FLAGREAD = 0;
	public static final int FLAGWRITE = 1;


	/**
	 * 块数
	 * @param length
	 * @return
	 */
	public static int blocksCount(int length){
		if (length <= 64){
			return 1;
		} else {
			int n = 0;
			if (length % 64 == 0){
				n = length / 64;
			} else {
				n = length / 64;
				n++;
			}
			return n;
		}
	}

	/**
	 * 文件大小
	 * @param length
	 * @return
	 */
	public static double getSize(int length) {
		return Double.parseDouble((String.format("%.2f", length / 1024.0)));
	}


	/**
	 * 文件夹大小
	 * @param folder
	 * @return
	 */
	public static double getFolderSize(Folder folder) {
		List<Object> children = folder.getChildren();
		double size = 0;
		for (Object child : children) {
			if (child instanceof File) {
				size += ((File)child).getSize();
			} else {
				size += getFolderSize((Folder)child);
			}
		}
		return Double.parseDouble((String.format("%.2f", size)));
	}

}
