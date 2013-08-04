package com.rickystyle.shareapp.free.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.rickystyle.shareapp.free.R;
import com.rickystyle.shareapp.free.animation.Rotate3dAnimation;
import com.rickystyle.shareapp.free.bean.BookmarkInfo;
import com.rickystyle.shareapp.free.consts.ShareAppConsts;
import com.rickystyle.shareapp.free.tool.BarcodeManager;
import com.rickystyle.shareapp.free.tool.DebugTool;
import com.rickystyle.shareapp.free.tool.IntentTool;
import com.rickystyle.shareapp.free.tool.PreferenceUtil;
import com.rickystyle.shareapp.free.tool.ShareAppTool;
import com.rickystyle.shareapp.free.widget.ShareAppDialog;

/**
 * bookmark主要邏輯<br/>
 * 1.keep住使用者想留下來的app<br/>
 * 2.一樣有分享邏輯
 * 
 * @author Ricky
 */
public class BookmarkActivity extends BaseActivity {
    private AlertDialog.Builder appChooseAlert;
    private SimpleAdapter       shareOptionAdapter;
    private ViewGroup           mContainer;
    private int                 currentPosition;
    BookmarkAdapter             adapter;
    List<Map<String, Object>>   bookmarkData;
    ListView                    bookmarkList;

    String[]                    dataFrom = new String[] { ShareAppConsts.KEY_BOOKMARK_ICONPATH, ShareAppConsts.KEY_BOOKMARK_DESC };
    int[]                       dataTo   = new int[] { R.id.IV_saoption_icon, R.id.TV_saoption_text };
    AnimationDrawable           frameAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bookmark);

        // ready shareOptionAdapter
        String[] optionDataFrom = new String[] { ShareAppConsts.KEY_SHAREAPP_OPTION_ICONID, ShareAppConsts.KEY_SHAREAPP_OPTION_DESC };
        int[] optionDataTo = new int[] { R.id.IV_saoption_icon, R.id.TV_saoption_text };
        List<Map<String, Object>> optionData = ShareAppTool.getShareAppOptionData(getResources(), R.array.shareapp_bookmark_option_text);
        shareOptionAdapter = new SimpleAdapter(this, optionData, R.layout.shareappoptionitem, optionDataFrom, optionDataTo);

        // anim
        mContainer = (ViewGroup) findViewById(R.id.RL_bookmark);
        ImageView animAndroid = (ImageView) findViewById(R.id.IV_android);
        animAndroid.setBackgroundResource(R.anim.anim_android);
        frameAnimation = (AnimationDrawable) animAndroid.getBackground();

        // makeApp choose Alert
        makeAppChooseAlert();

        bookmarkData = getBookmarkData();
        adapter = new BookmarkAdapter(this, bookmarkData, R.layout.shareappbookmarkitem, dataFrom, dataTo);
        bookmarkList = (ListView) findViewById(R.id.LV_bookmarkList);
        bookmarkList.setAdapter(adapter);
        bookmarkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
                currentPosition = position;
                appChooseAlert.show();
            }
        });

        bookmarkList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {
                ShareAppDialog.Builder deleteAlert = new ShareAppDialog.Builder(BookmarkActivity.this);
                deleteAlert.setTitle(R.string.bookmark_delete_title);
                deleteAlert.setMessage(R.string.bookmark_delete_text);
                deleteAlert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String packageName = (String) bookmarkData.get(position).get(ShareAppConsts.KEY_BOOKMARK_PACKAGENAME);
                        PreferenceUtil.removeBookmark(getApplicationContext(), packageName);
                        refreshBookmarkData();
                        Toast.makeText(getApplicationContext(), R.string.bookmark_remove_success, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                deleteAlert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                return false;
            }

        });

        // Since we are caching large views, we want to keep their cache
        // between each animation
        // mContainer.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
        // applyRotation(-1, 180, 90);
    }

    public void makeAppChooseAlert() {
        appChooseAlert = new AlertDialog.Builder(this);
        appChooseAlert.setAdapter(shareOptionAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ShareAppDialog.Builder shareAlert = new ShareAppDialog.Builder(BookmarkActivity.this);
                HashMap<String, Object> info = (HashMap<String, Object>) bookmarkData.get(currentPosition);
                String packageName = info.get(ShareAppConsts.KEY_BOOKMARK_PACKAGENAME) + "";
                String labelName = info.get(ShareAppConsts.KEY_BOOKMARK_DESC) + "";
                DebugTool.printVLog("OPTION_SHAREAPP:" + which);
                switch (which) {
                case ShareAppConsts.OPTION_BOOKMARK_SHAREAPP:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_BOOKMARK, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_SHAREAPP,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    ShareAppTool.shareapp(getResources(), BookmarkActivity.this, packageName, labelName);
                    break;
                case ShareAppConsts.OPTION_BOOKMARK_SHOWQRCODE:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_BOOKMARK, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_DISPLAYQRCODE,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    ShareAppTool.shareQR(BookmarkActivity.this, shareAlert, packageName, labelName);
                    break;
                case ShareAppConsts.OPTION_BOOKMARK_SEARCHMARKET:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_BOOKMARK, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_SEARCHMARKET,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    String url = BarcodeManager.getInstance().getBarcodeURLInMarket(packageName);
                    IntentTool.launchWeb(BookmarkActivity.this, url);
                    break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
                frameAnimation.start();
            }
        });
        t.start();
    }

    public void refreshBookmarkData() {
        adapter = new BookmarkAdapter(this, getBookmarkData(), R.layout.shareappoptionitem, dataFrom, dataTo);
        adapter.notifyDataSetChanged();
        bookmarkList.setAdapter(adapter);
    }

    /**
     * 取得app選項的資料
     * 
     * @return
     */
    private List<Map<String, Object>> getBookmarkData() {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        List<BookmarkInfo> bookmarks = PreferenceUtil.getBookmarks(this);
        for (BookmarkInfo info : bookmarks) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(ShareAppConsts.KEY_BOOKMARK_ICONPATH, info.getAppIconFilePath());
            map.put(ShareAppConsts.KEY_BOOKMARK_DESC, info.getAppName());
            map.put(ShareAppConsts.KEY_BOOKMARK_PACKAGENAME, info.getAppPackageName());
            data.add(map);
        }
        return data;
    }

    /**
     * bookmark的adapter<br/>
     * 可以寫成simpleAdapter,但為了日後調效或擴增,先寫成BookmarkAdapter
     * 
     * @author Ricky
     */
    class BookmarkAdapter extends SimpleAdapter {
        public BookmarkAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView appName = (TextView) view.findViewById(R.id.TV_saoption_text);
            ImageView appIcon = (ImageView) view.findViewById(R.id.IV_saoption_icon);
            appName.setTextColor(Color.WHITE);
            // appIcon.setLayoutParams(new RelativeLayout.LayoutParams(72, 72));
            return view;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean keyDown = super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            keyDown = false;
        }

        return keyDown;
    }

    private void applyRotation(int position, float start, float end) {
        // Find the center of the container
        final float centerX = mContainer.getWidth() / 2.0f;
        final float centerY = mContainer.getHeight() / 2.0f;

        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end, centerX, centerY, 310.0f, true);
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView(position));

        mContainer.startAnimation(rotation);
    }

    private final class DisplayNextView implements Animation.AnimationListener {
        private final int mPosition;

        private DisplayNextView(int position) {
            mPosition = position;
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            mContainer.post(new SwapViews(mPosition));
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    private final class SwapViews implements Runnable {
        private final int mPosition;

        public SwapViews(int position) {
            mPosition = position;
        }

        public void run() {
            final float centerX = mContainer.getWidth() / 2.0f;
            final float centerY = mContainer.getHeight() / 2.0f;
            Rotate3dAnimation rotation;

            if (mPosition > -1) {
                bookmarkList.setVisibility(View.GONE);
                // mImageView.setVisibility(View.VISIBLE);
                // mImageView.requestFocus();

                rotation = new Rotate3dAnimation(90, 180, centerX, centerY, 310.0f, false);
            } else {
                // mImageView.setVisibility(View.GONE);
                bookmarkList.setVisibility(View.VISIBLE);
                bookmarkList.requestFocus();

                rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false);
            }

            rotation.setDuration(500);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());

            mContainer.startAnimation(rotation);
        }
    }

    @Override
    protected String getGoogleAnalyticsAccountId() {
        return ShareAppConsts.ANALYTICS_ID;
    }
}
