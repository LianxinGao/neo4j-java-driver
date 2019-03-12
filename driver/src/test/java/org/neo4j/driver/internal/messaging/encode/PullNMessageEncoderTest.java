/*
 * Copyright (c) 2002-2019 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
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
package org.neo4j.driver.internal.messaging.encode;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.internal.messaging.ValuePacker;
import org.neo4j.driver.internal.messaging.request.PullAllMessage;
import org.neo4j.driver.internal.messaging.request.PullNMessage;
import org.neo4j.driver.v1.Value;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.neo4j.driver.v1.Values.value;

class PullNMessageEncoderTest
{
    private final PullNMessageEncoder encoder = new PullNMessageEncoder();
    private final ValuePacker packer = mock( ValuePacker.class );

    @Test
    void shouldEncodePullNMessage() throws Exception
    {
        encoder.encode( new PullNMessage( 100, 200 ), packer );

        Map<String,Value> meta = new HashMap<>();
        meta.put( "n", value( 100 ) );
        meta.put( "stmt_id", value( 200 ) );

        InOrder order = inOrder( packer );
        order.verify( packer ).packStructHeader( 1, PullNMessage.SIGNATURE );
        order.verify( packer ).pack( meta );
    }

    @Test
    void shouldAvoidStatementId() throws Exception
    {
        encoder.encode( new PullNMessage( 100, -1 ), packer );

        Map<String,Value> meta = new HashMap<>();
        meta.put( "n", value( 100 ) );

        InOrder order = inOrder( packer );
        order.verify( packer ).packStructHeader( 1, PullNMessage.SIGNATURE );
        order.verify( packer ).pack( meta );
    }

    @Test
    void shouldFailToEncodeWrongMessage()
    {
        assertThrows( IllegalArgumentException.class, () -> encoder.encode( PullAllMessage.PULL_ALL, packer ) );
    }
}