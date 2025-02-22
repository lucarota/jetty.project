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

package org.eclipse.jetty.io.internal;

import java.nio.charset.Charset;

import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.util.CharsetStringBuilder;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.thread.Invocable;

public class ContentSourceString
{
    private final Content.Source content;
    private final CharsetStringBuilder text;
    private final Promise<String> promise;
    private final DemandTask _demandTask;

    public ContentSourceString(Content.Source content, Charset charset, Promise<String> promise)
    {
        this.content = content;
        this.text = CharsetStringBuilder.forCharset(charset);
        this.promise = promise;
        // Inner class used instead of lambda for clarity in stack traces.
        this._demandTask = new DemandTask();
    }

    public void convert()
    {
        while (true)
        {
            Content.Chunk chunk = content.read();
            if (chunk == null)
            {
                content.demand(_demandTask);
                return;
            }
            if (Content.Chunk.isFailure(chunk))
            {
                promise.failed(chunk.getFailure());
                if (!chunk.isLast())
                    content.fail(chunk.getFailure());
                return;
            }
            text.append(chunk.getByteBuffer());
            chunk.release();
            if (chunk.isLast())
            {
                succeed();
                return;
            }
        }
    }

    private void succeed()
    {
        try
        {
            String result = text.build();
            promise.succeeded(result);
        }
        catch (Throwable x)
        {
            promise.failed(x);
        }
    }

    private class DemandTask extends Invocable.Task.Abstract
    {
        DemandTask()
        {
            super(Invocable.getInvocationType(promise));
        }

        @Override
        public void run()
        {
            convert();
        }
    }
}
