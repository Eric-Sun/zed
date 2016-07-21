package com.j13.zed.dz;

import java.util.List;

/**
 * Created by sunbo on 16/5/28.
 */
public class DZInfoLoadEvent {

    private List<DZInfo> dzInfoList ;

    public DZInfoLoadEvent(List<DZInfo> dzInfoList) {
        this.dzInfoList = dzInfoList;
    }

    public List<DZInfo> getDzInfoList() {
        return dzInfoList;
    }

    public void setDzInfoList(List<DZInfo> dzInfoList) {
        this.dzInfoList = dzInfoList;
    }
}
