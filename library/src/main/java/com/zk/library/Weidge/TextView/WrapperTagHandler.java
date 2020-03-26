package com.zk.library.Weidge.TextView;

import android.support.annotation.Nullable;
import android.text.Editable;

import org.xml.sax.Attributes;

public interface WrapperTagHandler {
    boolean handleTag(boolean opening, String tag, Editable output, @Nullable Attributes attributes);
}
