package com.hbbsolution.owner.work_management.view.workmanager;

import com.hbbsolution.owner.work_management.model.workmanager.WorkManagerResponse;
import com.hbbsolution.owner.work_management.model.workmanagerpending.JobPendingResponse;

/**
 * Created by tantr on 5/10/2017.
 */

public interface WorkManagerView {
    void getInfoJob (WorkManagerResponse mExample);
    void getInfoJobPending(JobPendingResponse mJobPendingResponse);
    void getError();
}
