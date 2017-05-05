package com.uxuan.util.config.impl;

import com.uxuan.util.config.ConfigMergeable;
import com.uxuan.util.config.ConfigValue;

interface MergeableValue extends ConfigMergeable {
    // converts a Config to its root object and a ConfigValue to itself
    ConfigValue toFallbackValue();
}
