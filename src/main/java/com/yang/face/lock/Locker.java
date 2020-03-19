package com.yang.face.lock;

import com.yang.face.constant.Constants;
import com.yang.face.service.yun.LockerMgrUtil;
import com.yang.face.service.yun.SubSystemLocker;
import com.yang.face.util.DateTimeUtil;

/**
 * @author yangyuyang
 * @date 2020/3/19 14:35
 */
public class Locker {

    //point：教室数量, 值为0 时是，无教室
    public static SubSystemLocker LOCKER = null;

    public int checkLocker() {
        getLock();

        if (LOCKER == null) {
            return -1;
        }

        if (LOCKER.getResult() < 0) {
            return LOCKER.getResult();
        }

        //试用版
        if (LOCKER.getYear() == 0 && LOCKER.getMonth() == 0 && LOCKER.getDay() == 0) {
            return LOCKER.getResult();
        }

        Integer dateInt = LOCKER.getYear() * 10000 + LOCKER.getMonth() * 100 + LOCKER.getDay();
        if (dateInt <= DateTimeUtil.getCurrentDateToInt())
        {
            return -1;
        }

        return LOCKER.getResult();
    }

    public SubSystemLocker getLock() {
        if (LOCKER == null || LOCKER.getResult() < 0 || LOCKER.getPoint() == 0) {
            LOCKER = new LockerMgrUtil().getLocker(Constants.SYS_LOCK_ID);
        }

        return LOCKER;
    }

}

