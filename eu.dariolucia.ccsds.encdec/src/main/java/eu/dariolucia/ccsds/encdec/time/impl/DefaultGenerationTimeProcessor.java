/*
 *   Copyright (c) 2019 Dario Lucia (https://www.dariolucia.eu)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package eu.dariolucia.ccsds.encdec.time.impl;

import eu.dariolucia.ccsds.encdec.definition.EncodedParameter;
import eu.dariolucia.ccsds.encdec.time.IGenerationTimeProcessor;

import java.time.Duration;
import java.time.Instant;

public class DefaultGenerationTimeProcessor implements IGenerationTimeProcessor {

    private final Instant referenceGenerationTime;

    public DefaultGenerationTimeProcessor(Instant referenceGenerationTime) {
        this.referenceGenerationTime = referenceGenerationTime;
    }

    @Override
    public Instant computeGenerationTime(EncodedParameter ei, Object value, Instant derivedGenerationTime, Duration derivedOffset, Integer fixedOffsetMs) {
        Instant baseTime = derivedGenerationTime == null ? referenceGenerationTime : derivedGenerationTime;
        if(baseTime == null) {
            throw new IllegalStateException("At least one between reference generation time and derived generation time must be set");
        }
        if(derivedOffset != null) {
            baseTime = baseTime.plusSeconds(derivedOffset.getSeconds());
            baseTime = baseTime.plusNanos(derivedOffset.getNano());
        }
        if(fixedOffsetMs != null) {
            baseTime = baseTime.plusMillis(fixedOffsetMs);
        }
        return baseTime;
    }
}
