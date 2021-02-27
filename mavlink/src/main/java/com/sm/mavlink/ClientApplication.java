package com.sm.mavlink;

import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavAutopilot;
import io.dronefleet.mavlink.common.MavState;
import io.dronefleet.mavlink.common.MavType;

import java.io.IOException;

public class ClientApplication {
//    public static void main(String[] args) throws IOException {
//
//        MavlinkConnection connection = MavlinkConnection.create(in, out); // InputStream, OutputStream
//        MavlinkMessage message;
//        while ((message = connection.next()) != null) {
//            // ...
//        }
//
//// Writing
//        connection.send1(
//                255, /* systemId */
//                0, /* componentId */
//                Heartbeat.builder()
//                        .type(MavType.MAV_TYPE_GCS)
//                        .autopilot(MavAutopilot.MAV_AUTOPILOT_INVALID)
//                        .systemStatus(MavState.MAV_STATE_UNINIT)
//                        .mavlinkVersion(3)
//                        .build());
//    }
}
