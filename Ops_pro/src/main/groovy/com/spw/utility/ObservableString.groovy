package com.spw.utility

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class ObservableString {
    String value
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this)

    ObservableString() {
        value = ""
    }

    ObservableString(String newValue) {
        value = newValue
    }

    public void setValue(String newValue) {
        String oldValue = this.value
        this.value = newValue
        if (oldValue.equals(newValue)) {
            return
        }
        this.pcs.firePropertyChange("value", oldValue, newValue)
    }

    public String getValue() {
        return value
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener)
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener)
    }
}
