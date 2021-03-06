/*******************************************************************************
 * (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 which accompany this distribution.
 *
 * The Apache License is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package io.cloudslang.lang.compiler;

import io.cloudslang.lang.entities.SlangSystemPropertyConstant;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

public class SlangSource {

    private final String source;
    private final String name;

    public SlangSource(String source, String name) {
        Validate.notNull(source, "Source cannot be null");

        this.source = source;
        this.name = name;
    }

    public static SlangSource fromFile(File file) {
        Validate.notNull(file, "File source cannot be null");
        Validate.isTrue(file.isFile(), "File source: " + file.getName() + " doesn't lead to a file, directories are not supported");

        String source;
        try {
            source = readFileToString(file);
        } catch (IOException e) {
            throw new RuntimeException("There was a problem reading the file: " + file.getName(), e);
        }
        return new SlangSource(source, Extension.removeExtension(file.getName()));
    }

    private static String readFileToString(File file) throws IOException {
        Charset charset = getCharset();
        return FileUtils.readFileToString(file, charset);
    }

    private static Charset getCharset() {
        Charset charset = Charset.defaultCharset();
        String cslangEncoding = System.getProperty(SlangSystemPropertyConstant.CSLANG_ENCODING.getValue());
        if (!StringUtils.isEmpty(cslangEncoding)) {
            charset = Charset.forName(cslangEncoding);
        }
        return charset;
    }

    public static SlangSource fromFile(URI uri) {
        return fromFile(new File(uri));
    }

    public static SlangSource fromBytes(byte[] bytes, String name) {
        return new SlangSource(new String(bytes, getCharset()), name);
    }

    public String getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("source", source)
                .append("name", name)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SlangSource that = (SlangSource) o;

        return new EqualsBuilder()
                .append(source, that.source)
                .append(name, that.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(source)
                .append(name)
                .toHashCode();
    }
}
