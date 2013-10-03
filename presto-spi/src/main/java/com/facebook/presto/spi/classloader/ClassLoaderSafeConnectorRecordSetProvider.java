/*
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
package com.facebook.presto.spi.classloader;

import com.facebook.presto.spi.ColumnHandle;
import com.facebook.presto.spi.ConnectorRecordSetProvider;
import com.facebook.presto.spi.RecordSet;
import com.facebook.presto.spi.Split;

import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class ClassLoaderSafeConnectorRecordSetProvider
        implements ConnectorRecordSetProvider
{
    private final ConnectorRecordSetProvider delegate;
    private final ClassLoader classLoader;

    public ClassLoaderSafeConnectorRecordSetProvider(ConnectorRecordSetProvider delegate, ClassLoader classLoader)
    {
        this.delegate = delegate;
        this.classLoader = classLoader;
    }

    @Override
    public boolean canHandle(Split split)
    {
        try (ThreadContextClassLoader threadContextClassLoader = new ThreadContextClassLoader(classLoader)) {
            return delegate.canHandle(split);
        }
    }

    @Override
    public RecordSet getRecordSet(Split split, List<? extends ColumnHandle> columns)
    {
        try (ThreadContextClassLoader threadContextClassLoader = new ThreadContextClassLoader(classLoader)) {
            return new ClassLoaderSafeRecordSet(delegate.getRecordSet(split, columns), classLoader);
        }
    }

    @Override
    public String toString()
    {
        try (ThreadContextClassLoader threadContextClassLoader = new ThreadContextClassLoader(classLoader)) {
            return delegate.toString();
        }
    }
}