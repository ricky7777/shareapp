package com.rickystyle.shareapp.free.bean;

import java.util.ArrayList;
import java.util.List;

/**
 *bookmarks,存放著很多筆bookmark
 * @author Ricky
 */
public class BookMarks {
    public List<BookmarkInfo> bookmarkList;

    public BookMarks() {
	bookmarkList = new ArrayList<BookmarkInfo>();
    }

    // 記得加delete method

    public void addBookmarkInfo(BookmarkInfo bookInfo) {
	bookmarkList.add(bookInfo);
    }

    public List<BookmarkInfo> getBookmarkList() {
	return bookmarkList;
    }

}
