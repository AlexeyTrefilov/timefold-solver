/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.Arrays;

public class IndexerKey {

    private Object[] indexProperties;

    public IndexerKey(Object[] indexProperties) {
        this.indexProperties = indexProperties;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(indexProperties);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IndexerKey)) {
            return false;
        }
        IndexerKey other = (IndexerKey) o;
        return Arrays.equals(indexProperties, other.indexProperties);
    }

}