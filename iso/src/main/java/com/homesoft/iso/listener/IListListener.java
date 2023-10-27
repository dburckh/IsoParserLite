package com.homesoft.iso.listener;

import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.ParseListener;
import com.homesoft.iso.Type;

/**
 * When used with a CompositeListener this flattens out the ilst container
 * This allows the ilst subtag values to be directly set by an AnnotationListener
 * input ilst{someTag{data{value}, ...}
 * output ilst{someTag{value}, ...}
 */
public class IListListener extends ProxyListener implements Type {

    private int lastType;

    public IListListener(ParseListener listener) {
        super(listener);
    }

    @Override
    public void onContainerStart(int type) {
        lastType = type;
    }

    @Override
    public void onParsed(int type, Object result) {
        if (type == BoxTypes.TYPE_data) {
            super.onParsed(lastType, result);
        }
    }

    @Override
    public void onContainerEnd(int type) {
        // Intentionally blank
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_ilst;
    }
}
