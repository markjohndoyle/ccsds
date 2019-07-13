/*
 * Copyright 2018-2019 Dario Lucia (https://www.dariolucia.eu)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.dariolucia.ccsds.tmtc.datalink.channel.receiver.processor;

import eu.dariolucia.ccsds.tmtc.datalink.pdu.AbstractTransferFrame;
import eu.dariolucia.ccsds.tmtc.transport.pdu.SpacePacket;
import eu.dariolucia.ccsds.tmtc.util.internal.TransformationListProcessor;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public class VirtualChannelReceiverSpacePacketProcessor<T extends AbstractTransferFrame> extends TransformationListProcessor<T, SpacePacket> {

    public VirtualChannelReceiverSpacePacketProcessor(Function<T, ? extends Collection<SpacePacket>> mapper, ExecutorService executor, boolean timely) {
        super(mapper, executor, timely);
    }

    public VirtualChannelReceiverSpacePacketProcessor(Function<T, ? extends Collection<SpacePacket>> mapper, boolean timely) {
        this(mapper, null, timely);
    }

    public VirtualChannelReceiverSpacePacketProcessor(Function<T, ? extends Collection<SpacePacket>> mapper) {
        this(mapper, false);
    }
}
