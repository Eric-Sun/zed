package com.j13.zed.dz;

import android.content.Context;

import com.j13.zed.api.dz.DZDataResponse;
import com.j13.zed.api.dz.DZListRequest;
import com.j13.zed.api.dz.DZResponse;
import com.j13.zed.api.InternetUtil;
import com.j13.zed.util.thread.CustomThreadPool;
import com.michael.corelib.internet.core.NetWorkException;
import com.michael.corelib.internet.core.util.JsonUtils;

import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by sunbo on 16/5/28.
 */
public class DZManager {

    private static DZManager instance = null;

    private static Context context = null;

    private DZManager(Context context) {
        this.context = context;

    }

    public static DZManager getInstance(Context context) {
        if (instance == null) {
            synchronized (DZManager.class) {
                if (instance == null) {
                    instance = new DZManager(context);
                }
            }
        }
        return instance;
    }


    public void load() {

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {

                List<DZInfo> dzInfoList = new LinkedList<DZInfo>();
                DZListRequest request = new DZListRequest();
                try {
                    String json = InternetUtil.request(context, request);

                    DZDataResponse response = JsonUtils.parse(json, DZDataResponse.class);

                    for (int i = 0; i < response.data.length; i++) {
                        DZResponse dzResponse = response.data[i];
                        DZInfo dzInfo = new DZInfo();
                        dzInfo.setContent(dzResponse.content);
                        dzInfo.setUserName(dzResponse.userName);
                        dzInfo.setImg(dzResponse.img);
                        dzInfoList.add(dzInfo);
                    }

                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                DZInfoLoadEvent event = new DZInfoLoadEvent(dzInfoList);
                EventBus.getDefault().post(event);

            }
        });
    }

}
