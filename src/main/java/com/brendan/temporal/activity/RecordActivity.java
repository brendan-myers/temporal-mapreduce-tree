package com.brendan.temporal.activity;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface RecordActivity {
    void run(Integer recordId, Integer offset, Integer length);
}
