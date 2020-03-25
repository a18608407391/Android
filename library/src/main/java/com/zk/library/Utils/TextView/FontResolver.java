package com.zk.library.Utils.TextView;

/**
 * Interface for font-resolving components.
 */
public interface FontResolver {

    FontFamily getDefaultFont();

    FontFamily getSansSerifFont();

    FontFamily getSerifFont();

    FontFamily getMonoSpaceFont();

    FontFamily getFont(String name);

}
