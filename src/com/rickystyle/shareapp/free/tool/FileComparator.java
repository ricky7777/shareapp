package com.rickystyle.shareapp.free.tool;

import java.io.File;
import java.util.Comparator;

import android.content.pm.ApplicationInfo;

/**
 * 檔案日期排序,最新的日期會排最上面
 * @author Ricky
 */
public class FileComparator implements Comparator<Object> {
    public FileComparator() {
    }

    public int compare(Object obj1, Object obj2) {
	String info1Path = ((ApplicationInfo) obj1).dataDir;
	String info2Path = ((ApplicationInfo) obj2).dataDir;
	File f1 = new File(info1Path);
	File f2 = new File(info2Path);
	long c1 = f1.lastModified();
	long c2 = f2.lastModified();
	// DebugTool.printVLog("c1:" + c1 + ",c2:" + c2);
	if (c1 > c2) {
	    return -1;
	} else if (c1 < c2) {
	    return 1;
	}
	return 0;

    }
}
