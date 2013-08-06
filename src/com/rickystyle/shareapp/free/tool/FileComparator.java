package com.rickystyle.shareapp.free.tool;

import java.util.Comparator;
import java.util.Date;

import com.rickystyle.shareapp.free.bean.AppInfoBean;

/**
 * 檔案日期排序,最新的日期會排最上面
 * 
 * @author Ricky
 */
public class FileComparator implements Comparator<Object> {
    public FileComparator() {
    }

    public int compare(Object obj1, Object obj2) {
        Date app1 = ((AppInfoBean) obj1).getLastLaunchtime();
        Date app2 = ((AppInfoBean) obj2).getLastLaunchtime();
        // String info1Path = ((AppInfoBean) obj1).getLastLaunchtime();
        // String info2Path = ((AppInfoBean) obj2).dataDir;
        // File f1 = new File(info1Path);
        // File f2 = new File(info2Path);
        long c1 = app1.getTime();
        long c2 = app2.getTime();
        // DebugTool.printVLog("c1:" + c1 + ",c2:" + c2);
        if (c1 > c2) {
            return -1;
        } else if (c1 < c2) {
            return 1;
        }
        return 0;

    }
}
