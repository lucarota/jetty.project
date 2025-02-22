//
// ========================================================================
// Copyright (c) 1995 Mort Bay Consulting Pty Ltd and others.
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Public License v. 2.0 which is available at
// https://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
// which is available at https://www.apache.org/licenses/LICENSE-2.0.
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
// ========================================================================
//

package org.eclipse.jetty.server;

import java.io.IOException;

import org.eclipse.jetty.io.ByteBufferPool;

public class MockConnector extends AbstractConnector
{
    public MockConnector(Server server)
    {
        super(server, server.getThreadPool(), server.getScheduler(), ByteBufferPool.NON_POOLING, 0);
    }

    @Override
    protected void accept(int acceptorID) throws IOException, InterruptedException
    {
    }

    @Override
    public Object getTransport()
    {
        return null;
    }
}
