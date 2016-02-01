import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BaseClass {
    private PropertyChangeSupport _changes = new PropertyChangeSupport(this);

    public void AddPropertyChangeListener(PropertyChangeListener l) {
        _changes.addPropertyChangeListener(l);
    }
    public void RemovePropertyChangeListener(PropertyChangeListener l) {
        _changes.removePropertyChangeListener(l);
    }

    public void RaisePropertyChanged(String propertyName, Object oldValue, Object newValue){
        _changes.firePropertyChange(propertyName, oldValue, newValue);
    }
}