package com.homesoft.iso.listener;

import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.ParseListener;
import com.homesoft.iso.TypedParseListener;

/**
 * Flattens out the ilst tag
 * input ilst{someTag{data{value}, ...}
 * output ilst{someTag{value}, ...}
 */
public class IListListener implements TypedParseListener {
    private final ParseListener delegate;

    private boolean inIList;

    private int lastType;

    public IListListener(ParseListener listener) {
        delegate = listener;
    }

    @Override
    public void onContainerStart(int type, Object result) {
        if (inIList) {
            lastType = type;
            return;
        } else {
            if (type == BoxTypes.TYPE_ilst) {
                inIList = true;
            }
        }
        delegate.onContainerStart(type, result);
    }

    @Override
    public void onParsed(int type, Object result) {
        if (inIList) {
            if (type == BoxTypes.TYPE_data) {
                delegate.onParsed(lastType, result);
            }
            return;
        }
        delegate.onParsed(type, result);
    }

    @Override
    public void onContainerEnd(int type) {
        if (inIList) {
            if (type == BoxTypes.TYPE_ilst) {
                inIList = false;
            } else {
                return;
            }
        }
        delegate.onContainerEnd(type);
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
