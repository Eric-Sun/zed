package com.j13.zed.dz;

import android.content.Context;
import android.os.Message;

import com.j13.zed.R;
import com.j13.zed.api.DZListRequest;
import com.j13.zed.api.DZResponse;
import com.j13.zed.api.ZedResponse;
import com.j13.zed.util.http.InternetUtil;
import com.j13.zed.util.thread.CustomThreadPool;
import com.michael.corelib.internet.core.NetWorkException;
import com.michael.corelib.internet.core.util.JsonUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

                    ZedResponse response = JsonUtils.parse(json, ZedResponse.class);

                    for (int i = 0; i < response.data.list.length; i++) {
                        DZResponse dzResponse = response.data.list[i];
                        DZInfo dzInfo = new DZInfo();
                        dzInfo.setContent(dzResponse.content);
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
