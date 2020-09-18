/*******************************************************************************
 * Copyright (C) 2020, Massimiliano Ziccardi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package it.jnrpe.services.network.netty.protocol.v2;

import it.jnrpe.engine.services.commands.ExecutionResult;
import java.util.Arrays;

public class NRPEV2Response extends NRPEV2AbstractPacket {
  public NRPEV2Response(ExecutionResult result) {
    super(
        2,
        0,
        result.getStatus().ordinal(),
        messageToBuffer(result.getMessage()),
        new byte[] {0, 0});
    updateCRC();
  }

  private static byte[] messageToBuffer(String msg) {
    byte[] buffer = Arrays.copyOf(msg.getBytes(), 1024);
    buffer[Math.min(1023, msg.length())] = 0;
    return buffer;
  }
}