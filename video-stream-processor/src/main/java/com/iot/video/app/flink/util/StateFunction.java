package com.iot.video.app.flink.util;

import com.iot.video.app.flink.processor.VideoMotionDetector;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;

public class StateFunction extends RichMapFunction<VideoEventStringData, VideoEventStringData> {

    private transient ValueState<VideoEventStringData> state;
    final String processedImageDir = "/tmp/processed-data/";

    @Override
    public VideoEventStringData map(VideoEventStringData in) throws Exception {
        VideoEventStringData existingState = null;


        if (state!=null){
            existingState = state.value();
        }

        List<VideoEventStringData> it = new ArrayList<VideoEventStringData>();
        it.add(in);

        VideoEventStringData processedState = VideoMotionDetector.detectMotion("vid-01",it.iterator(),processedImageDir,existingState);

        if (processedState!= null){
            state.update(processedState);
        }
        return processedState;

    }

    public void open(Configuration config) {
        ValueStateDescriptor<VideoEventStringData> descriptor =
                new ValueStateDescriptor<>(
                        "prev_state",
                        VideoEventStringData.class);
        state = getRuntimeContext().getState(descriptor);
    }


}
