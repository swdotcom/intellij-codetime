package com.software.codetime.managers;

import swdc.java.ops.manager.FileUtilManager;
import swdc.java.ops.model.FileChangeInfo;

import java.util.HashMap;
import java.util.Map;

public class FileAggregateDataManager {

    public static void clearFileChangeInfoSummaryData() {
        Map<String, FileChangeInfo> fileInfoMap = new HashMap<>();
        FileUtilManager.writeData(FileUtilManager.getFileChangeSummaryFile(), fileInfoMap);
    }
}
