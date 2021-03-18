/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  See the NOTICE file distributed with this work for additional
 *  information regarding copyright ownership.
 */

package org.seaborne.delta.zk.direct;

import org.apache.zookeeper.ZooKeeper;
import org.seaborne.delta.zk.ZkLock;

import java.util.function.Supplier;

public final class DirectZkLock implements ZkLock {
    private final Supplier<ZooKeeper> client;
    private final String lock;

    public DirectZkLock(final Supplier<ZooKeeper> client, final String lock) {
        this.client = client;
        this.lock = lock;
    }

    @Override
    public void close() throws Exception {
        this.client.get().delete(this.lock, this.client.get().exists(this.lock, false).getVersion());
    }
}
