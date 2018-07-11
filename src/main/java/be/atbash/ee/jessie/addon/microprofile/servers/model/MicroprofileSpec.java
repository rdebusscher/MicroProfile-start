/*
 * Copyright 2017-2018 Rudy De Busscher (https://www.atbash.be)
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
 */
package be.atbash.ee.jessie.addon.microprofile.servers.model;

import be.atbash.ee.jessie.core.model.MicroProfileVersion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum MicroprofileSpec {
    // @formatter:off
    CONFIG("config", "Config", Arrays.asList(MicroProfileVersion.MP12, MicroProfileVersion.MP13))
    , FAULT_TOLERANCE("fault_tolerance", "Fault Tolerance", Arrays.asList(MicroProfileVersion.MP12, MicroProfileVersion.MP13))
    , JWT_AUTH("JWT_auth", "JWT Auth", Arrays.asList(MicroProfileVersion.MP12, MicroProfileVersion.MP13))
    , HEALTH_METRICS("health_metrics", "Health Metrics", Arrays.asList(MicroProfileVersion.MP12, MicroProfileVersion.MP13))
    , HEALTH_CHECKS("health_checks", "Health Checks", Arrays.asList(MicroProfileVersion.MP12, MicroProfileVersion.MP13))
    , OPEN_API("open_API", "OpenAPI", Collections.singletonList(MicroProfileVersion.MP13))
    , OPEN_TRACING("open_tracing", "OpenTracing", Collections.singletonList(MicroProfileVersion.MP13))
    , REST_CLIENT("rest_client", "TypeSafe Rest Client", Collections.singletonList(MicroProfileVersion.MP13))
    ;
    // @formatter:on

    private String code;
    private String label;
    private List<MicroProfileVersion> mpVersions;

    MicroprofileSpec(String code, String label, List<MicroProfileVersion> mpVersions) {
        this.code = code;
        this.label = label;

        this.mpVersions = mpVersions;
    }

    public String getCode() {
        return code;
    }

    public List<MicroProfileVersion> getMpVersions() {
        return mpVersions;
    }

    public String getLabel() {
        return label;
    }

    public static MicroprofileSpec valueFor(String data) {
        MicroprofileSpec result = null;
        for (MicroprofileSpec spec : MicroprofileSpec.values()) {
            if (spec.code.equals(data)) {
                result = spec;
            }
        }
        return result;
    }
}
