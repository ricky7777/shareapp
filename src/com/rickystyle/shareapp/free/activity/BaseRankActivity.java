package com.rickystyle.shareapp.free.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.rickystyle.shareapp.free.R;
import com.rickystyle.shareapp.free.adapter.ShareAppAdapter;
import com.rickystyle.shareapp.free.consts.ShareAppConsts;
import com.rickystyle.shareapp.free.net.NetManager;
import com.rickystyle.shareapp.free.tool.StringUtils;
import com.rickystyle.shareapp.free.widget.ToggleButton;

/**
 * 因為Rank類的activity取資料的方式都一樣,只是發request的參數不一樣<br/>
 * 所以讓rank類的class去extends 這隻base class
 * 
 * @author Ricky
 */
abstract public class BaseRankActivity extends BaseActivity {
    static ProgressDialog     loadDialog;
    ListView                  appList;
    List<Map<String, Object>> appData  = new ArrayList<Map<String, Object>>();
    String[]                  dataFrom = new String[] { ShareAppConsts.KEY_RANK_APPNAME, ShareAppConsts.KEY_RANK_CREATOR, ShareAppConsts.KEY_RANK_SHARETIME };
    int[]                     dataTo   = new int[] { R.id.TV_shareapprank_name, R.id.TV_shareapprank_creator, R.id.TV_shareapprank_sharetime };
    int                       selectPosition;
    String                    appType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notifyDialogShow();
        // do nothing,交給繼承他的class去實作
    }

    @Override
    protected void onStart() {
        super.onStart();
        final ToggleButton appBtn = (ToggleButton) findViewById(R.id.IB_application);
        final ToggleButton gameBtn = (ToggleButton) findViewById(R.id.IB_game);
        if (!gameBtn.isChecked())
            appBtn.setChecked(true);

        appBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameBtn.setChecked(false);
                notifyDialogShow();
                appData = new ArrayList<Map<String, Object>>();
                appType = ShareAppConsts.APPTYPE_APPLICATION;
                NetManager.getInstance().getShareAppRankBoard(handler, ShareAppConsts.APPTYPE_APPLICATION, ShareAppConsts.DEFALT_RANGE_VALUE);
            }
        });

        gameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBtn.setChecked(false);
                notifyDialogShow();
                appData = new ArrayList<Map<String, Object>>();
                appType = ShareAppConsts.APPTYPE_GAME;
                NetManager.getInstance().getShareAppRankBoard(handler, ShareAppConsts.APPTYPE_GAME, ShareAppConsts.DEFALT_RANGE_VALUE);
            }
        });
    }

    protected Handler handler = new Handler() {
                                  @Override
                                  public void handleMessage(Message msg) {
                                      super.handleMessage(msg);
                                      switch (msg.what) {
                                      case ShareAppConsts.NET_GET_SHAREAPPBOARD_SUCCESS:
                                          makeAppData();
                                          RankAdapter adapter = new RankAdapter(BaseRankActivity.this, appData, R.layout.shareapprankitem, dataFrom, dataTo);
                                          appList.setAdapter(adapter);
                                          if (selectPosition != 0)
                                              appList.setSelection(selectPosition);

                                          notifyDialogDismiss();
                                          break;
                                      }
                                  }
                              };

    public void makeAppData() {
        String shareTimeText1 = getString(R.string.sharerank_time_text1);
        String shareTimeText2 = getString(R.string.sharerank_time_text2);

        JSONArray data = ShareAppAdapter.getInstance().getBillBoardData();
        for (int i = 0; i < data.length(); i++) {
            HashMap<String, Object> tempMap = new HashMap<String, Object>();
            JSONObject row = null;
            byte[] iconData = null;
            String apName = "";
            String rank = "";
            String packageName = "";
            String creator = "";
            String sharetime = "";
            try {
                row = (JSONObject) data.get(i);
                JSONObject iconObj = (JSONObject) row.get(ShareAppConsts.KEY_RANK_ICONDATA);
                JSONArray tmpIconObj = (JSONArray) iconObj.get(ShareAppConsts.KEY_RANK_ICONDATA_BYTES);
                iconData = new byte[tmpIconObj.length()];
                for (int j = 0; j < tmpIconObj.length(); j++) {
                    iconData[j] = Byte.parseByte(tmpIconObj.get(j).toString());
                }
                apName = row.getString(ShareAppConsts.KEY_RANK_APPNAME);
                rank = row.getString(ShareAppConsts.KEY_RANK_RANK);
                packageName = row.getString(ShareAppConsts.KEY_RANK_PACKAGENAME);
                creator = row.getString(ShareAppConsts.KEY_RANK_CREATOR);
                sharetime = shareTimeText1 + " " + row.getString(ShareAppConsts.KEY_RANK_SHARETIME) + " " + shareTimeText2;
            } catch (JSONException e) {
            }
            tempMap.put(ShareAppConsts.KEY_RANK_ICONDATA, iconData);
            tempMap.put(ShareAppConsts.KEY_RANK_APPNAME, apName);
            tempMap.put(ShareAppConsts.KEY_RANK_RANK, rank);
            tempMap.put(ShareAppConsts.KEY_RANK_PACKAGENAME, packageName);
            tempMap.put(ShareAppConsts.KEY_RANK_CREATOR, creator);
            tempMap.put(ShareAppConsts.KEY_RANK_SHARETIME, sharetime);
            appData.add(tempMap);
        }
    }

    class RankAdapter extends SimpleAdapter {
        public RankAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = super.getView(position, convertView, parent);
            }

            ImageView iconView = (ImageView) view.findViewById(R.id.IV_sharapprank_icon);
            TextView appNameView = (TextView) view.findViewById(R.id.TV_shareapprank_name);
            TextView creatorView = (TextView) view.findViewById(R.id.TV_shareapprank_creator);
            TextView shareTimeView = (TextView) view.findViewById(R.id.TV_shareapprank_sharetime);

            RatingBar ratingBar = (RatingBar) view.findViewById(R.id.RB_shareapprank_ratingbar);
            String appName = (String) appData.get(position).get(ShareAppConsts.KEY_RANK_APPNAME);
            String creator = (String) appData.get(position).get(ShareAppConsts.KEY_RANK_CREATOR);
            String shareTime = (String) appData.get(position).get(ShareAppConsts.KEY_RANK_SHARETIME);
            String rank = (String) appData.get(position).get(ShareAppConsts.KEY_RANK_RANK);
            byte[] icon = (byte[]) appData.get(position).get(ShareAppConsts.KEY_RANK_ICONDATA);

            // set name bold
            TextPaint tpaint = appNameView.getPaint();
            tpaint.setFakeBoldText(true);
            // DebugTool.printVLog("rank string:" + rank);
            Bitmap iconBitmap = null;
            if (icon != null) {
                iconBitmap = BitmapFactory.decodeByteArray(icon, 0, icon.length);
            } else {
                iconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_default_loading);
            }
            iconView.setImageBitmap(iconBitmap);

            appNameView.setText(appName);
            creatorView.setText(creator);
            shareTimeView.setText(shareTime);
            try {
                rank = rank.substring(0, rank.indexOf(".") + 2);
                // DebugTool.printILog("rank:" + rank);
                ratingBar.setRating(Float.parseFloat(rank));
            } catch (Exception e) {
                ratingBar.setRating(3);
            }

            int count = getCount();
            if (position == count - 1 && count % 10 == 0) {
                selectPosition = position - 4;
                notifyDialogShow();
                if (StringUtils.isBlankorNull(appType)) {
                    appType = ShareAppConsts.APPTYPE_APPLICATION;
                }
                NetManager.getInstance().getShareAppRankBoard(handler, appType, count + "");
            }
            return view;
        }
    }

    public void notifyDialogShow() {
        loadDialog = ProgressDialog.show(this, "", getString(R.string.alert_dialog_loading));
        loadDialog.setCancelable(true);
        loadDialog.show();
    }

    public static void notifyDialogDismiss() {
        if (loadDialog.isShowing()) {
            loadDialog.cancel();
        }
    }
}
