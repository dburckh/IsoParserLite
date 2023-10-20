package com.homesoft.iso.listener;

import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.ParseListener;
import com.homesoft.iso.TypedParseListener;

/**
 * When used with a CompositeListener this flattens out the ilst container
 * This allows the ilst subtag values to be directly set by an AnnotationListener
 * input ilst{someTag{data{value}, ...}
 * output ilst{someTag{value}, ...}
 */
public class IListListener implements TypedParseListener {
    private final ParseListener delegate;

    private int lastType;

    public IListListener(ParseListener listener) {
        delegate = listener;
    }

    @Override
    public void onContainerStart(int type, Object result) {
        lastType = type;
    }

    @Override
    public void onParsed(int type, Object result) {
        if (type == BoxTypes.TYPE_data) {
            delegate.onParsed(lastType, result);
        }
    }

    @Override
    public void onContainerEnd(int type) {
        // Intentionally blank
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_ilst;
    }
}
