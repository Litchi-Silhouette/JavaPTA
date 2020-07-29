/*
 * Bamboo - A Program Analysis Framework for Java
 *
 * Copyright (C) 2020 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2020 Yue Li <yueli@nju.edu.cn>
 * All rights reserved.
 *
 * This software is designed for the "Static Program Analysis" course at
 * Nanjing University, and it supports a subset of Java features.
 * Bamboo is only for educational and academic purposes, and any form of
 * commercial use is disallowed.
 */

package bamboo.options;

import bamboo.pta.options.Options;
import org.junit.Assert;
import org.junit.Test;

public class OptionsTest {

    @Test
    public void testHelp() {
        Options.parse("--help");
        if (Options.get().shouldShowHelp()) {
            Options.get().printHelp();
        }
    }

    @Test
    public void testVersion() {
        Options.parse("-V");
        if (Options.get().shouldShowVersion()) {
            Options.get().printVersion();
        }
    }

    @Test
    public void testOptions() {
        Options.parse("--no-implicit-entries", "-cs", "2-object");
        Assert.assertFalse(Options.get().analyzeImplicitEntries());
        Assert.assertTrue(Options.get().isMergeStringBuilders());
        Assert.assertEquals("2-object", Options.get().getContextSensitivity());
        Options.parse("--no-merge-string-builders");
        Assert.assertFalse(Options.get().isMergeStringBuilders());
    }

    @Test
    public void testSootArgs() {
        Options.parse("--no-implicit-entries",
                "-cs", "2-object",
                "--", "-cp", "a/b/c.jar", "Main");
        Assert.assertEquals(3, Options.get().getSootArgs().length);
    }
}
